//package org.hopto.mjancola.model;
//
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Test;
//
//import static junit.framework.Assert.assertEquals;
//import static org.junit.Assert.*;
//
//
//public class WalkTest
//{
//    private Walk testWalk;
//    private Walk testStartedWalk;
//    private final long MIN_DURATION = 10L;
//    private final long RUN_DURATION = 100L;
//
//    @Before
//    public void setUp()
//    {
//        testWalk = new Walk();
//        testStartedWalk = Walk.create();
//    }
//
//    @After
//    public void tearDown()
//    {
//        testWalk = null;
//    }
//
//    @Test
//    public void testAddSpeed()
//    {
//        testWalk.start();
//        testWalk.addSpeed( Walk.WALK_MIN );
//        assertEquals( "speed should be set", testWalk.getSpeed(), Walk.WALK_MIN );
//    }
//
//    @Test
//    public void testIsStarted()
//    {
//        testWalk.isStarting( Walk.WALK_MIN );
//        assertEquals( "speed should be set", testWalk.getSpeed(), Walk.WALK_MIN );
//    }
//
//    @Test
//    public void testIsStartingDoesntStart()
//    {
//        assertFalse( "should not start immediately", testWalk.isStarting( Walk.WALK_MIN ) );
//    }
//
//    @Test
//    public void testIsStartedShouldStart()
//    {
//        testWalk.isStarting( Walk.WALK_MIN );
//        testWalk.pending = System.currentTimeMillis() - (Walk.WALK_THRESHOLD + 1);
//        assertTrue( "should start after threshold", testWalk.isStarting( Walk.WALK_MIN ) );
//    }
//
//    @Test
//    public void testShouldStop()
//    {
//        testWalk.start();
//        testWalk.addSpeed(Walk.WALK_MIN - .1);
//        testWalk.pending = System.currentTimeMillis() - (Walk.WALK_THRESHOLD + 1);
//        GenericMovement learning = testWalk.addSpeed( Walk.WALK_MIN - .1 );
//        assertFalse( "stopping should stop", testWalk.running );
//    }
//
//    @Test
//    public void testShouldReturnLearningMode()
//    {
//        testWalk.start();
//        testWalk.addSpeed(Walk.WALK_MIN - .1);
//        testWalk.pending = System.currentTimeMillis() - (Walk.WALK_THRESHOLD + 1);
//        GenericMovement learning = testWalk.addSpeed( Walk.WALK_MIN - .1 );
//        assertTrue( "stopping should return LearningMode", (learning instanceof LearningMode) );
//    }
//
//    @Test
//    public void testPendingCancel()
//    {
//        testWalk.start();
//        testWalk.addSpeed(Walk.WALK_MIN - .1);
//        testWalk.pending = System.currentTimeMillis() - (Walk.WALK_THRESHOLD + 1);
//        assertNull( "stopping should return LearningMode", testWalk.addSpeed( Walk.WALK_MIN ) );
//    }
//
////    @Test
////    public void testGetInitialTimeMS()
////    {
////        assertTrue( "Initial Time should be less than one second", ( testStartedWalk.getDurationMS() < MIN_DURATION ) );
////    }
////
////    @Test
////    public void testGetTimeMS() throws InterruptedException
////    {
////        Thread.sleep(RUN_DURATION);
////        assertTrue( "Time should be non-zero", ( testStartedWalk.getDurationMS() >= RUN_DURATION ) );
////    }
//
//}
