package bgu.spl.mics.accessories;

import bgu.spl.mics.Future;

/**
 * Class used as a semaphore for acquiring and releasing vehicles.
 */
public class VehiclesSemaphore {

    private final int _permits;
    private int _free;
    private boolean[] _aqcuiredVehicles;

    /**
     * constructor.
     * @param numOfVehicles
     */
    public VehiclesSemaphore(int numOfVehicles) {
        _permits = numOfVehicles;
        _free = numOfVehicles;
        _aqcuiredVehicles = new boolean[numOfVehicles];
    }

    /**
     * Method responsible for acquiring a vehicle if the is one available.
     * in addition, this method is referring to a specific vehicle.
     * @return
     */
    public synchronized int acquire() {
        int index = -1;
        while (_free == 0) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        for (int i = 0; i < _aqcuiredVehicles.length; i++) {
            if (!_aqcuiredVehicles[i]) {
                _aqcuiredVehicles[i] = !_aqcuiredVehicles[i];
                index = i;
                break;
            }
        }
        _free--;
        return index;
    }

    /**
     * Method responsible for releasing an acquired vehicle for further deliveries.
     * @param indexToRelease integer as an index to the specific vehicle needs to be released.
     */
    public synchronized void release(int indexToRelease) {
        if (_free <= _permits) {
            _free++;
            _aqcuiredVehicles[indexToRelease] = false;
            notify();
        }
    }

    /**
     * Method for trying to acquire a vehicle.
     * return boolean as an answer if there is an available vehicle.
     */
    public synchronized boolean tryAcquire(Future<Integer> future) {
        if (_free == 0)
            return false;
        int index = acquire();
        future.resolve(index);
        return true;
    }

}
