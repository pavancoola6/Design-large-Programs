/**
 * Authors: Pavan Singara, Kunj Bhavsar and Anmol Singh Gill
 * CS351L : Final Project
 */

/**
 * This class represents the handler for all OutBound connections. That is,
 * this class will handle all of the client side elements of the
 * communications. This class will be used by both the Agent for its client
 * side with the Bank and the Auction House, as well as by the Auction House
 * for its client side with the Bank. The main purpose of this class is allow
 * for an Agent or an Auction House to send out messages to its connected
 * server. We have added an inner class called receiveMessages. The purpose
 * of this inner class is for a client to receive messages from a server
 * without having made a prior request for communication. That is, instead of
 * sending a request to a server, the server directly sends a message to a
 * client after a certain event occured.
 *
 */

package Communications;

import Agent.Agent;
import AuctionHouse.AuctionHouse;
import Message.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class OutBoundConnectionsHandler implements Runnable {
    /* Blocking queue for all the messages that the agent sends to the bank */
    public BlockingQueue<Message> OutGoingBlockingQueue;
    /* Socket of the bank-agent connection */
    public int bankSocketPort;
    /* Port number on which the connection is made */
    private int portNumber;
    /* Hostname of the bank */
    private String hostName;
    /* String representing the entity to which the connection is made */
    private String destination;

    private AuctionHouse auctionHouse = null;
    private Agent agent = null;

    /**
     * OutBoundConnectionsHandler Constructor
     * <p>
     * Simply initialize a new LinkedBlockingQueue for outgoing messages and
     * set the destination String.
     */
    public OutBoundConnectionsHandler(String destination) {
        OutGoingBlockingQueue = new LinkedBlockingQueue<Message>();
        this.destination = destination;
    }

    /**
     * after the Agent class has the information on the bank's hostname and
     * portnumber, set it up here as well
     *
     * @param hostName   string representing the hostname of the current
     *                   connection. This could be a name or an IP address.
     * @param portNumber integer representing the port on which the socket
     *                   will listen/connect.
     */
    public void setHostAndPort(String hostName, int portNumber) {
        this.portNumber = portNumber;
        this.hostName = hostName;
    }

    /**
     * setAuctionHouse method
     * If the current instance of the OutBoundConnectionsHandler belongs to
     * an Auction House, reference that auction house here in order to
     * interact with it.
     * @param auctionHouse referenced auction house that created the current
     *                    OutBoundConnectionsHandler
     */
    public void setAuctionHouse(AuctionHouse auctionHouse) {
        this.auctionHouse = auctionHouse;
    }

    /**
     * agent method
     * If the current instance of the OutBoundConnectionsHandler belongs to
     * an agent, reference that agent here in order to
     * interact with it.
     * @param agent referenced agent that created the current
     *                    OutBoundConnectionsHandler
     */
    public void setAgent(Agent agent) {
        this.agent = agent;
    }

    /**
     * run Method
     * When the OutBoundConnectionsHandler is initiated and its Thread is
     * started, the run method will be started. In run, the thread will
     * simply set up a communications channel with the server and infinitely
     * wait for messages to send to the server side of the communications.
     */
    @Override
    public void run() {
        /*
         * At this point, we can start the communication set up with the Bank.
         * We will pass Message objects through the sockets.
         */
        try (
                /* Set up a new socket using hostName and portNumber */
                Socket socket = new Socket(hostName, portNumber);
                /* Set up an output stream */
                ObjectOutputStream outputToServer =
                        new ObjectOutputStream(socket.getOutputStream());
                /* Set up an input stream */
                ObjectInputStream inputFromServer =
                        new ObjectInputStream(socket.getInputStream())
        ) {
            /* At first, we will want to save the port number through which
            we are connected with the bank. Note that this is a different
            number than the one given by user at configuration. The port
            number given by the user is called the "local port number". Here
            we refer to the unique port number between the bank and the agent.
            This number will be used later on as identification.
             */
            Message currentMessage = null;
            try {
                /* Read the first message from the bank - this will be the
                port number
                 */
                currentMessage = (Message) inputFromServer.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            if (agent!=null){
                System.out.println("\n\nFor more information on possible " +
                        "commands, " +
                        "you can consult the \"help\" menu at any time\n\n");
            }
            /* Save the port number */
            bankSocketPort =
                    Integer.parseInt(currentMessage.message);
            System.out.println("You are connected with the " + destination +
                    " on " +
                    "port: " +
                    bankSocketPort);
            /* Now if we are the agent-bank communication ONLY, read in the
            agent ID fro the bank
             */
            if (destination.equals("Bank") && agent != null) {
                try {
                /* Read the first message from the bank - this will be the
                port number
                 */
                    currentMessage = (Message) inputFromServer.readObject();
                    String agentID = currentMessage.message;
                    agent.setAgentID(agentID);
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
            /* Now start a thread that will listen to messages received from
            the bank. As you can notice, we now have 2 threads running. One
            that will manage all the messages that are sent to the bank and
            one that will just constantly wait for incoming messages from the
             bank */
            (new Thread(new receiveMessages(inputFromServer))).start();
            /*
             * Now infinitely wait for user inputs messages to be sent to the
             * bank
             */
            while (true) {
                try {
                    /* Take a message to send from the blocking queue */
                    Message toSend = OutGoingBlockingQueue.take();
                    /* Send it */
                    outputToServer.writeObject(toSend);
                } catch (InterruptedException | IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            System.out.println("Invalid host address or port number! Please " +
                    "restart the" +
                    " " +
                    "program and " +
                    "input a valid " +
                    "address corresponding to the Bank.\nIf the address is " +
                    "correct, make sure the Bank or Auction House you are " +
                    "trying to connect to" +
                    " is running before trying to " +
                    "connect agents or auction houses.");
            System.exit(1);
        }
    }

    /**
     * Class that will handle all the incoming messages and
     * print them to standard output. This class is a simple thread.
     */
    public class receiveMessages implements Runnable {

        /* input stream from server. Used to read receiving messages */
        ObjectInputStream inputFromServer;

        /**
         * receiveMessage constructor
         * Set up the inputFromServer variable.
         * @param inputFromServer input stream from server side
         */
        public receiveMessages(ObjectInputStream inputFromServer) {
            this.inputFromServer = inputFromServer;
        }

        /**
         * connectNewAuctionHouse method
         * When a new auctionHouse appears, this method will parse the string
         * received from the bank, retrieve the name of the auction house,
         * its address and port number, and will call a method in the
         * instance of our agent in order to achieve automatic connection
         * with the new auction house.
         * @param message string representing the message to be passed.
         */
        public void connectNewAuctionHouse(String message)
                throws IOException, InterruptedException {
            if (agent != null) {
                String auctionID =
                        message.substring(message.indexOf("Name:") + 6,
                                message.indexOf(","));
                String hostName =
                        message.substring(message.indexOf("Address:") + 9,
                                message.indexOf("Port:") - 2);
                int portNumber =
                        Integer.parseInt(message.substring(message
                                .indexOf("Port:") + 6));
                agent.connectToAuctionHouse(auctionID, hostName, portNumber);
            }
        }

        /**
         * parseReceivedMessage method
         *
         * In some cases, the serve will have to notify an agent or auction
         * house
         * of the occurance of certain events without the agent initiating the
         * communication or making a request. In such cases, the agent will
         * need to perform some operations related to that event. This method
         * takes in a message from the server side and parses it based on the
         * content of the message. Most messages will only require to be
         * printed. However, some other messages will require a deeper
         * inspection and will need the agent or auction house to perform
         * related operations.
         * @param currentMessage message coming from the server side
         * @throws IOException handling IOException
         * @throws InterruptedException handling Interrupted Exception
         */
        private void parseReceivedMessage(Message currentMessage)
                throws IOException, InterruptedException {
            /*
             * If the message is meant for an auction house, call
             * processBankInformation in auction house and process the message
             */
            if (auctionHouse != null) {
                auctionHouse.processBankInformation(currentMessage);
            /* If the message contains ID of an agent, set the AgentID
            variable */
            } else if (currentMessage.message.contains("YourIDis ")
                    && agent != null) {
                agent.setAgentID(currentMessage.message.split(" ")[1]);
                return;
            /* If the message contains "delete", update the auction house
            list in the agent's instance
             */
            } else if (agent != null && currentMessage.message.contains(
                    "delete")) {
                agent.removeAuctionHouse(currentMessage.message
                        .split(" ")[1]);
                return;
                /* If an agent had a successful bid, update the bid flag in agent */
            } else if ((agent!=null) && (currentMessage.message.contains(
                    "successful bid on"))) {
                agent.setBiddingFlag(true);
            /* If an agent finished or lost a bid, reset the bidding flag in
            agent
             */
            } else if ((agent!=null) && (currentMessage.message.contains("You" +
                    " did " +
                    "not transfer" +
                    " " +
                    "the money quickly enough") || currentMessage
                    .message.contains(
                            "You have " +
                                    "been " +
                                    "outbid on the Item:") ||
                    currentMessage.message.contains("Sold item"))) {
                agent.setBiddingFlag(false);
            /* If a new auction House was created, connect to it
            automatically */
            } else if (currentMessage.message.contains("A new auction " +
                    "house has been created - Name: AH-")) {
                connectNewAuctionHouse(currentMessage.message);
                /* If auction houses already exist, connect to all of them */
            } else if (currentMessage.message.contains("Here is the " +
                    "list of the auction houses that are currently up")) {
                String[] tempString = currentMessage.message.split(
                        "\\r?\\n");
                for (int i = 3; i < tempString.length; i++) {
                    connectNewAuctionHouse(tempString[i]);
                }
            }
            System.out.println(destination + ": " + currentMessage.message);
        }

        /**
         * run Method
         * Note that the receive Message inner class will be started as a
         * thread and run infinitely waiting from incoming messages from the
         * server side of a communication.
         */
        @Override
        public void run() {
            Message currentMessage;
            try {
                /* Wait for messages from the bank  or auction house  */
                while ((currentMessage = (Message)
                        inputFromServer.readObject()) != null) {
                    parseReceivedMessage(currentMessage);
                }
            } catch (IOException | ClassNotFoundException |
                    InterruptedException e) {
                if (destination.equals("Bank")) {
                    System.out.println("Connection lost with the Bank, exiting " +
                            "program");
                    System.exit(1);
                } else {
                    System.out.println("Connection lost with an Auction " +
                            "House, please ask the bank for existing Auction " +
                            "Houses for " +
                            "more information");
                }

            }
        }
    }
}
