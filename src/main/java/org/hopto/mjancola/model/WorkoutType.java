package org.hopto.mjancola.model;

public class WorkoutType
{
    private  String name         = null;
    private double maxSpeed;
    private double minSpeed;
    private long   timeThreshold;     // ignore out of speed ranges less than this duration
    private double   distanceThreshold; // ignore GPS changes less than this (meters)
    private long pending;
    private WorkoutType next;

   public WorkoutType(String name, double maxSpeed, double minSpeed, long timeThreshold, double distanceThreshold, WorkoutType next)
   {
       this.name = name;
       this.maxSpeed = maxSpeed;
       this.minSpeed = minSpeed;
       this.timeThreshold = timeThreshold;
       this.distanceThreshold = distanceThreshold;
       this.next = next;

   }
   public String getName()
   {
       return name;
   }

   public double getMaxSpeed()
   {
       return maxSpeed;
   }

   public double getMinSpeed()
   {
       return minSpeed;
   }

   public long getTimeThreshold()
   {
       return timeThreshold;
   }

   public double getDistanceThreshold()
   {
       return distanceThreshold;
   }

   public long getPending()
   {
       return pending;
   }

   public void setPending(long pending)
   {
       this.pending = pending;
   }

   public WorkoutType getNext()
   {
       return next;
   }
}
