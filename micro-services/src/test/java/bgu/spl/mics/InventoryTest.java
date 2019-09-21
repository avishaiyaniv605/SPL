package bgu.spl.mics;

import bgu.spl.mics.application.passiveObjects.BookInventoryInfo;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.OrderResult;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;

public class InventoryTest {

    Inventory testInventory;
    private BookInventoryInfo[] bookInventoryInfos;
    private OrderResult res;
    private int isAvailable;
    private HashMap<String,Integer> testInventoryMap;

    /**
     * Before each test we initialize the variables and getting books to work with.
     */
    @Before
    public void testSetUp() {
        testInventory = Inventory.getInstance();
        testInventoryMap = new HashMap<>();
        bookInventoryInfos = giveMeBooks();
        testInventory.load(bookInventoryInfos);
        isAvailable = -2;
    }

    /**
     * Test which checks if the singleton implementation is correct.
     */
    @Test
    public void testGetInstance() {
        assertNotNull(testInventory); //Checks if the singleton design pattern has been implemented correctly.
    }

    /**
     * Test which checks the loading process by trying to load the books
     * and see if the books are stored in the inventory by trying to take it.
     */
    //------- load -------
    @Test
    public void testLoad() {
        //checking if the items were loaded to inventory
        res = testInventory.take(bookInventoryInfos[0].getBookTitle());
        assertEquals(res,OrderResult.SUCCESSFULLY_TAKEN);
    }

    /**
     * Test which checks if after taking an existing book, the amount of available copies of that book, has decreased by 1.
     */
    //------- take -------
    @Test
    public void testTake_lastBook() {
        //If amount of taken book decreasing (from one to zero) after action.
        res = testInventory.take(bookInventoryInfos[2].getBookTitle());
        isAvailable = testInventory.checkAvailabiltyAndGetPrice(bookInventoryInfos[2].getBookTitle());
        assertEquals(-1, isAvailable);
    }

    /**
     * Test which checks if trying to take an existing book ends with the result of successfully taken.
     */
    @Test
    public void testTake_successfullyTaken() {
        res = testInventory.take(bookInventoryInfos[1].getBookTitle());
        assertEquals(res, OrderResult.SUCCESSFULLY_TAKEN);
    }

    /**
     * Test which checks if trying to take a non existing book ends with the result of non existing in stock.
     */
    @Test
    public void testTake_nonExisting() {
        //If tried to take non existing book
        res = testInventory.take("nonExistingBook");
        assertEquals(OrderResult.NOT_IN_STOCK,res);
    }

    /**
     *Test which checks if reaching for an existing book info ends with a correct corresponding result of the book's price.
     */
    //------- checkAvailabilityAndGetPrice -------
    @Test
    public void testCheckAvailabilityAndGetPrice_ExistingBook() {
        //checking if an existing book is available and received the same price
        isAvailable = testInventory.checkAvailabiltyAndGetPrice(bookInventoryInfos[2].getBookTitle());
        assertEquals(isAvailable,bookInventoryInfos[2].getPrice());
    }

    /**
     * Test which checks if reaching for a non existing book info ends with a correct corresponding result of -1
     */
    @Test
    public void testCheckAvailabilityAndGetPrice_NonExistingBook() {
        //checking if a non existing book is available
        isAvailable = testInventory.checkAvailabiltyAndGetPrice("nonExistingBook");
        Assert.assertEquals(-1, isAvailable);
    }

    /**
     * Test which checks if the operation of writing the inventory to a file.
     */
    //------- printInventoryToFile -------
    @Test
    public void printInventoryToFile() {
        String file = "books.txt";
        testInventory.printInventoryToFile(file);
        HashMap<String,Integer> books = new HashMap<>();

        // Deserialization
        try
        {
            // Reading the object from a file
            FileInputStream fileInputStream = new FileInputStream(file);
            ObjectInputStream in = new ObjectInputStream(fileInputStream);

            // Method for deserialization of object
            books = (HashMap<String, Integer>)in.readObject();

            in.close();
            fileInputStream.close();
        } catch(IOException ex) {} //when the file not found, an exception will be thrown

        catch(ClassNotFoundException ex) {} // when the file is empty, an exception will be thrown

        //after deserialization, we get the books hashmap from the inventory instance
        // checks if they hold the same books
        assertTrue(testInventoryMap.equals(books));
    }

    /**
     * A private method generating array of books for the different tests.
     * @return array of BookInventoryInfo objects
     */
    private BookInventoryInfo[] giveMeBooks(){
        BookInventoryInfo[] bookInventoryInfos= new BookInventoryInfo[3];
        bookInventoryInfos[0] = new BookInventoryInfo("HarryPotterAndTheChamberOfSecrets",2,80);
        bookInventoryInfos[1] = new BookInventoryInfo("50ShadesOfGray",3,99);
        bookInventoryInfos[2] = new BookInventoryInfo("midSummerNightDreams",1,105);
        for (int i = 0; i < bookInventoryInfos.length; i++) {
            testInventoryMap.put(bookInventoryInfos[i].getBookTitle(),bookInventoryInfos[i].getAmountInInventory());
        }
        return bookInventoryInfos;
    }
}