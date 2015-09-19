package com.hitherejoe.pickr;

import android.app.Application;
import android.content.Context;

import com.hitherejoe.pickr.injection.component.ApplicationComponent;
import com.hitherejoe.pickr.injection.component.DaggerApplicationComponent;
import com.hitherejoe.pickr.injection.module.ApplicationModule;

import timber.log.Timber;

public class PickrApplication extends Application {

    ApplicationComponent mApplicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) Timber.plant(new Timber.DebugTree());

        mApplicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .build();
    }

    public static PickrApplication get(Context context) {
        return (PickrApplication) context.getApplicationContext();
    }

    public ApplicationComponent getComponent() {
        return mApplicationComponent;
    }

    // Needed to replace the component with a test specific one
    public void setComponent(ApplicationComponent applicationComponent) {
        mApplicationComponent = applicationComponent;
    }
}
