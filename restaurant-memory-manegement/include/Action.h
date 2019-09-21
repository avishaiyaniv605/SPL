#ifndef ACTION_H_
#define ACTION_H_

#include <string>
#include <iostream>
#include "Customer.h"
#include "Table.h"


enum ActionStatus{
    PENDING, COMPLETED, ERROR
};

//Forward declaration
class Restaurant;

class BaseAction{
public:
    BaseAction();
    ActionStatus getStatus() const;
    virtual void act(Restaurant& restaurant)=0;
    virtual std::string toString() const=0;
    virtual BaseAction* clone() = 0;
    virtual ~BaseAction() = default;

protected:
    void complete();
    void error(std::string errorMsg);
    std::string getErrorMsg() const;
private:
    std::string errorMsg;
    ActionStatus status;
};


class OpenTable : public BaseAction {
public:
    OpenTable(int id, std::vector<Customer *> &customersList);
    void act(Restaurant &restaurant);
    std::string toString() const;
    virtual OpenTable* clone();
    virtual ~OpenTable() = default; // table is responsible of deleting customers
    OpenTable(const OpenTable& other);
    OpenTable(OpenTable&& other);
    void setCustomers(const std::vector<Table*>& tables);

private:
    const int tableId;
    std::vector<Customer *> customers;
    std::string _arguments;
};


class Order : public BaseAction {
public:
    Order(int id);
    void act(Restaurant &restaurant);
    std::string toString() const;
    virtual Order* clone();
    virtual ~Order() = default;
private:
    const int tableId;
};


class MoveCustomer : public BaseAction {
public:
    MoveCustomer(int src, int dst, int customerId);
    void act(Restaurant &restaurant);
    std::string toString() const;
    virtual MoveCustomer* clone();
    virtual ~MoveCustomer() = default;
private:
    const int srcTable;
    const int dstTable;
    const int id;
};


class Close : public BaseAction {
public:
    Close(int id);
    void act(Restaurant &restaurant);
    std::string toString() const;
    virtual Close* clone();
    virtual ~Close() = default;
private:
    const int tableId;
};


class CloseAll : public BaseAction {
public:
    CloseAll();
    void act(Restaurant &restaurant);
    std::string toString() const;
    virtual CloseAll* clone();
    virtual ~CloseAll() = default;
private:
};


class PrintMenu : public BaseAction {
public:
    PrintMenu();
    void act(Restaurant &restaurant);
    std::string toString() const;
    virtual PrintMenu* clone();
    virtual ~PrintMenu() = default;
private:
};


class PrintTableStatus : public BaseAction {
public:
    PrintTableStatus(int id);
    void act(Restaurant &restaurant);
    std::string toString() const;
    virtual PrintTableStatus* clone();
    virtual ~PrintTableStatus() = default;
private:
    const int tableId;
};


class PrintActionsLog : public BaseAction {
public:
    PrintActionsLog();
    void act(Restaurant &restaurant);
    std::string toString() const;
    virtual PrintActionsLog* clone();
    virtual ~PrintActionsLog() = default;
private:

};


class BackupRestaurant : public BaseAction {
public:
    BackupRestaurant();
    void act(Restaurant &restaurant);
    std::string toString() const;
    virtual BackupRestaurant* clone();
    virtual ~BackupRestaurant() = default;
private:
};


class RestoreResturant : public BaseAction {
public:
    RestoreResturant();
    void act(Restaurant &restaurant);
    std::string toString() const;
    virtual RestoreResturant* clone();
    virtual ~RestoreResturant() = default;
};


#endif