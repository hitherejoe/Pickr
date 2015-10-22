package com.hitherejoe.pickr.data.local;

import android.content.ContentValues;
import android.database.Cursor;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.gson.Gson;
import com.hitherejoe.pickr.data.model.PointOfInterest;
import com.hitherejoe.pickr.util.DataUtils;

public class Db {

    public Db() { }

    public static abstract class PointOfInterestTable {
        public static final String TABLE_NAME = "point_of_interests";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_ADDRESS = "address";
        public static final String COLUMN_LAT_LNG = "lat_lng";
        public static final String COLUMN_PHONE = "phone";
        public static final String COLUMN_LAT_LNG_BOUNDS = "lat_lng_bounds";

        public static final String CREATE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        COLUMN_ID + " TEXT PRIMARY KEY NOT NULL," +
                        COLUMN_NAME + " TEXT NOT NULL," +
                        COLUMN_ADDRESS + " TEXT NOT NULL," +
                        COLUMN_LAT_LNG + " TEXT NOT NULL," +
                        COLUMN_PHONE + " TEXT NOT NULL," +
                        COLUMN_LAT_LNG_BOUNDS + " TEXT NOT NULL" +
                        " ); ";

        public static ContentValues toContentValues(PointOfInterest pointOfInterest) {
            Gson gson = DataUtils.getGsonInstance();
            ContentValues values = new ContentValues();
            values.put(COLUMN_ID, pointOfInterest.id);
            values.put(COLUMN_NAME, pointOfInterest.name);
            values.put(COLUMN_ADDRESS, pointOfInterest.address);
            values.put(COLUMN_LAT_LNG, gson.toJson(pointOfInterest.latLng));
            values.put(COLUMN_PHONE, pointOfInterest.phoneNumber);
            values.put(COLUMN_LAT_LNG_BOUNDS, gson.toJson(pointOfInterest.latLngBounds));
            return values;
        }

        public static PointOfInterest parseCursor(Cursor cursor) {
            Gson gson = DataUtils.getGsonInstance();
            PointOfInterest pointOfInterest = new PointOfInterest();
            pointOfInterest.id = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID));
            pointOfInterest.name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME));
            pointOfInterest.address = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ADDRESS));
            pointOfInterest.latLng = gson.fromJson(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LAT_LNG)), LatLng.class);
            pointOfInterest.phoneNumber = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE));
            pointOfInterest.latLngBounds = gson.fromJson(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LAT_LNG_BOUNDS)), LatLngBounds.class);
            return pointOfInterest;
        }
    }
}
