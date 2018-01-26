package org.hopto.mjancola.model;

import android.location.Location;

import java.util.ArrayList;

public class Workout
{
    private static final int DISTANCE_OFFSET = 0;
    private static final int START_BEARING = 1;  // not used
    private static final int END_BEARING = 2;    // not used
    private WorkoutTotal         total;
    private String               workoutType;
    private long                 startTimeMillis;
    private ArrayList<DataPoint> dataPoints;
    private double               distance_threshold;

    public long getDurationMS()
    {
        return System.currentTimeMillis() - startTimeMillis;
    }

    public String getWorkoutType()
    {
        return workoutType;
    }


    public Workout( String type, double distance_threshold )
    {
        this.total = new WorkoutTotal();
        this.distance_threshold = distance_threshold;
        this.dataPoints = new ArrayList<DataPoint>();
        this.workoutType = type;
        this.startTimeMillis = System.currentTimeMillis();
    }
    public long getStartTimeMillis()
    {
        return startTimeMillis;
    }

    public double getDistance()
    {
        return total.getDistance();
    }

    public void addData(DataPoint current)
    {
        if (dataPoints.size() == 0)
        {
            dataPoints.add(current);
        }
        else
        {
            DataPoint last = getLastDataPoint();
            double incrementalDistance = getDistanceTo( current, last );

            if (incrementalDistance > distance_threshold)
            {
                total.addDistance( incrementalDistance );
                dataPoints.add(current);
            }
            // else ignore and wait until we've travelled further
        }
    }

    DataPoint getLastDataPoint()
    {
        DataPoint retVal = null;
        if (dataPoints.size() > 0)
        {
            retVal = dataPoints.get(dataPoints.size() -1);
        }
        return retVal;
    }
    /**
     * Helper method to determine how far it is from the last known location to this location
     * Only add to the workout if we've moved more than some threshold
     * @param current
     * @return
     */
    double getDistanceTo(DataPoint current, DataPoint last)
    {
        if (dataPoints.size() == 0)
        {
            return 0;
        }
        else
        {

            float[] results = new float[3];
            Location.distanceBetween(last.getLatitude(), last.getLongitude(), current.getLatitude(), current.getLongitude(), results);
            //
            return results[DISTANCE_OFFSET];
        }
    }

//
//    Location.distanceBetween(double startLatitude, double startLongitude, double endLatitude, double endLongitude, float[] results)
//
//    // If you have two Location objects:
//    float distance_meters = Location1.distanceTo(Location2)
//
//    // This is an approximation function, which does not take the
//    private double gps2m(float lat_a, float lng_a, float lat_b, float lng_b) {
//        float pk = (float) (180/3.14169);
//        float a1 = lat_a / pk;
//        float a2 = lng_a / pk;
//        float b1 = lat_b / pk;
//        float b2 = lng_b / pk;
//
//        float t1 = FloatMath.cos( a1 ) * FloatMath.cos(a2) * FloatMath.cos(b1) * FloatMath.cos(b2);
//        float t2 = FloatMath.cos(a1) * FloatMath.sin(a2) * FloatMath.cos(b1) * FloatMath.sin(b2);
//        float t3 = FloatMath.sin(a1) * FloatMath.sin(b1);
//        double tt = Math.acos(t1 + t2 + t3);
//
//        return 6366000*tt;



}
