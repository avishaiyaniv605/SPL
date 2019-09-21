//
// Created by Avishai yaniv on 2018-12-29.
//

#ifndef CLIENT_CLIENT_H
#define CLIENT_CLIENT_H

#include <future>
#include "connectionHandler.h"

/**
 * Class representing connection to the server.
 * getting information from the user, encoding it and sending it to server.
 * getting information from the server, decoding it and displaying result to user.
 */
class Client {
public:
    /**
     * Constructor, has connection details.
     * @param host
     * @param port
     */
    Client(std::string host, short port);

    /**
     * Destructor, closing the connection before destroying object.
     */
    ~Client();

    /**
     * While client is connected, this method reads data from the server,decoding it using decoder and displaying result.
     */
    void serverReader();

    /**
    * While client is connected, this method reads data from the user,encoding it using encoder and sending it to the server.
    */
    void userReader();
    /**
     * getter for the connection to the server.
     * @return
     */
    ConnectionHandler &getConnectionHandler();

private:
    const short _bufsize;
    ConnectionHandler _connectionHandler;
    MessageEncDec _messageEnc, _messageDec;
    bool _shouldTerminate;
    std::vector<char> _messageBytes;
    std::string _message;
    std::mutex _mutex;
    std::condition_variable _conditionVariable;
    std::unique_lock<std::mutex> _lock;
};

#endif //CLIENT_CLIENT_H
