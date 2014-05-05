package org.hopto.mjancola.model;

import android.content.Context;
import android.location.Location;
import android.util.Log;
import com.google.android.gms.location.DetectedActivity;
import org.hopto.mjancola.utility.Converter;
import org.hopto.mjancola.utility.SettingsHelper;

public class LearningMode implements GenericMovement
{
    private static final String TAG = "****" + LearningMode.class.getSimpleName();
    public static final String IDLE = "IDLE";
    // TEST CODE
//        private static double testSpeed = (-2.5* .44704);

    public static final String FOOT_NAME = "On Foot";
    public static final String TILT      = "Tilting";
    public static final String UNKNOWN   = "unknown";

    public static final double MPH_TO_METERS_PER_SEC   = .44703888;
    public static final String    TIME_REASON             = "TimeThreshold";
    public static       String WALK_NAME               = "Walking";
    private static      double WALK_MAX                = 4.7 * MPH_TO_METERS_PER_SEC;
    private static      double WALK_MIN                = 1.0 * MPH_TO_METERS_PER_SEC;
    private static      long   WALK_TIME_THRESHOLD     = 20L * 1000L;
    private static      double WALK_DISTANCE_THRESHOLD = 10;
    //    private static double BIKE_AVG_MAX = 23.0 * MPH_TO_METERS_PER_SEC;
    //    private static double BIKE_MIN_DURATION = 20*1000*60; // 20 minutes

    public static  String RUN_NAME               = "Running";
    private static double RUN_MAX                = 10.5 * MPH_TO_METERS_PER_SEC;
    private static double RUN_MIN                = 4.8 * MPH_TO_METERS_PER_SEC;
    private static long   RUN_TIME_THRESHOLD     = 20L * 1000L;
    private static double RUN_DISTANCE_THRESHOLD = 10;
    //    private static double BIKE_AVG_MAX = 23.0 * MPH_TO_METERS_PER_SEC;
    //    private static double BIKE_MIN_DURATION = 20*1000*60; // 20 minutes

    public static  String BIKE_NAME               = "Cycling";
    private static double BIKE_MAX                = 47.0 * MPH_TO_METERS_PER_SEC;
    private static double BIKE_MIN                = 10.6 * MPH_TO_METERS_PER_SEC;
    private static long   BIKE_TIME_THRESHOLD     = 20L * 1000L;  // 2 minutes
    private static double BIKE_DISTANCE_THRESHOLD = 10;
    //    private static double BIKE_AVG_MAX = 23.0 * MPH_TO_METERS_PER_SEC;
    //    private static double BIKE_MIN_DURATION = 20*1000*60; // 20 minutes

    public static  String CAR_NAME               = "Driving";
    private static double CAR_MAX                = 150.0 * MPH_TO_METERS_PER_SEC;
    private static double CAR_MIN                = 47.1 * MPH_TO_METERS_PER_SEC;
    private static long   CAR_TIME_THRESHOLD     = 20L * 1000L;
    private static double CAR_DISTANCE_THRESHOLD = 10;

    long NO = 0L; // not pending
    public static WorkoutType driving       = new WorkoutType( CAR_NAME, CAR_MAX, CAR_MIN, CAR_TIME_THRESHOLD, CAR_DISTANCE_THRESHOLD, null );
    public static WorkoutType biking        = new WorkoutType( BIKE_NAME, BIKE_MAX, BIKE_MIN, BIKE_TIME_THRESHOLD, BIKE_DISTANCE_THRESHOLD, driving );
    public static WorkoutType running       = new WorkoutType( RUN_NAME, RUN_MAX, RUN_MIN, RUN_TIME_THRESHOLD, RUN_DISTANCE_THRESHOLD, biking );
    public static WorkoutType walking       = new WorkoutType( WALK_NAME, WALK_MAX, WALK_MIN, WALK_TIME_THRESHOLD, WALK_DISTANCE_THRESHOLD, running );
    private        long        startedTimeMS = 0L;
    private Context context;

    // this stuff is the same as Generic Workout
    private static final String NO_ACTIVITY                         = "NONE";
    private              String pendingActivity                     = NO_ACTIVITY;
    long differentActivityPendingTime = NO;  // time when different activity detected


    double currentSpeed = 0L;

    SettingsHelper settings;

    public LearningMode( Context context )
    {
        this.context = context;
        settings = SettingsHelper.getInstance(context);
        startedTimeMS = System.currentTimeMillis();
        // TEST CODE
//                testSpeed = testSpeed + (5*.44704);
        currentSpeed = 0L;
    }

    @Override
    public long getDurationMS()
    {
        return System.currentTimeMillis() - startedTimeMS;
    }

    @Override
    public GenericMovement addLocation( Location location )
    {
        // according to Google, all locations will have Lat/Long
        // so first add the speed to the array
        if (location.hasSpeed())
        {
            double speed = location.getSpeed();
// TEST CODE
//            double speed = testSpeed;
            GenericMovement newMovement = addSpeed(speed);
            if (newMovement != null)
            {
                return newMovement;
            }
        }
        return null;
    }

    GenericMovement addSpeed( double speed )
    {
        currentSpeed = speed;

        if (isStarted(walking, speed))
        {
            return GenericWorkout.create(walking, context, TIME_REASON);
        }
        if (isStarted(running, speed))
        {
            return GenericWorkout.create(running, context, TIME_REASON);
        }
        if (isStarted(biking, speed))
        {
            return GenericWorkout.create(biking, context, TIME_REASON);
        }
        if (isStarted(driving, speed))
        {
            return GenericWorkout.create(driving, context, TIME_REASON);
        }

        return null;

    }


    private boolean isStarted(WorkoutType workoutType, double speed)
    {
        if ((speed >= workoutType.getMinSpeed()) && (speed <= workoutType.getMaxSpeed()))
         {
             if (workoutType.getPending() == NO)
             {
                 workoutType.setPending(System.currentTimeMillis());
             }
             else
             {
                 long duration = System.currentTimeMillis() - workoutType.getPending();
                 if (duration > workoutType.getTimeThreshold())
                 {
                     return true;
                 }
             }
         }
         else
         {
             // speed out of range
             workoutType.setPending(NO);
         }
        return false;
    }

    @Override
    public GenericMovement addDetectedActivity( DetectedActivity motion )
    {
        // need threshold control here
        // TODO - consider merging learning Mode with other activites?

        String newActivity = Converter.getMovementFromDetectedActivity(motion, context);
        if (newActivity != null)
        {
            if ( !newActivity.equals( getName() ))
            {
                // if same activity (other than current), keep adding, otherwise reset
                if ( pendingActivity.equals( newActivity ))
                {
                    long changePending = System.currentTimeMillis() - differentActivityPendingTime;
                    if (changePending > settings.getActivityChangeThresholdMS() )
                    {
                        // detected something other than what we were doing with a high level of confidence
                        // CAR to WALK - save and start new
                        // WALK to CAR - generally, promote but could be save and start new

                        // if walking, allow promot to run, bike, drive
                        // else if run, allow promot to bike, drive
                        // else if bike, allow promote to drive

                        if (pendingActivity.equals( LearningMode.CAR_NAME ))
                        {
                            if ( (getName().equals( LearningMode.BIKE_NAME )) ||
                                 (getName().equals( LearningMode.RUN_NAME )) ||
                                 (getName().equals( LearningMode.WALK_NAME )) )
                            {
                                return GenericWorkout.create(LearningMode.driving, context, pendingActivity);
                            }
                        }
                        else if (pendingActivity.equals( LearningMode.BIKE_NAME))
                        {
                            if ( (getName().equals( LearningMode.RUN_NAME )) ||
                                 (getName().equals( LearningMode.WALK_NAME )) )
                            {
                                return GenericWorkout.create(LearningMode.biking, context, pendingActivity);
                            }

                        }
                        else if (pendingActivity.equals( LearningMode.RUN_NAME ))
                        {
                            if (getName().equals( LearningMode.WALK_NAME ))
                            {
                                return GenericWorkout.create(LearningMode.running, context, pendingActivity);
                            }
                        }
                        // else pending activity is some unknown
                        return null;
                    }
                }
                else
                {
                    // got some other activity
                    resetPendingActivity( newActivity );
                }
            }
            else  // detected current activity - reset
            {
                // reset change timer
                resetPendingActivity( NO_ACTIVITY );
                Log.e( TAG, getName() + ": detected: " + newActivity + " ignoring" );
            }
        }
        return null;

    }

    private void resetPendingActivity( String newActivity )
    {
        differentActivityPendingTime = System.currentTimeMillis();
        pendingActivity = newActivity;
    }

    @Override
    public String getName()
    {
        return IDLE;
    }

    @Override
    public double getSpeed()
    {
        return currentSpeed;
    }

    @Override
    public double getDistance()
    {
        return 0;
    }
}
