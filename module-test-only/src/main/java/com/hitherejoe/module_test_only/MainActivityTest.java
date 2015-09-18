package com.hitherejoe.module_test_only;


import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.hitherejoe.pickr.data.model.Location;
import com.hitherejoe.pickr.ui.activity.MainActivity;
import com.hitherejoe.pickr.util.MockModelsUtil;
import com.hitherejoe.module_test_only.injection.TestComponentRule;
import com.hitherejoe.module_test_only.util.ClearDataRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.when;

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
    public void testCharactersShowAndAreScrollableInFeed() {
        int[] characterIds =
                InstrumentationRegistry.getTargetContext().getResources().getIntArray(com.hitherejoe.pickr.R.array.characters);
        List<Location> mockLocations = MockModelsUtil.createListOfMockCharacters(characterIds.length);
        stubMockCharacters(characterIds, mockLocations);
        main.launchActivity(null);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        checkCharactersDisplayOnRecyclerView(mockLocations);
    }

    @Test
    public void testCharactersNoDescriptionIsShown() {
        when(component.getMockWatchTowerService().getCharacter(anyInt()))
                .thenReturn(Observable.<Location>empty());
        int[] characterIds = new int[]{ 1 };
        Location mockLocation = MockModelsUtil.createMockCharacter();
        List<Location> mockLocations = new ArrayList<>();
        mockLocations.add(mockLocation);
        stubMockCharacters(characterIds, mockLocations);
        main.launchActivity(null);
        checkCharactersDisplayOnRecyclerView(mockLocations);
    }

    @Test
    public void testClickOnCardOpensCharacterActivity() {
        int[] characterIds =
                InstrumentationRegistry.getTargetContext().getResources().getIntArray(com.hitherejoe.pickr.R.array.characters);
        List<Location> mockLocations = MockModelsUtil.createListOfMockCharacters(characterIds.length);
        stubMockCharacters(characterIds, mockLocations);
        main.launchActivity(null);
        onView(withText(mockLocations.get(0).name))
                .perform(click());
        onView(withText(com.hitherejoe.pickr.R.string.text_lorem_ipsum))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testClickOnView() {
        when(component.getMockWatchTowerService().getCharacter(anyInt()))
                .thenReturn(Observable.<Location>empty());
        int[] characterIds = new int[]{ 1 };
        Location mockLocation = MockModelsUtil.createMockCharacter();
        List<Location> mockLocations = new ArrayList<>();
        mockLocations.add(mockLocation);
        stubMockCharacters(characterIds, mockLocations);
        main.launchActivity(null);
        onView(withText("View"))
                .perform(click());
        onView(withText(com.hitherejoe.pickr.R.string.text_lorem_ipsum))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testClickOnCollections() {
        when(component.getMockWatchTowerService().getCharacter(anyInt()))
                .thenReturn(Observable.<Location>empty());
        int[] characterIds = new int[]{ 1 };
        Location mockLocation = MockModelsUtil.createMockCharacter();
        List<Location> mockLocations = new ArrayList<>();
        mockLocations.add(mockLocation);
        stubMockCharacters(characterIds, mockLocations);
        main.launchActivity(null);
        onView(withText("Collections"))
                .perform(click());
        onView(withText("Films"))
                .check(matches(isDisplayed()));
    }

    private void checkCharactersDisplayOnRecyclerView(List<Location> charactersToCheck) {
        for (int i = 0; i < charactersToCheck.size(); i++) {
            onView(withId(com.hitherejoe.pickr.R.id.recycler_characters))
                    .perform(RecyclerViewActions.scrollToPosition(i));
            checkCharacterDisplays(charactersToCheck.get(i));
        }
    }

    private void checkCharacterDisplays(Location location) {
        onView(withText(location.name))
                .check(matches(isDisplayed()));
    }

    private void stubMockCharacters(int[] ids, List<Location> mockLocations) {
        for (int i = 0; i < mockLocations.size(); i++) {
            when(component.getMockWatchTowerService().getCharacter(ids[i]))
                    .thenReturn(Observable.just(mockLocations.get(i)));
        }
    }
}