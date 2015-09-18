package com.hitherejoe.pickr.ui.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.maps.android.SphericalUtil;
import com.hitherejoe.pickr.AndroidBoilerplateApplication;
import com.hitherejoe.pickr.R;
import com.hitherejoe.pickr.data.DataManager;
import com.hitherejoe.pickr.data.model.Location;
import com.hitherejoe.pickr.ui.adapter.SearchHolder;
import com.hitherejoe.pickr.util.DialogFactory;
import com.hitherejoe.pickr.util.SnackbarFactory;

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

public class SearchActivity extends BaseActivity implements GoogleApiClient.OnConnectionFailedListener, SearchView.OnQueryTextListener {

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.recycler_places)
    RecyclerView mPlacesRecycler;

    @Bind(R.id.layout_search)
    CoordinatorLayout mLayoutSearch;

    private EasyRecyclerAdapter<Place> mEasyRecycleAdapter;
    private CompositeSubscription mSubscriptions;
    private DataManager mDataManager;
    private ReactiveLocationProvider mLocationProvider;
    private android.location.Location mCurrentKnownLocation;
    private ProgressDialog mProgressDialog;
    protected GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0 /* clientId */, this)
                .addApi(Places.GEO_DATA_API)
                .build();

        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        mDataManager = AndroidBoilerplateApplication.get(this).getComponent().dataManager();
        mSubscriptions = new CompositeSubscription();
        mLocationProvider = new ReactiveLocationProvider(this);
        mProgressDialog = DialogFactory.createProgressDialog(this, R.string.text_getting_location);

        setupToolbar();
        setupRecyclerView();
        retrieveDeviceCurrentLocation();
    }

    private void retrieveDeviceCurrentLocation() {
        LocationRequest request = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
                .setSmallestDisplacement(100)
                .setInterval(TimeUnit.MINUTES.toMillis(1));

        Observable<android.location.Location> lastKnownLocationObservable = mLocationProvider.getLastKnownLocation();
        mSubscriptions.add(
                new ReactiveLocationProvider(this)
                        .getUpdatedLocation(request)
                        .onErrorResumeNext(lastKnownLocationObservable)
                        .startWith(lastKnownLocationObservable)
                        .subscribe(new Observer<android.location.Location>() {
                            @Override
                            public void onCompleted() {
                            }

                            @Override
                            public void onError(Throwable e) {
                                mProgressDialog.dismiss();
                                Timber.e("Couldn't get users current location " + e);
                            }

                            @Override
                            public void onNext(android.location.Location location) {
                                mCurrentKnownLocation = location;
                                mProgressDialog.dismiss();
                            }
                        }));
    }

    private void setupToolbar() {
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Search");
        }
    }

    private void setupRecyclerView() {
        mPlacesRecycler.setLayoutManager(new LinearLayoutManager(this));
        mEasyRecycleAdapter = new EasyRecyclerAdapter<>(this, SearchHolder.class, mLocationListener);
        mPlacesRecycler.setAdapter(mEasyRecycleAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);
        setupSearchView(menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void setupSearchView(Menu menu) {
        ActionBar actionBar = getSupportActionBar();
        Context context = actionBar != null ? actionBar.getThemedContext() : this;
        final SearchView searchView = new SearchView(context);
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(this);
        menu.findItem(R.id.action_search).setActionView(searchView);
    }

    private void checkPlace(final Place place) {
        mSubscriptions.add(mDataManager.getLocation(place.getId())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(mDataManager.getScheduler())
                .subscribe(new Subscriber<Location>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e("There was an error retrieving the place..." + e);
                    }

                    @Override
                    public void onNext(Location location) {
                        if (location == null) {
                            savePlace(place);
                        } else {
                            DialogFactory.createSimpleOkErrorDialog(SearchActivity.this, "Error", "This place is already saved!").show();
                        }
                    }
                }));
    }

    private void savePlace(Place place) {
        mProgressDialog = DialogFactory.createProgressDialog(this, R.string.text_saving_location);
        mProgressDialog.show();
        Location location = Location.fromPlace(place);
        mSubscriptions.add(mDataManager.saveLocation(location)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(mDataManager.getScheduler())
                .subscribe(new Subscriber<Location>() {
                    @Override
                    public void onCompleted() {
                        SnackbarFactory.createSnackbar(SearchActivity.this, mLayoutSearch, getString(R.string.text_place_saved)).show();
                        mProgressDialog.dismiss();
                        finish();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e("There was an error saving the place... " + e);
                    }

                    @Override
                    public void onNext(Location location) {

                    }
                }));
    }

    /**
     * Called when the Activity could not connect to Google Play services and the auto manager
     * could resolve the error automatically.
     * In this case the API is not available and notify the user.
     *
     * @param connectionResult can be inspected to determine the cause of the failure
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        //    Log.e(TAG, "onConnectionFailed: ConnectionResult.getErrorCode() = "
        //          + connectionResult.getErrorCode());

        // TODO(Developer): Check error code and notify the user of error state and resolution.
        Toast.makeText(this,
                "Could not connect to Google API Client: Error " + connectionResult.getErrorCode(),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        mEasyRecycleAdapter.setItems(new ArrayList<Place>());

        if (newText.length() > 0) {
            LatLng latLng = new LatLng(mCurrentKnownLocation.getLatitude(), mCurrentKnownLocation.getLongitude());
            mSubscriptions.add(mDataManager.getPlaces(mGoogleApiClient, newText, convertCenterAndRadiusToBounds(latLng, 10))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(mDataManager.getScheduler())
                    .subscribe(new Subscriber<Place>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            Timber.e("There was an error saving the place... " + e);
                        }

                        @Override
                        public void onNext(Place autocompletePrediction) {
                            mEasyRecycleAdapter.addItem(autocompletePrediction);
                        }
                    }));
        }

        return false;
    }

    public static class PlaceAutocomplete {

        public CharSequence placeId;
        public CharSequence description;

        public PlaceAutocomplete(CharSequence placeId, CharSequence description) {
            this.placeId = placeId;
            this.description = description;
        }

        @Override
        public String toString() {
            return description.toString();
        }
    }

    public LatLngBounds convertCenterAndRadiusToBounds(LatLng center, double radius) {
        LatLng southwest = SphericalUtil.computeOffset(center, radius * Math.sqrt(2.0), 225);
        LatLng northeast = SphericalUtil.computeOffset(center, radius * Math.sqrt(2.0), 45);
        return new LatLngBounds(southwest, northeast);
    }

    private SearchHolder.LocationListener mLocationListener = new SearchHolder.LocationListener() {
        @Override
        public void onLocationPress(Place location) {
            checkPlace(location);
        }
    };
}