package com.hitherejoe.pickr.data.local;

import android.content.Context;
import android.database.Cursor;

import com.google.android.gms.location.places.Place;
import com.hitherejoe.pickr.data.model.Location;
import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

public class DatabaseHelper {

    private BriteDatabase mBriteDb;

    public DatabaseHelper(Context context) {
        mBriteDb = SqlBrite.create().wrapDatabaseHelper(new DbOpenHelper(context));
    }

    public BriteDatabase getBriteDb() {
        return mBriteDb;
    }

    public Observable<Location> saveLocation(final Location location) {
        return Observable.create(new Observable.OnSubscribe<Location>() {
            @Override
            public void call(Subscriber<? super Location> subscriber) {
                mBriteDb.insert(Db.LocationTable.TABLE_NAME, Db.LocationTable.toContentValues(location));
                subscriber.onNext(location);
                subscriber.onCompleted();
            }
        });
    }

    public Observable<Location> deleteLocation(final Location location) {
        return Observable.create(new Observable.OnSubscribe<Location>() {
            @Override
            public void call(Subscriber<? super Location> subscriber) {
                mBriteDb.delete(Db.LocationTable.TABLE_NAME, Db.LocationTable.COLUMN_ID + "=?", location.id);
                subscriber.onNext(location);
                subscriber.onCompleted();
            }
        });
    }

    public Observable<Location> getLocation(final String id) {
        return mBriteDb.createQuery(Db.LocationTable.TABLE_NAME,
                "SELECT * FROM " + Db.LocationTable.TABLE_NAME + " WHERE " + Db.LocationTable.COLUMN_ID + "=?", id)
                .map(new Func1<SqlBrite.Query, Location>() {
                    @Override
                    public Location call(SqlBrite.Query query) {
                        Location result = null;
                        Cursor cursor = query.run();
                        if (cursor.getCount() == 1 && cursor.moveToFirst()) {
                            Location location = Db.LocationTable.parseCursor(cursor);
                            if (location.id.equals(id)) result = location;
                        }
                        cursor.close();
                        return result;
                    }
                });
    }

    public Observable<List<Location>> getLocations() {
        return mBriteDb.createQuery(Db.LocationTable.TABLE_NAME,
                "SELECT * FROM " + Db.LocationTable.TABLE_NAME)
                .map(new Func1<SqlBrite.Query, List<Location>>() {
                    @Override
                    public List<Location> call(SqlBrite.Query query) {
                        Cursor cursor = query.run();
                        List<Location> result = new ArrayList<>();
                        while (cursor.moveToNext()) {
                            result.add(Db.LocationTable.parseCursor(cursor));
                        }
                        cursor.close();
                        return result;
                    }
                });
    }

    public Observable<Void> clearTables() {
        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
                mBriteDb.beginTransaction();
                try {
                    Cursor cursor = mBriteDb.query("SELECT name FROM sqlite_master WHERE type='table'");
                    while (cursor.moveToNext()) {
                        mBriteDb.delete(cursor.getString(cursor.getColumnIndex("name")), null);
                    }
                    cursor.close();
                    mBriteDb.setTransactionSuccessful();
                    subscriber.onCompleted();
                } finally {
                    mBriteDb.endTransaction();
                }
            }
        });
    }

}
