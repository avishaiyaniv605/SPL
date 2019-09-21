//
// Created by Avishai yaniv on 2018-12-29.
//

#include <stdlib.h>
#include "../include/Client.h"
#include <thread>
#include <iostream>

/**
 * Class used to initiate the client side of the program.
 * @param argc
 * @param argv
 * @return
 */
int main (int argc, char **argv) {

    //Receiving connection details.

    std::string host = argv[1];
    short port = atoi(argv[2]);


//    std::string host = "127.0.0.1";
//    short port = 7777;

    Client client(host, port);

    if (!client.getConnectionHandler().connect()) {
        std::cerr << "Cannot connect to " << host << ":" << port << std::endl;
        return 1;
    }

    //Initiating Threads.
    std::thread thFromUser (&Client::userReader, &client); //Thread responsible for reading from user keyboard.
    std::thread thFromServer (&Client::serverReader, &client);//Thread responsible for reading server via socket.
    thFromUser.join(); //main thread is blocked until we are done reading form both sources.
    thFromServer.join();

    client.getConnectionHandler().close();
    return 0;
}
