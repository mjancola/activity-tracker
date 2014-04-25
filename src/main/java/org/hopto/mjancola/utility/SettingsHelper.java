package org.hopto.mjancola.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SettingsHelper
{
    private static final String NONE = "NONE";
    private long   stopThresholdMS;
    private long   activityChangeThresholdMS;
    private double minSpeed;
    private long minDurationMS;
    private int activityChangePercent;

    public long getStopThresholdMS()
    {
        return stopThresholdMS;
    }

    public long getActivityChangeThresholdMS()
    {
        return activityChangeThresholdMS;
    }

    public double getMinSpeedMetersPerSecond()
    {
        return minSpeed;
    }

    public long getMinDurationMS()
    {
        return minDurationMS;
    }

    public int getActivityChangePercent()
    {
        return activityChangePercent;
    }

    public static final String STOP_THRESHOLD_MS            = "prefStopThresholdMin";
    public static final String ACTIVITY_CHANGE_THRESHOLD_MS = "prefActivityChangeThresholdSec";
    public static final String MIN_SPEED_METERS_PER_SECOND  = "prefMinSpeedMetersPerSecond";
    public static final String MIN_DURATION_MS              = "prefMinDurationMin";
    public static final String ACTIVITY_CHANGE_PERCENT      = "prefActivityChangePercent";

    private static final String DEFAULT_STOP_THRESHOLD_MS            = "180000";  // 3 minutes
    private static final String DEFAULT_ACTIVITY_CHANGE_THRESHOLD_MS = "20000";  // 20 seconds
    private static final String DEFAULT_MIN_SPEED_METERS_PER_SECOND  = ".89407776";
    private static final String DEFAULT_MIN_DURATION_MS              = "60000"; // 1 minutes
    private static final String DEFAULT_ACTIVITY_CHANGE_PERCENT      = "90";

    private static SettingsHelper instance;
    private static Context        context;

    // hide constructor
    private SettingsHelper()
    {
    }

    public void update()
    {
        // get values from shared preferences
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences( context );

        String temp = getAndPersist( sharedPrefs, STOP_THRESHOLD_MS, DEFAULT_STOP_THRESHOLD_MS );
        stopThresholdMS = (Long.valueOf( temp ));  // 3 minutes

        temp = getAndPersist( sharedPrefs, ACTIVITY_CHANGE_THRESHOLD_MS, DEFAULT_ACTIVITY_CHANGE_THRESHOLD_MS );
        activityChangeThresholdMS = Long.valueOf(temp);

        temp = getAndPersist( sharedPrefs, MIN_SPEED_METERS_PER_SECOND, DEFAULT_MIN_SPEED_METERS_PER_SECOND );
        minSpeed = Double.valueOf(temp);

        temp = getAndPersist( sharedPrefs, MIN_DURATION_MS, DEFAULT_MIN_DURATION_MS );
        minDurationMS = (Long.valueOf(temp));

        temp = getAndPersist( sharedPrefs, ACTIVITY_CHANGE_PERCENT, DEFAULT_ACTIVITY_CHANGE_PERCENT );
        activityChangePercent = Integer.valueOf(temp);

    }

    private String getAndPersist( SharedPreferences sharedPrefs, String key, String def )
    {
        String temp = sharedPrefs.getString( key, NONE);
        if (temp.equals(NONE))
        {
            // use and persist our defaults
            temp = def;
            SharedPreferences.Editor editor = sharedPrefs.edit();
            editor.putString( key, temp );
            editor.commit();
        }
        return temp;
    }

    public static SettingsHelper getInstance(Context contextHandle)
    {
        if ( instance == null )
        {
            context = contextHandle;
            instance = new SettingsHelper();
        }
        instance.update();

        return instance;
    }
}
