package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;

/**
 * DeliveryEvent is a message sent to notify that a book needs to be delivered
 */
public class DeliveryEvent implements Event {

    private String _address;    // the address to deliver to
    private int _distance;      // the distance for delivery

    /**
     * DeliveryEvent constructor
     * @param address is the address to deliver to
     * @param distance is the distance for delivery
     */
    public DeliveryEvent(String address, int distance){
        _address = address;
        _distance = distance;
    }

    /**
     * Retrieves the address of the delivery
     * @return String which is the address
     */
    public String get_address() {
        return _address;
    }

    /**
     * Retrieves the distance of the delivery
     * @return int which is the distance
     */
    public int get_distance() {
        return _distance;
    }



}
