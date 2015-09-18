package com.hitherejoe.pickr.injection.component;

import android.app.Application;

import com.hitherejoe.pickr.data.DataManager;
import com.hitherejoe.pickr.injection.module.ApplicationModule;
import com.hitherejoe.pickr.ui.activity.MainActivity;
import com.squareup.otto.Bus;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {

    void inject(MainActivity mainActivity);

    Application application();
    DataManager dataManager();
    Bus eventBus();
}