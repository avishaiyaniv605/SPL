//
// Created by Avishai yaniv on 2018-12-29.
//

#include "../include/Client.h"

Client::Client(std::string host, short port) :
        _bufsize(1024),
        _connectionHandler(host, port),
        _messageEnc(),
        _messageDec(),
        _shouldTerminate(false),
        _messageBytes(),
        _message(""),
        _mutex(),
        _conditionVariable(),
        _lock(_mutex)
{}

void Client::userReader() {
    std::string logoutCheck;    // checks whether the input is logout
    while (!this->_shouldTerminate) {
        char _buf[_bufsize];
        std::cin.getline(_buf, _bufsize);
        std::string line(_buf);

        _messageBytes = _messageEnc.encode(line);
        int len(_messageBytes.size());
        char bytes[len];
        for (int i = 0 ;i < len; i++)
            bytes[i] = _messageBytes.at(i);

        if (!_connectionHandler.sendBytes(bytes, len)) {
            std::cout << "Disconnected. Exiting...\n" << std::endl;
            this->_shouldTerminate = true;
            break;
        }

        if (line == "LOGOUT") {
            _conditionVariable.wait(_lock);
        }
    }
}

void Client::serverReader() {
    while (!this->_shouldTerminate) {
        char ch;

        while (!_connectionHandler.getBytes(&ch, 1)) {} // blocking until answer from server is received
        _message = _messageDec.decodeNextByte(ch);

        if (_message == "ACK 3")
            _shouldTerminate = true;

        if (!_message.empty()) {
            std::cout << _message << std::endl;
        }

        if (_message == "ACK 3" || _message == "ERROR 3") {
            _conditionVariable.notify_all();
        }
    }
}

ConnectionHandler &Client::getConnectionHandler() {
    return this->_connectionHandler;
}

Client::~Client() {
    this->_connectionHandler.close();
}

