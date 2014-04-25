package org.hopto.mjancola.service;


import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.location.DetectedActivity;
import org.hopto.mjancola.R;
import org.hopto.mjancola.activity.SpeedActivity;
import org.hopto.mjancola.model.GenericMovement;
import org.hopto.mjancola.model.LearningMode;
import org.hopto.mjancola.utility.Converter;

/**
 * Main worker class for application
 *  - tracks movement events broadcast by @MovementListenerService
 *  - exposes stats to any activity which attaches
 *  - writes to the DB
 */
public class MovementTrackerService extends Service implements GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener
{
    private static final String TAG = "****" + MovementTrackerService.class.getSimpleName();

    private final IBinder mBinder      = new MyLocalBinder();
    private       double  currentSpeed = 0;
    private       String  currentType  = "";
    private GenericMovement currentMovement;
    private long   duration = 0L;
    private double distance = 0L;

    private ActivityRecognitionClient mActivityRecognitionClient;


    public double getSpeed()
    {
        return currentSpeed;
    }

    public String getType()
    {
        return currentType;
    }

    public long getDuration()
    {
        return duration;
    }

    public double getDistance()
    {
        return distance;
    }

    @Override
    public int onStartCommand( Intent intent, int flags, int startId )
    {
        currentMovement = new LearningMode( getApplicationContext() );

        // Connect to the ActivityRecognitionService
        mActivityRecognitionClient = new ActivityRecognitionClient( this, this, this );
        mActivityRecognitionClient.connect();

        // Register mMessageReceiver to receive messages.
        LocalBroadcastManager.getInstance( this ).registerReceiver( mMessageReceiver,
                                                                    new IntentFilter( MovementListenerService.LOCATION_CHANGE_DETECTED ) );

        // Register mMessageReceiver to receive messages.
        LocalBroadcastManager.getInstance( this ).registerReceiver(mMessageReceiver,
                                                                     new IntentFilter(ActivityRecognitionService.ACTIVITY_DETECTED));

        setForegroundNotification( 0 );

        return ( START_NOT_STICKY );
    }

    // handler for received Intents for ACTIVITY_DETECTED event
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive( Context context, Intent intent )
        {
            // Extract data included in the Intent
            Bundle extras = intent.getExtras();

            // Extract data included in the Intent
            DetectedActivity motion = (DetectedActivity) intent.getParcelableExtra( ActivityRecognitionService.MOTION_EVENT );
            if ( motion != null )
            {
                GenericMovement newMovement = currentMovement.addDetectedActivity( motion );
                if ( newMovement != null )
                {
                    currentMovement = newMovement;
                }
            }

            Location loc = extras.getParcelable( MovementListenerService.LOCATION );
            if ( loc != null )
            {
                GenericMovement newMovement = currentMovement.addLocation( loc );
                if ( newMovement != null )
                {
                    currentMovement = newMovement;
                }
            }

            currentSpeed = currentMovement.getSpeed();
            currentType = currentMovement.getName();
            duration = currentMovement.getDurationMS();
            distance = currentMovement.getDistance();

//            if ( ( getType().equals( LearningMode.WALK_NAME ) ) ||
//                 ( getType().equals( LearningMode.RUN_NAME ) ) ||
//                 ( getType().equals( LearningMode.BIKE_NAME ) ) )
//            {
                setForegroundNotification( 0 );
//            }
//            else
//            {
//                // clear notification area
//                stopForeground( true );
//            }
        }
    };

    public IBinder onBind( Intent intent )
    {
        return mBinder;
    }

    @Override public void onConnected( Bundle bundle )
    {
        Intent intent = new Intent( this, ActivityRecognitionService.class );
        PendingIntent callbackIntent = PendingIntent.getService( this, 0, intent,
                                                                 PendingIntent.FLAG_UPDATE_CURRENT );
        mActivityRecognitionClient.requestActivityUpdates( 1000, callbackIntent );
    }

    @Override public void onDisconnected()
    {

    }

    @Override public void onConnectionFailed( ConnectionResult connectionResult )
    {

    }

    public class MyLocalBinder extends Binder
    {
        public MovementTrackerService getService()
        {
            return MovementTrackerService.this;
        }
    }


    @Override
    public void onDestroy()
    {
        LocalBroadcastManager.getInstance( this ).unregisterReceiver( mMessageReceiver );
        Log.e( TAG, " onDestroy called" );
    }


    protected void setForegroundNotification( int actionFlags )
    {


        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder( this );


        notificationBuilder.setContentTitle( "Activity Tracker" );

        notificationBuilder.setContentText( getType() + " : " + Converter.formatSpeed( getSpeed() ) );


        notificationBuilder.setSmallIcon( getTypeIcon( getType() ) );
        notificationBuilder.setLargeIcon( ( ( (BitmapDrawable) getResources().getDrawable( R.drawable.ic_launcher ) ).getBitmap() ) );

        notificationBuilder.setContentIntent( contentIntentForNotification() );

        Intent startServiceIntent = new Intent( this, ( (Object) this ).getClass() );

        Intent startStreamingIntent = (Intent) startServiceIntent.clone();
        PendingIntent startStreamingAction =
        PendingIntent.getService( this,
                                  0,
                                  startStreamingIntent,
                                  PendingIntent.FLAG_CANCEL_CURRENT );
        notificationBuilder.addAction( android.R.drawable.ic_media_play,
                                       "started",
                                       startStreamingAction );


        NotificationCompat.InboxStyle big = new NotificationCompat.InboxStyle(
        notificationBuilder );
        Notification notification = big.build();

        startForeground( 1244, notification );
    }

    private PendingIntent contentIntentForNotification()
    {
        Intent intent = new Intent(getApplicationContext(), SpeedActivity.class);
        return PendingIntent.getActivity(this, 0, intent, 0);
    }

    private int getTypeIcon( String type )
    {
        if ( type.equals( LearningMode.WALK_NAME ) )
        {
            return R.drawable.hike_white_notif;
        }
        else if ( type.equals( LearningMode.RUN_NAME ) )
        {
            return R.drawable.run_white_notif;
        }
        else if ( type.equals( LearningMode.BIKE_NAME ) )
        {
            return R.drawable.bike_white_notif;
        }
        else
        {
            return R.drawable.none_20;
        }

    }


    private void stop()
    {
        stopForeground( true );
    }
}