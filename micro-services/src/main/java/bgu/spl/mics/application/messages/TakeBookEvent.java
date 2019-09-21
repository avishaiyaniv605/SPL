package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.OrderResult;

/**
 * TakeBookEvent is a message sent to take a book from the inventory
 */
public class TakeBookEvent implements Event<OrderResult> {

    private final String _book;     //books title

    /**
     * TakeBookEvent constructor
     * @param bookToOrder is the name of the book
     */
    public TakeBookEvent(String bookToOrder) {
        _book = bookToOrder;
    }

    /**
     * Retrieves the books which TakeBookEvent attempts to take from the inventory
     * @return String which is the name of the book
     */
    public String get_book() {
        return _book;
    }
}
