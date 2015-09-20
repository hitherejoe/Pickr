package com.hitherejoe.pickr.data.local;

import android.content.Context;
import android.database.Cursor;

import com.hitherejoe.pickr.data.model.PointOfInterest;
import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import timber.log.Timber;

public class DatabaseHelper {

    private BriteDatabase mBriteDb;

    public DatabaseHelper(Context context) {
        mBriteDb = SqlBrite.create().wrapDatabaseHelper(new DbOpenHelper(context));
    }

    public BriteDatabase getBriteDb() {
        return mBriteDb;
    }

    public Observable<PointOfInterest> saveLocation(final PointOfInterest pointOfInterest) {
        return Observable.create(new Observable.OnSubscribe<PointOfInterest>() {
            @Override
            public void call(Subscriber<? super PointOfInterest> subscriber) {
                mBriteDb.insert(Db.PointOfInterestTable.TABLE_NAME, Db.PointOfInterestTable.toContentValues(pointOfInterest));
                subscriber.onNext(pointOfInterest);
                subscriber.onCompleted();
            }
        });
    }

    public Observable<PointOfInterest> deleteLocation(final PointOfInterest pointOfInterest) {
        return Observable.create(new Observable.OnSubscribe<PointOfInterest>() {
            @Override
            public void call(Subscriber<? super PointOfInterest> subscriber) {
                mBriteDb.delete(Db.PointOfInterestTable.TABLE_NAME, Db.PointOfInterestTable.COLUMN_ID + "=?", pointOfInterest.id);
                subscriber.onNext(pointOfInterest);
                subscriber.onCompleted();
            }
        });
    }

    public Observable<PointOfInterest> getLocation(final String id) {
        return mBriteDb.createQuery(Db.PointOfInterestTable.TABLE_NAME,
                "SELECT * FROM " + Db.PointOfInterestTable.TABLE_NAME + " WHERE " + Db.PointOfInterestTable.COLUMN_ID + "=?", id)
                .map(new Func1<SqlBrite.Query, PointOfInterest>() {
                    @Override
                    public PointOfInterest call(SqlBrite.Query query) {
                        PointOfInterest result = null;
                        Cursor cursor = query.run();
                        if (cursor.moveToFirst()) {
                            PointOfInterest pointOfInterest = Db.PointOfInterestTable.parseCursor(cursor);
                            if (pointOfInterest.id.equals(id)) result = pointOfInterest;
                        }
                        cursor.close();
                        return result;
                    }
                });
    }

    public Observable<List<PointOfInterest>> getLocations() {
        return mBriteDb.createQuery(Db.PointOfInterestTable.TABLE_NAME,
                "SELECT * FROM " + Db.PointOfInterestTable.TABLE_NAME)
                .map(new Func1<SqlBrite.Query, List<PointOfInterest>>() {
                    @Override
                    public List<PointOfInterest> call(SqlBrite.Query query) {
                        Cursor cursor = query.run();
                        List<PointOfInterest> result = new ArrayList<>();
                        while (cursor.moveToNext()) {
                            PointOfInterest l = Db.PointOfInterestTable.parseCursor(cursor);
                            if(l.name.equals("Warsaw")) Timber.e("LOCATION: " + l.id);
                            result.add(Db.PointOfInterestTable.parseCursor(cursor));
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
