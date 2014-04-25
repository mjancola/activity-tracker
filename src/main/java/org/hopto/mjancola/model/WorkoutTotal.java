package org.hopto.mjancola.model;

/**
 * Simple container class for a give workouts total
 * Should be maintained as new data is added.
 */
public class WorkoutTotal
{
    private long durationMS = 0L;
    private double distance = 0;

    public long getDurationMS()
    {
        return durationMS;
    }

    public void setDurationMS( long durationMS )
    {
        this.durationMS = durationMS;
    }

    public double getDistance()
    {
        return distance;
    }

    public void addDistance( double units )
    {
        this.distance = this.distance + units;
    }
}