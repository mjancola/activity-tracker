package org.hopto.mjancola.utility;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class WorkoutDataSource
{

    // Database fields
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private String[] allColumns = { MySQLiteHelper.COLUMN_WORKOUT_ID,
                                    MySQLiteHelper.COLUMN_WORKOUT_TYPE,
                                    MySQLiteHelper.COLUMN_WORKOUT_START_MS,
                                    MySQLiteHelper.COLUMN_WORKOUT_DISTANCE_M,
                                    MySQLiteHelper.COLUMN_WORKOUT_DURATION_MS,
                                    MySQLiteHelper.COLUMN_WORKOUT_AVG_SPEED,
                                    MySQLiteHelper.COLUMN_WORKOUT_START_REASON,
                                    MySQLiteHelper.COLUMN_WORKOUT_END_REASON
    };

    public WorkoutDataSource( Context context )
    {
        dbHelper = new MySQLiteHelper( context );
    }

    public void open() throws SQLException
    {
        database = dbHelper.getWritableDatabase();
    }

    public void close()
    {
        dbHelper.close();
    }

    public WorkoutDAO createWorkout( String type, long start, long distance, long duration, long avgSpeed, String startReason, String endReason )
    {
        if (( type == null ))
        {
            return null;
        }
        else
        {
            ContentValues values = new ContentValues();
            values.put( MySQLiteHelper.COLUMN_WORKOUT_TYPE, type);
            values.put(MySQLiteHelper.COLUMN_WORKOUT_START_MS, start);
            values.put(MySQLiteHelper.COLUMN_WORKOUT_DISTANCE_M, distance);
            values.put(MySQLiteHelper.COLUMN_WORKOUT_DURATION_MS, duration);
            values.put( MySQLiteHelper.COLUMN_WORKOUT_AVG_SPEED, avgSpeed);
            values.put(MySQLiteHelper.COLUMN_WORKOUT_START_REASON, startReason);
            values.put(MySQLiteHelper.COLUMN_WORKOUT_END_REASON, endReason);

            long insertId = database.insert( MySQLiteHelper.TABLE_WORKOUTS, null,
                                             values );
            Cursor cursor = database.query( MySQLiteHelper.TABLE_WORKOUTS,
                                            allColumns, MySQLiteHelper.COLUMN_WORKOUT_ID + " = " + insertId, null,
                                            null, null, null );
            cursor.moveToFirst();
            WorkoutDAO workoutDAO = cursorToWorkoutDAO( cursor );
            cursor.close();
            return workoutDAO;
        }
    }

    public void deleteWorkout( WorkoutDAO workoutDAO )
    {
        long id = workoutDAO.getId();
        System.out.println( "Workout deleted with id: " + id );
        database.delete( MySQLiteHelper.TABLE_WORKOUTS, MySQLiteHelper.COLUMN_WORKOUT_ID
                                                     + " = " + id, null );
    }

    public List<WorkoutDAO> getAllWorkoutDAOs()
    {
        List<WorkoutDAO> AllWorkoutDAOs = new ArrayList<WorkoutDAO>();


        Cursor cursor = database.query( MySQLiteHelper.TABLE_WORKOUTS,
                                        allColumns, null, null, null, null, null );

        cursor.moveToLast();
        while ( !cursor.isBeforeFirst() )
        {
            WorkoutDAO workoutDAO = cursorToWorkoutDAO( cursor );
            AllWorkoutDAOs.add( workoutDAO );
            cursor.moveToPrevious();
        }
        // Make sure to close the cursor
        cursor.close();

        //AllWorkoutDAOs.re
        return AllWorkoutDAOs;
    }

    // TODO - hardcoded order
//          + COLUMN_WORKOUT_ID + " integer primary key autoincrement, "
//            + COLUMN_WORKOUT_TYPE + " text not null, "
//            + COLUMN_WORKOUT_START_MS + " integer not null, "
//            + COLUMN_WORKOUT_DURATION_MS + " integer not null, "
//            + COLUMN_WORKOUT_DISTANCE_M + " integer not null, "
//            + COLUMN_WORKOUT_AVG_SPEED + " integer not null, "
//            + COLUMN_WORKOUT_START_REASON + " text, "
//            + COLUMN_WORKOUT_END_REASON + " text);";
    private WorkoutDAO cursorToWorkoutDAO( Cursor cursor )
    {
        WorkoutDAO workoutDAO = new WorkoutDAO(cursor.getLong(0),
                                               cursor.getString(1),
                                               cursor.getLong(2),
                                               cursor.getLong(3),
                                               cursor.getLong(4),
                                               cursor.getLong(5),
                                               cursor.getString(6),
                                               cursor.getString(7));

        return workoutDAO;
    }

    public static WorkoutDataSource create( Context context )
    {
        WorkoutDataSource workoutDataSource = new WorkoutDataSource(context);
        workoutDataSource.open();
        return workoutDataSource;
    }
}