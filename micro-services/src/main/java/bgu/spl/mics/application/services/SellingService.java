package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.*;

/**
 * Selling service in charge of taking orders from customers.
 * Holds a reference to the {@link MoneyRegister} singleton of the store.
 * Handles {@link BookOrderEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link Inventory}.
 * <p>
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class SellingService extends MicroService {

    private int _currTick;

    public SellingService(String name) {
        super(name);
    }

    @Override
    protected void initialize() {
        // --- TerminateBroadcast subscription
        subscribeBroadcast(TerminateBroadcast.class, ev -> {
            terminate();
        });

        // --- TickBroadcast subscription
        subscribeBroadcast(TickBroadcast.class,tickEV->{
            _currTick = tickEV.getCurrentTick();
        });

        // --- BookOrderEvent subscription
        subscribeEvent(BookOrderEvent.class, ev -> {
            int orderTick = ev.get_orderTick();
            OrderReceipt orderReceipt = null;
            // checks if book is available
            Future<Integer> isAvailable = sendEvent(new CheckAvailabilityEvent(ev.get_bookToOrderTitle()));
            if (isAvailable != null) {              // there's a micro service which can handle it
                Integer price = isAvailable.get();  //waits until resolved and then gets price
                if (price != -1 ) {
                    boolean refund = false;
                    synchronized (ev.get_customer()) {
                        MoneyRegister.getInstance().chargeCreditCard(ev.get_customer(), price);
                        refund = !(0 <= ev.get_customer().getAvailableCreditAmount());
                    }
                    if (!refund) { // customer has enough money
                        // tries to take the book from the inventory using other micro services
                        Future<OrderResult> isTaken = sendEvent(new TakeBookEvent(ev.get_bookToOrderTitle()));
                        if (isTaken != null && isTaken.get() == OrderResult.SUCCESSFULLY_TAKEN) {   // there's a micro service which can handle it
                            sendBook(ev.get_customer(), price);                                // and it was successfully taken
                            orderReceipt = createReceiptAndFileOrder(ev.get_customer(), ev.get_bookToOrderTitle(), price, Math.max(_currTick, orderTick), orderTick);
                        } else if (isTaken.get() == OrderResult.NOT_IN_STOCK)
                            refund = true;
                    }
                    if (refund)
                        MoneyRegister.getInstance().chargeCreditCard(ev.get_customer(), -1 * price);
                }
            }
            complete(ev,orderReceipt);   //whether it was bought or not, complete invokes the customer from waiting
        });
    }

    private void sendBook(Customer customer, Integer price) {
        String address = customer.getAddress();
        int distance = customer.getDistance();
        sendEvent(new DeliveryEvent(address,distance));
    }

    /**
     * This method creates a receipt for a customer order
     * @param c is the customer bought
     * @param bookTitle is the name of the bought book
     * @param bookPrice is the price of the book
     * @param issuedTick is the time when the receipt was created
     * @param orderProcessTick is the time when the order was made
     * @return an object typed OrderReceipt holds all the information above
     */
    private OrderReceipt createReceiptAndFileOrder(Customer c, String bookTitle, int bookPrice,int issuedTick,int orderProcessTick){
        String seller = this.getName();
        int customerId = c.getId();
        String _bookTitle = bookTitle;
        int _bookPrice = bookPrice;
        int tIssuedTick = issuedTick;
        int tOrderTick = orderProcessTick;
        int processTick = orderProcessTick;
        OrderReceipt orderReceipt = new OrderReceipt(0,seller,customerId,_bookTitle,_bookPrice,tIssuedTick,tOrderTick,processTick);
        MoneyRegister.getInstance().file(orderReceipt);
        return orderReceipt;
    }
}


















