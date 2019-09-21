package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.BookInventoryInfo;
import bgu.spl.mics.application.passiveObjects.OrderResult;

import java.awt.print.Book;

/**
 * CheckAvailabilityEvent is a message sent to check if a book is in stock (in the inventory)
 */
public class CheckAvailabilityEvent implements Event<Integer> {

    private String _bookToOrder;    // the book to check

    /**
     * CheckAvailabilityEvent constructor
     * @param bookToOrder is the name of the book to order
     */
    public CheckAvailabilityEvent(String bookToOrder) {
        _bookToOrder = bookToOrder;
    }

    /**
     * Retrieves the name of the book
     * @return the book of the book to check in the inventory
     */
    public String get_bookToOrder() {
        return _bookToOrder;
    }
}
