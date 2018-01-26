package org.hopto.mjancola.fragment;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import org.hopto.mjancola.R;
import org.hopto.mjancola.activity.EditWorkoutDAOActivity;
import org.hopto.mjancola.utility.Converter;
import org.hopto.mjancola.utility.WorkoutDAO;
import org.hopto.mjancola.utility.WorkoutDataSource;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class EditWorkoutDAOFragment extends Fragment
{
    private Button   updateButton;
    private Button   cancelButton;
    private Button   deleteButton;
    private Spinner workoutType;
    private EditText workoutDate;
    private EditText workoutMiles;
    private EditText workoutDuration;

    private long workoutAvg;
    private String workoutEndReason;
    private String workoutStartReason;

    private SimpleDateFormat formatter;
    private Calendar completedDate = Calendar.getInstance();

    private int  day;
    private int  month;
    private int  year;
    private Long workout_id;

    WorkoutDataSource dataSource;
    private WorkoutDAO oldWorkout;
    private String[] allTypes;


    @Override
    public View onCreateView( LayoutInflater inflater,
                              ViewGroup container,
                              Bundle savedInstanceState )
    {
        allTypes = getResources().getStringArray(R.array.workout_types);

        View editWorkout = inflater.inflate( R.layout.workout_dao,
                                             container,
                                             false );
        updateButton = (Button) editWorkout.findViewById( R.id.workout_update_button );
        cancelButton = (Button) editWorkout.findViewById( R.id.workout_cancel_button );
        deleteButton = (Button) editWorkout.findViewById( R.id.workout_delete_button );

        workoutType = (Spinner) editWorkout.findViewById( R.id.workout_type );
        workoutDate = (EditText) editWorkout.findViewById( R.id.workout_date );
        workoutMiles = (EditText) editWorkout.findViewById( R.id.workout_miles );
        workoutDuration = (EditText) editWorkout.findViewById( R.id.workout_duration );

        formatter = new SimpleDateFormat( "dd MMM yyyy" );

        return editWorkout;
    }

    @Override
    public void onActivityCreated( Bundle savedInstanceState )
    {
        super.onActivityCreated( savedInstanceState );
        updateButton.setOnClickListener( new updateClickListener() );
        cancelButton.setOnClickListener( new cancelClickListener() );
        deleteButton.setOnClickListener( new deleteClickListener() );
        workoutDate.setOnClickListener( new dateClickListener() );

        dataSource = (( EditWorkoutDAOActivity) getActivity()).getDatasource();
        Bundle extras = getActivity().getIntent().getExtras();

        if (extras == null)
        {
            // coding error
            Toast.makeText( getActivity(), "Error getting workout, try again later", 3000 ).show();

            getActivity().finish();
        }
        workout_id = extras.getLong( WorkoutDAO.INTENT_ID );
        if (workout_id != null)
        {
            // get the old workout
            List<WorkoutDAO> workouts = dataSource.getAllWorkoutDAOs();
            for (WorkoutDAO w : workouts)
            {
                if (w.getId() == workout_id)
                {
                    oldWorkout = w;
                    workoutType.setSelection( getIndexForType( w.getType() ) );
                    completedDate.setTimeInMillis( w.getStart() );
                    workoutDate.setText( formatter.format(completedDate.getTime()) );
                    workoutDuration.setText( Converter.formatDuration(w.getDuration()) );
                    workoutMiles.setText( Converter.formatDistance(w.getDistance()/100));
                    // non-editable fields
                    workoutAvg = w.getAvgSpeed();
                    workoutEndReason = w.getEndReason();
                    workoutStartReason = w.getStartReason();
                    break;
                }
            }
        }
    }

    private int getIndexForType( String type )
    {

        for (int i = 0; i < allTypes.length; i++)
        {
            if (allTypes[i].equals(type))
            {
                return i;
            }
        }
        return 0;
    }

    public void chooseDate()
    {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                                                        getActivity(), setDateCallback,
                                                        completedDate.get( Calendar.YEAR ),
                                                        completedDate.get( Calendar.MONTH ),
                                                        completedDate.get( Calendar.DAY_OF_MONTH ) );
        datePickerDialog.show();
    }

    DatePickerDialog.OnDateSetListener setDateCallback = new DatePickerDialog.OnDateSetListener()
    {
        public void onDateSet( DatePicker view, int year, int monthOfYear,
                               int dayOfMonth )
        {
            completedDate.set( Calendar.YEAR, year );
            completedDate.set( Calendar.MONTH, monthOfYear );
            completedDate.set( Calendar.DAY_OF_MONTH, dayOfMonth );
            workoutDate.setText( formatter.format( completedDate.getTime() ) );
        }
    };


    private class updateClickListener implements View.OnClickListener
    {
        @Override public void onClick( View v )
        {
            // convert back
            // TODO...

            if (dataSource.createWorkout( getStringFromWorkout(workoutType.getSelectedItemId()), getMillis( workoutDate.getText().toString() ), Long.valueOf(workoutMiles.getText().toString()), Long.valueOf(workoutDuration.getText().toString()), workoutAvg, workoutStartReason, workoutEndReason ) != null)
            {
                dataSource.deleteWorkout( oldWorkout );
                getActivity().finish();

            }
            else
            {
                // noop - user failed to fill in required data
                Toast.makeText( getActivity(), "Error, unable to update, try again later", 30 ).show();
            }


        }
    }

    private long getMillis( String workoutDate )
    {
        // TODO
        return System.currentTimeMillis();
    }

    private String getStringFromWorkout( long selectedItemId )
    {
        return allTypes[(int)selectedItemId];
    }


    private class cancelClickListener implements View.OnClickListener
    {
        @Override public void onClick( View v )
        {
            getActivity().finish();

        }
    }


    private class deleteClickListener implements View.OnClickListener
    {
        @Override public void onClick( View v )
        {
            dataSource.deleteWorkout( oldWorkout );
            getActivity().finish();

        }
    }



    private class dateClickListener implements View.OnClickListener
    {
        @Override public void onClick( View view )
        {
            chooseDate();
        }
    }




}
