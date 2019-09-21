#include <iostream>
#include <vector>
#include "../include/Customer.h"

Customer::Customer(std::string c_name, int c_id) :  name (c_name), id(c_id) {};

std::string Customer::getName() const
{
    return name;
};

int Customer::getId() const
{
    return id;
};


// Vegetarian Customer

VegetarianCustomer::VegetarianCustomer(std::string name, int id) : Customer(name,id) {};

std::vector<int> VegetarianCustomer::order(const std::vector<Dish> &menu)   //orders the smallest id and most expensive beverage (non-alcoholic)
{
    std::vector<int> ans;
    int dishID = -1;
    int bvgPrice = -1;

    // finding dish
    for (auto dish : menu) {
        if (dish.getType() == DishType::VEG && (dishID > dish.getId() || dishID == -1)) {
            dishID = dish.getId();
        }
    }
    if (dishID != -1)
    {
        ans.push_back(dishID);
    }
    dishID = -1;
    // finding beverage
    for (auto dish : menu) {
        if (dish.getType() == DishType::BVG && (bvgPrice < dish.getPrice() || dishID == -1))
        {
            bvgPrice = dish.getPrice();
            dishID = dish.getId();
        }
    }
    if (bvgPrice != -1)
        ans.push_back(dishID);
    if (ans.size() == 2)
        return ans;
    ans.clear();
    return ans;
};

std::string VegetarianCustomer::toString() const
{
    std::string ans;
    ans.append(getName() + "," + "veg");
    return ans;
};

VegetarianCustomer* VegetarianCustomer::clone(){
    return new VegetarianCustomer(*this);
};


// Cheap Customer

CheapCustomer::CheapCustomer(std::string name, int id) : Customer(name,id) , _ordered(false) {};

std::vector<int> CheapCustomer::order(const std::vector<Dish> &menu)    //orders cheapest dish, only once.
{
    std::vector<int> ans;
    if (_ordered)
        return ans;
    int cheapestID = -1;

    int price = -1;
    for (auto dish : menu) {
        if (price > dish.getPrice() || cheapestID == -1)
        {
            price = dish.getPrice();
            cheapestID = dish.getId();
        }
    }
    ans.push_back(cheapestID);
    _ordered = true;
    return ans;
};

std::string CheapCustomer::toString() const
{
    std::string ans;
    ans.append(getName() + "," + "chp");
    return ans;
};

CheapCustomer* CheapCustomer::clone(){
    return new CheapCustomer(*this);
};


// Spicy Customer

SpicyCustomer::SpicyCustomer(std::string name, int id) : Customer(name,id), _ordered(false){};

std::vector<int> SpicyCustomer::order(const std::vector<Dish> &menu)    //orders the most expensive spicy dish, and then cheapest non-alcoholic beverage
{
    std::vector<int> ans;
    int orderID = -1;
    int price = -1;

    //orders dish
    if (!_ordered) {
        for (auto dish: menu) {
            if (dish.getType() == DishType::SPC && (price < dish.getPrice() || orderID == -1))
            {
                price = dish.getPrice();
                orderID = dish.getId();
                _ordered = true;
            }
        }
    }

    //orders beverage
    else if (_ordered)
    {
        for (auto dish : menu) {
            if (dish.getType() == DishType::BVG && (price > dish.getPrice() || orderID == -1))
            {
                orderID= dish.getId();
                price= dish.getPrice();
            }


        }
    }
    ans.push_back(orderID);
    return ans;
};

std::string SpicyCustomer::toString() const
{
    std::string ans;
    ans.append(getName() + "," + "spc");
    return ans;
};

SpicyCustomer* SpicyCustomer::clone()
{
    return new SpicyCustomer(*this);
};


// Alcoholic Customer

AlchoholicCustomer::AlchoholicCustomer(std::string name, int id) : Customer(name,id), _ordered(false), _canOrder(true), _alcPrice(-1), _alcId(-1)
{};

std::vector<int> AlchoholicCustomer::order(const std::vector<Dish> &menu)   //orders cheapest alcoholic beverage and then next expensive till there are no more
{
    std::vector<int> ans;
    int orderID = -1;
    int orderPrice = -1;
    //first order
    if (!_ordered) {
        for (auto dish : menu) {
            if (dish.getType() == DishType::ALC && (orderPrice > dish.getPrice() || orderID == -1))
            {
                orderID = dish.getId();
                orderPrice = dish.getPrice();
            }
        }
        _alcPrice = orderPrice;
        _alcId = orderID;
        _ordered = true;
        ans.push_back(orderID);
    }

        //further orders
    else if (_canOrder)
    {
        orderID = findNextAlcoholicIndex(menu);
        if (orderID == -1) {
            _canOrder = false;
        }
        else {
            ans.push_back(orderID);
        }
    }
    return ans;
};

std::string AlchoholicCustomer::toString() const
{
    std::string ans;
    ans.append(getName() + "," + "alc");
    return ans;
};

int AlchoholicCustomer::findNextAlcoholicIndex(const std::vector<Dish> &menu)
{
    int nextPrice = _alcPrice;
    int nextId = -1;
    bool found = false;
    for (auto dish : menu)
    {
        int currID = dish.getId();
        int currPrice = dish.getPrice();
        if (!(dish.getType() == DishType::ALC))
        {
            continue;
        }
        if (_alcPrice == currPrice && currID > _alcId)
        {
            _alcId = currID;
            return currID;
        }
        if (!found && nextPrice < currPrice)
        {
            nextId = currID;
            nextPrice = currPrice;
            found = true;
        }
        else if (found && nextPrice > currPrice && currPrice > _alcPrice)
        {
            nextId = currID;
            nextPrice = currPrice;
        }
    }
    if(nextId != -1){
        _alcPrice = nextPrice;
        _alcId = nextId;
    }
    return nextId;
};

AlchoholicCustomer* AlchoholicCustomer::clone()
{
    return new AlchoholicCustomer(*this);
};