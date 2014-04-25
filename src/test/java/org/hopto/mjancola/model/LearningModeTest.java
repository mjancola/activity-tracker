package org.hopto.mjancola.model;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class LearningModeTest
{
    private LearningMode testLearningMode;
    private final long MIN_DURATION = 10L;
    private final long RUN_DURATION = 100L;

    @Before
    public void setUp()
    {
        testLearningMode = new LearningMode(null);
    }

    @After
    public void tearDown()
    {
        testLearningMode = null;
    }

    @Test
    public void testNotNull()
    {
        assertNotNull("LearningMode should not be null", testLearningMode);
    }

//    @Test
//    public void testAddSpeedDoesNotStartImmediately()
//    {
//        assertNull( "adding first run speed should not start a run", testLearningMode.addSpeed( Run.RUN_MIN ) );
//    }
//
//    @Test
//    public void testAddSpeed()
//    {
//        testLearningMode.addSpeed(Run.RUN_MIN);
//        assertTrue( " should still track speed", ( testLearningMode.getSpeed() == Run.RUN_MIN ) );
//    }
//    @Test
//    public void testGetType()
//    {
//        assertTrue("Learning mode should be correct type ", (testLearningMode.getType().equals( LearningMode.NAME )));
//    }

    @Test
    public void testGetInitialTimeMS()
    {
        assertTrue( "Initial Time should be less than one second", ( testLearningMode.getDurationMS() < MIN_DURATION ) );
    }

    @Test
    public void testGetTimeMS() throws InterruptedException
    {
        Thread.sleep(RUN_DURATION);
        assertTrue( "Time should be non-zero", ( testLearningMode.getDurationMS() >= RUN_DURATION ) );
    }

}
