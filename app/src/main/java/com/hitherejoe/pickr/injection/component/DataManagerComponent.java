package com.hitherejoe.pickr.injection.component;

import com.hitherejoe.pickr.data.DataManager;
import com.hitherejoe.pickr.injection.module.DataManagerModule;
import com.hitherejoe.pickr.injection.scope.PerDataManager;

import dagger.Component;

@PerDataManager
@Component(dependencies = ApplicationComponent.class, modules = DataManagerModule.class)
public interface DataManagerComponent {

    void inject(DataManager dataManager);
}