package org.hopto.mjancola.model;

import android.os.IBinder;

/**
 * Interface for binder returned from location listener
 */
public interface MyMovement extends IBinder
{
    public String getMovement();

    // force the service to stop - save battery
    public void forceStop();
}
