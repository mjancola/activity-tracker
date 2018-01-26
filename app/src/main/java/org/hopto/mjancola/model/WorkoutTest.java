package org.hopto.mjancola.model;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class WorkoutTest
{
    private static final String ACTIVITY_TYPE      = "Test Activity";
    private static final double DISTANCE_THRESHOLD = 12;
    private static final double ZERO               = 0;
    private static final long   SHORT_DURATION     = 500L;

    // added real data points, but these are really irrelevant because the Location API is stubbed by Robolectric!
    private static final double START_LATITUDE    = 40.095253; // 40.095253, -75.211375
    private static final double START_LONGITUDE   = -75.211375;
    private static final double SMALL_CHANGE_LATITUDE = 40.095270; // 40.095270, -75.211390
    private static final double SMALL_CHANGE_LONGITUDE =  -75.211390;
    private static final double END_LATITUDE       = 40.095136; // 40.095136, -75.211222
    private static final double END_LONGITUDE = -75.211222;
    private static final double DISTANCE      = 18; //meters according to //56feet

    private TestWorkout testWorkout;


    @Before
    public void setUp()
    {
        testWorkout = new TestWorkout( ACTIVITY_TYPE, DISTANCE_THRESHOLD );
    }

    @After
    public void tearDown()
    {
        testWorkout = null;
    }

    @Test
    public void testNotNull()
    {
        assertNotNull( "initial workout should not be null", testWorkout );
    }

    @Test
    public void testGetInitialDistance()
    {
        //
        assertEquals( "initial distance should be zero", testWorkout.getDistance(), ZERO );
    }

    @Test
    public void testGetType()
    {
        assertEquals("activity type should be set", ACTIVITY_TYPE, testWorkout.getWorkoutType() );
    }


    private void addDataPoint()
    {
        DataPoint mockDataPoint = mock(DataPoint.class);
        when(mockDataPoint.getLatitude()).thenReturn( START_LATITUDE );
        when(mockDataPoint.getLongitude()).thenReturn( START_LONGITUDE );

        testWorkout.addData( mockDataPoint );
    }
    @Test
    public void testAddDataPoint()
    {
        addDataPoint();
        assertEquals("latitude should be set", START_LATITUDE, testWorkout.getLastDataPoint().getLatitude());
        assertEquals("longitude should be set", START_LONGITUDE, testWorkout.getLastDataPoint().getLongitude());

    }

    @Test
    public void testGetDurationMS() throws InterruptedException
    {
        addDataPoint();
        Thread.sleep( 10L );
        long duration = testWorkout.getDurationMS();
        assertTrue( "duration should be non-zero and small", ((duration < SHORT_DURATION) && (duration != 0L )) );
    }

    @Test
    public void testGetDistanceAfterAdd()
    {
        addDataPoint();
        assertEquals( "distance with only one point should be zero", testWorkout.getDistance(), ZERO );
    }


    @Test
    public void testAddDataIgnoresSmallMovement()
    {
        addDataPoint();

        // now add a point not far from the first
        DataPoint mockDataPoint = mock(DataPoint.class);
        when(mockDataPoint.getLatitude()).thenReturn( SMALL_CHANGE_LATITUDE );
        when(mockDataPoint.getLongitude()).thenReturn( SMALL_CHANGE_LONGITUDE );

        testWorkout.addData( mockDataPoint );

        assertEquals("latitude should remain at start", START_LATITUDE, testWorkout.getLastDataPoint().getLatitude());
        assertEquals("longitude should remain at start", START_LONGITUDE, testWorkout.getLastDataPoint().getLongitude());

        //assertTrue(order.isFilled());
        //verify(mockWarehouse).remove("Talisker", 50);
    }

    @Test
    public void testAddDataAddsData()
    {
        addDataPoint();

        // now add a point FAR from the first
        DataPoint mockDataPoint = mock(DataPoint.class);
        when(mockDataPoint.getLatitude()).thenReturn( END_LATITUDE );
        when(mockDataPoint.getLongitude()).thenReturn( END_LONGITUDE );

        // fake the distance to this new dataPoint as more than the threshold
        testWorkout.distanceTo = (DISTANCE_THRESHOLD + 1);

        testWorkout.addData( mockDataPoint );

        assertEquals("new latitude should be set", END_LATITUDE, testWorkout.getLastDataPoint().getLatitude());
        assertEquals("new longitude should be set", END_LONGITUDE, testWorkout.getLastDataPoint().getLongitude());

        //assertTrue(order.isFilled());
        //verify(mockWarehouse).remove("Talisker", 50);
    }

    private class TestWorkout extends Workout
    {
        double distanceTo = 0L;

        public TestWorkout( String type, double distance_threshold )
        {
            super( type, distance_threshold );
        }

        @Override
        double getDistanceTo(DataPoint current, DataPoint last)
        {
            return distanceTo;
        }
    }
}

