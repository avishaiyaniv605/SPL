package bgu.spl.mics;

import org.junit.Before;
import org.junit.Test;
import bgu.spl.mics.Future;
import java.util.concurrent.TimeUnit;
import static org.junit.Assert.*;

public class FutureTest {

    Future _future;

    @Before
    public void setUp() throws Exception
    {
        _future = new Future<String>();
    }

    /**
     * checks if future object has been initialized
     */
    @Test
    public void testInitialization()
    {
        assertNotNull(_future);
    }

    /**
     * checks if future object hasn't been resolved yet and its result is still null.
     */
    @Test
    public void get()
    {
        Object getFromFuture = null;
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.currentThread().sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                _future.resolve(new Object ());
            }
        });
        t1.start();
        assertNotNull(_future.get());
    }

    /**
     * checks if future object has been resolved with given object.
     */
    @Test
    public void resolve()
    {
        _future.resolve("Resolved");
        assertEquals("Resolved",_future.get());
    }

    /**
     * checks if future object has not modified the resolved value whenever it already has one.
     */
    @Test
    public void resolveTwice()
    {
        // resolve once
        String result = "Resolved";
        _future.resolve(result);

        // resolve again , Future must deny this one
        _future.resolve("AGAIN!");

        assertEquals(result,_future.get());
    }

    /**
     *  checks if future object has been resolved.
     */
    @Test
    public void isDoneFalse()
    {
        assertEquals(false,_future.isDone());
    }

    /**
     *  checks if future object has been resolved.
     */
    @Test
    public void isDoneTrue()
    {
        _future.resolve(new Object());
        assertEquals(true,_future.isDone());
    }


    /**
     * checks what future object returns according to two scenarios
     * first is when a future object holds a result, it will be returned before the expected time
     * second is when a future object does not hold a result and the waiting time passed, the result must to be null.
     */
    @Test
    public void getWithTimeArgueBeforeTimePassed()
    {

        Object getFromFuture = null;
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.currentThread().sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                _future.resolve(new Object ());
            }
        });
        t1.start();
        assertNotNull(_future.get(5,TimeUnit.SECONDS));
    }
    /**
     * checks what future object returns according to two scenarios
     * first is when a future object holds a result, it will be returned before the expected time
     * second is when a future object does not hold a result and the waiting time passed, the result must to be null.
     */
    @Test
    public void getWithTimeArgueAfterTimePassed()
    {

        Object getFromFuture = null;
        assertNull(_future.get(2,TimeUnit.SECONDS));
    }
}