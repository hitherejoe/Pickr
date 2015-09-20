package com.hitherejoe.module_test_only;


import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.hitherejoe.pickr.data.model.PointOfInterest;
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
        List<PointOfInterest> mockPointOfInterests = MockModelsUtil.createListOfMockCharacters(characterIds.length);
        stubMockCharacters(characterIds, mockPointOfInterests);
        main.launchActivity(null);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        checkCharactersDisplayOnRecyclerView(mockPointOfInterests);
    }

    @Test
    public void testCharactersNoDescriptionIsShown() {
        when(component.getMockWatchTowerService().getCharacter(anyInt()))
                .thenReturn(Observable.<PointOfInterest>empty());
        int[] characterIds = new int[]{ 1 };
        PointOfInterest mockPointOfInterest = MockModelsUtil.createMockCharacter();
        List<PointOfInterest> mockPointOfInterests = new ArrayList<>();
        mockPointOfInterests.add(mockPointOfInterest);
        stubMockCharacters(characterIds, mockPointOfInterests);
        main.launchActivity(null);
        checkCharactersDisplayOnRecyclerView(mockPointOfInterests);
    }

    @Test
    public void testClickOnCardOpensCharacterActivity() {
        int[] characterIds =
                InstrumentationRegistry.getTargetContext().getResources().getIntArray(com.hitherejoe.pickr.R.array.characters);
        List<PointOfInterest> mockPointOfInterests = MockModelsUtil.createListOfMockCharacters(characterIds.length);
        stubMockCharacters(characterIds, mockPointOfInterests);
        main.launchActivity(null);
        onView(withText(mockPointOfInterests.get(0).name))
                .perform(click());
        onView(withText(com.hitherejoe.pickr.R.string.text_lorem_ipsum))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testClickOnView() {
        when(component.getMockWatchTowerService().getCharacter(anyInt()))
                .thenReturn(Observable.<PointOfInterest>empty());
        int[] characterIds = new int[]{ 1 };
        PointOfInterest mockPointOfInterest = MockModelsUtil.createMockCharacter();
        List<PointOfInterest> mockPointOfInterests = new ArrayList<>();
        mockPointOfInterests.add(mockPointOfInterest);
        stubMockCharacters(characterIds, mockPointOfInterests);
        main.launchActivity(null);
        onView(withText("View"))
                .perform(click());
        onView(withText(com.hitherejoe.pickr.R.string.text_lorem_ipsum))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testClickOnCollections() {
        when(component.getMockWatchTowerService().getCharacter(anyInt()))
                .thenReturn(Observable.<PointOfInterest>empty());
        int[] characterIds = new int[]{ 1 };
        PointOfInterest mockPointOfInterest = MockModelsUtil.createMockCharacter();
        List<PointOfInterest> mockPointOfInterests = new ArrayList<>();
        mockPointOfInterests.add(mockPointOfInterest);
        stubMockCharacters(characterIds, mockPointOfInterests);
        main.launchActivity(null);
        onView(withText("Collections"))
                .perform(click());
        onView(withText("Films"))
                .check(matches(isDisplayed()));
    }

    private void checkCharactersDisplayOnRecyclerView(List<PointOfInterest> charactersToCheck) {
        for (int i = 0; i < charactersToCheck.size(); i++) {
            onView(withId(com.hitherejoe.pickr.R.id.recycler_characters))
                    .perform(RecyclerViewActions.scrollToPosition(i));
            checkCharacterDisplays(charactersToCheck.get(i));
        }
    }

    private void checkCharacterDisplays(PointOfInterest pointOfInterest) {
        onView(withText(pointOfInterest.name))
                .check(matches(isDisplayed()));
    }

    private void stubMockCharacters(int[] ids, List<PointOfInterest> mockPointOfInterests) {
        for (int i = 0; i < mockPointOfInterests.size(); i++) {
            when(component.getMockWatchTowerService().getCharacter(ids[i]))
                    .thenReturn(Observable.just(mockPointOfInterests.get(i)));
        }
    }
}