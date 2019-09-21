package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.*;
import java.util.HashMap;
import java.util.Vector;

/**
 * APIService is in charge of the connection between a client and the store.
 * It informs the store about desired purchases using {@link BookOrderEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class APIService extends MicroService{

    private Customer _customer;                             // api's actual customer
    private HashMap<Integer, Vector<String>> _booksTicks;   // customer's orders ticks
    private final int _lastOrderTick;                       // customer's last order tick

    /**
     * APIService constructor
     * @param customer is the customer which this API object represents
     * @param booksTicks are the orders ticks
     */
    public APIService(Customer customer, HashMap<Integer,Vector<String>> booksTicks) {
        super("APIService : " + customer.getId());
        _customer = customer;
        _booksTicks = booksTicks;
        _lastOrderTick = findLastTick();
    }

    /**
     * Finds the last order tick of this API's customer
     * @return the number of the last order tick
     */
    private int findLastTick() {
        int max = -1 ;
        for (Integer currTick : _booksTicks.keySet())
        {
            if (currTick > max)
                max = currTick;
        }
        return max;
    }

    @Override
    protected void initialize() {
        // --- TerminateBroadcast subscription
        subscribeBroadcast(TerminateBroadcast.class, ev -> {
            terminate();
        });

        // --- TickBroadcast subscription
        subscribeBroadcast(TickBroadcast.class, ev -> {
            Vector<Future<OrderReceipt>> orders = new Vector<>();
            int currTick = ev.getCurrentTick();
            if (!_booksTicks.containsKey(currTick)) //if there's no order in this tick
                return;
            Vector<String> books = _booksTicks.get(currTick);
            for (String currBook : books)   //for each book in the current tick, make an order request
            {
                Future<OrderReceipt> order = sendEvent(new BookOrderEvent(_customer,currBook,currTick));
                orders.add(order);      // keeps the orders so we can get the results (receipts)
                if (order == null) { // if there are no micro services that can handle this event
                    return;
                }
            }
            for (Future<OrderReceipt> future : orders) {
                OrderReceipt oR = future.get();
                if (oR != null) {
                    _customer.takeReceipt(oR);
                }
            }
            if (_lastOrderTick == currTick) {   //if it is the last customer's order, terminate API
                terminate();
            }
        });
    }

    /**
     * returns the customer held in the API object
     * @return Customer of this API
     */
    public Customer get_customer()
    {
        return _customer;
    }
}
