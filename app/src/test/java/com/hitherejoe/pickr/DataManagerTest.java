package com.hitherejoe.pickr;


import com.hitherejoe.pickr.data.DataManager;
import com.hitherejoe.pickr.data.local.DatabaseHelper;
import com.hitherejoe.pickr.data.model.PointOfInterest;
import com.hitherejoe.pickr.util.DefaultConfig;
import com.hitherejoe.pickr.util.MockModelsUtil;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import rx.Observable;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static uk.co.ribot.assertjrx.api.Assertions.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = DefaultConfig.EMULATE_SDK, manifest = DefaultConfig.MANIFEST)
public class DataManagerTest {

    private DataManager mDataManager;

    @Before
    public void setUp() {
        mDataManager = mock(DataManager.class);
    }

    @Test
    public void shouldSaveLocation() throws Exception {
        PointOfInterest pointOfInterest = MockModelsUtil.createMockPointOfInterest();
        when(mDataManager.saveLocation(RuntimeEnvironment.application, pointOfInterest)).thenReturn(Observable.just(pointOfInterest));
        assertThat(mDataManager.saveLocation(RuntimeEnvironment.application, pointOfInterest).toBlocking())
                .emitsSingleValue(pointOfInterest);
    }

    @Test
    public void shouldDeleteLocation() throws Exception {
        PointOfInterest pointOfInterest = MockModelsUtil.createMockPointOfInterest();
        when(mDataManager.deleteLocation(pointOfInterest)).thenReturn(Observable.just(pointOfInterest));
        assertThat(mDataManager.deleteLocation(pointOfInterest).toBlocking())
                .emitsSingleValue(pointOfInterest);
    }

}
