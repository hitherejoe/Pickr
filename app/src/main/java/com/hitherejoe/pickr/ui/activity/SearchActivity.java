package com.hitherejoe.pickr.ui.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.hitherejoe.pickr.PickrApplication;
import com.hitherejoe.pickr.R;
import com.hitherejoe.pickr.data.DataManager;
import com.hitherejoe.pickr.data.model.PointOfInterest;
import com.hitherejoe.pickr.ui.adapter.AutocompletePlaceHolder;
import com.hitherejoe.pickr.util.DataUtils;
import com.hitherejoe.pickr.util.DialogFactory;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.charmas.android.reactivelocation.ReactiveLocationProvider;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;
import uk.co.ribot.easyadapter.EasyRecyclerAdapter;

public class SearchActivity extends BaseActivity implements GoogleApiClient.OnConnectionFailedListener {

    @Bind(R.id.progress_indicator)
    ProgressBar mProgressBar;

    @Bind(R.id.recycler_suggestions)
    RecyclerView mPlacesRecycler;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    private static final int BOUNDS_RADIUS = 10;

    protected GoogleApiClient mGoogleApiClient;

    private CompositeSubscription mSubscriptions;
    private DataManager mDataManager;
    private Dialog mDialog;
    private EasyRecyclerAdapter<PointOfInterest> mEasyRecycleAdapter;
    private Location mCurrentKnownLocation;
    private ProgressDialog mProgressDialog;
    private ReactiveLocationProvider mLocationProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0, this)
                .addApi(Places.GEO_DATA_API)
                .build();

        mDataManager = PickrApplication.get(this).getComponent().dataManager();
        mSubscriptions = new CompositeSubscription();
        mLocationProvider = new ReactiveLocationProvider(this);
        mProgressDialog = DialogFactory.createProgressDialog(this, R.string.dialog_text_getting_location);

        retrieveDeviceCurrentLocation();
        setupToolbar();
        setupRecyclerView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mDialog != null) mDialog.dismiss();
        mSubscriptions.unsubscribe();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);
        setupSearchView(menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Timber.e("Connection error: " + result.getErrorCode() + " : " + result.getErrorCode());
        DialogFactory.createSimpleOkErrorDialog(
                this,
                "Error",
                "Could not connect to Google API Client: Error " + result.getErrorCode()
        ).show();
    }

    private void setupToolbar() {
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getString(R.string.label_search));
        }
    }

    private void setupRecyclerView() {
        mPlacesRecycler.setLayoutManager(new LinearLayoutManager(this));
        mEasyRecycleAdapter = new EasyRecyclerAdapter<>(this, AutocompletePlaceHolder.class, mLocationListener);
        mPlacesRecycler.setAdapter(mEasyRecycleAdapter);
    }

    private void setupSearchView(Menu menu) {
        ActionBar actionBar = getSupportActionBar();
        Context context = actionBar != null ? actionBar.getThemedContext() : this;
        SearchView searchView = new SearchView(context);
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String queryText) {
                mEasyRecycleAdapter.setItems(new ArrayList<PointOfInterest>());
                if (queryText.length() > 0) {
                    getAutocompleteResults(queryText);
                } else {
                    mSubscriptions.unsubscribe();
                    mProgressBar.setVisibility(View.GONE);
                }
                return false;
            }
        });
        searchView.requestFocus();
        menu.findItem(R.id.action_search).setActionView(searchView);
    }

    private void retrieveDeviceCurrentLocation() {
        LocationRequest request = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
                .setSmallestDisplacement(100)
                .setInterval(TimeUnit.MINUTES.toMillis(1));

        Observable<android.location.Location> lastKnownLocationObservable = mLocationProvider.getLastKnownLocation();
        mSubscriptions.add(new ReactiveLocationProvider(this)
                .getUpdatedLocation(request)
                .onErrorResumeNext(lastKnownLocationObservable)
                .startWith(lastKnownLocationObservable)
                .subscribe(new Observer<Location>() {
                    @Override
                    public void onCompleted() { }

                    @Override
                    public void onError(Throwable e) {
                        mProgressDialog.dismiss();
                        Timber.e("Couldn't get users current location " + e);
                    }

                    @Override
                    public void onNext(Location location) {
                        mCurrentKnownLocation = location;
                        mProgressDialog.dismiss();
                    }
                }));
    }

    private void savePlace(final PointOfInterest pointOfInterest) {
        mProgressDialog = DialogFactory.createProgressDialog(this, R.string.dialog_text_saving_location);
        mProgressDialog.show();

        mSubscriptions.add(mDataManager.saveLocation(this, pointOfInterest)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(mDataManager.getScheduler())
                .subscribe(new Subscriber<PointOfInterest>() {
                    @Override
                    public void onCompleted() { }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e("There was an error saving the place... " + e);
                    }

                    @Override
                    public void onNext(PointOfInterest pointOfInterest) {
                        mProgressDialog.dismiss();
                        if (pointOfInterest == null) {
                            if (mDialog == null) {
                                mDialog = DialogFactory.createSimpleOkErrorDialog(
                                        SearchActivity.this,
                                        getString(R.string.dialog_error_title),
                                        getString(R.string.dialog_text_place_exists)
                                );
                            }
                            mDialog.show();
                        } else {
                            finish();
                        }
                    }
                }));
    }

    private void getAutocompleteResults(String queryText) {
        mProgressBar.setVisibility(View.VISIBLE);
        LatLng latLng = new LatLng(
                mCurrentKnownLocation.getLatitude(),
                mCurrentKnownLocation.getLongitude()
        );
        LatLngBounds latLngBounds = DataUtils.latitudeLongitudeToBounds(latLng, BOUNDS_RADIUS);
        mSubscriptions.add(mDataManager.getPredictions(mGoogleApiClient, queryText, latLngBounds)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(mDataManager.getScheduler())
                .subscribe(new Subscriber<PointOfInterest>() {
                    @Override
                    public void onCompleted() {
                        mProgressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mProgressBar.setVisibility(View.GONE);
                        Timber.e("There was an error getting place suggestions... " + e);
                    }

                    @Override
                    public void onNext(PointOfInterest pointOfInterest) {
                        mEasyRecycleAdapter.addItem(pointOfInterest);
                    }
                }));
    }

    private AutocompletePlaceHolder.LocationListener mLocationListener = new AutocompletePlaceHolder.LocationListener() {
        @Override
        public void onLocationPress(PointOfInterest pointOfInterest) {
            savePlace(pointOfInterest);
        }
    };
}