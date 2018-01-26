package org.hopto.mjancola.utility;

import android.content.Context;
import com.google.android.gms.location.DetectedActivity;
import org.hopto.mjancola.model.LearningMode;

public class Converter
{
    private static final double METERS_TO_MILES             = 0.000621371;
    private static final double MPS_TO_MPH                  = 2.23694;

    public static String formatDuration( final long durationMS )
    {
        long totalSeconds = ( durationMS / 1000L );
        long seconds = totalSeconds % 60;

        long totalMinutes = totalSeconds / 60;
        long minutes = totalMinutes % 60;

        long totalHours = totalMinutes / 60;
        long hours = totalHours % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    public static long formatDistanceTimeOneHundred(final double distance)
    {
        return Math.round(distance * 100);
    }

    public static String formatDistance(final double distance)
    {
        // convert meters to miles
        return String.format("%2.2f", (distance * METERS_TO_MILES));
    }

    public static String formatSpeed(final double metersPerSecond)
    {
        // convert meters to miles
        return String.format("%2.2f", (metersPerSecond * MPS_TO_MPH));
    }

    public static String getMovementFromDetectedActivity( DetectedActivity motion, Context context )
    {
        if (motion.getConfidence() < SettingsHelper.getInstance( context ).getActivityChangePercent())
        {
            return null;
        }
        switch ( motion.getType() )
        {
            case 0:
                return LearningMode.CAR_NAME;
            case 1:
                return LearningMode.BIKE_NAME;
            case 2:
                return LearningMode.FOOT_NAME;
            case 3:
                return LearningMode.IDLE;
//          // IGNORING other cases (UNKNOWN, and TILT)
            default:
                return null;

        }
    }

}
