#ifndef CUSTOMER_H_
#define CUSTOMER_H_

#include <vector>
#include <string>
#include "Dish.h"

class Customer{
public:
    Customer(std::string c_name, int c_id);
    virtual std::vector<int> order(const std::vector<Dish> &menu)=0;
    virtual std::string toString() const = 0;
    std::string getName() const;
    int getId() const;
    virtual ~Customer() = default;
    virtual Customer* clone() = 0;
private:
    const std::string name;
    const int id;

};


class VegetarianCustomer : public Customer {
public:
    VegetarianCustomer(std::string name, int id);
    std::vector<int> order(const std::vector<Dish> &menu);
    std::string toString() const;
    virtual ~VegetarianCustomer() = default;
    virtual VegetarianCustomer* clone();

private:
};


class CheapCustomer : public Customer {
public:
    CheapCustomer(std::string name, int id);
    std::vector<int> order(const std::vector<Dish> &menu);
    std::string toString() const;
    virtual ~CheapCustomer() = default;
    virtual CheapCustomer* clone();

private:
    bool _ordered;
};


class SpicyCustomer : public Customer {
public:
    SpicyCustomer(std::string name, int id);
    std::vector<int> order(const std::vector<Dish> &menu);
    std::string toString() const;
    virtual ~SpicyCustomer() = default;
    virtual SpicyCustomer* clone();

private:
    bool _ordered;
};


class AlchoholicCustomer : public Customer {
public:
    AlchoholicCustomer(std::string name, int id);
    std::vector<int> order(const std::vector<Dish> &menu);
    std::string toString() const;
    virtual ~AlchoholicCustomer() = default;
    virtual AlchoholicCustomer* clone();

private:
    bool _ordered;
    bool _canOrder;
    int _alcPrice;
    int _alcId;
    int findNextAlcoholicIndex(const std::vector<Dish> &menu);
};


#endif