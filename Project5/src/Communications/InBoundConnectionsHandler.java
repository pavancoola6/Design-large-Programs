/**
 * Authors: Pavan Singara, Kunj Bhavsar and Anmol Singh Gill
 * CS351L : Final Project
 */

/**
 * InBoundConnectionsHandler Class
 */

package Communications;

import AuctionHouse.AuctionHouse;
import Bank.Bank;
import Message.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * InBoundConnectionsHandler class
 * This class will handle all of the sockets connections server side
 * connections.
 */
public class InBoundConnectionsHandler implements Runnable {
    /* Current socket */
    private Socket socket;
    /* Instance of the Bank */
    private Bank bank;
    /* Instance of the Auction House */
    private AuctionHouse auctionHouse;
    /* Flag indicating if the current handler is called by the bank */
    private boolean isBank;
    /* String representing the source of where a message comes from */
    private String source;
    /* String representing output */
    private String initOut;
    /* ObjectOutputStream representing output to client */
    private ObjectOutputStream outputToClient;
    /* ObjectInputStream representing input from client */
    private ObjectInputStream inputFromClient;

    /**
     * InBoundConnectionsHandler constructor
     *Takes in a socket and an object as parameter. Will set up the above
     * defined vairables and initialize them.
     * @param socket socket on which the connection is set up
     * @param ref object - instance of the class calling the
     *            InBoundConnectionHandler
     * @throws IOException handling IOException
     */
    public InBoundConnectionsHandler(Socket socket, Object ref)
            throws IOException {
        /* Socket on which the connection is set up */
        this.socket = socket;
        /* Set isBank to false initially */
        isBank = false;
        /* Set the variables based on the passed in Object */
        variableSetter(ref);
        /* Initialize a new ObjectOutputStream */
        outputToClient = new ObjectOutputStream(socket.getOutputStream());
        /* Initialize a new ObjectInputStream */
        inputFromClient = new ObjectInputStream(socket.getInputStream());
    }

    /**
     * variableSetter method
     * Two classes could potentially instantiate an
     * InboundConnectionsHandler: Bank and AuctionHouse (the two classes that
     * will be used as a server side of communications). However, different
     * variables should be used based if the InboundConnectionsHandler is
     * instantiated by a Bank or by and Auction House. This method checks
     * which class called InBoundConnectionsHandler and set up variables
     * accordingly.
     * @param ref Object that is passed in as reference (could be Bank or
     *            AuctionHouse)
     */
    private void variableSetter(Object ref){
        /* If ref is a Bank object */
        if (ref instanceof Bank){
            bank = (Bank) ref;
            source = "bank";
            initOut = "Welcome, you have successfully established a" +
                    " connection with the Bank";
            isBank = true;
            /* if ref is an instance of AuctionHouse */
        } else if(ref instanceof AuctionHouse){
            auctionHouse = (AuctionHouse) ref;
            source = "auction_house";
            initOut = "Welcome, you have successfully established a" +
                    " connection with the Auction House.";
        }
    }

    /**
     * sendMessage method
     *
     * Use this method to send messages to an agent or an auction house
     * without previous request.
     * @param message string corresponding to the message to be sent.
     * @throws IOException throwing IO exception
     */
    public void sendMessage(String message) throws IOException {
        outputToClient.writeObject(new Message(source,
                message, null, null,
                0));
    }

    /**
     * run Method
     * Note that the InBoundConnectionsHandler is basically just a simple
     * thread that will infinitely wait for incoming messages from its client
     * side of the communication. Whenever a message is received, we will
     * send that message to an appropriate method in our instance of Bank or
     * AuctionHouse. This method will parse the message and return a
     * response string based on the parsed message
     */
    @Override
    public void run() {
        /* Set up a bridge between the client and the server */
        String outputLine;
        /* Process the first received message here */
        outputLine = "" + socket.getPort();
        try {
            outputToClient.writeObject(new Message(source,
                    outputLine, null
                    ,null, 0));
            outputLine = initOut;
            outputToClient.writeObject(new Message(source,
                    outputLine, null
                    ,null, 0));
            /*
             * Every time the client talks to the server, the server can process
             * the message below. Here, the call backs to the Bank's or
             * AuctionHouse's "processIncomingMessage" methods
             */
            Message currentMessage;
            while ((currentMessage = (Message) inputFromClient.readObject())
                    != null) {
                /* Call processMessage below from either bank or AH */
                outputLine = (isBank? bank.processIncomingMessage(currentMessage
                        ,socket,
                        this):
                        auctionHouse.processIncomingMessage(currentMessage,
                                socket,this));
                outputToClient.writeObject(new Message(source, outputLine,
                        null, null, 0));
            }
            socket.close();
        } catch (IOException | ClassNotFoundException | InterruptedException e) {
            if (isBank){
                System.out.println("Connection Lost, check " +
                        "bank info for more information" );
                try {
                    bank.processIncomingMessage(new Message(
                            "bank", "delete",
                            null, null,
                            socket.getPort()),socket, this);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

        }
    }
}
