package bgu.spl.mics.application;

import bgu.spl.mics.accessories.MyJsonParser;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.accessories.FilePrinter;
import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.application.services.*;
import com.google.gson.*;

import java.io.*;
import java.util.*;

/**
 * This is the Main class of the application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output serialized objects.
 */
public class BookStoreRunner {

    private Vector<MicroService> _microServices;
    private HashMap<Integer, Customer> _customers;

    /**
     * Constructor, initializing the process of getting information from Json file.
     *
     * @param paths
     */
    public BookStoreRunner(String[] paths) {
        _microServices = new Vector<>();
        _customers = new HashMap<>();
        parseJSONAndLoad(paths[0]);
        startThreadsAndJoin();
        printToFiles(paths);

    }

    /**
     * Method responsible for printing object to file using serialization.
     *
     * @param args String array containing details the paths for the object which need to be written to file.
     */
    private void printToFiles(String[] args) {
        FilePrinter.printToFile(_customers, args[1]);
        Inventory.getInstance().printInventoryToFile(args[2]);
        MoneyRegister.getInstance().printOrderReceipts(args[3]);
        MoneyRegister.getInstance().printObject(args[4]);
    }

    /**
     * Assisting method responsible for initiating the threads.
     * cycling through the microServices data structure and starting each one of them.
     */
    private void startThreadsAndJoin() {
        ArrayList<Thread> threads = new ArrayList<>();
        for (MicroService microService : _microServices) {
            Thread thread = new Thread(microService);
            thread.setName(microService.getName());
            threads.add(thread);
            thread.start();
        }
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                return;
            }
        }
    }

    /**
     * This method is responsible of parsing and loading information and instances from JSON file
     *
     * @param jsonPath is the JSON file converted into a String file
     */
    private void parseJSONAndLoad(String jsonPath) {

        Gson gson = new Gson();
        MyJsonParser jsonData = null;
        try {
            jsonData = gson.fromJson(new FileReader(jsonPath), MyJsonParser.class);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // books parse and load
        BookInventoryInfo[] books = parseBooks(jsonData);
        Inventory.getInstance().load(books);

        // vehicles parse and load
        DeliveryVehicle[] vehicles = parseResources(jsonData);
        ResourcesHolder.getInstance().load(vehicles);

        // services pars and load
        TimeService timeService = parseTime(jsonData);

        int[] numOfServices = parseNumOfServices(jsonData);
        HashMap<Customer, HashMap<Integer, Vector<String>>> apis = parseCustomers(jsonData);

        loadServices(numOfServices);
        loadAPIs(apis);

        _microServices.add(timeService);


    }

    /**
     * Assisting method responsible for loading customers and creating threads representing each customer as a web client
     *
     * @param apis
     */
    private void loadAPIs(HashMap<Customer, HashMap<Integer, Vector<String>>> apis) {
        for (Customer customer : _customers.values()) {
            _microServices.add(new APIService(customer, apis.get(customer)));
        }
    }

    /**
     * Parsing orders from json file and adding each order to a HashMap keyed by the tick it is supposed to be initiated at.
     *
     * @param orderSchedules Orders array from json file.
     * @return
     */
    private HashMap<Integer, Vector<String>> parseOrders(MyJsonParser.OrderSchedule[] orderSchedules) {
        HashMap<Integer, Vector<String>> orders = new HashMap<>();

        for (int i = 0; i < orderSchedules.length; i++) {
            String currTitle = orderSchedules[i].getBookTitle();
            int currTick = orderSchedules[i].getTick();

            if (!orders.containsKey(currTick)) {
                orders.put(currTick, new Vector<>());
            }
            orders.get(currTick).add(currTitle);
        }
        return orders;
    }

    /**
     * Getting the information of the customers from the json file, parsing it into customer objects and adding it to a data structure.
     * HashMap keyed by the customer itself and the value is a HashMap keyed by the tick and its values are all the orders
     * supposed to take place at that tick.
     *
     * @param jsonData
     * @return
     */
    private HashMap<Customer, HashMap<Integer, Vector<String>>> parseCustomers(MyJsonParser jsonData) {
        MyJsonParser.Customer[] customersJson = jsonData.get_services().get_customers();
        HashMap<Customer, HashMap<Integer, Vector<String>>> ans = new HashMap<>();

        for (int i = 0; i < customersJson.length; i++) {
            int id = customersJson[i].get_id();
            String name = customersJson[i].get_name();
            String address = customersJson[i].getAddress();
            int distance = customersJson[i].get_distance();
            int cardNumer = customersJson[i].get_creditCard().get_number();
            int amountInAcount = customersJson[i].get_creditCard().get_amount();

            Customer currCustomer = new Customer(name, id, distance, address, amountInAcount, cardNumer);
            _customers.put(id, currCustomer);
            MyJsonParser.OrderSchedule[] orderSchedules = customersJson[i].get_orderSchedule();
            ans.put(currCustomer, parseOrders(orderSchedules));

        }
        return ans;
    }

    /**
     * Method responsible for getting the information about the number of services in the system.
     * This method handles all services beside API and TIME.
     *
     * @param jsonData MyJsonParser object with the data from the json file.
     * @return
     */
    private int[] parseNumOfServices(MyJsonParser jsonData) {
        int sellingServices = jsonData.get_services().get_selling();
        int inventoryServices = jsonData.get_services().get_inventoryServices();
        int logisticsServices = jsonData.get_services().get_logistics();
        int resourcesServices = jsonData.get_services().get_resourcesService();

        int[] numOfServices = new int[4];
        numOfServices[0] = sellingServices;
        numOfServices[1] = inventoryServices;
        numOfServices[2] = logisticsServices;
        numOfServices[3] = resourcesServices;

        return numOfServices;
    }

    /**
     * This method loads micro services which are selling, inventory, logistics and resources micro services
     * numOfServices is an array which represents how much micro services we will have in the program
     *
     * @param numOfServices indexes: 0- selling, 1- inventory, 2- logistics, 3- resources
     */
    private void loadServices(int[] numOfServices) {
        int services = numOfServices[0];
        for (int j = 0; j < services; j++) {
            _microServices.add(new SellingService("SellingService " + j));
        }
        services = numOfServices[1];
        for (int j = 0; j < services; j++) {
            _microServices.add(new InventoryService("InventoryService " + j));
        }
        services = numOfServices[2];
        for (int j = 0; j < services; j++) {
            _microServices.add(new LogisticsService("LogisticsService " + j));
        }
        services = numOfServices[3];
        for (int j = 0; j < services; j++) {
            _microServices.add(new ResourceService("ResourcesService " + j));
        }
    }

    /**
     * This method parses books objects from JSON file
     *
     * @param jsonData MyJsonParser object with the data from the json file.
     * @return books array parsed off the json
     */
    private BookInventoryInfo[] parseBooks(MyJsonParser jsonData) {
        Vector<MyJsonParser.InitialInventory> initialInventoryVector = jsonData.get_initialInventory();

        BookInventoryInfo[] books = new BookInventoryInfo[initialInventoryVector.size()];
        BookInventoryInfo currBook;
        String currBookTitle;
        int currAmount;
        int currPrice;

        for (int i = 0; i < initialInventoryVector.size(); i++) {
            currBookTitle = initialInventoryVector.get(i).get_bookTitle();
            currAmount = initialInventoryVector.get(i).get_amount();
            currPrice = initialInventoryVector.get(i).get_price();
            currBook = new BookInventoryInfo(currBookTitle, currAmount, currPrice);
            books[i] = currBook;
        }

        return books;
    }

    /**
     * This method parses vehicles objects from JSON file
     *
     * @param jsonData MyJsonParser object with the data from the json file.
     * @return
     */
    private DeliveryVehicle[] parseResources(MyJsonParser jsonData) {
        MyJsonParser.Vehicle[] vehicles = jsonData.get_initialResources().get(0).get_vehicles();
        DeliveryVehicle[] deliveryVehicles = new DeliveryVehicle[vehicles.length];
        DeliveryVehicle currVehicle;
        int currSpeed;
        int currLicence;

        for (int i = 0; i < vehicles.length; i++) {
            currLicence = vehicles[i].get_licance();
            currSpeed = vehicles[i].get_speed();
            currVehicle = new DeliveryVehicle(currLicence, currSpeed);
            deliveryVehicles[i] = currVehicle;

        }
        return deliveryVehicles;
    }

    /**
     * Method responsible for getting the Time object from the json file.
     *
     * @param jsonData MyJsonParser object with the data from the json file.
     * @return
     */
    private TimeService parseTime(MyJsonParser jsonData) {
        MyJsonParser.Time time = jsonData.get_services().get_time();
        TimeService timeService = new TimeService(time.get_speed(), time.get_duration());
        return timeService;
    }

    /**
     * args = {
     * input : json path,
     * output : customers path, output : books path, output : orders path , output : money register path
     * }
     *
     * @param args
     */
    public static void main(String[] args) {
        new BookStoreRunner(args);
    }






}
