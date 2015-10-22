package com.hitherejoe.pickr.util;

import android.content.Context;

import com.hitherejoe.pickr.PickrApplication;
import com.hitherejoe.pickr.data.DataManager;
import com.hitherejoe.pickr.data.local.DatabaseHelper;
import com.hitherejoe.pickr.injection.component.DaggerDataManagerTestComponent;
import com.hitherejoe.pickr.injection.component.TestComponent;
import com.hitherejoe.pickr.injection.module.DataManagerTestModule;

import org.mockito.Mock;

/**
 * Extension of DataManager to be used on a testing environment.
 * It uses DataManagerTestComponent to inject dependencies that are different to the
 * normal runtime ones. e.g. mock objects etc.
 * It also exposes some helpers like the DatabaseHelper or the Retrofit service that are helpful
 * during testing.
 */
public class TestDataManager extends DataManager {

    public TestDataManager(Context context) {
        super(context);
    }

    @Override
    protected void injectDependencies(Context context) {
        TestComponent testComponent = (TestComponent)
                PickrApplication.get(context).getComponent();
        DaggerDataManagerTestComponent.builder()
                .testComponent(testComponent)
                .dataManagerTestModule(new DataManagerTestModule(context))
                .build()
                .inject(this);
    }

    public DatabaseHelper getDatabaseHelper() {
        return mDatabaseHelper;
    }

}