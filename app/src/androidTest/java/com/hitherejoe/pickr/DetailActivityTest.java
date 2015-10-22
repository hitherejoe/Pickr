package com.hitherejoe.pickr;


import android.content.Context;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.hitherejoe.pickr.ui.activity.DetailActivity;
import com.hitherejoe.pickr.injection.TestComponentRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getTargetContext;

@RunWith(AndroidJUnit4.class)
public class DetailActivityTest {

    private Context mContext;

    @Rule
    public final ActivityTestRule<DetailActivity> main =
            new ActivityTestRule<>(DetailActivity.class, false, false);

    @Rule
    public final TestComponentRule component = new TestComponentRule();

    @Before
    public void initTargetContext() {
        mContext = getTargetContext();
    }

    @Test
    public void testPointOfInterestDetailsDisplayed() {
        //Todo: Write test
    }

}