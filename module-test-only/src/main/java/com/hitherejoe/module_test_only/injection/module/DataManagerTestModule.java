package com.hitherejoe.module_test_only.injection.module;

import android.content.Context;

import com.hitherejoe.pickr.data.local.DatabaseHelper;
import com.hitherejoe.pickr.injection.scope.PerDataManager;

import dagger.Module;
import dagger.Provides;
import rx.Scheduler;
import rx.schedulers.Schedulers;

/**
 * Provides dependencies for an app running on a testing environment
 * This allows injecting mocks if necessary
 */
@Module
public class DataManagerTestModule {

    private final Context mContext;

    public DataManagerTestModule(Context context) {
        mContext = context;
    }

    @Provides
    @PerDataManager
    DatabaseHelper provideDatabaseHelper() {
        return new DatabaseHelper(mContext);
    }

    @Provides
    @PerDataManager
    Scheduler provideSubscribeScheduler() {
        return Schedulers.immediate();
    }
}