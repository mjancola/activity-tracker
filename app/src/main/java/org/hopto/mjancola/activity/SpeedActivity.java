package org.hopto.mjancola.activity;

import android.content.*;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import org.hopto.mjancola.R;
import org.hopto.mjancola.service.MovementListenerService;
import org.hopto.mjancola.service.MovementTrackerService;
import org.hopto.mjancola.utility.Converter;

import java.util.Timer;
import java.util.TimerTask;

public class SpeedActivity extends FragmentActivity implements SharedPreferences.OnSharedPreferenceChangeListener

{
    private static final String TAG             = "****" + SpeedActivity.class.getSimpleName();
    private static final String DURATION        = "duration";
    private static final String MILES           = "miles";
    private static final String MPH             = "mph";
    private static final String TYPE            = "type";
    private static final String BOUND_LISTENER  = "boundListener";
    private static final String BOUND_TRACKER   = "boundTracker";
    private static final int    RESULT_SETTINGS = -1;
    private TextView speedTextView;
    private TextView speedDuration;
    private TextView speedMiles;
    private TextView speedMPH;
    private TextView speedType; // "  ~ RUN ~  "
    private Timer    syncTimer;
    private String mode = "";
    private MovementListenerService movementListenerHandle;
    private MovementTrackerService  movementTrackerHandle;
    private ToggleButton            startStopButton;
    //private Button listButton;
    private boolean boundListener = false;
    private boolean boundTracker  = false;



    @Override
    public void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );

        // init preferences if this is the first time
        //PreferenceManager.setDefaultValues(this, R.xml.settings, false);

        // update with any user specifics
        updatePreferences( );

        setContentView( R.layout.speed_recognition );
        speedTextView = (TextView) findViewById( R.id.speed_text );
        speedDuration = (TextView) findViewById( R.id.speed_time );
        speedMiles = (TextView) findViewById( R.id.speed_miles );
        speedMPH = (TextView) findViewById( R.id.speed_mph );
        speedType = (TextView) findViewById( R.id.speed_type_text );
        startStopButton = (ToggleButton) findViewById( R.id.speed_recognition_start_stop_button );
        //listButton = (Button) findViewById( R.id.speed_recognition_list_button );

        // if we have data, populate
        if ( savedInstanceState != null)
        {
           speedDuration.setText( savedInstanceState.getString(DURATION));
           speedMiles.setText( savedInstanceState.getString(MILES));
           speedMPH.setText( savedInstanceState.getString(MPH));
           speedType.setText( savedInstanceState.getString(TYPE));
        }
        else
        {
            // only start services if we are not restoring
            startSpeedService();
            startMovementTrackerService();
        }


    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
    {
        // big stick - just update 'em all
        updatePreferences();
    }

    private void updatePreferences()
    {
        // tell SettingsHelper to refresh
    }


    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putString(DURATION, speedDuration.getText().toString());
        outState.putString(MILES, speedMiles.getText().toString());
        outState.putString(MPH, speedMPH.getText().toString());
        outState.putString(TYPE, speedType.getText().toString());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId())
        {
            case R.id.menu_legal:
                startActivity( new Intent( this, LegalInfoActivity.class ) );
                break;
            case R.id.menu_activity_recognition:
                startActivity( new Intent( this, ActivityRecognitionActivity.class ) );
                break;
            case R.id.menu_my_location:
                startActivity( new Intent( this, MyLocationDemoActivity.class ) );
                break;
            case R.id.menu_settings:
                Intent i = new Intent( this, UserSettingsActivity.class );
                startActivityForResult( i, RESULT_SETTINGS );
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
        case RESULT_SETTINGS:
            restartWithNewSettings();

            break;

        }

    }

    private void restartWithNewSettings()
    {
        // TODO
        SharedPreferences sharedPrefs = PreferenceManager
                  .getDefaultSharedPreferences( this );

          StringBuilder builder = new StringBuilder();

          builder.append(sharedPrefs.getString("prefUsername", "NULL"));

          builder.append(", "
                  + sharedPrefs.getBoolean("prefSendReport", false));

          builder.append(", "
                  + sharedPrefs.getString("prefSyncFrequency", "NULL"));

        Toast.makeText( this, builder.toString(), 3000 ).show();
        // TODO
        return;
    }


    private void startSpeedService()
    {
        // start or connect to a service which monitors speed (and broadcasts said events)
        Intent i = new Intent( this, MovementListenerService.class );

        startService( i );
    }

    private void startMovementTrackerService()
    {
        // start or connect to a service which listens to broadcasts and logs activity
        Intent i = new Intent( this, MovementTrackerService.class );

        startService( i );
    }

    @Override
    public void onStart()
    {
        super.onStart();


    }

    public void listPress(View view)
    {
        // jump to the listing activity
        Intent list = new Intent(this, ListWorkoutsActivity.class);
        startActivity(list);

    }

    public void startStopPress(View view) {
        // Is the toggle on?
        boolean on = ((ToggleButton) view).isChecked();

        if (on)
        {
            // restart the service
            startSpeedService();
            startDisplayUpdateTask();

        }
        else
        {
            // stop the service
            if ( movementListenerHandle != null)
            {
                movementListenerHandle.forceStop();
            }
            stopDisplayUpdateTask();
        }
    }


    private ServiceConnection myListener = new ServiceConnection()
    {

        public void onServiceConnected( ComponentName className,
                                        IBinder service )
        {
            MovementListenerService.MyLocalBinder binder = (MovementListenerService.MyLocalBinder) service;
            movementListenerHandle = binder.getService();
            startStopButton.setChecked( true );
            boundListener = true;
            Log.e( TAG, "myListener: CONNECTED" );

        }

        public void onServiceDisconnected( ComponentName arg0 )
        {
            startStopButton.setChecked( true );
            boundListener = false;
            Log.e( TAG, "myListener: DISCONNECTED" );

        }
    };

    private ServiceConnection myTracker = new ServiceConnection()
    {

        public void onServiceConnected( ComponentName className,
                                        IBinder service )
        {
            MovementTrackerService.MyLocalBinder binder = (MovementTrackerService.MyLocalBinder) service;
            movementTrackerHandle = binder.getService();
            boundTracker = true;
            Log.e( TAG, "myTracker: CONNECTED" );

        }

        public void onServiceDisconnected( ComponentName arg0 )
        {
            boundTracker = false;
            Log.e( TAG, "myTracker: DISCONNECTED" );

        }
    };

    @Override
    public void onResume()
    {
        super.onResume();

        // bind to the services
        Intent intent = new Intent(this, MovementListenerService.class);
        bindService(intent, myListener, Context.BIND_AUTO_CREATE);
        String logString;

        // bind to tracker service so we can get data
        intent = new Intent(this, MovementTrackerService.class);
        bindService(intent, myTracker, Context.BIND_AUTO_CREATE);


        startDisplayUpdateTask();

        // Register mMessageReceiver to receive messages.
        //LocalBroadcastManager.getInstance( this ).registerReceiver( mMessageReceiver,
        //                                                            new IntentFilter( MovementListenerService.LOCATION_CHANGE_DETECTED ) );
    }

//    public static String fixedLengthStringLine( String string, int length )
//    {
//        return String.format( "%1$" + ( length - 1 ) + "s", string + "\n" );
//    }



    //    private String getMovementFromSpeed( float metersPerSecond )
    //    {
    //        if ( metersPerSecond < 0.2 )
    //        {
    //            return "Still";
    //        }
    //        else if ( metersPerSecond < 2.0 )
    //        {
    //            return "walking";
    //        }
    //        else if ( metersPerSecond < 5.0 )
    //        {
    //            return "jogging";
    //        }
    //        else if ( metersPerSecond < 10.0 )
    //        {
    //            return "cycling";
    //        }
    //        else
    //        {
    //            return "unknown";
    //        }
    //    }

    @Override
    protected void onPause()
    {
        // Unregister since the activity is not visible
//        LocalBroadcastManager.getInstance( this ).unregisterReceiver( mMessageReceiver );
        super.onPause();
        if (boundTracker)
        {
            if (myTracker != null)
            {
                getBaseContext().unbindService( myTracker );
            }
        }
        if (boundListener)
        {
            if (myListener != null)
            {
                getBaseContext().unbindService( myListener );
            }
        }

        // terminate the timer

    }

    private void stopDisplayUpdateTask()
    {
        if ( syncTimer != null )
        {
            syncTimer.cancel();
        }

        speedTextView.setText("PAUSED");

    }

    private void startDisplayUpdateTask()
    {
        // stop any previous
        stopDisplayUpdateTask();

        syncTimer = new Timer();
        syncTimer.schedule( new TimerTask()
        {
            @Override
            public void run()
            {
                // Run on UI thread since it's affecting the UI
                SpeedActivity.this.runOnUiThread( new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if (movementTrackerHandle != null)
                        {
                            speedType.setText("  ~ " + movementTrackerHandle.getType() + " ~  ");
                            speedMPH.setText( Converter.formatSpeed( movementTrackerHandle.getSpeed() ) + " mph");
                            speedMiles.setText(Converter.formatDistance( movementTrackerHandle.getDistance() ) + " miles");
                            speedDuration.setText( Converter.formatDuration( movementTrackerHandle.getDuration() ) );
                            speedTextView.setText("WORKING");
                        }
                        else
                        {
                            speedTextView.setText("CONNECTING");
                        }
                        // update the seconds count
                        // activitySecondsTextView.setText( secondCount );
                    }
                } );
            }
        }, 0, 1000 ); // initial delay 0, interval 1sec
    }


    @Override
    public void onDestroy()
    {
        super.onDestroy();
        // TODO - kill the services too
    }
}
