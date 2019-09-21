#include <iostream>
#include "../include/Dish.h"

Dish::Dish(int d_id, std::string d_name, int d_price, DishType d_type) : id(d_id), name(d_name), price(d_price), type(d_type) {};

int Dish::getId() const
{
    return id;
};

std::string Dish::getName() const
{
    return name;
};

int Dish::getPrice() const
{
    return price;
};

DishType Dish::getType() const
{
    return type;
};

std::string Dish::toString() const
{
    std::string ans (name + " ");
    if (type == DishType::VEG)
        ans.append("VEG ");
    if (type == DishType::SPC)
        ans.append("SPC ");
    if (type == DishType::BVG)
        ans.append("BVG ");
    if (type == DishType::ALC)
        ans.append("ALC ");
    ans.append(std::to_string(price));
    return ans;

};

Dish::Dish(const Dish& other):id(other.id), name(other.name), price(other.price), type(other.type) {

};

