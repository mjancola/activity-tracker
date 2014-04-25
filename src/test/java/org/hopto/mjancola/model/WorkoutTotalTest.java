package org.hopto.mjancola.model;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class WorkoutTotalTest
{
    private static final double TEST_DISTANCE    = 4.5;
    private static final double INITIAL_DISTANCE = 0;
    private static final long   INITIAL_DURATION = 0L;
    private static final long   TEST_DURATION    = 13445;
    private WorkoutTotal testWorkoutTotal;

    @Before
    public void setUp()
    {
        testWorkoutTotal = new WorkoutTotal();
    }

    @After
    public void tearDown()
    {
        testWorkoutTotal = null;
    }

    @Test
    public void testNotNull()
    {
        assertNotNull("new WorkoutTotal should not be null", testWorkoutTotal);
    }

    @Test
    public void testInitialDistance()
    {
        assertEquals( "Initial distance should be zero", INITIAL_DISTANCE, testWorkoutTotal.getDistance() );
    }

    @Test
    public void testInitialDuration()
    {
        assertEquals( "Initial duration should be zero", INITIAL_DURATION, testWorkoutTotal.getDurationMS() );
    }

    @Test
    public void testAddDistance()
    {
        testWorkoutTotal.addDistance(TEST_DISTANCE);
        assertEquals( "Add distance should add", TEST_DISTANCE, testWorkoutTotal.getDistance() );

    }

    @Test
    public void testSetDuration()
    {
        testWorkoutTotal.setDurationMS( TEST_DURATION );
        assertEquals( "set duration should set", TEST_DURATION, testWorkoutTotal.getDurationMS() );
    }
}
