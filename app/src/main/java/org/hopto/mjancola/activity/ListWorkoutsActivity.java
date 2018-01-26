package org.hopto.mjancola.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;
import org.hopto.mjancola.R;
import org.hopto.mjancola.adapter.WorkoutDAOAdapter;
import org.hopto.mjancola.utility.WorkoutDAO;
import org.hopto.mjancola.utility.WorkoutDataSource;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ListWorkoutsActivity extends Activity
{

    private ListView       listView1;
    private WorkoutDataSource datasource;
    public static final long MILLIS_ONE_DAY = 1000L * 60L * 60L * 24L;
    public static SimpleDateFormat formatter;
    private       Spinner          WorkoutDAOFilterDropdown;
    private       long             filterDeltaMS;
    private       WorkoutDAOAdapter  adapter;
    private boolean debug = false;
    private static final long DEBUG_MODE = -1L;


    @Override
    public void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.workout_list );
        WorkoutDAOFilterDropdown = (Spinner) findViewById( R.id.workout_filter_spinner );
        WorkoutDAOFilterDropdown.setOnItemSelectedListener( new WorkoutDAOFilterChange() );
        WorkoutDAOFilterDropdown.setSelection( 0 );

        datasource = new WorkoutDataSource( this );
        datasource.open();

        listView1 = (ListView) findViewById( R.id.listView1 );
        formatter = new SimpleDateFormat( "dd MMM yyyy" );
    }

    private void updateData()
    {
        List<WorkoutDAO> allWorkoutDAOs = datasource.getAllWorkoutDAOs();
        List<WorkoutDAO> values = new ArrayList<WorkoutDAO>();
         // filter out WorkoutDAOs which are not DUE:
        int beforeCount = allWorkoutDAOs.size();
        for (WorkoutDAO w : allWorkoutDAOs)
        {
            if ( !filterWorkout( w ))
            {
                // due, add to working list
                values.add( w );
            }
        }
        int after = values.size();
        Log.d( "THISWEEK:", beforeCount + " before filter; " + after + " after" );


         WorkoutDAO[] overdue_WorkoutDAOs = values.toArray(new WorkoutDAO[values.size()]);

         adapter = new WorkoutDAOAdapter(this,
           R.layout.list_row, overdue_WorkoutDAOs);


         listView1.setAdapter(adapter);

         listView1.setOnItemClickListener( new AdapterView.OnItemClickListener()
         {
             public void onItemClick( AdapterView<?> parent, View view, int position, long id )
             {

                 Intent editWorkoutDAOIntent = EditWorkoutDAOActivity.createIntent( parent.getContext() );
                 editWorkoutDAOIntent.putExtra( WorkoutDAO.INTENT_ID, adapter.getItem( position ).getId() );

                 startActivity( editWorkoutDAOIntent );
             }
         } );

    }

    private boolean filterWorkout( WorkoutDAO w )
    {
        if (filterDeltaMS == DEBUG_MODE)
        {
            return false;
        }
        else if (w.getDuration() < (2 * (1000L))) // TODO FIX THISSpeedActivity.MIN_DURATION_MS)) // filter out short workouts
        {
            return true;
        }

        if  (( filterDeltaMS == 0 ) ||                                          // if we are not filtering
            ( w.getStart()  > (System.currentTimeMillis() - filterDeltaMS) ) )  // OR apply filter
        {
            return false;
        }
        else
        {
            return true;
        }
    }


    //      // Will be called via the onClick attribute
//      // of the buttons in main.xml
//    public void onClick(View view) {
//
//        switch (view.getId()) {
//        case R.id.add:
//            Intent addIntent = WorkoutDAOActivity.createIntent( this );
//            startActivity(addIntent);
//          break;
//        }
//    }

    @Override
     protected void onResume() {
         super.onResume();
         updateData();
     }

     @Override
     protected void onPause() {
         //datasource.close();
         super.onPause();
     }


    private class WorkoutDAOFilterChange implements AdapterView.OnItemSelectedListener
    {

        @Override public void onItemSelected( AdapterView<?> parent, View view, int pos, long l )
        {
            String filter = parent.getItemAtPosition( pos ).toString();
            String[] allFilters = getResources().getStringArray( R.array.workout_filters );

            if ( filter.equals( allFilters[0] ) ) // HACK - this should be programmatically linked: "This week"
            {
                filterDeltaMS = ( 7L * MILLIS_ONE_DAY );
            }
            else if ( filter.equals( allFilters[1] ) )
            {
                filterDeltaMS = ( 30L * MILLIS_ONE_DAY );
            }
            else if ( filter.equals( allFilters[2] ) ) // all
            {
                filterDeltaMS = 0L;
            }
            else // debug
            {
                filterDeltaMS = DEBUG_MODE;
            }
            adapter.notifyDataSetChanged();
            updateData();
        }

        @Override public void onNothingSelected( AdapterView<?> adapterView )
        {
        }
    }
}

