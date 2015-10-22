package com.hitherejoe.pickr;


import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.hitherejoe.pickr.data.model.PointOfInterest;
import com.hitherejoe.pickr.injection.TestComponentRule;
import com.hitherejoe.pickr.ui.activity.MainActivity;
import com.hitherejoe.pickr.util.ClearDataRule;
import com.hitherejoe.pickr.util.MockModelsUtil;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;

import java.util.List;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

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

    @Rule
    public TestRule chain = RuleChain.outerRule(component).around(clearDataRule).around(main);

    //TODO: Write some more tests...

    @Test
    public void testLocationsShowInFeed() {
        List<PointOfInterest> mockPointOfInterests =
                MockModelsUtil.createListOfMockPointOfInterests(4);
        for (PointOfInterest mockPointOfInterest : mockPointOfInterests) {
            component.getDataManager().saveLocation(
                    InstrumentationRegistry.getTargetContext(), mockPointOfInterest).subscribe();
        }
        main.launchActivity(null);
        checkLocationsDisplayOnRecyclerView(mockPointOfInterests);
    }

    @Test
    public void testPressOnNameOpensDetailActivity() {
        PointOfInterest mockPointOfInterest = MockModelsUtil.createMockPointOfInterest();
        component.getDataManager().saveLocation(InstrumentationRegistry.getTargetContext(), mockPointOfInterest).subscribe();
        main.launchActivity(null);
        onView(withText(mockPointOfInterest.name))
                .perform(click());
        onView(withText(mockPointOfInterest.address))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testPressOnSearchOpensSearchActivity() {
        main.launchActivity(null);
        onView(withId(com.hitherejoe.pickr.R.id.action_open_search))
                .perform(click());
        onView(withId(com.hitherejoe.pickr.R.id.recycler_suggestions))
                .check(matches(isDisplayed()));
    }

    private void checkLocationsDisplayOnRecyclerView(List<PointOfInterest> charactersToCheck) {
        for (int i = 0; i < charactersToCheck.size(); i++) {
            onView(withId(R.id.recycler_places))
                    .perform(RecyclerViewActions.scrollToPosition(i));
            checkLocationDisplays(charactersToCheck.get(i));
        }
    }

    private void checkLocationDisplays(PointOfInterest pointOfInterest) {
        onView(withText(pointOfInterest.name))
                .check(matches(isDisplayed()));
    }


}