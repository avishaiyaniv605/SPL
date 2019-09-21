package bgu.spl.mics.application.passiveObjects;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Passive data-object representing a information about a certain book in the inventory.
 * You must not alter any of the given public methods of this class. 
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
public class BookInventoryInfo {

	private final String _bookTitle;
	private AtomicInteger _amountInInventory;
	private final int _price;

	public BookInventoryInfo(String _bookTitle, int amountInInventory, int price) {
		this._bookTitle = _bookTitle;
		this._amountInInventory = new AtomicInteger(amountInInventory);
		_price = price;
	}

	/**
	 * Retrieves the title of this book.
	 * <p>
	 * @return The title of this book.
	 */
	public String getBookTitle() {
		return _bookTitle;
	}

	/**
	 * Retrieves the amount of books of this type in the inventory.
	 * <p>
	 * @return amount of available books.
	 */
	public int getAmountInInventory() {
		return _amountInInventory.get();
	}

	/**
	 * Retrieves the price for  book.
	 * <p>
	 * @return the price of the book.
	 */
	public int getPrice() {
		return _price;
	}

	public boolean takeBook(int amount)
	{
		if (_amountInInventory.get() == 0)
			return false;
		return _amountInInventory.compareAndSet(amount,amount-1);
	}

	@Override
	public String toString() {
		String str = "";
		str += "title   : " + getBookTitle() + "\n";
		str += "amount  : " + getAmountInInventory() + "\n";
		str += "price   : " + getPrice();
		return str;
	}
}
