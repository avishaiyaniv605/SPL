#include <iostream>
#include <fstream>
#include <vector>
#include <sstream>
#include "../include/Restaurant.h"


Restaurant::Restaurant(const std::string &configFilePath):
        open(true),tables(std::vector<Table*>()),menu(std::vector<Dish>()),actionsLog( std::vector<BaseAction*>())
{
    readFile(configFilePath);
};


void Restaurant::start()
{
    std::string userChoice;
    std::cout << "Restaurant is now open!" << std::endl;
    std::string firstWord;
    BaseAction* currAction;
    int nextId = 0;
    do{
        std::string userInput;
        std::getline(std::cin, userInput);
        //removes unnecessary spaces
        userInput.erase(userInput.find_last_not_of(" \n\r\t")+1);
        firstWord = userInput.substr(0, userInput.find(' '));
        std::vector<std::string> tableInstructions = parseInput(userInput);

        if (firstWord == "open") {
            nextId = openTable(tableInstructions,nextId);
            //if an error occurred, reduce the number of illegal customers that were added
            if (actionsLog.at(actionsLog.size()-1)->getStatus() == ActionStatus::ERROR)
                nextId =nextId - (tableInstructions.size() - 2);
        }
        else if(firstWord == "order"){
            std::string::size_type sz;
            int orderTable = std::stoi (tableInstructions.at(1),&sz);
            currAction = new Order(orderTable);
            currAction->act(*this);
            actionsLog.push_back(currAction);
        }
        else if(firstWord == "move"){
            std::string::size_type sz;
            int srcTable = std::stoi (tableInstructions.at(1),&sz);
            int destTable = std::stoi (tableInstructions.at(2),&sz);
            int customerId = std::stoi (tableInstructions.at(3),&sz);

            currAction = new MoveCustomer(srcTable,destTable,customerId);
            currAction->act(*this);
            actionsLog.push_back(currAction);

        }
        else if(firstWord == "close"){
            std::string::size_type sz;
            int table = std::stoi (tableInstructions.at(1),&sz);
            currAction = new Close(table);
            currAction->act(*this);
            actionsLog.push_back(currAction);
        }
        else if(firstWord == "closeall"){
            currAction = new CloseAll();
            currAction->act(*this);
            actionsLog.push_back(currAction);
        }
        else if(firstWord == "menu"){
            currAction = new PrintMenu();
            currAction->act(*this);
            actionsLog.push_back(currAction);
        }
        else if(firstWord == "status"){
            std::string::size_type sz;
            int table = std::stoi (tableInstructions.at(1),&sz);
            currAction = new PrintTableStatus(table);
            currAction->act(*this);
            actionsLog.push_back(currAction);
        }
        else if(firstWord == "log"){
            currAction = new PrintActionsLog();
            currAction->act(*this);
            actionsLog.push_back(currAction);
        }
        else if(firstWord == "backup"){
            currAction = new BackupRestaurant();
            currAction->act(*this);
            actionsLog.push_back(currAction);

        }
        else if(firstWord == "restore"){
            currAction = new RestoreResturant();
            currAction->act(*this);
            actionsLog.push_back(currAction);
        }
        else { // bad input
            std::cout << "wrong input" << std::endl;
        }
    }
    while(firstWord != "closeall");
    clearLogs();
    clearTables();

};

int Restaurant::openTable(std::vector<std::string>  tableInstructions,int nextId)
{
    int tableId = std::stoi(tableInstructions.at(1));
    std::vector<Customer *> customersToAdd;
    std::string name;
    std::string type;
    int tableIstructionsLength = tableInstructions.size();
    for (int i = 2; i < tableIstructionsLength; i++) {
        std::string pair = tableInstructions.at(i);
        int indexOf = pair.find(',');
        name = pair.substr(0,indexOf);
        type = pair.substr(indexOf +1, pair.length());
        Customer *toAdd;
        if (type == "veg") {
            toAdd= new VegetarianCustomer(name, nextId);
        } else if (type == "spc") {
            toAdd = new SpicyCustomer(name, nextId);
        } else if (type == "chp") {
            toAdd = new CheapCustomer(name, nextId);
        } else { //alc
            toAdd = new AlchoholicCustomer(name, nextId);
        }
        nextId++;
        customersToAdd.push_back(toAdd);
    }
    OpenTable* op = new OpenTable(tableId, customersToAdd);
    op->act(*this);
    actionsLog.push_back(op);
    return nextId;
};

const std::vector<std::string> Restaurant::parseInput(std::string &str) {
    std::vector<std::string> v;
    int i = 0;
    int strLength = str.length();
    while(i < strLength) {
        if(str.at(i) == *" "){
            std::string sub = str.substr(0, i);
            v.push_back(sub);
            str.erase(0,i + 1);
            i = 0;
        }
        i++;
        strLength = str.length();
    }
    v.push_back(str);
    return v;
};

int Restaurant::getNumOfTables() const
{
    return tables.size();
};

Table* Restaurant::getTable(int ind)
{
    int numOfTables = tables.size();
    if (ind > numOfTables)
        return nullptr;
    return tables.at(ind);
};

const std::vector<BaseAction*> & Restaurant::getActionsLog() const
{
    return actionsLog;
};

std::vector<Dish>& Restaurant::getMenu()
{
    return menu;
};

void Restaurant::readFile(const std::string &configFilePath)
{
    std::ifstream infile(configFilePath);

    if (!infile.is_open())
        return;

    std::string currLine;
    int caseNumber = 1;

    while(std::getline(infile,currLine))
    {
        caseNumber = parseLine(currLine,caseNumber);
    }
    infile.close();

};

int Restaurant::parseLine(const std::string &currLine, int caseNumber)
{
    if (currLine.empty() || currLine.at(0) == '#')
    {
        return caseNumber;
    }
    //next line parsing
    switch (caseNumber)
    {
        case 1:     // number of tables
        {
            std::string::size_type sz;
            int size = std::stoi (currLine,&sz);
            tables.reserve(size);
            caseNumber++;

            break;
        }
        case 2:     // tables capacity
        {
            std::stringstream ss(currLine);
            int i;
            while (ss >> i)
            {
                tables.push_back(new Table(i));
                if (ss.peek() == ',')
                    ss.ignore();
            }
            caseNumber++;

            break;
        }
        case 3:     // dishes
        {
            insertNewDish(currLine);

            break;
        }
        default: return caseNumber;
    }

    return caseNumber;
};

DishType Restaurant::parseDishType (const std::string& dish)
{
    if (dish == "VEG")
        return DishType::VEG;
    else if (dish == "SPC")
        return DishType::SPC;
    else if (dish == "BVG")
        return DishType::BVG;
    else //ALC
        return DishType::ALC;
};

void Restaurant::insertNewDish(const std::string& currLine)
{
    std::string dishName;
    int dishPrice = -1;
    DishType dishType;

    int currDetail = 1;
    int lastIndex = -1;

    int currLineSize = currLine.size();
    for (int i = 0; i < currLineSize && currDetail != 3; ++i)
    {
        if (currLine.at(i) != ',')
            continue;
        if (currDetail == 1)    //dish name found
        {
            dishName = currLine.substr(0,i);
            lastIndex = i;
            currDetail++;
        }
        else if (currDetail == 2)   //dish type and price found
        {
            dishType = parseDishType(currLine.substr(lastIndex+1,3));
            std::string::size_type sz;
            dishPrice = std::stoi (currLine.substr(i+1),&sz);
        }

    }
    if (dishPrice != -1)
        menu.push_back(Dish(menu.size(),dishName,dishPrice,dishType));
};

Restaurant::~Restaurant()
{
    clearLogs();
    clearTables();
};

Restaurant::Restaurant(const Restaurant& other):
        open(other.open),tables(std::vector<Table*>()),menu(other.menu),actionsLog(std::vector<BaseAction*>())
{
    int size = other.tables.size();
    for (int i = 0; i < size; ++i) {
        tables.push_back(other.tables.at(i)->clone());
    }
    size = other.actionsLog.size();
    for (int i = 0; i < size; ++i) {
        BaseAction* currAction = other.actionsLog.at(i)->clone();
        // if instanceof openTable , set customers
        if (dynamic_cast<OpenTable*>(currAction)){
            ((OpenTable*)currAction)->setCustomers(tables);
        }
        actionsLog.push_back(currAction);

    }
};

Restaurant& Restaurant::operator=(const Restaurant &other)
{
    if(&other == this) {
        return *this;
    }
    for (auto table : tables) {
        delete(table);
    }
    tables.clear();
    for (auto toAdd : other.tables) {
        tables.push_back(toAdd->clone());
    }
    for (auto action : actionsLog) {
        delete(action);
    }
    actionsLog.clear();
    for (auto toAdd : other.actionsLog) {
        actionsLog.push_back(toAdd->clone());
    }
    open = other.open;
    menu = std::vector<Dish>(other.menu);
    return *this;
};

Restaurant::Restaurant(Restaurant&& other):open(other.open),tables(other.tables),menu(other.menu),actionsLog(other.actionsLog)
{
    int size = other.tables.size();
    for (int i = 0; i < size; i++) {
        if (other.tables.at(i) != nullptr) {
            delete other.tables.at(i);
            other.tables.at(i) = nullptr;
        }
    }
    size = other.actionsLog.size();
    for (int i = 0; i < size; i++) {
        if (other.tables.at(i) != nullptr)
        {
            delete other.tables.at(i);
            other.actionsLog.at(i) = nullptr;
        }
    }
};

Restaurant& Restaurant::operator=(Restaurant&& other) {
    for (auto table : tables) {
        if (table != nullptr)
        {
            delete (table);
        }
    }
    tables.clear();
    for (auto toAdd : other.tables) {
        tables.push_back(toAdd->clone());
    }
    int size = other.tables.size();
    for (int i = 0; i < size; i++) {
        if (other.tables.at(i) != nullptr)
        {
            delete other.tables.at(i);
            other.tables.at(i) = nullptr;
        }
    }
    for (auto action : actionsLog) {
        if (action != nullptr)
        {
            delete (action);
        }
    }
    actionsLog.clear();
    for (auto toAdd : other.actionsLog) {
        actionsLog.push_back(toAdd->clone());
    }
    size = other.actionsLog.size();
    for (int i = 0; i < size; i++) {
        if (other.actionsLog.at(i) != nullptr)
        {
            delete other.actionsLog.at(i);
        }
        other.actionsLog.at(i) = nullptr;
    }
    return *this;
};

void Restaurant::clearLogs()
{
    for (auto log : actionsLog)
        if(log != nullptr) {
            delete log;
        }
    actionsLog.clear();
};

void Restaurant::clearTables()
{
    for (auto table : tables)
        if (table != nullptr) {
            delete table;
        }
    tables.clear();
};