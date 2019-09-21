#include <iostream>
#include <vector>
#include "../include/Table.h"

Table::Table(int t_capacity) :
        capacity(t_capacity), open(false) , customersList(std::vector<Customer*>()) , orderList(std::vector<OrderPair>()){};

int Table::getCapacity() const
{
    return capacity;
};

void Table::addCustomer(Customer *customer)
{
    customersList.push_back(customer);
};

void Table::removeCustomer(int id)
{
    if (!customersList.empty()) {
        int numOfCustomers = customersList.size();
        for (int i = 0; i < numOfCustomers; ++i) {
            if (customersList.at(i)->getId() == id){
                customersList.erase((customersList.begin() + i));
                numOfCustomers--;
            }
        }
    }
    if(!orderList.empty()) {
        std::vector<OrderPair> newOrdersList;
        int numOfOrders = orderList.size();
        for(int i=0;i < numOfOrders;i++){
            if(!(orderList.at(i).first == id)){
                newOrdersList.push_back(orderList.at(i));
            }
        }
        orderList = std::move(newOrdersList);
    }
    if(customersList.empty()){
        open = false;
    };
};

Customer *Table::getCustomer(int id)
{
    for (auto customer : customersList) {
        if (customer->getId() == id) {
            return customer;
        }
    }
    return nullptr;
};

std::vector<Customer *> &Table::getCustomers()
{
    return  customersList;
};

std::vector<OrderPair> &Table::getOrders()
{
    return orderList;
};

void Table::order(const std::vector<Dish> &menu)
{
    for (auto customer: customersList) {
//        Customer* currCustomer = customer;
        std::vector<int> orders = customer->order(menu);
        for (auto order : orders) {
            if (order == -1)
                continue;
            OrderPair x(customer->getId(), menu.at(order));
            orderList.push_back(x);
        }
    }
};

void Table::openTable()
{
    open = true;
};

void Table::closeTable()
{
    open = false;
    for (auto customer : customersList) {
        if (customer != nullptr)
            delete customer;
    }
    orderList.clear();
    customersList.clear();
};

int Table::getBill()
{
    int billSum = 0;
    for (auto order : orderList) {
        billSum += order.second.getPrice();
    }
    return billSum;
};

bool Table::isOpen()
{
    return open;
};

Table::~Table()
{
    for (auto customer: customersList) {
        if (customer != nullptr) {
            delete customer;
        }
    }
    customersList.clear();
};

Table::Table(const Table& other):
        capacity(other.capacity),open(other.open),customersList(std::vector<Customer*>()),orderList(other.orderList)
{
    int numOfCustomers = other.customersList.size();
    for (int i = 0; i < numOfCustomers; ++i) {
        customersList.push_back(other.customersList.at(i)->clone());
    }
};

Table& Table::operator=(const Table &other) {
    if(&other == this) {
        return *this;
    }
    for (auto customer : customersList) {
        delete(customer);
    }
    customersList.clear();
    for (auto toAdd : other.customersList) {
        customersList.push_back(toAdd->clone());
    }
    capacity = other.capacity;
    open = other.open;
    orderList = std::vector<OrderPair>(other.orderList);
    return *this;
};

Table::Table(Table&& other):capacity(other.capacity),open(other.open),customersList(other.customersList),orderList(other.orderList)
{
    int numOfCustomers = other.customersList.size();
    for (int i = 0; i < numOfCustomers; i++) {
        if (other.customersList.at(i) != nullptr)
        {
            delete(other.customersList.at(i));
        }
        other.customersList.at(i) = nullptr;
    }
};

Table& Table::operator=(Table&& other)
{
    for (auto customer : customersList) {
        if (customer != nullptr)
        {
            delete (customer);
        }
    }
    customersList.clear();
    for (auto toAdd : other.customersList) {
        customersList.push_back(toAdd->clone());
    }
    int numOfCustomers = other.customersList.size();
    for (int i = 0; i < numOfCustomers; ++i) {
        if (other.customersList.at(i) != nullptr)
        {
            delete(other.customersList.at(i));
        }
        other.customersList.at(i) = nullptr;
    }

    return *this;
};

Table* Table::clone()
{
    Table* tableToClone = new Table(capacity);
    tableToClone->orderList = std::vector<OrderPair>(orderList);
    tableToClone->open = open;
    for (auto customer : customersList){
        tableToClone->addCustomer(customer->clone());
    }
    return tableToClone;
};








































