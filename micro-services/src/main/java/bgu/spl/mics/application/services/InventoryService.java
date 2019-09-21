package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CheckAvailabilityEvent;
import bgu.spl.mics.application.messages.TakeBookEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.passiveObjects.*;

/**
 * InventoryService is in charge of the book inventory and stock.
 * Holds a reference to the {@link Inventory} singleton of the store.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */

public class InventoryService extends MicroService{

	public InventoryService(String name) {
		super(name);
	}

	@Override
	protected void initialize() {
		// --- TerminateBroadcast subscription
		subscribeBroadcast(TerminateBroadcast.class, ev -> {
			terminate();
		});

		// --- CheckAvailabilityEvent subscription
		subscribeEvent(CheckAvailabilityEvent.class, check_ev ->{
			//checks if books is available and gets its price
			int price = Inventory.getInstance().checkAvailabiltyAndGetPrice(check_ev.get_bookToOrder());
			complete(check_ev,price);
		});

		// --- TakeBookEvent subscription
		subscribeEvent(TakeBookEvent.class, take_ev->{
			//takes the book from the inventory
			OrderResult res = Inventory.getInstance().take(take_ev.get_book());
			complete(take_ev,res);
		});
	}

}
