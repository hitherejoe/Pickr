package com.hitherejoe.pickr;


import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.hitherejoe.pickr.injection.TestComponentRule;
import com.hitherejoe.pickr.ui.activity.MainActivity;
import com.hitherejoe.pickr.util.ClearDataRule;


import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityTest {

    @Rule
    public final ActivityTestRule<MainActivity> main =
            new ActivityTestRule<>(MainActivity.class, false, false);

    @Rule
    public final TestComponentRule component = new TestComponentRule();

    @Rule
    public final ClearDataRule clearDataRule = new ClearDataRule(component);

    @Before
    public void setUp() {
        clearDataRule.clearData();
    }

    @Test
    public void testLocationsShowAndAreScrollableInFeed() {
        //TODO: Write test
    }

    @Test
    public void testNoLocationsMessageIsShown() {
        //TODO: Write test
    }

    @Test
    public void testClickOnPlaceOpensDetailActivity() {
        //TODO: Write test
    }

    @Test
    public void testLongPressOnPlaceOpensDialog() {
        //TODO: Write test
    }

    @Test
    public void testClickOnSearchOpensSearchActivity() {
        //TODO: Write test
    }

    @Test
    public void testClickOnFABOpensPlacePickerActivity() {
        //TODO: Write test
    }

}