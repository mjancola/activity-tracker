package org.hopto.mjancola.model;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

public class DataPointTest
{
    private DataPoint testDataPoint;
    public double TEST_LONGITUDE = 145.3;
    public double TEST_LATITUDE  = 47.2;
    public double DOUBLE_MARGIN  = 0.001;
    public long TEST_TIME      = 12345678L;

    @Before
    public void setUp()
    {
        testDataPoint = new DataPoint( TEST_TIME, TEST_LATITUDE, TEST_LONGITUDE );
    }

    @After
    public void tearDown()
    {
        testDataPoint = null;
    }

    @Test
    public void testNotNull()
    {
        assertNotNull("new DataPoint should not be null", testDataPoint);
    }
    @Test
    public void testGetLatitude()
    {
        assertEquals( "Latitude not properly set", testDataPoint.getLatitude(), TEST_LATITUDE, DOUBLE_MARGIN );
    }

    @Test
    public void testGetLongitude()
    {
        assertEquals( "Longitude not properly set", testDataPoint.getLongitude(), TEST_LONGITUDE, DOUBLE_MARGIN );

    }
    @Test
    public void testGetTime()
    {
        assertEquals("datapoint time not properly set", testDataPoint.getTime(), TEST_TIME);
    }
}
