package org.hopto.mjancola.utility;

public class WorkoutDAO
{
    private long id;
    private String type;
    private long   start;
    private long   distance;
    private long   duration;
    private long   avgSpeed;
    private String startReason;
    private String endReason;
    public static String INTENT_ID = "Intent_ID";

    public WorkoutDAO( Long id, String type, Long start, Long distance, Long duration, Long avgSpeed, String startReason, String endReason )
    {
        if (id == null) this.id = 99L;
        else this.id = id;

        if (type == null) type = "NONE";
        this.type = type;

        if (start == null) this.start = 0L;
        else this.start = start;

        if (distance == null) this.distance = 0L;
        else this.distance = distance;

        if (duration == null) this.duration = 0L;
        else this.duration = duration;

        if (avgSpeed == null) this.avgSpeed = 0L;
        else this.avgSpeed = avgSpeed;

        if (startReason == null) this.startReason = "NONE";
        else this.startReason = startReason;

        if (endReason == null) this.endReason = "NONE";
        else this.endReason = endReason;
    }

    public long getId()
    {
        return id;
    }

    public String getType()
    {
        return type;
    }

    public long getStart()
    {
        return start;
    }

    public long getDistance()
    {
        return distance;
    }

    public long getDuration()
    {
        return duration;
    }

    public long getAvgSpeed()
    {
        return avgSpeed;
    }

    public String getStartReason()
    {
        return startReason;
    }

    public String getEndReason()
    {
        return endReason;
    }


}
