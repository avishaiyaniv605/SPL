//
// Created by Avishai yaniv on 2018-12-24.
//

#include <string>
#include "../include/MessageEncDec.h"

MessageEncDec::MessageEncDec() :
        _messageFromUser(""),
        _messageFromServer(""),
        _strDelimiter("\0"),
        _delimiter('\0'),
        _opCode(std::vector<char>()),
        _messageToSend_Bytes(std::vector<char>()),
        _bytes(std::vector<char>()),
        _shortOpCode(-1),
        _firstDetail(true),
        _numOfDelimiters(-1),
        _lastIndex(-1)
        {}

void MessageEncDec::parseMessage(std::string firstWord) {
    if (firstWord == "REGISTER") {
        parseRegister_Login(true);
    } else if (firstWord == "LOGIN") {
        parseRegister_Login(false);
    } else if (firstWord == "LOGOUT") {
        parseLogout();
    } else if (firstWord == "FOLLOW") {
        parseFollow();
    } else if (firstWord == "POST") {
        parsePost();
    } else if (firstWord == "PM") {
        parsePM();
    } else if (firstWord == "USERLIST") {
        parseUserList();
    } else if (firstWord == "STAT") {
        parseStat();
    }
}

void MessageEncDec::parseMessage(char nextByte) {
    switch (_shortOpCode) {
        case 9: {
            parseNotification(nextByte);
            break;
        }
        case 10: {
            parseACK(nextByte);
            break;
        }
        case 11: {
            parseError(nextByte);
            break;
        }
    }
}

std::vector<char> MessageEncDec::encode(std::string message) {
    _messageToSend_Bytes.clear();
    _opCode.clear();
    _bytes.clear();
    std::string firstWord = message.substr(0, message.find(" "));
    _messageFromUser = message;
    parseMessage(firstWord);
    return _messageToSend_Bytes;
}

std::string MessageEncDec::decodeNextByte(char nextByte) {
    if (_opCode.size() < 2) {
        _opCode.push_back(nextByte);
        if (_opCode.size() == 2) {
            _bytes.clear();
            _lastIndex = 0;
            _shortOpCode = bytesToShort(_opCode);
            _numOfDelimiters = -1;
            _messageFromServer = "";
            _firstDetail = true;
        }
    } else {
        parseMessage(nextByte);
        if (nextByte == _delimiter) {
            _numOfDelimiters--;
            if (_numOfDelimiters == 0) {
                _opCode.clear();
                _messageToSend_Bytes.clear();
                return _messageFromServer;
            }
        }
    }
    return "";
}

void MessageEncDec::parseRegister_Login(bool toRegister) {
    removeFirst();
    if (toRegister) {
        _shortOpCode = 1;
    } else {
        _shortOpCode = 2;
    }
    _opCode = shortToBytes(_shortOpCode); //opcode
    std::copy(_opCode.begin(), _opCode.end(), std::back_inserter(_messageToSend_Bytes));
    _opCode.clear();

    std::string userName = getNextWordAndShrink(_messageFromUser); //username
    std::vector<char> tmpVec(userName.length());
    std::copy(userName.c_str(), userName.c_str() + userName.length(), tmpVec.begin());
    _messageToSend_Bytes.insert(std::end(_messageToSend_Bytes), std::begin(tmpVec), std::end(tmpVec));
    _messageToSend_Bytes.push_back(_delimiter);

    std::string password = getNextWordAndShrink(_messageFromUser); //password
    tmpVec = std::vector<char>(password.length());
    std::copy(password.c_str(), password.c_str() + password.length(), tmpVec.begin());
    _messageToSend_Bytes.insert(std::end(_messageToSend_Bytes), std::begin(tmpVec), std::end(tmpVec));
    _messageToSend_Bytes.push_back(_delimiter);

}

void MessageEncDec::parseLogout() {
    _shortOpCode = 3;
    _opCode = shortToBytes(_shortOpCode);
    std::copy(_opCode.begin(), _opCode.end(), std::back_inserter(_messageToSend_Bytes));
    _opCode.clear();
}

void MessageEncDec::parseFollow() {
    removeFirst();
    _shortOpCode = 4;
    _opCode = shortToBytes(_shortOpCode);
    std::copy(_opCode.begin(), _opCode.end(), std::back_inserter(_messageToSend_Bytes));
    _opCode.clear();

    std::string followUnfollow = getNextWordAndShrink(_messageFromUser);
    _messageToSend_Bytes.push_back(followUnfollow.at(0));

    std::string numOfUsers = getNextWordAndShrink(_messageFromUser); //num of users
    short numOfUsers_short = boost::lexical_cast<short>(numOfUsers);
    std::vector<char> numOfUsers_vector = shortToBytes(numOfUsers_short);
    _messageToSend_Bytes.insert(std::end(_messageToSend_Bytes), std::begin(numOfUsers_vector),
                                std::end(numOfUsers_vector));

    int size = std::stoi(numOfUsers); //adding users
    for (int i = 0; i < size; ++i) {
        std::string nextUser = getNextWordAndShrink(_messageFromUser);
        std::vector<char> tmpVec(nextUser.length());
        std::copy(nextUser.c_str(), nextUser.c_str() + nextUser.length(), tmpVec.begin());
        _messageToSend_Bytes.insert(std::end(_messageToSend_Bytes), std::begin(tmpVec), std::end(tmpVec));
        _messageToSend_Bytes.push_back(_delimiter);
    }
}

void MessageEncDec::parsePost() {
    removeFirst();
    _shortOpCode = 5;
    _opCode = shortToBytes(_shortOpCode);
    std::copy(_opCode.begin(), _opCode.end(), std::back_inserter(_messageToSend_Bytes));
    _opCode.clear();

    std::vector<char> tmpVec(_messageFromUser.length());
    std::copy(_messageFromUser.c_str(), _messageFromUser.c_str() + _messageFromUser.length(), tmpVec.begin());
    _messageToSend_Bytes.insert(std::end(_messageToSend_Bytes), std::begin(tmpVec), std::end(tmpVec));
    _messageToSend_Bytes.push_back(_delimiter);
}

void MessageEncDec::parsePM() {
    removeFirst();
    _shortOpCode = 6;
    _opCode = shortToBytes(_shortOpCode);
    std::copy(_opCode.begin(), _opCode.end(), std::back_inserter(_messageToSend_Bytes));
    _opCode.clear();

    std::string userName = getNextWordAndShrink(_messageFromUser);
    std::vector<char> tmpVec(userName.length());
    std::copy(userName.c_str(), userName.c_str() + userName.length(), tmpVec.begin());
    _messageToSend_Bytes.insert(std::end(_messageToSend_Bytes), std::begin(tmpVec), std::end(tmpVec));
    _messageToSend_Bytes.push_back(_delimiter);

    tmpVec = std::vector<char>(_messageFromUser.length());
    std::copy(_messageFromUser.c_str(), _messageFromUser.c_str() + _messageFromUser.length(), tmpVec.begin());
    _messageToSend_Bytes.insert(std::end(_messageToSend_Bytes), std::begin(tmpVec), std::end(tmpVec));
    _messageToSend_Bytes.push_back(_delimiter);
}

void MessageEncDec::parseUserList() {
    _shortOpCode = 7;
    _opCode = shortToBytes(_shortOpCode);
    std::copy(_opCode.begin(), _opCode.end(), std::back_inserter(_messageToSend_Bytes));
    _messageToSend_Bytes.push_back(_delimiter);
    _opCode.clear();
}

void MessageEncDec::parseStat() {
    removeFirst();
    _shortOpCode = 8;
    _opCode = shortToBytes(_shortOpCode);
    std::copy(_opCode.begin(), _opCode.end(), std::back_inserter(_messageToSend_Bytes));
    _opCode.clear();

    std::string userName = getNextWordAndShrink(_messageFromUser);
    std::vector<char> tmpVec(userName.length());
    std::copy(userName.c_str(), userName.c_str() + userName.length(), tmpVec.begin());
    _messageToSend_Bytes.insert(std::end(_messageToSend_Bytes), std::begin(tmpVec), std::end(tmpVec));
    _messageToSend_Bytes.push_back(_delimiter);
}

void MessageEncDec::parseNotification(char nextByte) {
    if (_firstDetail) {
        _messageFromServer = "NOTIFICATION ";
        findKind(nextByte);
    } else if (nextByte != _delimiter) {
        _bytes.push_back(nextByte);
    } else {
        std::string nextWord(_bytes.begin(), _bytes.end());
        _messageFromServer += " " + nextWord;
        _bytes.clear();
    }

}

void MessageEncDec::findKind(char byte) {
    if (byte == 0) {
        _numOfDelimiters = 3;
        _messageFromServer += "PM";
    } else {
        _messageFromServer += "Public";
        _numOfDelimiters = 2;
    }
    _firstDetail = false;
    _bytes.clear();
}

void MessageEncDec::parseACK(char nextByte) {
    if (_firstDetail) {
        _bytes.push_back(nextByte);
        if (_bytes.size() == 2) {
            _messageFromServer = "ACK";
            _firstDetail = false;
            short messageOpCode = bytesToShort(_bytes);
            _messageFromServer += " " + std::to_string(messageOpCode);
            _bytes.clear();
        }
    } else {
        int cmd = _messageFromServer.at(4) - '0';
        parseOptional(cmd, nextByte);
    }

}

void MessageEncDec::parseOptional(char cmd, char nextByte) {
    switch (cmd) {
        case 4: {// follow
            if (_lastIndex < 2) {
                _bytes.push_back(nextByte);
                _lastIndex++;
                if (_bytes.size() == 2) {
                    short numOfUsers = bytesToShort(_bytes);
                    _numOfDelimiters = numOfUsers;
                    _messageFromServer += " " + std::to_string(numOfUsers);
                    _lastIndex = 2;
                }
            } else if (nextByte != _delimiter) {
                _bytes.push_back(nextByte);
            }
            else{
                std::string nextWord(_bytes.begin() + _lastIndex, _bytes.end());
                _messageFromServer += " " + nextWord;
                _lastIndex = _bytes.size();
            }
            break;
        }
        case 7: { // userList. operates in the exact same way as follow parsing so it is send to that parser.
            parseOptional(4, nextByte);
            break;
        }
        case 8: {// stat
            _bytes.push_back(nextByte);
            if (_bytes.size() == 2) {
                short numOfUsers = bytesToShort(_bytes);
                _messageFromServer += " " + std::to_string(numOfUsers);
                _bytes.clear();
            }
            else{
                int numOfSpaces = count(_messageFromServer.begin(),_messageFromServer.end(),' ');
                if(numOfSpaces == 4){
                    _numOfDelimiters = 1;
                }
            }
        }
            break;

        default:
            _numOfDelimiters = 1;
    }
}

void MessageEncDec::parseError(char nextByte) {
    if (_firstDetail) {
        _bytes.push_back(nextByte);
        if (_bytes.size() == 2) {
            _messageFromServer = "ERROR";
            _numOfDelimiters = 1;
            _firstDetail = false;
            short messageOpCode = bytesToShort(_bytes);
            _messageFromServer += " " + std::to_string(messageOpCode);
            _bytes.clear();
        }
    }
}

std::string MessageEncDec::getNextWordAndShrink(std::string &strToShorten) {
    size_t found = strToShorten.find(' '); //if there is only one word.
    if (found != std::string::npos) {
        std::string firstWord = strToShorten.substr(0, strToShorten.find(' '));
        removeFirst();
        return firstWord;
    }
    return strToShorten;
}

void MessageEncDec::removeFirst() {
    _messageFromUser = _messageFromUser.substr(_messageFromUser.find(' ') + 1, _messageFromUser.length());
}

short MessageEncDec::bytesToShort(std::vector<char> bytesArr) {
    short result = (short) ((bytesArr[0] & 0xff) << 8);
    result += (short) (bytesArr[1] & 0xff);
    return result;
}

std::vector<char> MessageEncDec::shortToBytes(short num) {
    std::vector<char> bytesArr(2);
    bytesArr[0] = ((num >> 8) & 0xFF);
    bytesArr[1] = (num & 0xFF);
    return bytesArr;
}






