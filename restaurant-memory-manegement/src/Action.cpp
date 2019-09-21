#include <iostream>
#include <vector>
#include "../include/Action.h"
#include "../include/Restaurant.h"

extern Restaurant* backup;


// ------------------ BaseAction

BaseAction::BaseAction() :  errorMsg(""), status(ActionStatus::PENDING) {};

ActionStatus BaseAction::getStatus() const
{
    return status;
};

void BaseAction::complete()
{
    status = ActionStatus::COMPLETED;
};

void BaseAction::error(std::string errorMsg)
{
    this->errorMsg = errorMsg;
    status = ActionStatus::ERROR;

};

std::string BaseAction::getErrorMsg() const
{
    if (!errorMsg.empty())
        return errorMsg;
    return "";
};


// ------------------ OpenTable

OpenTable::OpenTable(int id, std::vector<Customer *> &customersList) :
        tableId(id) , customers(std::vector<Customer *>()),_arguments("")
{
    customers = customersList;
};

void OpenTable::act(Restaurant &restaurant)
{
    Table* tableToOpen = restaurant.getTable(tableId);
    int numOfCustomer = customers.size();
    for (auto customer: customers) {
        _arguments.append(customer->toString() + " ");
    }
    if(tableToOpen->getCapacity() >= numOfCustomer && !(tableToOpen->isOpen())) {
        tableToOpen->openTable();
        for (auto customer : customers) {
            tableToOpen->addCustomer(customer);
        }
        complete();
    }
    else{
        for (auto customer : customers) {
            delete customer;
        }
        customers.clear();
        error("Error: Table does not exist or is already open");
        std::cout<< getErrorMsg() << std::endl;

    }
};

std::string OpenTable::toString() const
{
    std::string msg("open " + std::to_string(tableId ) + " ");
    if (getStatus() == ActionStatus::COMPLETED)
    {
        msg.append(_arguments + "Completed");
    }
    else if (getStatus() == ActionStatus::ERROR)
    {
        msg.append(_arguments + getErrorMsg());
    }
    else
    {
        msg.append("Pending");
    }
    return msg;
};

OpenTable* OpenTable::clone()
{
    std::vector<Customer *> toClone;
    OpenTable* opToClone = new OpenTable(tableId,toClone);
    if (getStatus() == ActionStatus::COMPLETED)
    {
        opToClone->complete();
    }
    else if (getStatus() == ActionStatus::ERROR)
    {
        opToClone->error(getErrorMsg());
    }
    opToClone-> _arguments = _arguments;
    return opToClone;
};

OpenTable::OpenTable(const OpenTable& other) :
        tableId(other.tableId) , customers(std::vector<Customer *>()), _arguments("")
{
    for (auto customer : other.customers)
        customers.push_back(customer->clone());
};

OpenTable::OpenTable(OpenTable&& other) :
        tableId(other.tableId) ,  customers(std::vector<Customer *>()), _arguments("")
{
    for (auto customer : other.customers) {
        if (customer != nullptr) {
            customers.push_back(customer->clone());
            delete customer;
            customer = nullptr;
        }
    }
};

void OpenTable::setCustomers(const std::vector<Table*>& tables)
{
    int size = tables.size();
    for (int i = 0; i < size ; ++i) {
        if (i == tableId)
        {
            for ( auto customer : (tables.at(i)->getCustomers()))
            {
                customers.push_back(customer);
            }
        }
    }
};


// ------------------ Order

Order::Order(int id) : tableId(id) {};

void Order::act(Restaurant &restaurant)
{
    Table *tmpTable = restaurant.getTable(tableId);
    if (tmpTable != nullptr && tmpTable->isOpen())
    {
        int lastOrder = tmpTable->getOrders().size();
        tmpTable->order(restaurant.getMenu());
        std::vector<OrderPair> orders = tmpTable->getOrders();
        int numOfOrders = orders.size();
        while (lastOrder < numOfOrders)
        {
            OrderPair order = orders.at(lastOrder);
            Customer* tmpCustomer = restaurant.getTable(tableId)->getCustomer(order.first);
            std::cout << tmpCustomer->getName() << " ordered " << order.second.getName() << std::endl;
            lastOrder++;
        }
        complete();
    }
    else
    {
        this->error("Error: Table does not exist or is not open");
        std::cout << getErrorMsg() << std::endl;
    }
};

std::string Order::toString() const
{
    std::string msg("order " + std::to_string(tableId ) + " ");

    if (getStatus() == ActionStatus::COMPLETED)
    {
        msg.append("Completed");
    }
    else if (getStatus() == ActionStatus::ERROR)
    {
        msg.append(getErrorMsg());
    }
    else
    {
        msg.append("Pending");
    }
    return msg;
};

Order* Order::clone()
{
    Order* newOrder = new Order(tableId);
    if (getStatus() == ActionStatus::COMPLETED)
    {
        newOrder->complete();
    }
    else if (getStatus() == ActionStatus::ERROR)
    {
        newOrder->error(getErrorMsg());
    }
    return newOrder;
};


// ------------------ MoveCustomer

MoveCustomer::MoveCustomer(int src, int dst, int customerId) : srcTable(src), dstTable(dst), id(customerId) {};

void MoveCustomer::act(Restaurant &restaurant)
{
    Table* srcT = restaurant.getTable(srcTable);
    Table* dstT = restaurant.getTable(dstTable);
    std::vector<OrderPair> &srcOrders = srcT->getOrders();
    std::vector<OrderPair> &dstOrders = dstT->getOrders();
    Customer *c = srcT->getCustomer(id);
    int numOfCustomers = dstT->getCustomers().size();
    if(srcT != nullptr && srcT->isOpen() && dstT != nullptr && dstT->isOpen() && c != nullptr && numOfCustomers < dstT->getCapacity()) {
        dstT->addCustomer(c);
        for (auto orderPair: srcOrders) {
            if(orderPair.first == id){
                dstOrders.push_back(orderPair);
            }
        }
        srcT->removeCustomer(id);
        complete();
    }
    else{
        error("Error: Cannot move customer") ;
        std::cout << getErrorMsg() << std::endl;
    }
};

std::string MoveCustomer::toString() const
{
    std::string msg("move " + std::to_string(srcTable) + " " + std::to_string(dstTable) + " " + std::to_string(id) + " ");
    if (getStatus() == ActionStatus::COMPLETED)
    {
        msg.append("Completed");
    }
    else if (getStatus() == ActionStatus::ERROR)
    {
        msg.append(getErrorMsg());
    }
    else
    {
        msg.append("Pending");
    }
    return msg;
};

MoveCustomer* MoveCustomer::clone()
{
    MoveCustomer* moveCustomer = new MoveCustomer(srcTable,dstTable,id);
    if (getStatus() == ActionStatus::COMPLETED)
    {
        moveCustomer->complete();
    }
    else if (getStatus() == ActionStatus::ERROR)
    {
        moveCustomer->error(getErrorMsg());
    }
    return moveCustomer;
};


// ------------------ Close

Close::Close(int id):tableId(id) {};

void Close::act(Restaurant &restaurant)
{
    Table* tableToClose = restaurant.getTable(tableId);
    if(tableToClose != nullptr && tableToClose->isOpen()){
        std::string stat = "Table " + std::to_string(tableId) + " was closed. ";
        stat.append("Bill " + std::to_string(tableToClose->getBill()) + "NIS");
        std::cout << stat << std::endl;
        tableToClose->closeTable();
        complete();
    }
    else{
        error("Error: Table does not exist or is not open");
        std::cout<< getErrorMsg() << std::endl;
    }
};

std::string Close::toString() const
{
    std::string msg("close " + std::to_string(tableId) + " ");
    if (getStatus() == ActionStatus::COMPLETED)
    {
        msg.append("Completed");
    }
    else if (getStatus() == ActionStatus::ERROR)
    {
        msg.append(getErrorMsg());
    }
    else
    {
        msg.append("Pending");
    }
    return msg;
};

Close* Close::clone()
{
    Close* close = new Close(tableId);
    if (getStatus() == ActionStatus::COMPLETED)
    {
        close->complete();
    }
    else if (getStatus() == ActionStatus::ERROR)
    {
        close->error(getErrorMsg());
    }
    return close;
};


// ------------------ CloseAll

CloseAll::CloseAll() = default;

void CloseAll::act(Restaurant &restaurant)
{
    int restCapacity = restaurant.getNumOfTables();
    for (int i = 0; i < restCapacity; ++i) {
        Table* currTable = restaurant.getTable(i);
        if (currTable->isOpen())
        {
            std::string stat = "Table " + std::to_string(i) + " was closed.";
            stat.append(" Bill " + std::to_string(currTable->getBill()) + "NIS");
            std::cout << stat << std::endl;
            currTable->closeTable();
        }
    }
    complete();
};

std::string CloseAll::toString() const
{
    std::string msg("closeall ");
    if (getStatus() == ActionStatus::COMPLETED)
    {
        msg.append("Completed");
    }
    else
    {
        msg.append("Pending");
    }
    return msg;
};

CloseAll* CloseAll::clone()
{
    CloseAll* close = new CloseAll();
    if (getStatus() == ActionStatus::COMPLETED)
    {
        close->complete();
    }
    return close;
};


// ------------------ PrintMenu

PrintMenu::PrintMenu() = default;

void PrintMenu::act(Restaurant &restaurant)
{
    std::vector<Dish> menu = restaurant.getMenu();
    for (const auto &dish : menu) {
        std:: cout << dish.toString() << "NIS" << std::endl;
    }
    complete();
};

std::string PrintMenu::toString() const
{
    std::string msg("menu " );
    if (getStatus() == ActionStatus::COMPLETED)
    {
        msg.append("Completed");
    }
    else
    {
        msg.append("Pending");
    }
    return msg;
};

PrintMenu* PrintMenu::clone()
{
    PrintMenu* printMenu = new PrintMenu();
    if (getStatus() == ActionStatus::COMPLETED)
    {
        printMenu->complete();
    }
    return printMenu;
};


// ------------------ PrintTableStatus

PrintTableStatus::PrintTableStatus(int id): tableId(id) {};

void PrintTableStatus::act(Restaurant &restaurant)
{
    Table* tableToPrint = restaurant.getTable(tableId);
    if(tableToPrint == nullptr){
        return;
    }
    if(!tableToPrint->isOpen()){
        std::cout << "Table " << tableId << " status: " << "closed" << std::endl;
    }
    else{
        std::vector<Customer *>& tmpCustomer = tableToPrint->getCustomers();
        std::vector<OrderPair>& tmpOrders = tableToPrint->getOrders();
        std::cout << "Table " << tableId << " status: " << "open" << std::endl;
        std::cout << "Customers:" << std::endl;
        for (auto customer : tmpCustomer) {
            std::cout << customer->getId() << " " << customer->getName() << std::endl;
        }
        std::cout << "Orders:" << std::endl;
        for (auto order : tmpOrders) {
            std::cout << order.second.getName() << " " << order.second.getPrice() << "NIS " << order.first << std::endl;
        }
        std::cout << "Current Bill: " << tableToPrint->getBill() << "NIS" << std::endl;
    }
    complete();
};

std::string PrintTableStatus::toString() const
{
    std::string msg("status " + std::to_string(tableId) + " ");
    if (getStatus() == ActionStatus::COMPLETED)
    {
        msg.append("Completed");
    }
    else
    {
        msg.append("Pending");
    }
    return msg;
};

PrintTableStatus* PrintTableStatus::clone()
{
    PrintTableStatus* printTableStatus = new PrintTableStatus(tableId);
    if (getStatus() == ActionStatus::COMPLETED)
    {
        printTableStatus->complete();
    }
    return printTableStatus;
};


// ------------------ PrintActionsLog

PrintActionsLog::PrintActionsLog() = default;

void PrintActionsLog::act(Restaurant &restaurant)
{
    std::vector<BaseAction*> actsLog = restaurant.getActionsLog();
    for (auto action : actsLog) {
        std::cout << action->toString() << std::endl;
    }
    complete();
};

std::string PrintActionsLog::toString() const
{
    std::string msg("log " );
    if (getStatus() == ActionStatus::COMPLETED)
    {
        msg.append("Completed");
    }
    else
    {
        msg.append("Pending");
    }
    return msg;
};

PrintActionsLog* PrintActionsLog::clone()
{
    PrintActionsLog* printActionsLog = new PrintActionsLog();
    if (getStatus() == ActionStatus::COMPLETED)
    {
        printActionsLog->complete();
    }
    return printActionsLog;
};


// ------------------ BackupRestaurant

BackupRestaurant::BackupRestaurant() = default;

void BackupRestaurant::act(Restaurant &restaurant)
{
    if(backup != nullptr) {
        delete(backup);
    }
    backup = new Restaurant(restaurant);
    complete();

};

std::string BackupRestaurant::toString() const
{
    std::string msg("backup ");
    if (getStatus() == ActionStatus::COMPLETED)
    {
        msg.append("Completed");
    }
    else
    {
        msg.append("Pending");
    }
    return msg;
};

BackupRestaurant* BackupRestaurant::clone()
{
    BackupRestaurant* backupRestaurant = new BackupRestaurant();
    if (getStatus() == ActionStatus::COMPLETED)
    {
        backupRestaurant->complete();
    }
    else if (getStatus() == ActionStatus::ERROR)
    {
        backupRestaurant->error(getErrorMsg());
    }
    return backupRestaurant;
};


// ------------------ RestoreRestaurant

RestoreResturant::RestoreResturant() = default;

void RestoreResturant::act(Restaurant &restaurant)
{
    if (backup == nullptr)
    {
        error("Error: No backup available");
        std::cout << getErrorMsg() << std::endl;
        return;
    }
    restaurant = *backup;
    complete();
};

std::string RestoreResturant::toString() const
{
    std::string msg("restore ");
    if (getStatus() == ActionStatus::COMPLETED)
    {
        msg.append("Completed");
    }
    else if (getStatus() == ActionStatus::ERROR)
    {
        msg.append(getErrorMsg());
    }
    else
    {
        msg.append("Pending");
    }
    return msg;
};

RestoreResturant* RestoreResturant::clone()
{
    RestoreResturant* restoreResturant = new RestoreResturant();
    if (getStatus() == ActionStatus::COMPLETED)
    {
        restoreResturant->complete();
    }
    else if (getStatus() == ActionStatus::ERROR)
    {
        restoreResturant->error(getErrorMsg());
    }
    return restoreResturant;
};