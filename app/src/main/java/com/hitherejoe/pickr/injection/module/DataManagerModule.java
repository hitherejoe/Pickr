package com.hitherejoe.pickr.injection.module;

import android.content.Context;

import com.hitherejoe.pickr.data.local.DatabaseHelper;
import com.hitherejoe.pickr.data.local.PreferencesHelper;
import com.hitherejoe.pickr.data.remote.AndroidBoilerplateService;
import com.hitherejoe.pickr.data.remote.RetrofitHelper;
import com.hitherejoe.pickr.injection.scope.PerDataManager;

import dagger.Module;
import dagger.Provides;
import rx.Scheduler;
import rx.schedulers.Schedulers;

/**
 * Provide dependencies to the DataManager, mainly Helper classes and Retrofit services.
 */
@Module
public class DataManagerModule {

    private final Context mContext;

    public DataManagerModule(Context context) {
        mContext = context;
    }

    @Provides
    @PerDataManager
    PreferencesHelper providePreferencesHelper() {
        return new PreferencesHelper(mContext);
    }

    @Provides
    @PerDataManager
    DatabaseHelper provideDatabaseHelper() {
        return new DatabaseHelper(mContext);
    }

    @Provides
    @PerDataManager
    AndroidBoilerplateService provideAndroidBoilerplateService() {
        return new RetrofitHelper().newAndroidBoilerplateService();
    }

    @Provides
    @PerDataManager
    Scheduler provideSubscribeScheduler() {
        return Schedulers.io();
    }
}