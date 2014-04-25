package org.hopto.mjancola.model;

import org.hopto.mjancola.utility.WorkoutDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GenericWorkoutTest
{
    private static final String MOCK_TYPE               = "MTN BIKING";
    private static final double MOCK_DISTANCE_THRESHOLD = 100L;
    private static final double MAX_SPEED               = 20;
    private static final String PROMOTED_ACTIVITY       = "ROAD_BIKING";
    private static final String START_REASON            = "TimeThreshold";
    private GenericWorkout testGenericWorkout;

    @Before
    public void setUp()
    {
        WorkoutType mockWorkoutType = mock( WorkoutType.class );
        when( mockWorkoutType.getName() ).thenReturn( MOCK_TYPE );
        when( mockWorkoutType.getDistanceThreshold() ).thenReturn( MOCK_DISTANCE_THRESHOLD );
        when( mockWorkoutType.getMaxSpeed()).thenReturn( MAX_SPEED);

        WorkoutType mockSecondWorkoutType = mock( WorkoutType.class );
        when (mockSecondWorkoutType.getName()).thenReturn(PROMOTED_ACTIVITY);

        when( mockWorkoutType.getNext() ).thenReturn(mockSecondWorkoutType);

        WorkoutDataSource mockDataSource = mock( WorkoutDataSource.class);


        testGenericWorkout = GenericWorkout.create(mockWorkoutType, null, START_REASON);
    }

    @After
    public void tearDown()
    {
        testGenericWorkout = null;
    }

    @Test
    public void testNotNull()
    {
        assertNotNull("new GenericWorkout should not be null", testGenericWorkout);
    }

    @Test
    public void testStop()
    {
        testGenericWorkout.stopPendingTime = System.currentTimeMillis() - ( 1 ); // TODO SpeedActivity.STOP_THRESHOLD_MS + 1);
        GenericMovement newActivity = testGenericWorkout.addSpeed(0);
        assertTrue("stopping for the stop threshold time should return new LearningMode", (newActivity instanceof LearningMode));
    }

    @Test
    public void testPromote()
    {
        GenericMovement newActivity = testGenericWorkout.addSpeed(MAX_SPEED + 1);
        assertTrue( "exceeding max speed should promote", newActivity.getName().equals( PROMOTED_ACTIVITY ) );
    }

}
