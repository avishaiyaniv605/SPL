package bgu.spl.mics.application.passiveObjects;

import bgu.spl.mics.accessories.VehiclesSemaphore;
import bgu.spl.mics.Future;
import com.sun.jmx.remote.internal.ArrayQueue;
import sun.util.resources.cldr.fur.CalendarData_fur_IT;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Passive object representing the resource manager.
 * You must not alter any of the given public methods of this class.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private methods and fields to this class.
 */
public class ResourcesHolder {

    private static class ResourcesHolderHolder {
        private static ResourcesHolder _resourceHolder = new ResourcesHolder();
    }

    /**
     * Retrieves the single instance of this class.
     */
    public static ResourcesHolder getInstance() {
        return ResourcesHolderHolder._resourceHolder;
    }

    // A collection of delivery vehicles
    private LinkedList<DeliveryVehicle> _deliveryVehicles;

    // A queue of futures waiting to get resolved with a vehicle
    private ConcurrentLinkedQueue<Future<DeliveryVehicle>> _futures;

    private ResourcesHolder(){
        _futures = new ConcurrentLinkedQueue<>();
        _deliveryVehicles = new LinkedList<>();
    }

    /**
     * Tries to acquire a vehicle and gives a future object which will
     * resolve to a vehicle.
     * <p>
     * @return 	{@link Future<DeliveryVehicle>} object which will resolve to a
     * 			{@link DeliveryVehicle} when completed.
     */
    public Future<DeliveryVehicle> acquireVehicle() {
        if (_deliveryVehicles == null)	// if we have no cars at all
            return null;
        Future<DeliveryVehicle> future = new Future<>();
        _futures.add(future);			// at first, we add the future to futures list,
        // maybe there's a thread running in release method and can handle it
        DeliveryVehicle canBeAcquired = _deliveryVehicles.poll();	//we take the first car in the queue
        if (canBeAcquired != null) {    // if there's a car it wouldn't be not null
            synchronized (future) {		// if another tread resolved this future (in release), we wait
                if (future.isDone()) {  // otherwise, we resolve the future and the other thread, if there's one, will wait
                    _deliveryVehicles.add(canBeAcquired);	// if it was handled, we add back 'canBeAcquired' to the queue
                }
                else {										// else, we resolve it and remove from futures queue
                    future.resolve(canBeAcquired);
                    _futures.remove(future);
                }
            }
        }
        return future;
    }

    /**
     * Releases a specified vehicle, opening it again for the possibility of
     * acquisition.
     * <p>
     * @param vehicle	{@link DeliveryVehicle} to be released.
     */
    public void releaseVehicle(DeliveryVehicle vehicle) {
        // a flag to determine whether the current thread will resolve a future or not
        boolean resolved = false;
        for (Iterator<Future<DeliveryVehicle>> iterator = _futures.iterator(); iterator.hasNext(); ) {
            Future<DeliveryVehicle> future = iterator.next();
            synchronized (future)        // if there's a thread running in acquire with the same future
            {                            // it locks it so the other thread will get a resolved one
                if (_futures.contains(future) || vehicle == null)    // this contains check indicates whether a certain
                {                                                    // future was resolved (in acquire) while current thread was waiting
                    if (!future.isDone()) {
                        future.resolve(vehicle);
                        _futures.remove(future);
                        resolved = true;
                    }
                }
            }
            if (!resolved)    //push vehicle in vehicles list because there's no future waiting to get resolved
                _deliveryVehicles.add(vehicle);
        }

    }

    /**
     * Receives a collection of vehicles and stores them.
     * <p>
     * @param vehicles	Array of {@link DeliveryVehicle} instances to store.
     */
    public void load(DeliveryVehicle[] vehicles) {
        _deliveryVehicles.addAll(Arrays.asList(vehicles));
        if (_deliveryVehicles.size() == 0)
            _deliveryVehicles = null;
    }
}
