package com.hitherejoe.pickr;

import android.database.Cursor;

import com.hitherejoe.pickr.data.local.DatabaseHelper;
import com.hitherejoe.pickr.data.local.Db;
import com.hitherejoe.pickr.data.model.PointOfInterest;
import com.hitherejoe.pickr.util.DefaultConfig;
import com.hitherejoe.pickr.util.MockModelsUtil;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.Collections;
import java.util.List;

import rx.observers.TestSubscriber;

import static junit.framework.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = DefaultConfig.EMULATE_SDK, manifest = DefaultConfig.MANIFEST)
public class DatabaseHelperTest {

    private DatabaseHelper mDatabaseHelper;

    @Before
    public void setUp() {
        mDatabaseHelper = new DatabaseHelper(RuntimeEnvironment.application);
        mDatabaseHelper.clearTables().subscribe();
    }

    @Test
    public void shouldSetCharacters() throws Exception {
        List<PointOfInterest> pointOfInterests = MockModelsUtil.createListOfMockCharacters(5);

        TestSubscriber<PointOfInterest> result = new TestSubscriber<>();
        mDatabaseHelper.setCharacters(pointOfInterests).subscribe(result);
        result.assertNoErrors();
        result.assertReceivedOnNext(pointOfInterests);

        Cursor cursor = mDatabaseHelper.getBriteDb()
                .query("SELECT * FROM " + Db.CharacterTable.TABLE_NAME);
        assertEquals(5, cursor.getCount());
        for (PointOfInterest pointOfInterest : pointOfInterests) {
            cursor.moveToNext();
            assertEquals(pointOfInterest, Db.CharacterTable.parseCursor(cursor));
        }
    }

    @Test
    public void shouldGetCharacters() throws Exception {
        List<PointOfInterest> pointOfInterests = MockModelsUtil.createListOfMockCharacters(5);

        mDatabaseHelper.setCharacters(pointOfInterests).subscribe();

        TestSubscriber<List<PointOfInterest>> result = new TestSubscriber<>();
        mDatabaseHelper.getCharacters().subscribe(result);
        result.assertNoErrors();
        result.assertReceivedOnNext(Collections.singletonList(pointOfInterests));
    }
}