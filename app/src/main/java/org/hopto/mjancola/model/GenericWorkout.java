package org.hopto.mjancola.model;

import android.content.Context;
import android.location.Location;
import android.util.Log;
import com.google.android.gms.location.DetectedActivity;
import org.hopto.mjancola.utility.Converter;
import org.hopto.mjancola.utility.SettingsHelper;
import org.hopto.mjancola.utility.WorkoutDataSource;

public class GenericWorkout implements GenericMovement
{
    private static final long   MIN_DISTANCE_MILES_TIMES_ONEHUNDRED = 10L;  // tenth of a mile
    private final        String TAG                                 = "****" + GenericWorkout.class.getSimpleName();
    //public static  long   STOP_THRESHOLD_MS; //                   = 1000L * 60L * 3L;  // 3 minutes
    //public static  long   ACTIVITY_CHANGE_THRESHOLD_MS; //        = 1000L * 20L;  // 20 seconds
    private static final String NO_ACTIVITY                         = "NONE";
    private              String pendingActivity                     = NO_ACTIVITY;
    //private static double MIN_SPEED; //                           = 2.0 * LearningMode.MPH_TO_METERS_PER_SEC;
    //public static  double MIN_DURATION_MS; //                     = 1 * 60 * 1000; // 1 minutes
    private WorkoutType workoutType;
    long stopPendingTime              = NO;  // time when zero speed detected
    long differentActivityPendingTime = NO;  // time when different activity detected

    private String startReason;
    private long   exceedsSpeedPendingTime;
    private static final long NO = 0L;
    private WorkoutDataSource dataSource;

    double currentSpeed;
    private Workout workout;
    private Context context;
    private static SettingsHelper settings;

    // TEST CODE
//        private static double testSpeed = (3* .44704);
//        private double[] testLatitudes = {40.095253, 40.095270, 40.095270, 40.091867};
//        private double[] testLongitudes = {-75.211375, -75.211390, -75.211222, -75.224662};
//        private static final double START_LATITUDE    = 40.095253; // 40.095253, -75.211375
//    private static final double START_LONGITUDE   = -75.211375;
//    private static final double SMALL_CHANGE_LATITUDE = 40.095270; // 40.095270, -75.211390
//    private static final double SMALL_CHANGE_LONGITUDE =  -75.211390;
//    private static final double END_LATITUDE       = 40.095136; // 40.095136, -75.211222
//    private static final double END_LONGITUDE = -75.211222;
//    private static int counter = 0;
// END TEST CODE

    public static GenericWorkout createWithData( WorkoutType workoutType, Workout workout, String startReason, Context context )
    {
        settings = SettingsHelper.getInstance(context);
        GenericWorkout newWorkout = new GenericWorkout();
        newWorkout.workoutType = workoutType;
// TEST CODE
//        testSpeed = testSpeed + (5 * .44704);

        newWorkout.workout = workout;
        newWorkout.startReason = startReason;
        newWorkout.context = context;

        newWorkout.dataSource = WorkoutDataSource.create( context );
        newWorkout.currentSpeed = 0L;
        return newWorkout;
    }

    public static GenericWorkout create( WorkoutType workoutType, Context context, String startReason )
    {
        Workout newWorkout = new Workout(workoutType.getName(), workoutType.getDistanceThreshold());
        return createWithData( workoutType, newWorkout, startReason, context );
    }



    @Override
    public long getDurationMS()
    {
        return workout.getDurationMS();
    }

    @Override
    public GenericMovement addLocation( Location location )
    {
        // according to Google, all locations will have Lat/Long
        // so first add the speed to the array
        appendDataPoint(new DataPoint(location.getTime(), location.getLatitude(), location.getLongitude()));
// TEST CODE
//        counter++;
//        if (counter < testLatitudes.length)
//        {
//            appendDataPoint(new DataPoint(location.getTime(), testLatitudes[counter], testLongitudes[counter]));
//        }
// END TEST CODE
        double speed = 0;
        if (location.hasSpeed() == true)
        {
            speed = location.getSpeed();
        }
// TEST CODE
//          double speed = testSpeed;
        GenericMovement newMovement = addSpeed(speed);
        if (newMovement != null)
        {
            return newMovement;
        }

        return null;

    }


    private void appendDataPoint(DataPoint newData)
    {
        workout.addData(newData);
    }

    GenericMovement addSpeed( double speed )
    {
        currentSpeed = speed;

        if (currentSpeed <= settings.getMinSpeedMetersPerSecond())
        {
            // exceed no longer pending
            exceedsSpeedPendingTime = NO;

            if (stopPendingTime != NO)
            {
                long stopPending = System.currentTimeMillis() - stopPendingTime;
                if (stopPending > settings.getStopThresholdMS() )
                {
                    persistSelf(LearningMode.TIME_REASON);
                    return new LearningMode(context);
                }
            }
            else
            {
                // start the stop timer
                stopPendingTime = System.currentTimeMillis();
            }

        }
        else if (currentSpeed > workoutType.getMaxSpeed())
        {
            // stop no longer pending
            stopPendingTime = NO;

            if (exceedsSpeedPendingTime != NO)
            {
                long pendingTime = System.currentTimeMillis() - exceedsSpeedPendingTime;
//                if (pendingTime > workoutType.getTimeThreshold() )
                // must be at increased speed for at least 20 seconds to actually promote
                if (pendingTime > (20L*1000L) )
                {
                    // pass the data on to the next type
                    if (workoutType.getNext() != null)
                    {
                        return GenericWorkout.createWithData(workoutType.getNext(), workout, LearningMode.TIME_REASON, context);
                    }
                    else
                    {
                        // we are going faster than any known activity
                        // discard and start again
                        return new LearningMode(context);
                    }
                }
                // else noop
            }
            else
            {
                exceedsSpeedPendingTime = System.currentTimeMillis();
            }
        }
        else
        {
            // in range
            stopPendingTime = NO;
            exceedsSpeedPendingTime = NO;
            // TODO
            // add to average
        }

        return null;
    }

    private boolean speedInRange(double speed)
    {
        if ((speed >= workoutType.getMinSpeed()) && (speed <= workoutType.getMaxSpeed()))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    private void persistSelf(String endReason)
    {
        // TODO - might need another variant of this method which
        // does not subtract if we are persisting due to a different reason - i.e
        // different Motion Detected
        long actualDurationMS = getDurationMS();
        if (endReason.equals(LearningMode.TIME_REASON))  //
        {
            actualDurationMS = actualDurationMS - settings.getStopThresholdMS();
        }


        if ((actualDurationMS > settings.getMinDurationMS()) &&
            (Double.valueOf(Converter.formatDistance(Converter.formatDistanceTimeOneHundred( getDistance() ))) > MIN_DISTANCE_MILES_TIMES_ONEHUNDRED ))
        {
//            public WorkoutDAO createWorkout( String type, long start, long distance, long duration, long avgSpeed, String startReason, String endReason )
            if (dataSource.createWorkout( workoutType.getName(),
                                          workout.getStartTimeMillis(),
                                          Converter.formatDistanceTimeOneHundred(getDistance()),
                                          actualDurationMS,
                                          0L,
                                          startReason,
                                          endReason ) == null)
            {
                // failed, try to add an error log
                dataSource.createWorkout("FAILED", workout.getStartTimeMillis(), 0L, 0L, 0L, "", "");
            }


        }
        // else noop


    }

    @Override
    public GenericMovement addDetectedActivity( DetectedActivity motion )
    {
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

                        // if walking, allow promote to run, bike, drive
                        // else if run, allow promote to bike, drive
                        // else if bike, allow promote to drive

                        if (pendingActivity.equals( LearningMode.CAR_NAME ))
                        {
                            if ( (getName().equals( LearningMode.BIKE_NAME )) ||
                                 (getName().equals( LearningMode.RUN_NAME )) ||
                                 (getName().equals( LearningMode.WALK_NAME )) )
                            {
                                return GenericWorkout.createWithData(LearningMode.driving, workout, LearningMode.CAR_NAME, context);
                            }
                        }
                        else if (pendingActivity.equals( LearningMode.BIKE_NAME))
                        {
                            if ( (getName().equals( LearningMode.RUN_NAME )) ||
                                 (getName().equals( LearningMode.WALK_NAME )) )
                            {
                                return GenericWorkout.createWithData(LearningMode.biking, workout, LearningMode.BIKE_NAME, context);
                            }

                        }
                        else if (pendingActivity.equals( LearningMode.RUN_NAME ))
                        {
                            if (getName().equals( LearningMode.WALK_NAME ))
                            {
                                return GenericWorkout.createWithData(LearningMode.running, workout, LearningMode.WALK_NAME, context);
                            }
                        }
                        else if ( (pendingActivity.equals(LearningMode.IDLE)) ||
                                  (pendingActivity.equals( LearningMode.TILT)) ||
                                  (pendingActivity.equals(LearningMode.UNKNOWN)) )
                        {
                            // just ignore for now - Speed zeros will capture this
                            Log.e( TAG, getName() + ": detected: " + newActivity + " ignoring" );
                        }
                        else if ( (pendingActivity.equals( LearningMode.FOOT_NAME )) &&
                                  (( getName().equals( LearningMode.WALK_NAME )) || (getName().equals( LearningMode.RUN_NAME )) ) )
                        {
                            // ignore ON_FOOT detection if this is a walk or run
                            resetPendingActivity( newActivity );
                        }
                        else // else activity demotion, save and start new
                        {
                            persistSelf( newActivity );
                            return new LearningMode( context );
                        }
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

    public String getName()
    {
        return workoutType.getName();
    }

    @Override
    public double getSpeed()
    {
        return currentSpeed;
    }

    @Override
    public double getDistance()
    {
        return workout.getDistance();
    }
}
