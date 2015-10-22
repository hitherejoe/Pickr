package com.hitherejoe.pickr.injection.component;

import com.hitherejoe.pickr.injection.module.DataManagerTestModule;
import com.hitherejoe.pickr.injection.scope.PerDataManager;

import dagger.Component;

@PerDataManager
@Component(dependencies = TestComponent.class, modules = DataManagerTestModule.class)
public interface DataManagerTestComponent extends DataManagerComponent {
}
