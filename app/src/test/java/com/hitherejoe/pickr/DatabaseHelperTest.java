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

    private static final String POI_DUPLICATE_ID = "1234";
    private DatabaseHelper mDatabaseHelper;

    @Before
    public void setUp() {
        mDatabaseHelper = new DatabaseHelper(RuntimeEnvironment.application);
        mDatabaseHelper.clearTables().subscribe();
    }

    @Test
    public void shouldSaveLocation() throws Exception {
        PointOfInterest pointOfInterest = MockModelsUtil.createMockPointOfInterest();
        pointOfInterest.id = POI_DUPLICATE_ID;
        TestSubscriber<PointOfInterest> result = new TestSubscriber<>();
        mDatabaseHelper.saveLocation(pointOfInterest).subscribe(result);
        result.assertNoErrors();
        result.assertValue(pointOfInterest);

        Cursor cursor = mDatabaseHelper.getBriteDb()
                .query("SELECT * FROM " + Db.PointOfInterestTable.TABLE_NAME);
        assertEquals(1, cursor.getCount());
        cursor.moveToNext();
        assertEquals(pointOfInterest, Db.PointOfInterestTable.parseCursor(cursor));
    }

    @Test
    public void shouldNotSaveDuplicateLocation() throws Exception {
        PointOfInterest pointOfInterest = MockModelsUtil.createMockPointOfInterest();
        pointOfInterest.id = POI_DUPLICATE_ID;
        TestSubscriber<PointOfInterest> result = new TestSubscriber<>();
        mDatabaseHelper.saveLocation(pointOfInterest).subscribe(result);
        result.assertNoErrors();
        result.assertValue(pointOfInterest);

        Cursor cursor = mDatabaseHelper.getBriteDb()
                .query("SELECT * FROM " + Db.PointOfInterestTable.TABLE_NAME);
        assertEquals(1, cursor.getCount());
        cursor.moveToNext();
        assertEquals(pointOfInterest, Db.PointOfInterestTable.parseCursor(cursor));

        TestSubscriber<PointOfInterest> secondResult = new TestSubscriber<>();
        mDatabaseHelper.saveLocation(pointOfInterest).subscribe(secondResult);
        result.assertNoErrors();
        result.assertValue(pointOfInterest);

        Cursor duplicateCursor = mDatabaseHelper.getBriteDb()
                .query("SELECT * FROM " + Db.PointOfInterestTable.TABLE_NAME);
        assertEquals(1, duplicateCursor.getCount());
        duplicateCursor.moveToNext();
        assertEquals(pointOfInterest, Db.PointOfInterestTable.parseCursor(duplicateCursor));
    }

    @Test
    public void shouldGetLocation() throws Exception {
        PointOfInterest pointOfInterest = MockModelsUtil.createMockPointOfInterest();
        mDatabaseHelper.saveLocation(pointOfInterest).subscribe();

        TestSubscriber<PointOfInterest> result = new TestSubscriber<>();
        mDatabaseHelper.getLocation(pointOfInterest.id).subscribe(result);
        result.assertNoErrors();
        result.assertValue(pointOfInterest);

        Cursor cursor = mDatabaseHelper.getBriteDb()
                .query("SELECT * FROM " + Db.PointOfInterestTable.TABLE_NAME);
        assertEquals(1, cursor.getCount());
        cursor.moveToNext();
        assertEquals(pointOfInterest, Db.PointOfInterestTable.parseCursor(cursor));
    }

    @Test
    public void shouldGetAllLocations() throws Exception {
        List<PointOfInterest> pointsOfInterest = MockModelsUtil.createListOfMockPointOfInterests(5);
        for (PointOfInterest poi : pointsOfInterest) {
            mDatabaseHelper.saveLocation(poi).subscribe();
        }

        TestSubscriber<List<PointOfInterest>> result = new TestSubscriber<>();
        mDatabaseHelper.getLocations().subscribe(result);
        result.assertNoErrors();
        result.assertReceivedOnNext(Collections.singletonList(pointsOfInterest));
    }

    @Test
    public void shouldDeleteLocation() throws Exception {
        PointOfInterest pointOfInterest = MockModelsUtil.createMockPointOfInterest();
        TestSubscriber<PointOfInterest> result = new TestSubscriber<>();
        mDatabaseHelper.saveLocation(pointOfInterest).subscribe(result);
        result.assertNoErrors();
        result.assertValue(pointOfInterest);
        Cursor saveCursor = mDatabaseHelper.getBriteDb()
                .query("SELECT * FROM " + Db.PointOfInterestTable.TABLE_NAME);
        assertEquals(1, saveCursor.getCount());
        saveCursor.moveToNext();
        assertEquals(pointOfInterest, Db.PointOfInterestTable.parseCursor(saveCursor));

        mDatabaseHelper.deleteLocation(pointOfInterest).subscribe(result);
        result.assertNoErrors();
        Cursor deleteCursor = mDatabaseHelper.getBriteDb()
                .query("SELECT * FROM " + Db.PointOfInterestTable.TABLE_NAME);
        assertEquals(0, deleteCursor.getCount());
    }
}