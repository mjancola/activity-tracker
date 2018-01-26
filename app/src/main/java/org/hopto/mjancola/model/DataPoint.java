package org.hopto.mjancola.model;

public class DataPoint
{
    private double longitude;
    private double latitude;
    private long timeMS;

    public DataPoint(long timeMS, double lat, double lon)
    {
        this.longitude = lon;
        this.latitude = lat;
        this.timeMS = timeMS;
    }

    public double getLongitude()
    {
        return longitude;
    }

    public double getLatitude()
    {
        return latitude;
    }

    public long getTime()
    {
        return timeMS;
    }
}
