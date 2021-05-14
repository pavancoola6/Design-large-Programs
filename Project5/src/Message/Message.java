/**
 * Authors: Pavan Singara, Kunj Bhavsar and Anmol Singh Gill
 * CS351L : Final Project
 */

/**
 * This class represents the message objects that will be send back and forth
 * in our distributed auction project. Messages are sent between a client and
 * a server and will be processed by either side of the communication. The
 * messages should hold certain information such as its destination, its
 * source and the actual message content. Note that this class should implement
 * serializable as it will be passed through ObjectInputStream and
 * ObjectOutputStream.
 *
 */

package Message;

import java.io.Serializable;

public class Message implements Serializable {

    /* Actual message, should be instanceof String */
    public String message;
    /* String to represent where the message came from */
    public String source;
    /* String Auction House Address */
    public String AHAddress;
    /* String Auction House Port */
    public String AHPort;
    /* Int representing the port on which the message is passing */
    public int socketPort;

    /**
     * Message constructor
     * This constructor simply sets up and initializes the above mentioned
     * variables.
     * @param source String representing the source of the message
     * @param message String representing the actual message being sent/received
     * @param AHAddress Only applies to Auction Houses - helps to keep track
     *                  of Auction Houses addresses
     * @param AHPort Only applies to Auction Houses - used to keep track of
     *               Auction House's port
     * @param socketPort integer representing the port on which the message
     *                   is being sent.
     */
    public Message(String source, String message, String AHAddress,
                   String AHPort, int socketPort) {
        /* Initialize source with passed in argument */
        this.source = source;
        /* Initialize message with passed in argument */
        this.message = message;
        /* Initialize AHAddress with passed in argument */
        this.AHAddress = AHAddress;
        /* Initialize AHPort with passed in argument */
        this.AHPort = AHPort;
        /* Initialize socketPort with passed in argument */
        this.socketPort = socketPort;
    }
}
