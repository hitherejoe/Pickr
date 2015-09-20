package com.hitherejoe.pickr;


import com.hitherejoe.pickr.data.DataManager;
import com.hitherejoe.pickr.data.local.DatabaseHelper;
import com.hitherejoe.pickr.data.model.PointOfInterest;
import com.hitherejoe.pickr.util.DefaultConfig;
import com.hitherejoe.pickr.util.MockModelsUtil;
import com.squareup.otto.Bus;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.List;

import rx.schedulers.Schedulers;

import static org.mockito.Mockito.mock;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = DefaultConfig.EMULATE_SDK, manifest = DefaultConfig.MANIFEST)
public class DataManagerTest {

    private DataManager mDataManager;
    private DatabaseHelper mDatabaseHelper;

    @Before
    public void setUp() {
        mDatabaseHelper = new DatabaseHelper(RuntimeEnvironment.application);
        mDatabaseHelper.clearTables().subscribe();
        mDataManager = new DataManager(mDatabaseHelper, mock(Bus.class), Schedulers.immediate());
    }

    @Test
    public void shouldSyncCharacters() throws Exception {
        int[] ids = new int[]{ 10034, 14050, 10435, 35093 };
        List<PointOfInterest> pointOfInterests = MockModelsUtil.createListOfMockCharacters(4);

    }

}
