package com.hitherejoe.pickr.data.local;

import android.content.ContentValues;
import android.database.Cursor;

import com.google.gson.Gson;
import com.hitherejoe.pickr.data.model.Location;
import com.hitherejoe.pickr.util.DataUtils;

public class Db {

    public Db() { }

    public static abstract class LocationTable {
        public static final String TABLE_NAME = "locations";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_NAME = "name";

        public static final String CREATE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        COLUMN_ID + " INTEGER PRIMARY KEY NOT NULL," +
                        COLUMN_NAME + " TEXT NOT NULL" +
                        " ); ";

        public static ContentValues toContentValues(Location location) {
            Gson gson = DataUtils.getGsonInstance();
            ContentValues values = new ContentValues();
            values.put(COLUMN_NAME, location.name);
            return values;
        }

        public static Location parseCursor(Cursor cursor) {
            Gson gson = DataUtils.getGsonInstance();
            Location location = new Location();
            location.name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME));
            return location;
        }
    }
}
