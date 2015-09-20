package com.hitherejoe.pickr.ui.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.hitherejoe.pickr.PickrApplication;
import com.hitherejoe.pickr.R;
import com.hitherejoe.pickr.data.BusEvent;
import com.hitherejoe.pickr.data.DataManager;
import com.hitherejoe.pickr.data.model.PointOfInterest;
import com.hitherejoe.pickr.ui.adapter.PointOfInterestHolder;
import com.hitherejoe.pickr.util.DialogFactory;
import com.hitherejoe.pickr.util.SnackbarFactory;
import com.squareup.otto.Subscribe;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;
import uk.co.ribot.easyadapter.EasyRecyclerAdapter;

public class MainActivity extends BaseActivity {

    @Bind(R.id.layout_main)
    CoordinatorLayout mLayoutRoot;

    @Bind(R.id.progress_indicator)
    ProgressBar mProgressBar;

    @Bind(R.id.recycler_characters)
    RecyclerView mCharactersRecycler;

    @Bind(R.id.text_no_places)
    TextView mNoPlacesText;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    private static final int PLACE_PICKER_REQUEST = 1020;
    private static final int REQUEST_CODE_PLAY_SERVICES = 1235;

    private CompositeSubscription mCompositeSubscription;
    private DataManager mDataManager;
    private EasyRecyclerAdapter<PointOfInterest> mEasyRecycleAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        applicationComponent().inject(this);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mCompositeSubscription = new CompositeSubscription();
        mDataManager = PickrApplication.get(this).getComponent().dataManager();
        PickrApplication.get(this).getComponent().eventBus().register(this);
        setupToolbar();
        setupRecyclerView();
        if (checkPlayServices()) loadLocations();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place selectedPlace = PlacePicker.getPlace(data, this);
                savePlace(selectedPlace);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PickrApplication.get(this).getComponent().eventBus().unregister(this);
        mCompositeSubscription.unsubscribe();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_open_search:
                startActivity(new Intent(this, SearchActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Subscribe
    public void onPlaceAdded(BusEvent.PlaceAdded event) {
        loadLocations();
        SnackbarFactory.createSnackbar(
                this,
                mLayoutRoot,
                getString(R.string.text_place_saved)
        ).show();
    }

    @OnClick(R.id.fab_add_place)
    public void onAddPlaceCLick() {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        Context context = getApplicationContext();
        try {
            startActivityForResult(builder.build(context), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    private void setupToolbar() {
        setSupportActionBar(mToolbar);
    }

    private void setupRecyclerView() {
        mCharactersRecycler.setLayoutManager(new LinearLayoutManager(this));
        mEasyRecycleAdapter = new EasyRecyclerAdapter<>(this, PointOfInterestHolder.class, mLocationListener);
        mCharactersRecycler.setAdapter(mEasyRecycleAdapter);
    }

    private void savePlace(Place place) {
        PointOfInterest pointOfInterest = PointOfInterest.fromPlace(place);
        mCompositeSubscription.add(mDataManager.saveLocation(this, pointOfInterest)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(mDataManager.getScheduler())
                .subscribe(new Subscriber<PointOfInterest>() {
                    @Override
                    public void onCompleted() {
                        Timber.e("IN MAIN COMPLETE");
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(PointOfInterest pointOfInterest) {
                        if (pointOfInterest == null) {
                            SnackbarFactory.createSnackbar(
                                    MainActivity.this,
                                    mLayoutRoot,
                                    getString(R.string.text_place_exists)
                            ).show();
                        }
                    }
                }));
    }

    private void loadLocations() {
        mCompositeSubscription.add(mDataManager.getLocations()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(mDataManager.getScheduler())
                .subscribe(new Subscriber<List<PointOfInterest>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable error) {
                        Timber.e("There was an error retrieving the locations " + error);
                        mProgressBar.setVisibility(View.GONE);
                        DialogFactory.createSimpleErrorDialog(MainActivity.this).show();
                    }

                    @Override
                    public void onNext(List<PointOfInterest> pointOfInterests) {
                        mProgressBar.setVisibility(View.GONE);
                        if (pointOfInterests.size() > 0) {
                            mEasyRecycleAdapter.setItems(pointOfInterests);
                            mCharactersRecycler.setVisibility(View.VISIBLE);
                            mNoPlacesText.setVisibility(View.GONE);
                        } else {
                            mCharactersRecycler.setVisibility(View.GONE);
                            mNoPlacesText.setVisibility(View.VISIBLE);
                        }
                    }
                }));
    }

    private void showDeleteDialog(final PointOfInterest pointOfInterest) {
        DialogFactory.createSimpleYesNoErrorDialog(MainActivity.this, "Delete location", "Delete location?",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteLocation(pointOfInterest);
                    }
                }).show();
    }

    private void deleteLocation(PointOfInterest pointOfInterest) {
        mCompositeSubscription.add(mDataManager.deleteLocation(pointOfInterest)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(mDataManager.getScheduler())
                .subscribe(new Subscriber<PointOfInterest>() {
                    @Override
                    public void onCompleted() {
                        SnackbarFactory.createSnackbar(
                                MainActivity.this, mLayoutRoot, "Location deleted").show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e("There was an error deleting the location " + e);
                    }

                    @Override
                    public void onNext(PointOfInterest pointOfInterest) {
                        mEasyRecycleAdapter.removeItem(pointOfInterest);
                    }
                }));
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(
                        resultCode, this, REQUEST_CODE_PLAY_SERVICES).show();
            } else {
                Dialog playServicesDialog = DialogFactory.createSimpleOkErrorDialog(
                        this,
                        getString(R.string.dialog_error_title),
                        getString(R.string.error_message_play_services)
                );
                playServicesDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        finish();
                    }
                });
                playServicesDialog.show();
            }
            return false;
        }
        return true;
    }

    private PointOfInterestHolder.LocationListener mLocationListener = new PointOfInterestHolder.LocationListener() {
        @Override
        public void onLocationLongPress(PointOfInterest pointOfInterest) {
            showDeleteDialog(pointOfInterest);
        }
    };

}
