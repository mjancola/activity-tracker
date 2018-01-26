package org.hopto.mjancola.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

public class ActivityRecognitionService extends IntentService
{
    public static final String ACTIVITY_DETECTED = "ARS-motion-detected";
    public static final String MOTION_EVENT      = "ARS-motion-type";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public ActivityRecognitionService( String name )
    {
        super( name );
    }

    public ActivityRecognitionService()
    {
        super( "ActivityRecognitionService" );
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        if ( ActivityRecognitionResult.hasResult( intent ))
        {
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
            // Put your application specific logic here (i.e. result.getMostProbableActivity())
            Log.e( "*****", "Got result: " + result.getMostProbableActivity().toString() );

            // broadcast the change
            broadCastMotion(result.getMostProbableActivity());
        }
    }

    private void broadCastMotion(DetectedActivity motion)
    {
        Intent intent = new Intent(ActivityRecognitionService.ACTIVITY_DETECTED);
        // add data (parcelable DetectedActivity)
        intent.putExtra(ActivityRecognitionService.MOTION_EVENT, motion);
        LocalBroadcastManager.getInstance( this ).sendBroadcast(intent);
    }

}
