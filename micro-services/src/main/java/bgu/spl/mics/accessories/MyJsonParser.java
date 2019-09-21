package bgu.spl.mics.accessories;

import bgu.spl.mics.application.passiveObjects.BookInventoryInfo;

import java.util.List;
import java.util.Vector;

/**
 * MyJsonParser is a class connecting the json file with the runner of the program.
 * It holds representations of objects similar to those of the json file in order to receive that data directly.
 * This class holds objects which contain getters, it used as a bridge between the Json file and the rest of the program (especially runner).
 */
public class MyJsonParser {

    private Vector<InitialInventory> initialInventory;
    private Vector<InitialResources> initialResources;
    private Services services = new Services();

    public Vector<InitialInventory> get_initialInventory(){
        return initialInventory;
    }

    public Vector<InitialResources> get_initialResources() {
        return initialResources;
    }

    public Services get_services() {
        return services;
    }

    public class CreditCard{

        private int number;
        private int amount;

        public int get_number(){
            return number;
        }

        public int get_amount(){
            return amount;
        }
    }

    public class Time{

        private int speed;
        private int duration;

        public int get_speed(){
            return speed;
        }

        public int get_duration(){
            return duration;
        }
    }

    public class InitialInventory {

        private String bookTitle;
        private int amount;
        private int price;

        public int get_amount() {
            return amount;
        }

        public int get_price() {
            return price;
        }

        public String get_bookTitle(){
            return bookTitle;
        }
    }

    public class InitialResources{

        private Vehicle[] vehicles;


        public Vehicle[] get_vehicles(){
            return vehicles;
        }
    }

    public class Services{

        private Time time;
        private int selling;
        private int inventoryService;
        private int logistics;
        private int resourcesService;
        Customer[] customers;


        public Time get_time() {
            return time;
        }

        public int get_selling() {
            return selling;
        }

        public int get_inventoryServices() {
            return inventoryService;
        }

        public int get_logistics() {
            return logistics;
        }

        public int get_resourcesService() {
            return resourcesService;
        }

        public Customer[] get_customers() {
            return customers;
        }

    }

    public class Customer{

        private int id;
        private String name;
        private String address;
        private int distance;
        private CreditCard creditCard;
        private OrderSchedule[] orderSchedule;

        public int get_id() {
            return id;
        }

        public String get_name() {
            return name;
        }

        public String getAddress() {
            return address;
        }

        public int get_distance() {
            return distance;
        }

        public CreditCard get_creditCard() {
            return creditCard;
        }

        public OrderSchedule[] get_orderSchedule(){
            return orderSchedule;
        }
    }

    public class Vehicle{

        private int license;
        private int speed;

        public int get_licance() {
            return license;
        }

        public int get_speed() {
            return speed;
        }
    }

    public class OrderSchedule {

        private  String bookTitle;
        private int tick;

        public String getBookTitle() {
            return bookTitle;
        }

        public int getTick() {
            return tick;
        }
    }
}
