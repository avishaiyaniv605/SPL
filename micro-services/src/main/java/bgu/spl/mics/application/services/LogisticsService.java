package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CarAcquireEvent;
import bgu.spl.mics.application.messages.DeliveryEvent;
import bgu.spl.mics.application.messages.ReleaseVehicle;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.passiveObjects.*;

/**
 * Logistic service in charge of delivering books that have been purchased to customers.
 * Handles {@link DeliveryEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LogisticsService extends MicroService {

    public LogisticsService(String name) {
        super(name);
    }

    @Override
    protected void initialize() {
        // --- TerminateBroadcast subscription
        subscribeBroadcast(TerminateBroadcast.class, ev -> {
            terminate();
        });

        // --- DeliveryEvent subscription
        subscribeEvent(DeliveryEvent.class, delEv->{
            // sends a request to get a vehicle
            Future<Future<DeliveryVehicle>> future = sendEvent(new CarAcquireEvent());
            if(future != null){
                Future<DeliveryVehicle> futureOfDelivery = future.get();    // this future will be always resolved
                if (futureOfDelivery == null)
                    return;
                DeliveryVehicle deliveryVehicle = futureOfDelivery.get();   // this future holds the vehicle and will be resolved once there's a free vehicle
                if(deliveryVehicle != null){	// a vehicle was found and now it delivers the book
                    deliveryVehicle.deliver(delEv.get_address(),delEv.get_distance());
                    sendEvent(new ReleaseVehicle(deliveryVehicle));
                    System.out.println("DELIVERED!");
                }
            }
        });
    }

}
