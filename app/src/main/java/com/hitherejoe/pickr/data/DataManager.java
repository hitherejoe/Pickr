package com.hitherejoe.pickr.data;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLngBounds;
import com.hitherejoe.pickr.PickrApplication;
import com.hitherejoe.pickr.data.local.DatabaseHelper;
import com.hitherejoe.pickr.data.model.PointOfInterest;
import com.hitherejoe.pickr.data.model.AutocompletePlace;
import com.hitherejoe.pickr.injection.component.DaggerDataManagerComponent;
import com.hitherejoe.pickr.injection.module.DataManagerModule;
import com.squareup.otto.Bus;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.functions.Action0;
import rx.functions.Func1;
import timber.log.Timber;

public class DataManager {

    @Inject protected DatabaseHelper mDatabaseHelper;
    @Inject protected Scheduler mSubscribeScheduler;
    @Inject protected Bus mEventBus;

    public DataManager(Context context) {
        injectDependencies(context);
    }

    /* This constructor is provided so we can set up a DataManager with mocks from unit test.
     * At the moment this is not possible to do with Dagger because the Gradle APT plugin doesn't
     * work for the unit test variant, plus Dagger 2 doesn't provide a nice way of overriding
     * modules */
    public DataManager(DatabaseHelper databaseHelper,
                       Bus eventBus,
                       Scheduler subscribeScheduler) {
        mDatabaseHelper = databaseHelper;
        mEventBus = eventBus;
        mSubscribeScheduler = subscribeScheduler;
    }

    protected void injectDependencies(Context context) {
        DaggerDataManagerComponent.builder()
                .applicationComponent(PickrApplication.get(context).getComponent())
                .dataManagerModule(new DataManagerModule(context))
                .build()
                .inject(this);
    }

    public Scheduler getScheduler() {
        return mSubscribeScheduler;
    }

    public Observable<List<PointOfInterest>> getLocations() {
        return mDatabaseHelper.getLocations();
    }

    public Observable<PointOfInterest> saveLocation(final Context context, final PointOfInterest pointOfInterest) {

        return doesLocationExist(pointOfInterest.id).flatMap(new Func1<Boolean, Observable<PointOfInterest>>() {
            @Override
            public Observable<PointOfInterest> call(Boolean doesLocationExist) {
                if (doesLocationExist) return Observable.just(null);

                return mDatabaseHelper.saveLocation(pointOfInterest).doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        postEventSafely(context, new BusEvent.PlaceAdded());
                    }
                });
            }
        });
    }

    public Observable<PointOfInterest> deleteLocation(PointOfInterest pointOfInterest) {
        return mDatabaseHelper.deleteLocation(pointOfInterest);
    }

    public Observable<AutocompletePlace> getAutocompleteResults(final GoogleApiClient mGoogleApiClient, final String query, final LatLngBounds bounds) {
        return Observable.create(new Observable.OnSubscribe<AutocompletePlace>() {
            @Override
            public void call(Subscriber<? super AutocompletePlace> subscriber) {

                PendingResult<AutocompletePredictionBuffer> results =
                        Places.GeoDataApi.getAutocompletePredictions(mGoogleApiClient, query,
                                bounds, null);

                AutocompletePredictionBuffer autocompletePredictions = results
                        .await(60, TimeUnit.SECONDS);

                final Status status = autocompletePredictions.getStatus();
                if (!status.isSuccess()) {
                    autocompletePredictions.release();
                    subscriber.onError(null);
                } else {
                    for (AutocompletePrediction autocompletePrediction : autocompletePredictions) {
                        subscriber.onNext(
                                new AutocompletePlace(
                                        autocompletePrediction.getPlaceId(),
                                        autocompletePrediction.getDescription()
                                ));
                    }
                    autocompletePredictions.release();
                    subscriber.onCompleted();
                }
            }
        });
    }

    public Observable<Boolean> doesLocationExist(String id) {
        return mDatabaseHelper.getLocation(id).flatMap(new Func1<PointOfInterest, Observable<Boolean>>() {
            @Override
            public Observable<Boolean> call(PointOfInterest pointOfInterest) {
                return Observable.just(pointOfInterest != null);
            }
        });
    }

    public Observable<PointOfInterest> getPredictions(final GoogleApiClient mGoogleApiClient, final String query, final LatLngBounds bounds) {
        return getAutocompleteResults(mGoogleApiClient, query, bounds)
                .flatMap(new Func1<AutocompletePlace, Observable<PointOfInterest>>() {
                    @Override
                    public Observable<PointOfInterest> call(AutocompletePlace autocompletePlace) {
                        return getCompleteResult(mGoogleApiClient, autocompletePlace.placeId.toString());
                    }
                });
    }

    public Observable<PointOfInterest> getCompleteResult(final GoogleApiClient mGoogleApiClient, final String id) {
        return Observable.create(new Observable.OnSubscribe<PointOfInterest>() {
            @Override
            public void call(final Subscriber<? super PointOfInterest> subscriber) {
                final PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                        .getPlaceById(mGoogleApiClient, id);
                placeResult.setResultCallback(new ResultCallback<PlaceBuffer>() {
                    @Override
                    public void onResult(PlaceBuffer places) {
                        if (!places.getStatus().isSuccess()) {
                            places.release();
                            subscriber.onError(null);
                        } else {
                            subscriber.onNext(PointOfInterest.fromPlace(places.get(0)));
                            places.close();
                            subscriber.onCompleted();
                        }
                    }
                });
            }
        });
    }

    private void postEventSafely(final Context context, final Object event) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                PickrApplication.get(context).getComponent().eventBus().post(event);
            }
        });
    }

}
