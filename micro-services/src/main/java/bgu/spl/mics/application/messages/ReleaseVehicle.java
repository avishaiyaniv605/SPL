package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;

/**
 * ReleaseVehicle is a message sent to release a vehicle which has finished its delivery
 */
public class ReleaseVehicle implements Event<DeliveryVehicle> {

    private DeliveryVehicle _deliveryVehicle;   // a vehicle to release

    /**
     * ReleaseVehicle constructor
     * @param deliveryVehicle is a vehicle to release
     */
    public ReleaseVehicle(DeliveryVehicle deliveryVehicle){
        _deliveryVehicle = deliveryVehicle;
    }

    /**
     * Retrieves the vehicle needs to be released
     * @return DeliveryVehicle object which has finished its delivery
     */
    public DeliveryVehicle get_deliveryVehicle(){
        return _deliveryVehicle;
    }
}
