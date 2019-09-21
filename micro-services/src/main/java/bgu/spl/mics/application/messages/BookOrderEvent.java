package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.BookInventoryInfo;
import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;

/**
 * BookOrderEvent is a message sent to inform that an order attempt has been made
 */
public class BookOrderEvent implements Event<OrderReceipt> {

    private Customer _customer;         // customer which tries to make an order
    private String _bookToOrderTitle;   // book's title
    private int _orderTick;

    /**
     * BookOrderEvent constructor
     * @param customer is the customer tries to order the {@param title} book
     * @param title is the title of the book {@param customer} tries to order
     */
    public BookOrderEvent(Customer customer, String title, int orderTick){
        _bookToOrderTitle = title;
        _customer = customer;
        _orderTick = orderTick;
    }

    /**
     * Retrieves the customer held in the BookOrderEvent
     * @return Customer object
     */
    public Customer get_customer() {
        return _customer;
    }

    /**
     * Retrieves the title of the book held in BookOrderEvent object
     * @return String which represents the book's title
     */
    public String get_bookToOrderTitle() {
        return _bookToOrderTitle;
    }


    public int get_orderTick(){
        return _orderTick;
    }
}

