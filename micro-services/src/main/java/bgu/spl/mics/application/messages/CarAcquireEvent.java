package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;

/**
 * CarAcquireEvent is a message sent to get a vehicle from ResourceHolder
 */
public class CarAcquireEvent implements Event<Future<DeliveryVehicle>> {
}