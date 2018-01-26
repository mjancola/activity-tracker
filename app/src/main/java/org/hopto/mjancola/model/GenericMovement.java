package org.hopto.mjancola.model;

import android.location.Location;
import com.google.android.gms.location.DetectedActivity;

public interface GenericMovement
{
    public GenericMovement addLocation(Location location);
    public GenericMovement addDetectedActivity(DetectedActivity detectedActivity);
    public String getName();
    public double getSpeed();
    public double getDistance();
    public long getDurationMS();
}
