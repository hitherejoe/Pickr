package com.hitherejoe.pickr.injection;


import android.support.test.InstrumentationRegistry;

import com.hitherejoe.pickr.PickrApplication;
import com.hitherejoe.pickr.data.local.DatabaseHelper;
import com.hitherejoe.pickr.injection.component.DaggerTestComponent;
import com.hitherejoe.pickr.injection.component.TestComponent;
import com.hitherejoe.pickr.injection.module.ApplicationTestModule;
import com.hitherejoe.pickr.util.TestDataManager;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;


/**
 * Test rule that creates and sets a Dagger TestComponent into the application overriding the
 * existing application component.
 * Use this rule in your test case in order for the app to use mock dependencies.
 * It also exposes some of the dependencies so they can be easily accessed from the tests, e.g. to
 * stub mocks etc.
 */
public class TestComponentRule implements TestRule {

    private TestComponent mTestComponent;

    public TestDataManager getDataManager() {
        return (TestDataManager) mTestComponent.dataManager();
    }

    public DatabaseHelper getDatabaseHelper() {
        return getDataManager().getDatabaseHelper();
    }

    private void setupDaggerTestComponentInApplication() {
        PickrApplication application = PickrApplication
                .get(InstrumentationRegistry.getTargetContext());
        if (application.getComponent() instanceof TestComponent) {
            mTestComponent = (TestComponent) application.getComponent();
        } else {
            mTestComponent = DaggerTestComponent.builder()
                    .applicationTestModule(new ApplicationTestModule(application))
                    .build();
            application.setComponent(mTestComponent);
        }
    }

    @Override
    public Statement apply(final Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                try {
                    setupDaggerTestComponentInApplication();
                    base.evaluate();
                } finally {
                    mTestComponent = null;
                }
            }
        };
    }
}