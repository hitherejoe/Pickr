package com.hitherejoe.pickr.ui.activity;

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

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.hitherejoe.pickr.PickrApplication;
import com.hitherejoe.pickr.R;
import com.hitherejoe.pickr.data.DataManager;
import com.hitherejoe.pickr.data.model.Location;
import com.hitherejoe.pickr.ui.adapter.LocationHolder;
import com.hitherejoe.pickr.util.DialogFactory;
import com.hitherejoe.pickr.util.SnackbarFactory;

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

    private CompositeSubscription mCompositeSubscription;
    private DataManager mDataManager;
    private EasyRecyclerAdapter<Location> mEasyRecycleAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        applicationComponent().inject(this);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mCompositeSubscription = new CompositeSubscription();
        mDataManager = PickrApplication.get(this).getComponent().dataManager();

        setupToolbar();
        setupRecyclerView();
        loadLocations();
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
        mEasyRecycleAdapter = new EasyRecyclerAdapter<>(this, LocationHolder.class, mLocationListener);
        mCharactersRecycler.setAdapter(mEasyRecycleAdapter);
    }

    private void savePlace(Place place) {
        Location location = Location.fromPlace(place);
        mCompositeSubscription.add(mDataManager.saveLocation(location)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(mDataManager.getScheduler())
                .subscribe(new Subscriber<Location>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Location location) {

                    }
                }));
    }

    private void loadLocations() {
        Timber.e("CALLED");
        mCompositeSubscription.add(mDataManager.getLocations()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(mDataManager.getScheduler())
                .subscribe(new Subscriber<List<Location>>() {
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
                    public void onNext(List<Location> locations) {
                        mProgressBar.setVisibility(View.GONE);
                        if (locations.size() > 0) {
                            mEasyRecycleAdapter.setItems(locations);
                            mEasyRecycleAdapter.notifyDataSetChanged();
                        } else {
                            mCharactersRecycler.setVisibility(View.GONE);
                            mNoPlacesText.setVisibility(View.VISIBLE);
                        }

                    }
                }));
    }

    private void showDeleteDialog(final Location location) {
        DialogFactory.createSimpleYesNoErrorDialog(MainActivity.this, "Delete location", "Delete location?",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteLocation(location);
                    }
                }).show();
    }

    private void deleteLocation(Location location) {
        mCompositeSubscription.add(mDataManager.deleteLocation(location)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(mDataManager.getScheduler())
                .subscribe(new Subscriber<Location>() {
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
                    public void onNext(Location location) {
                        mEasyRecycleAdapter.removeItem(location);
                    }
                }));
    }

    private LocationHolder.LocationListener mLocationListener = new LocationHolder.LocationListener() {
        @Override
        public void onLocationLongPress(Location location) {
            showDeleteDialog(location);
        }
    };

}
