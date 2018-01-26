package org.hopto.mjancola.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import org.hopto.mjancola.R;
import org.hopto.mjancola.utility.WorkoutDataSource;

public class EditWorkoutDAOActivity extends Activity
{

    private WorkoutDataSource datasource;


    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.workout_main );

        datasource = new WorkoutDataSource( this );
        datasource.open();
    }


    public static Intent createIntent( Context context )
    {
        return new Intent( context, EditWorkoutDAOActivity.class );
    }

    public WorkoutDataSource getDatasource()
    {
        return datasource;
    }

}
