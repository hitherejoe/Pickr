package com.hitherejoe.pickr.data;

import android.content.Context;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLngBounds;
import com.hitherejoe.pickr.AndroidBoilerplateApplication;
import com.hitherejoe.pickr.data.local.DatabaseHelper;
import com.hitherejoe.pickr.data.local.PreferencesHelper;
import com.hitherejoe.pickr.data.model.Location;
import com.hitherejoe.pickr.data.remote.AndroidBoilerplateService;
import com.hitherejoe.pickr.injection.component.DaggerDataManagerComponent;
import com.hitherejoe.pickr.injection.module.DataManagerModule;
import com.hitherejoe.pickr.ui.activity.SearchActivity;
import com.squareup.otto.Bus;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.functions.Func1;

public class DataManager {

    @Inject protected AndroidBoilerplateService mAndroidBoilerplateService;
    @Inject protected DatabaseHelper mDatabaseHelper;
    @Inject protected PreferencesHelper mPreferencesHelper;
    @Inject protected Scheduler mSubscribeScheduler;
    @Inject protected Bus mEventBus;

    public DataManager(Context context) {
        injectDependencies(context);
    }

    /* This constructor is provided so we can set up a DataManager with mocks from unit test.
     * At the moment this is not possible to do with Dagger because the Gradle APT plugin doesn't
     * work for the unit test variant, plus Dagger 2 doesn't provide a nice way of overriding
     * modules */
    public DataManager(AndroidBoilerplateService watchTowerService,
                       DatabaseHelper databaseHelper,
                       Bus eventBus,
                       PreferencesHelper preferencesHelper,
                       Scheduler subscribeScheduler) {
        mAndroidBoilerplateService = watchTowerService;
        mDatabaseHelper = databaseHelper;
        mEventBus = eventBus;
        mPreferencesHelper = preferencesHelper;
        mSubscribeScheduler = subscribeScheduler;
    }

    protected void injectDependencies(Context context) {
        DaggerDataManagerComponent.builder()
                .applicationComponent(AndroidBoilerplateApplication.get(context).getComponent())
                .dataManagerModule(new DataManagerModule(context))
                .build()
                .inject(this);
    }

    public Scheduler getScheduler() {
        return mSubscribeScheduler;
    }

    public Observable<List<Location>> getLocations() {
        return mDatabaseHelper.getLocations().flatMapIterable(new Func1<List<Location>, Iterable<? extends Location>>() {
            @Override
            public Iterable<? extends Location> call(List<Location> locations) {
                return locations;
            }
        }).toList();
    }

    public Observable<Location> saveLocation(Location location) {
        return mDatabaseHelper.saveLocation(location);
    }

    public Observable<Location> deleteLocation(Location location) {
        return mDatabaseHelper.deleteLocation(location);
    }

    public Observable<SearchActivity.PlaceAutocomplete> getAutocompleteResults(final GoogleApiClient mGoogleApiClient, final String query, final LatLngBounds bounds) {
        return Observable.create(new Observable.OnSubscribe<SearchActivity.PlaceAutocomplete>() {
            @Override
            public void call(Subscriber<? super SearchActivity.PlaceAutocomplete> subscriber) {
                PendingResult<AutocompletePredictionBuffer> results =
                        Places.GeoDataApi.getAutocompletePredictions(mGoogleApiClient, query,
                                bounds, null);
                AutocompletePredictionBuffer autocompletePredictions = results
                        .await(60, TimeUnit.SECONDS);

                // Confirm that the query completed successfully, otherwise return null
                final Status status = autocompletePredictions.getStatus();
                if (!status.isSuccess()) {
                    autocompletePredictions.release();
                    subscriber.onError(null);
                } else {
                    // Copy the results into our own data structure, because we can't hold onto the buffer.
                    // AutocompletePrediction objects encapsulate the API response (place ID and description).
                    for (AutocompletePrediction autocompletePrediction : autocompletePredictions) {
                        subscriber.onNext(new SearchActivity.PlaceAutocomplete(autocompletePrediction.getPlaceId(), autocompletePrediction.getDescription()));
                    }
                    // Release the buffer now that all data has been copied.
                    autocompletePredictions.release();
                    subscriber.onCompleted();
                }
            }
        });
    }

    public Observable<Location> getLocation(String id) {
        return mDatabaseHelper.getLocation(id);
    }

    public Observable<Place> getPlaces(final GoogleApiClient mGoogleApiClient, final String query, final LatLngBounds bounds) {
        return getAutocompleteResults(mGoogleApiClient, query, bounds).flatMap(new Func1<SearchActivity.PlaceAutocomplete, Observable<Place>>() {
            @Override
            public Observable<Place> call(SearchActivity.PlaceAutocomplete placeAutocomplete) {
                return getPlace(mGoogleApiClient, placeAutocomplete.placeId.toString());
            }
        });
    }

    public Observable<Place> getPlace(final GoogleApiClient mGoogleApiClient, final String id) {
        return Observable.create(new Observable.OnSubscribe<Place>() {
            @Override
            public void call(final Subscriber<? super Place> subscriber) {
                PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                        .getPlaceById(mGoogleApiClient, id);
                placeResult.setResultCallback(new ResultCallback<PlaceBuffer>() {
                    @Override
                    public void onResult(PlaceBuffer places) {
                        if (!places.getStatus().isSuccess()) {
                            // Request did not complete successfully
                            places.release();
                            subscriber.onError(null);
                        } else {
                            subscriber.onNext(places.get(0));
                            subscriber.onCompleted();
                        }
                    }
                });
            }
        });
    }

}
