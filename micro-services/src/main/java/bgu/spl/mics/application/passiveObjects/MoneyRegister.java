package bgu.spl.mics.application.passiveObjects;


import bgu.spl.mics.accessories.FilePrinter;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Passive object representing the store finance management. 
 * It should hold a list of receipts issued by the store.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private fields and methods to this class as you see fit.
 */
public class MoneyRegister implements Serializable {


	private static class MoneyRegisterHolder {
		private static MoneyRegister _moneyHolder = new MoneyRegister();
	}

	private List<OrderReceipt> _ordersList;

	/**
	 * Retrieves the single instance of this class.
	 */
	public static MoneyRegister getInstance() {
		return MoneyRegisterHolder._moneyHolder;
	}

	private MoneyRegister()
	{
		_ordersList = new ArrayList<>();
	}

	/**
	 * Saves an order receipt in the money register.
	 * <p>
	 * @param r		The receipt to save in the money register.
	 */
	public void file (OrderReceipt r) {
		_ordersList.add(r);
	}

	/**
	 * Retrieves the current total earnings of the store.
	 */
	public int getTotalEarnings() {
		int total = 0;
		for (OrderReceipt order : _ordersList)
			total += order.getPrice();
		return total;
	}

	/**
	 * returns all the order receipts in the system.
	 * @return
	 */
	public List<OrderReceipt> getOrderReceipts()
	{
		return _ordersList;
	}

	/**
	 * Prints to a file named @filename a serialized object List<OrderReceipt> which holds all the order receipts
	 * currently in the MoneyRegister
	 * This method is called by the main method in order to generate the output..
	 */
	public void printOrderReceipts(String filename) {
		FilePrinter.printToFile(_ordersList,filename);
	}

	/**
	 * Charges the credit card of the customer a certain amount of money.
	 * <p>
	 * @param amount 	amount to charge
	 */
	public void chargeCreditCard(Customer c, int amount) {
		int amountLeft = c.getAvailableCreditAmount();
		while (!c.charge(amountLeft,amount))
		{
			amountLeft = c.getAvailableCreditAmount();
		}
	}

	public void printObject(String moneyPath) {
		FilePrinter.printToFile(this,moneyPath);
	}


	private void readObject(ObjectInputStream aInputStream) throws ClassNotFoundException, IOException
	{
		_ordersList = (List) aInputStream.readObject();
	}

	private void writeObject(ObjectOutputStream aOutputStream) throws IOException
	{
		aOutputStream.writeObject(_ordersList);
	}

}
