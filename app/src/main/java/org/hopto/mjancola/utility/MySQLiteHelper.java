package org.hopto.mjancola.utility;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper
{

    public static final String TABLE_WORKOUTS = "workouts";
    public static final String COLUMN_WORKOUT_ID = "_id";
    public static final String COLUMN_WORKOUT_TYPE = "type";
    public static final String COLUMN_WORKOUT_START_MS = "start_ms";
    public static final String COLUMN_WORKOUT_DURATION_MS = "duration_ms";
    public static final String COLUMN_WORKOUT_DISTANCE_M = "distance_m";
    public static final String COLUMN_WORKOUT_AVG_SPEED = "avg_speed";
    public static final String COLUMN_WORKOUT_START_REASON = "start_reason";
    public static final String COLUMN_WORKOUT_END_REASON = "end_reason";


    private static final String DATABASE_NAME = "workouts.db";
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
        + TABLE_WORKOUTS + "("
        + COLUMN_WORKOUT_ID + " integer primary key autoincrement, "
        + COLUMN_WORKOUT_TYPE + " text not null, "
        + COLUMN_WORKOUT_START_MS + " integer not null, "
        + COLUMN_WORKOUT_DURATION_MS + " integer not null, "
        + COLUMN_WORKOUT_DISTANCE_M + " integer not null, "
        + COLUMN_WORKOUT_AVG_SPEED + " integer not null, "
        + COLUMN_WORKOUT_START_REASON + " text, "
        + COLUMN_WORKOUT_END_REASON + " text);";

    public MySQLiteHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database)
    {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        Log.w( MySQLiteHelper.class.getName(),
               "Upgrading database from version " + oldVersion + " to "
               + newVersion + ", which will destroy all old data" );
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WORKOUTS);
        onCreate(db);
    }

}