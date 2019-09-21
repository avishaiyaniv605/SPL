//
// Created by Avishai yaniv on 2018-12-24.

#ifndef CLIENT_MESSAGEENCDEC_H
#define CLIENT_MESSAGEENCDEC_H

#include <vector>
#include <boost/lexical_cast.hpp>


/**
 * Class used to encode and decode the data coming from user or server.
 */
class MessageEncDec{
public:
    //--- Constructor ---
    MessageEncDec();

    //--- Methods ---
    std::string decodeNextByte(char nextByte);
    std::vector<char> encode(std::string message);

private:
    //--- Methods ---
    /**
     * Receiving first word and deciding accodringly how to act.
     * @param firstWord
     */
    void parseMessage(std::string firstWord);

    /**
     * encoding register message received from user.
     * @param toRegister
     */
    void parseRegister_Login(bool toRegister);

    /**
     *encoding logout message received from user.
     */
    void parseLogout();

    /**
    *encoding follow message received from user.
    */
    void parseFollow();

    /**
     *encoding post message received from user.
     */
    void parsePost();

    /**
     *encoding pm message received from user.
     */
    void parsePM();

    /**
     *encoding userlist message received from user.
     */
    void parseUserList();

    /**
     *encoding stat message received from user.
     */
    void parseStat();

    /**
     * in class method used to get the first word out form a string and remove that word from the string.
     * @param strToShorten
     * @return
     */
    std::string getNextWordAndShrink(std::string& strToShorten);
    void removeFirst();

    /**
     * Method used to convert bytes into short for decoding purposes.
     * @param bytesArr
     * @return
     */
    short bytesToShort(std::vector<char> bytesArr);

    /**
     * Method used to convert short into bytes for encoding purposes.
     * @param num
     * @return
     */
    std::vector<char> shortToBytes(short num);

    /**
     *decoding notification message received from server.
     * @param nextByte
     */
    void parseNotification(char nextByte);

    /**
     *decoding ACK message received from server.
     * @param nextByte
     */
    void parseACK(char nextByte);

    /**
     *decoding error message received from server.
     * @param nextByte
     */
    void parseError(char nextByte);

    /**
     * Receiving char after char and deciding accodringly how to act.
     * @param nextByte
     */
    void parseMessage(char nextByte);

    /**
     * in class method used to find if a message received from server is pm or post message.
     * @param byte
     */
    void findKind(char byte);

    /**
     * method parsing the ack's that need to be answered in a unique way.
     * @param cmd
     * @param nextByte
     */
    void parseOptional(char cmd, char nextByte);

    //--- Attributes ---
    std::string  _messageFromUser, _messageFromServer,  _strDelimiter;
    char _delimiter;
    std::vector<char> _opCode, _messageToSend_Bytes, _bytes;
    short _shortOpCode;
    bool _firstDetail;
    int _numOfDelimiters, _lastIndex;

};

#endif //CLIENT_MESSAGEENCDEC_H
