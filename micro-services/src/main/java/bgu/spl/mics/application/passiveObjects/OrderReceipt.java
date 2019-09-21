package bgu.spl.mics.application.passiveObjects;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Passive data-object representing a receipt that should 
 * be sent to a customer after the completion of a BookOrderEvent.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
public class OrderReceipt  implements Serializable {

	private int _orderId;
	private String _seller;
	private int _customerId;
	private String _bookTitle;
	private int _price;
	private int _issuedTick;
	private int _orderTick;
	private int _processTick;


	public OrderReceipt(int _orderId, String _seller, int _customerId, String _bookTitle, int _price, int _issuedTick, int _orderTick, int _processTick) {
		this._orderId = _orderId;
		this._seller = _seller;
		this._customerId = _customerId;
		this._bookTitle = _bookTitle;
		this._price = _price;
		this._issuedTick = _issuedTick;
		this._orderTick = _orderTick;
		this._processTick = _processTick;
	}

	/**
	 * Retrieves the orderId of this receipt.
	 */
	public int getOrderId()
	{
		return _orderId;
	}

	/**
	 * Retrieves the name of the selling service which handled the order.
	 */
	public String getSeller() {
		return _seller;
	}

	/**
	 * Retrieves the ID of the customer to which this receipt is issued to.
	 * <p>
	 * @return the ID of the customer
	 */
	public int getCustomerId() {
		return _customerId;
	}

	/**
	 * Retrieves the name of the book which was bought.
	 */
	public String getBookTitle() {
		return _bookTitle;
	}

	/**
	 * Retrieves the price the customer paid for the book.
	 */
	public int getPrice() {
		return _price;
	}

	/**
	 * Retrieves the tick in which this receipt was issued.
	 */
	public int getIssuedTick() {
		return _issuedTick;
	}

	/**
	 * Retrieves the tick in which the customer sent the purchase request.
	 */
	public int getOrderTick() {
		return _orderTick;
	}

	/**
	 * Retrieves the tick in which the treating selling service started
	 * processing the order.
	 */
	public int getProcessTick() {
		return _processTick;
	}

	private void readObject(ObjectInputStream aInputStream) throws ClassNotFoundException, IOException
	{
		_orderId = aInputStream.readInt();
		_seller = aInputStream.readUTF();
		_customerId = aInputStream.readInt();
		_bookTitle = aInputStream.readUTF();
		_price = aInputStream.readInt();
		_issuedTick =aInputStream.readInt();
		_orderTick = aInputStream.readInt();
		_processTick = aInputStream.readInt();
	}

	private void writeObject(ObjectOutputStream aOutputStream) throws IOException
	{
		aOutputStream.writeInt(_orderId);
		aOutputStream.writeUTF(_seller);
		aOutputStream.writeInt(_customerId);
		aOutputStream.writeUTF(_bookTitle);
		aOutputStream.writeInt(_price);
		aOutputStream.writeInt(_issuedTick);
		aOutputStream.writeInt(_orderTick);
		aOutputStream.writeInt(_processTick);
	}

	@Override
	public String toString() {
		String str = "";
		str += "customer   : " + getCustomerId() + "\n";
		str += "order tick : " + getOrderTick() + "\n";
		str += "id         : " + getOrderId() + "\n";
		str += "price      : " + getPrice() + "\n";
		str += "seller     : " + getSeller();
		return str;
	}

}
