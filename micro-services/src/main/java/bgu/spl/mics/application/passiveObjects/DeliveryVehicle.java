package bgu.spl.mics.application.passiveObjects;

import java.util.concurrent.TimeUnit;

/**
 * Passive data-object representing a delivery vehicle of the store.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may add ONLY private fields and methods to this class.
 */
public class DeliveryVehicle {


	private final int _licence;
	private final int _speed; 	// number of milliseconds needed for 1KM

	/**
	 * Constructor.
	 */
	public DeliveryVehicle(int license, int speed) {
		_licence = license;
		_speed = speed;
	}
	/**
	 * Retrieves the license of this delivery vehicle.
	 */
	public int getLicense() {
		return _licence;
	}

	/**
	 * Retrieves the speed of this vehicle person.
	 * <p>
	 * @return Number of ticks needed for 1 Km.
	 */
	public int getSpeed() {
		return _speed;
	}

	/**
	 * Simulates a delivery by sleeping for the amount of time that
	 * it takes this vehicle to cover {@code distance} KMs.
	 * <p>
	 * @param address	The address of the customer.
	 * @param distance	The distance from the store to the customer.
	 */
	public void deliver(String address, int distance) {
		long timeToSleep = distance * _speed;

		try {   // thread goes to sleep -- simulates a delivery
			Thread.sleep(timeToSleep);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}


	}
}
