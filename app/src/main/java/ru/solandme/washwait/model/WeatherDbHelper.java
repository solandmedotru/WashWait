package ru.solandme.washwait.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

import ru.solandme.washwait.model.WeatherContract.LocationEntry;
import ru.solandme.washwait.model.WeatherContract.WeatherEntry;
import ru.solandme.washwait.model.pojo.forecast.WeatherForecast;


public class WeatherDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "weather.db";
    private static final String TAG = WeatherDbHelper.class.getSimpleName();

    public WeatherDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_LOCATION_TABLE = "CREATE TABLE " + LocationEntry.TABLE_NAME + " (" +
                LocationEntry._ID + " INTEGER PRIMARY KEY, " +
                LocationEntry.COLUMN_CITY_NAME + " TEXT NOT NULL, " +
                LocationEntry.COLUMN_CITY_ID + " REAL NOT NULL, " +
                LocationEntry.COLUMN_COUNTRY + " TEXT NOT NULL, " +
                LocationEntry.COLUMN_COORD_LAT + " REAL NOT NULL, " +
                LocationEntry.COLUMN_COORD_LONG + " REAL NOT NULL " +
                " );";

        final String SQL_CREATE_WEATHER_TABLE = "CREATE TABLE " + WeatherEntry.TABLE_NAME + " (" +

                WeatherEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                WeatherEntry.COLUMN_LOC_KEY + " INTEGER NOT NULL, " +
                WeatherEntry.COLUMN_DATE + " INTEGER NOT NULL, " +
                WeatherEntry.COLUMN_SHORT_DESC + " TEXT NOT NULL, " +
                WeatherEntry.COLUMN_WEATHER_ID + " INTEGER NOT NULL, " +

                WeatherEntry.COLUMN_MIN_TEMP + " REAL NOT NULL, " +
                WeatherEntry.COLUMN_MAX_TEMP + " REAL NOT NULL, " +

                WeatherEntry.COLUMN_HUMIDITY + " REAL NOT NULL, " +
                WeatherEntry.COLUMN_PRESSURE + " REAL NOT NULL, " +
                WeatherEntry.COLUMN_WIND_SPEED + " REAL NOT NULL, " +
                WeatherEntry.COLUMN_DEGREES + " REAL NOT NULL, " +
                WeatherEntry.COLUMN_RAIN + " REAL NOT NULL, " +
                WeatherEntry.COLUMN_SNOW + " REAL NOT NULL, " +

                " FOREIGN KEY (" + WeatherEntry.COLUMN_LOC_KEY + ") REFERENCES " +
                LocationEntry.TABLE_NAME + " (" + LocationEntry._ID + ") ON DELETE CASCADE, " +

                " UNIQUE (" + WeatherEntry.COLUMN_DATE + ", " +
                WeatherEntry.COLUMN_LOC_KEY + ") ON CONFLICT REPLACE);";

        sqLiteDatabase.execSQL(SQL_CREATE_LOCATION_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_WEATHER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + LocationEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + WeatherEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public void onDowngrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        onUpgrade(sqLiteDatabase, oldVersion, newVersion);
    }

    public void clearCache() {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + LocationEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + WeatherEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public void saveWeather(WeatherForecast weather) {
        // Gets the data repository in write mode
        SQLiteDatabase db = getWritableDatabase();

        Cursor cursor = db.query(
                LocationEntry.TABLE_NAME,
                null,
                LocationEntry.COLUMN_CITY_ID + " = ?",
                new String[]{String.valueOf(weather.getCity().getId())},
                null,
                null,
                null);

        int delete;
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            int id = cursor.getInt(cursor.getColumnIndex(LocationEntry._ID));
            delete = db.delete(LocationEntry.TABLE_NAME,
                    LocationEntry.COLUMN_CITY_ID + " = ?",
                    new String[]{String.valueOf(weather.getCity().getId())});
            Log.e(TAG, "deleteLocations count: " + delete);

            delete = db.delete(WeatherEntry.TABLE_NAME,
                    WeatherEntry.COLUMN_LOC_KEY + " = ?",
                    new String[]{String.valueOf(id)});
            Log.e(TAG, "deleteWeather count: " + delete);
            cursor.close();
        }

        cursor = db.query(
                WeatherEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                WeatherEntry.COLUMN_DATE + " ASC");
        Log.e(TAG, "after_deleteWeather: " + cursor.getCount());

        ContentValues values = new ContentValues();
        if (null != weather.getCity().getCoord()) {
            values.put(LocationEntry.COLUMN_CITY_NAME, weather.getCity().getName());
            values.put(LocationEntry.COLUMN_CITY_ID, weather.getCity().getId());
            values.put(LocationEntry.COLUMN_COORD_LAT, weather.getCity().getCoord().getLat());
            values.put(LocationEntry.COLUMN_COORD_LONG, weather.getCity().getCoord().getLon());
            values.put(LocationEntry.COLUMN_COUNTRY, weather.getCity().getCountry());
        } else {
            values.put(LocationEntry.COLUMN_CITY_NAME, "");
            values.put(LocationEntry.COLUMN_CITY_ID, "");
            values.put(LocationEntry.COLUMN_COORD_LAT, "");
            values.put(LocationEntry.COLUMN_COORD_LONG, "");
            values.put(LocationEntry.COLUMN_COUNTRY, "");
        }
        long id = db.insert(LocationEntry.TABLE_NAME, null, values);

        Log.e(TAG, "saveWeather: " + id);
        values.clear();

        for (int i = 0; i < weather.getList().size(); i++) {
            values.put(WeatherEntry.COLUMN_LOC_KEY, id);
            values.put(WeatherEntry.COLUMN_DATE, weather.getList().get(i).getDt());
            values.put(WeatherEntry.COLUMN_WEATHER_ID, weather.getList().get(i).getWeather().get(0).getId());
            values.put(WeatherEntry.COLUMN_SHORT_DESC, weather.getList().get(i).getWeather().get(0).getDescription());
            values.put(WeatherEntry.COLUMN_MIN_TEMP, weather.getList().get(i).getTemp().getMin());
            values.put(WeatherEntry.COLUMN_MAX_TEMP, weather.getList().get(i).getTemp().getMax());
            values.put(WeatherEntry.COLUMN_HUMIDITY, weather.getList().get(i).getHumidity());
            values.put(WeatherEntry.COLUMN_PRESSURE, weather.getList().get(i).getPressure());
            values.put(WeatherEntry.COLUMN_WIND_SPEED, weather.getList().get(i).getSpeed());
            values.put(WeatherEntry.COLUMN_DEGREES, weather.getList().get(i).getDeg());
            values.put(WeatherEntry.COLUMN_RAIN, weather.getList().get(i).getRain());
            values.put(WeatherEntry.COLUMN_SNOW, weather.getList().get(i).getSnow());

            db.insert(WeatherEntry.TABLE_NAME, null, values);
        }
        values.clear();
        db.close();
    }

    public Cursor getLastWeather(int cityId) {

        SQLiteDatabase db = getReadableDatabase();

        SQLiteQueryBuilder sWeatherByLocationSettingQueryBuilder;
        sWeatherByLocationSettingQueryBuilder = new SQLiteQueryBuilder();

        sWeatherByLocationSettingQueryBuilder.setTables(
                WeatherContract.WeatherEntry.TABLE_NAME + " INNER JOIN " +
                        WeatherContract.LocationEntry.TABLE_NAME +
                        " ON " + WeatherContract.WeatherEntry.TABLE_NAME +
                        "." + WeatherContract.WeatherEntry.COLUMN_LOC_KEY +
                        " = " + WeatherContract.LocationEntry.TABLE_NAME +
                        "." + WeatherContract.LocationEntry._ID);

        final String sLocationSettingSelection =
                WeatherContract.LocationEntry.TABLE_NAME +
                        "." + LocationEntry.COLUMN_CITY_ID + " = ? ";

        String[] selectionArgs = new String[]{String.valueOf(cityId)};
        Cursor cursor = sWeatherByLocationSettingQueryBuilder.query(
                db,
                null,
                sLocationSettingSelection,
                selectionArgs,
                null,
                null,
                WeatherEntry.COLUMN_DATE + " ASC"
        );

        Log.e(TAG, "getLastWeather: " + cursor.getCount());

        db.close();

        return cursor;
    }
}
