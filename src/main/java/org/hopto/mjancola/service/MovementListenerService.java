package org.hopto.mjancola.service;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.*;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import org.hopto.mjancola.model.MyMovement;

import java.io.FileDescriptor;

public class MovementListenerService extends Service implements LocationListener,
                                                     ConnectionCallbacks,
                                                     OnConnectionFailedListener,
                                                     MyMovement
{
    private static final String TAG                      = "****" + MovementListenerService.class.getSimpleName();
    public static final  String LOCATION_CHANGE_DETECTED = "location-changed";
    public static final String LOCATION = "location";
    private LocationClient mLocationClient;

    public static final  String          SPEED_EVENT = "speed-change";
    private              boolean         isRecording = false;
    // These settings are the same as the settings for the map. They will in fact give you updates
    // at the maximal rates currently possible.
    private static final LocationRequest REQUEST     = LocationRequest.create()
    .setInterval( 5000 )         // 5 seconds
    .setFastestInterval( 16 )    // 16ms = 60fps
    .setPriority( LocationRequest.PRIORITY_HIGH_ACCURACY );

    private final IBinder mBinder = new MyLocalBinder();

    @Override
    public int onStartCommand( Intent intent, int flags, int startId )
    {
        //recordActivity();

        setUpLocationClientIfNeeded();
        mLocationClient.connect();
        return START_STICKY;
    }

    public IBinder onBind( Intent intent )
    {
        return mBinder;
    }


    public class MyLocalBinder extends Binder
    {
        public MovementListenerService getService()
        {
            return MovementListenerService.this;
        }
    }


    private void setUpLocationClientIfNeeded()
    {
        if ( mLocationClient == null )
        {
            mLocationClient = new LocationClient(
            getApplicationContext(),
            this,  // ConnectionCallbacks
            this ); // OnConnectionFailedListener
        }
    }


    @Override
    public void onDestroy()
    {
        super.onDestroy();
        Log.e( TAG, " onDestroy called" );
        forceStop();
    }

    @Override
    public void forceStop()
    {
        if (mLocationClient != null)
        {
            mLocationClient.disconnect();
        }
        stop();
    }

//    private void recordActivity()
//    {
//        if ( !isRecording )
//        {
//            Log.e( TAG, "started" );
//            isRecording = true;
//
//            CharSequence text = getText( R.string.speed_service_started );
//
//            setForegroundNotification( 0 );
//        }
//    }
//
//    protected void setForegroundNotification( int actionFlags )
//    {
//
//
//        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder( this );
//
//
//            notificationBuilder.setContentTitle( "SpeedTitle" );
//
//            notificationBuilder.setContentText( "SpeedText" );
//            notificationBuilder.setSmallIcon( R.drawable.ic_plusone_medium_off_client );
//            notificationBuilder.setLargeIcon( ( ( (BitmapDrawable) getResources().getDrawable(R.drawable.ic_plusone_medium_off_client ) ).getBitmap() ) );
//
//            //notificationBuilder.setContentIntent( contentIntentForNotification() );
//
//            Intent startServiceIntent = new Intent( this, ( (Object) this ).getClass() );
//
//                Intent startStreamingIntent = (Intent) startServiceIntent.clone();
//                PendingIntent startStreamingAction =
//                PendingIntent.getService( this,
//                                          0,
//                                          startStreamingIntent,
//                                          PendingIntent.FLAG_CANCEL_CURRENT );
//                notificationBuilder.addAction( android.R.drawable.ic_media_play,
//                                               "started",
//                                               startStreamingAction );
//
//
//            NotificationCompat.InboxStyle big = new NotificationCompat.InboxStyle(
//            notificationBuilder );
//            Notification notification = big.build();
//
//            startForeground( 1243, notification );
//        }


    private void stop()
    {
        if ( isRecording )
        {
            Log.w( "****MovementListenerService", "Got to stop()!" );
            isRecording = false;
            stopForeground( true );
        }
    }

    @Override public void onLocationChanged( Location location )
    {
        //if (location.hasSpeed())
        {
            // Broadcast speed
            // 1 meter per second = 2.23694 mph
            // float mph = location.getSpeed() * 2.23694f;
            broadcastSpeed( location );
        }
    }

    private void broadcastSpeed(Location location)
    {
        Intent intent = new Intent( MovementListenerService.LOCATION_CHANGE_DETECTED);
//        float mph = location.getSpeed() * 2.23694f;
//        intent.putExtra( MovementListenerService.SPEED_EVENT, mph );
        intent.putExtra( MovementListenerService.LOCATION, location );
        LocalBroadcastManager.getInstance( this ).sendBroadcast(intent);
    }


    /**
     * Callback called when connected to GCore. Implementation of {@link com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks}.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        mLocationClient.requestLocationUpdates( REQUEST, this);  // LocationListener
    }

    /**
     * Callback called when disconnected from GCore. Implementation of {@link com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks}.
     */
    @Override
    public void onDisconnected() {
        // Do nothing
    }

    /**
     * Implementation of {@link com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener}.
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Do nothing
    }

    // **************START MYMOVEMENT METHODS **********************************
    @Override public String getMovement()
    {
        return null;
    }

    @Override public String getInterfaceDescriptor() throws RemoteException
    {
        return null;
    }

    @Override public boolean pingBinder()
    {
        return false;
    }

    @Override public boolean isBinderAlive()
    {
        return false;
    }

    @Override public IInterface queryLocalInterface( String descriptor )
    {
        return null;
    }

    @Override public void dump( FileDescriptor fd, String[] args ) throws RemoteException
    {

    }

    @Override public void dumpAsync( FileDescriptor fd, String[] args ) throws RemoteException
    {

    }

    @Override public boolean transact( int code, Parcel data, Parcel reply, int flags ) throws RemoteException
    {
        return false;
    }

    @Override public void linkToDeath( DeathRecipient recipient, int flags ) throws RemoteException
    {

    }

    @Override public boolean unlinkToDeath( DeathRecipient recipient, int flags )
    {
        return false;
    }
    // **************START MYMOVEMENT METHODS **********************************

}