package com.hitherejoe.pickr.data.local;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.gson.Gson;
import com.hitherejoe.pickr.data.model.Location;
import com.hitherejoe.pickr.util.DataUtils;

import java.util.Locale;

public class Db {

    public Db() { }

    public static abstract class LocationTable {
        public static final String TABLE_NAME = "locations";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_ADDRESS = "address";
        public static final String COLUMN_LAT_LNG = "lat_lng";
        public static final String COLUMN_PHONE = "phone";
        public static final String COLUMN_PRICE = "price";
        public static final String COLUMN_RATING = "rating";
        public static final String COLUMN_LAT_LNG_BOUNDS = "lat_lng_bounds";
        public static final String COLUMN_WEBSITE = "website";
        public static final String COLUMN_LOCALE = "locale";

        public static final String CREATE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        COLUMN_ID + " INTEGER PRIMARY KEY NOT NULL," +
                        COLUMN_NAME + " TEXT NOT NULL," +
                        COLUMN_ADDRESS + " TEXT NOT NULL," +
                        COLUMN_LAT_LNG + " TEXT NOT NULL," +
                        COLUMN_PHONE + " TEXT NOT NULL," +
                        COLUMN_PRICE + " INTEGER NOT NULL," +
                        COLUMN_RATING + " REAL NOT NULL," +
                        COLUMN_LAT_LNG_BOUNDS + " TEXT NOT NULL," +
                        COLUMN_WEBSITE + " TEXT NOT NULL," +
                        COLUMN_LOCALE + " TEXT NOT NULL" +
                        " ); ";

        public static ContentValues toContentValues(Location location) {
            Gson gson = DataUtils.getGsonInstance();
            ContentValues values = new ContentValues();
            values.put(COLUMN_NAME, location.name);
            values.put(COLUMN_ADDRESS, location.address);
            values.put(COLUMN_LAT_LNG, gson.toJson(location.latLng));
            values.put(COLUMN_PHONE, location.phoneNumber);
            values.put(COLUMN_PRICE, location.priceLevel);
            values.put(COLUMN_RATING, location.rating);
            values.put(COLUMN_LAT_LNG_BOUNDS, gson.toJson(location.latLngBounds));
            values.put(COLUMN_WEBSITE, location.websiteUri.toString());
            values.put(COLUMN_LOCALE, gson.toJson(location.locale));
            return values;
        }

        public static Location parseCursor(Cursor cursor) {
            Gson gson = DataUtils.getGsonInstance();
            Location location = new Location();
            location.name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME));
            location.address = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ADDRESS));
            location.latLng = gson.fromJson(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LAT_LNG)), LatLng.class);
            location.phoneNumber = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE));
            location.priceLevel = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRICE));
            location.rating = cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_RATING));
            location.latLngBounds = gson.fromJson(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LAT_LNG_BOUNDS)), LatLngBounds.class);
            location.websiteUri = Uri.parse(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_WEBSITE)));
            location.locale = gson.fromJson(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LOCALE)), Locale.class);
            return location;
        }
    }
}
