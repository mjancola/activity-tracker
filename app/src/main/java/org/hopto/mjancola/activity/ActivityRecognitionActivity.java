package org.hopto.mjancola.activity;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.tasks.Task;

import org.hopto.mjancola.R;
import org.hopto.mjancola.service.ActivityRecognitionService;

import java.util.Timer;
import java.util.TimerTask;

public class ActivityRecognitionActivity extends FragmentActivity
                                         implements GoogleApiClient.ConnectionCallbacks,
                                                    GoogleApiClient.OnConnectionFailedListener
{
    private GoogleApiClient mActivityRecognitionClient;
    private TextView                  activityBroadcastTextView;
    private Timer                     syncTimer;
    private String movementName = "";
    private int movementConfidence = 0;
    private int secondCount = 0;


    @Override
    public void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_recognition );
        activityBroadcastTextView = (TextView) findViewById( R.id.activity_broadcast_recognition_text );


        mActivityRecognitionClient = new GoogleApiClient.Builder(this)
                .addApi(ActivityRecognition.API)
                .addConnectionCallbacks(this) //this is refer to connectionCallbacks interface implementation.
                .addOnConnectionFailedListener(this) //this is refer to onConnectionFailedListener interface implementation.
                .build();

        mActivityRecognitionClient.connect();
    }

    @Override
    public void onStart()
    {
        super.onStart();
        startDisplayUpdateTask();
    }

    @Override
    public void onResume()
    {
      super.onResume();

      // Register mMessageReceiver to receive messages.
      LocalBroadcastManager.getInstance( this ).registerReceiver(mMessageReceiver,
          new IntentFilter(ActivityRecognitionService.ACTIVITY_DETECTED));
    }

    public static String fixedLengthStringLine(String string, int length)
    {
        return String.format("%1$"+(length-1)+ "s", string+"\n");
    }

    // handler for received Intents for ACTIVITY_DETECTED event
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive( Context context, Intent intent )
        {
            // Extract data included in the Intent
            DetectedActivity motion = (DetectedActivity) intent.getParcelableExtra( ActivityRecognitionService.MOTION_EVENT );
            String newMovementName = getMovementFromDetectedActivity( motion );
            movementConfidence = motion.getConfidence();

            // get the old text
            StringBuffer sb = new StringBuffer(activityBroadcastTextView.getText().toString());
            if ( movementName.equals( newMovementName ) )
            {
                // just update seconds and confidence
                String newText = ( secondCount + " : " + movementConfidence + " : " + movementName );
                sb.replace( 0, 15, fixedLengthStringLine( newText, 15 ) );
            }
            else
            {
                // start a new line
                secondCount = 0;
                movementName = newMovementName;
                String newText = ( secondCount + " : " + movementConfidence + " : " + movementName );
                sb.insert( 0, fixedLengthStringLine( newText, 15 ) );
            }

            // writeback to view
            activityBroadcastTextView.setText( sb.toString() );
        }
    };

    private String getMovementFromDetectedActivity( DetectedActivity motion )
    {
        switch ( motion.getType() )
        {
            case 0:
                return "Driving";
            case 1:
                return "Cycling";
            case 2:
                return "On Foot";
            case 3:
                return "Still";
            case 5:
                return "Tilting";
            default:  // same as 4
                return "unknown";
        }
    }

    @Override
    protected void onPause()
    {
        // Unregister since the activity is not visible
        LocalBroadcastManager.getInstance( this ).unregisterReceiver( mMessageReceiver );
        super.onPause();
    }

    private void startDisplayUpdateTask()
    {
        if ( syncTimer != null )
        {
            syncTimer.cancel();
        }
        syncTimer = new Timer();
        syncTimer.schedule( new TimerTask()
        {
            @Override
            public void run()
            {
                // Run on UI thread since it's affecting the UI
                ActivityRecognitionActivity.this.runOnUiThread( new Runnable()
                {
                    @Override
                    public void run()
                    {
                        secondCount++;
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
    }

    // Called when a connection to the ActivityRecognitionClient has been established.
    public void onConnected( Bundle connectionHint )
    {
        Intent intent = new Intent( this, ActivityRecognitionService.class );
        PendingIntent callbackIntent = PendingIntent.getService( this, 0, intent,
                                                                 PendingIntent.FLAG_UPDATE_CURRENT );

        ActivityRecognitionClient activityRecognitionClient = ActivityRecognition.getClient(this);
        Task task = activityRecognitionClient.requestActivityUpdates(1000L, callbackIntent);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }


    @Override
    public void onConnectionFailed( ConnectionResult connectionResult )
    {

    }
}
