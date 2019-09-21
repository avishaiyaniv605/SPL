package bgu.spl.mics.application.passiveObjects;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Passive data-object representing a customer of the store.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
public class Customer implements Serializable {

	private String _name;
	private int _id;
	private int _distance;
	private String _address;
	private List<OrderReceipt> _receipts;
	private AtomicInteger _availableCreditAmount;
	private int _creditCard;

	public Customer(String _name, int _id, int _distance, String _address, int _availableCreditAmount, int _creditCard)
	{
		_receipts = new ArrayList<>();
		this._name = _name;
		this._id = _id;
		this._distance = _distance;
		this._address = _address;
		this._availableCreditAmount = new AtomicInteger(_availableCreditAmount);
		this._creditCard = _creditCard;
	}

	/**
	 * Retrieves the name of the customer.
	 */
	public String getName()
	{
		return _name;
	}

	/**
	 * Retrieves the ID of the customer  .
	 */
	public int getId()
	{
		return _id;
	}

	/**
	 * Retrieves the address of the customer.
	 */
	public String getAddress()
	{
		return _address;
	}

	/**
	 * Retrieves the distance of the customer from the store.
	 */
	public int getDistance()
	{
		return _distance;
	}

	/**
	 * Retrieves a list of receipts for the purchases this customer has made.
	 * <p>
	 * @return A list of receipts.
	 */
	public List<OrderReceipt> getCustomerReceiptList() {
		return _receipts;
	}

	/**
	 * Retrieves the amount of money left on this customers credit card.
	 * <p>
	 * @return Amount of money left.
	 */
	public int getAvailableCreditAmount() {
		return _availableCreditAmount.get();
	}

	/**
	 * Retrieves this customers credit card serial number.
	 */
	public int getCreditNumber() {
		return _creditCard;
	}

	public boolean charge(int lastAmout, int amountToCharge) {
		return _availableCreditAmount.compareAndSet(lastAmout,lastAmout - amountToCharge);
	}

	public void takeReceipt(OrderReceipt receipt) {
		_receipts.add(receipt);
	}

	private void readObject(ObjectInputStream aInputStream) throws ClassNotFoundException, IOException
	{
		 _name = aInputStream.readUTF();
		_id = aInputStream.readInt();
		_distance = aInputStream.readInt();
		_address = aInputStream.readUTF();
		_receipts = (List) aInputStream.readObject();
		_availableCreditAmount = new AtomicInteger(aInputStream.readInt());
		_creditCard = aInputStream.readInt();
	}

	private void writeObject(ObjectOutputStream aOutputStream) throws IOException
	{
		aOutputStream.writeUTF(_name);
		aOutputStream.writeInt(_id);
		aOutputStream.writeInt(_distance);
		aOutputStream.writeUTF(_address);
		aOutputStream.writeObject(_receipts);
		aOutputStream.writeInt(_availableCreditAmount.get());
		aOutputStream.writeInt(_creditCard);
	}

	@Override
	public String toString() {
		String str = "id    : " + _id + "\n";
		str += "name  : " + getName() + "\n";
		str += "addr  : " + getAddress() + "\n";
		str += "dist  : " + getDistance() + "\n";
		str += "card  : " + getCreditNumber() + "\n";
		str += "money : " + getAvailableCreditAmount();
		return str;
	}
}
