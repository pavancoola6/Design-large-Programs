/**
 * Authors: Pavan Singara, Kunj Bhavsar and Anmol Singh Gill
 * CS351L : Final Project
 */

/**
 * The class represents the Bank part of the Distributed Auction House
 * project. The bank is central to any transaction in the project. Without a
 * central Bank to which the agents and auction houses will connect, it will
 * be impossible to have a functional distributed auction implementation. The
 * bank acts as a server for both the auction houses and the clients. It will
 * wait for incoming connections from both these parties through sockets and
 * establish communication channels through which it will be able to manage
 * several elements such as account management and money transfers.
 *
 */

package Bank;

import Communications.InBoundConnectionsHandler;
import Message.Message;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * Bank Class
 *
 * NOTE: you will need a port number before running BANK.
 * Use an open port, we suggest 4444 or 5000
 */
public class Bank {
    /* HashMap from an agent ID to its current balance */
    private HashMap<String, Integer> agentToBalance;
    /* HashMap from an Agent port connection to its Agent ID */
    public HashMap<Integer, String> portToAgent;
    /*
     * HashMap from an Agent ID to its connection handler
     * instance
     */
    private HashMap<String, InBoundConnectionsHandler>
            socketToAgentConnectionHandler;
    /* HashMap from an Auction House ID to its current balance */

    private HashMap<String, Integer> auctionHouseToBalance;
    /* HashMap from an Auction House port connection to its ID */
    private HashMap<Integer, String> portToAuctionHouse;
    /*
     * HashMap from an Aucion House ID to its connection handler
     * instance
     */
    private HashMap<String, InBoundConnectionsHandler>
            socketToAHConnectionHandler;
    /* Count of all the connected Agent */
    private int agentCount;
    /* Count of all the connected Auction Houses */
    private int auctionHouseCount;
    /* Linked List of Auction House addresses, used to send to agents */
    private HashMap<String, String[]> auctionHousesInfo;
    /* UI instance */
    private UInteractions ui;
    /* Initial balances of both the agent and the auction houses accounts */
    private final int INITIAL_AGENT_BALANCE = 100;
    private final int INITIAL_AUCTION_HOUSE_BALANCE = 0;
    /* HashMap mapping an agent to its currently blocked funds*/
    private HashMap<String, Integer> agentToBlockedFunds = new HashMap<>();

    /**
     * Bank constructor
     *
     * The constructor does not take any arguments but sets up and
     * initializes all the needed variables that will be used throughout our
     * simulation.
     */
    public Bank() {
        /* Initialize all the above defined variables */
        agentToBalance = new HashMap<>();
        /* Initialize HashMap from port to Agent ID */
        portToAgent = new HashMap<>();
        /* Initialize HashMap from auctionHouseID to account Balance */
        auctionHouseToBalance = new HashMap<>();
        /* Keep hash map of all the auction houses info (address, ID, port#) */
        auctionHousesInfo = new HashMap<>();
        /* Keep a HashMap of ports to auction houses ID */
        portToAuctionHouse = new HashMap<>();
        /* Initialize a hash map from Agent sockets to Connections Handlers */
        socketToAgentConnectionHandler = new HashMap<>();
        /* Initialize a hash map from AH sockets to Connections Handlers */
        socketToAHConnectionHandler = new HashMap<>();
        /* Create a new UI interactions instance */
        ui = new UInteractions();
        /* Start the agent count and auction house count at 1 */
        agentCount = 1;
        auctionHouseCount = 1;
    }


    /**
     * Main will be started when running Bank.
     * Note that bank needs an input argument representing the port Number on
     * which the Bank should be running! Otherwise the program cannot be
     * executed.
     *
     * @param args Integer representing the port # in which connections would
     *            be made.
     */
    public static void main(String[] args) {
        int portNumber = Integer.parseInt(args[0]);
        Socket socket;
        /* Create a new server socket */
        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            /* Instantiate a new bank */
            Bank b = new Bank();
            /* Start the UI interactions on a new thread */
            b.startUInteractions();
            System.out.println("Welcome the the Bank");
            System.out.println("The bank is connected on the address: "
                    + InetAddress.getLocalHost().getHostAddress() + ", and " +
                    "listens on " +
                    "port:" +
                    " " + serverSocket.getLocalPort());
            /* Constantly read input from the command line */
            while ((socket = serverSocket.accept()) != null) {
                new Thread(new InBoundConnectionsHandler(socket, b)).start();

                System.out.println("New Connection Created");
            }
        } catch (IOException e) {
            System.err.println("Could not listen on port " + portNumber);
            System.exit(-1);
        }
    }


    /**
     * startUInteractions method
     * <p>
     * This method starts the command line interactions processing
     */
    private void startUInteractions() {
        new Thread(ui).start();
    }

    /**
     * blockFunds Method
     *
     * Return false if the bid is too high or too low
     * Block funds by subtracting the bid from the account's
     * current funds and add to its blocked funds
     *
     * @param agentID the agent
     * @param funds the bid
     * @return boolean indicating success
     */
    private boolean blockFunds(String agentID, int funds) {


        if ((funds > agentToBalance.get(agentID) || funds <= 0)) {
            System.err.println("This print statement should only " +
                    "executed if a bid is invalid");
            return false;
        }
        agentToBlockedFunds.replace(agentID, agentToBlockedFunds.get(agentID)
                + funds);
        agentToBalance.replace(agentID, agentToBalance.get(agentID) - funds);
        return true;

    }

    /**
     * unBlockFunds Method
     * Release the blocked funds of an agent
     * @param agentID the agent ID
     * @param funds to release integer representing amount to release
     */
    private void unblockFunds(String agentID, int funds) {
        /* If agent ID does not exist */
        if (!agentToBlockedFunds.containsKey(agentID))
            System.err.println("Agent Id is not in the map");
        /* If funds are bigger than current balance in blocked funds */
        if (funds > agentToBlockedFunds.get(agentID)) {
            System.err.println(funds);
            System.err.println(agentToBlockedFunds.get(agentID));
            System.err.println("Error has occurred in unblock funds");
            /* Else proceed to unblocking */
        } else {
            agentToBlockedFunds.replace(agentID,
                    agentToBlockedFunds.get(agentID) - funds);
            agentToBalance.replace(agentID,
                    agentToBalance.get(agentID) + funds);
        }
    }

    /**
     * transfer Funds method
     * Transfer funds from one bank account to another via their id
     * @param agentID the agent
     * @param auctionHouse the auctionHouse
     * @param funds to transfer
     * @return boolean indicating success
     */
    private boolean transferFunds(String agentID, String auctionHouse,
                                  int funds) {
        /* If funds are too big or invalid */
        if (funds > agentToBlockedFunds.get(agentID) || funds < 0) {
            System.err.println("Not enough funds in blocked funds");
            System.err.println("Error has occurred in transfer funds");
            return false;
            /* proceed to fund transferring */
        } else {
            agentToBlockedFunds.replace(agentID,
                    agentToBlockedFunds.get(agentID) - funds);
            auctionHouseToBalance.replace(auctionHouse,
                    auctionHouseToBalance.get(auctionHouse) + funds);
            return true;
        }
    }



    /**
     * untransfer funds
     * Used to undo the previous transferring of funds
     * @param agentID the agent to untransfer from
     * @param auctionHouse the auction house to transfer to
     */
    private void untransferFunds(String agentID, String auctionHouse,
                                 int funds) {

        /* If funds to untransfer are invalid */
        if (funds < 0) {
            System.err.println(funds);
            System.err.println(agentToBlockedFunds.get(agentID));
            System.err.println("Problem untransferring funds");
            /*
             * Note: funds bigger than agentToBlockedFunds.get(agentID) is okay.
             * Proceed to untransferring of funds
             */
        } else {
            agentToBlockedFunds.replace(agentID,
                    agentToBlockedFunds.get(agentID) + funds);
            auctionHouseToBalance.replace(auctionHouse, auctionHouseToBalance
                    .get(auctionHouse) - funds);
        }
    }




    /**
     * processIncomingMessage method
     * This is one of the main methods of our Bank program. This message will
     * be called with an Incoming Message as a parameter, a Socket and an
     * InboundConnectionsHandler instance. Based on the message received, it
     * will filter different behaviors. For instance, if a message came from
     * an agent with a specific request, the bank will filter that request
     * here and call appropriate methods to process it.
     * @param incomingMessage message that's incoming
     * @param socket of the client - used to finds agent or auction house ID
     *               for instance.
     * @param connectionsHandler to handle the communicating client
     * @return String indicating the response to the client
     */
    public synchronized String processIncomingMessage(Message incomingMessage,
                                                      Socket socket,
                                                      InBoundConnectionsHandler
                                                              connectionsHandler)
            throws IOException {
        switch (incomingMessage.source.toLowerCase()) {
            /* In case the message comes from an agent */
            case "agent":
                /*
                 * If the agent wants to create an account, make sure it does
                 * not already exist
                 */
                if (incomingMessage.message.equals("create account")) {
                    if (portToAgent.containsKey(socket.getPort())) {
                        return "An account for this agent already exists";
                    } else {
                        /* Create a new account and balance for the agent */
                        agentToBalance.putIfAbsent("A-" + agentCount,
                                INITIAL_AGENT_BALANCE);
                        portToAgent.putIfAbsent(socket.getPort(),
                                "A-" + agentCount);
                        socketToAgentConnectionHandler.putIfAbsent("A-" + agentCount,
                                connectionsHandler);
                        agentToBlockedFunds.putIfAbsent("A-" + agentCount, 0);
                        /* Now increment the agent count */
                        socketToAgentConnectionHandler.get("A-" + agentCount)
                                .sendMessage("YourIDis " + "A-" + agentCount);
                        agentCount++;
                        /* String to return and output to the agent */
                        String toRet = "Congratulations, you have just " +
                                "created" +
                                " a " +
                                "new" +
                                " account, your current " +
                                "balance is: 100. \n";
                        /* Inform the agent of existing auction houses */
                        if (socketToAHConnectionHandler.size() == 0) {
                            toRet = toRet + "There are currently no auction " +
                                    "house connected to the bank. You will be" +
                                    " notified when an auction house connects.";
                        } else {
                            toRet = toRet + "Here is the list of the auction " +
                                    "houses that are currently up on which " +
                                    "you will be automatically connected: \n\n";
                            String strTemp = "";
                            for (String str : auctionHousesInfo.keySet()) {
                                strTemp =
                                        strTemp + "Name: "+ str + ", Address: " +
                                                auctionHousesInfo.get(str)[0]
                                                + ", Port: " +
                                                auctionHousesInfo.get(str)[1] +
                                                "\n";
                            }
                            toRet = toRet + strTemp;
                        }
                        return toRet;
                    }
                    /*
                     * Force the agent to create an account before making any
                     * other transaction.
                     */
                } else if (!portToAgent.containsKey(socket.getPort())) {
                    return "You have not configured an account with the Bank." +
                            " Please input: <bank create account> before " +
                            "making any transaction";
                }
                /*
                 * Agent requests a list of all the currently AH connected to
                 * the bank
                 */
                else if (incomingMessage.message.contains("current AH " +
                        "connections")) {
                    String toRet = "";
                    if (socketToAHConnectionHandler.size() == 0) {
                        toRet = toRet + "There are currently no auction " +
                                "house connected to the bank. You will be" +
                                " notified when an auction house connects.";
                    } else {
                        toRet = toRet + "Here is the list of the auction " +
                                "houses that are connected currently connected " +
                                "to the bank: \n\n";
                        String strTemp = "";

                        for (String str : auctionHousesInfo.keySet()) {
                            strTemp =
                                    strTemp + "Name: " + str + ", Address: " +
                                            auctionHousesInfo.get(str)[0]
                                            + ", Port: " +
                                            auctionHousesInfo.get(str)[1] + "\n";
                        }
                        toRet = toRet + strTemp;
                    }
                    return toRet;
                }
                /* The agent requests information on its account */
                else if (incomingMessage.message.equals("account info")) {
                    String agentID = portToAgent.get(socket.getPort());
                    int balance = agentToBalance.get(agentID);
                    String toRet = "\n- User ID: " + agentID + "\n";
                    toRet = toRet + "- Balance: " + balance + "\n";
                    toRet = toRet + "- Bank Port: " + socket.getPort() + "\n";
                    return toRet;
                    /* The agent wants to transfer funds to an auction house */
                } else if (incomingMessage.message.contains("transfer")) {
                    List<String> info = parseString(incomingMessage.message);
                    System.out.println();
                    if (info.size() != 2) {
                        /* Message should have auctionHouse ID and funds to
                        transfer */
                        System.err.println("Transfer funds message should " +
                                "have two pieces of information");
                    } else {
                        /* If the message is valid, transfer the funds */
                        if (transferFunds(portToAgent.get((socket.getPort())),
                                info.get(0), Integer.parseInt(info.get(1)))) {
                            /* Inform the targeted auction House on
                            transferred money
                             */
                            sendMessageToAuctionHouse(info.get(0),
                                    portToAgent.get(socket.getPort()) +
                                            " transferred " + info.get(1));
                            return portToAgent.get(socket.getPort()) + " " +
                                    "transferred " + info.get(1);
                        } else {

                            return portToAgent.get(socket.getPort()) + " " +
                                    "not transferred, insufficient funds, or " +
                                    "invalid command.\n Please ensure you have " +
                                    "placed a bid on an auction house and make " +
                                    "sure the funds transferred are valid." +
                                    info.get(0);
                        }
                    }
                }
                return "Invalid command, please select a valid option";

            /* In case the message comes from an auction house */
            case "auction_house":
                /* First thing, the auction house needs to create an account */
                if (incomingMessage.message.equals("create account")) {
                    /* If account already exists */
                    if (portToAuctionHouse.containsKey(socket.getPort())) {
                        return "An account for this auction house " +
                                "already exists";
                    } else {
                        /* Create a new auction house */
                        auctionHouseToBalance.putIfAbsent("AH-" +
                                        auctionHouseCount,
                                INITIAL_AUCTION_HOUSE_BALANCE);
                        /* Set up all the HashMap associated with the account */
                        portToAuctionHouse.putIfAbsent(socket.getPort(),
                                "AH-" + auctionHouseCount);
                        /* Now set up the connection handler between the
                        agent ID and the connectionsHandler
                         */
                        socketToAHConnectionHandler.putIfAbsent("AH-" +
                                        auctionHouseCount,
                                connectionsHandler);
                        /* Finally, alert the agents that a new auction house
                        has been created and where it is accessible.
                         */
                        sendMessageToAllAgents("A new auction " +
                                "house has been " +
                                "created - Name: " + "AH-" + auctionHouseCount +
                                ", Address: " + incomingMessage.AHAddress
                                + ", Port: " + incomingMessage.AHPort);
                        /*
                         * Add the auction house info to the
                         * auctionHousesInfo map
                         */
                        auctionHousesInfo.putIfAbsent("AH-" + auctionHouseCount,
                                new String[]{incomingMessage.AHAddress,
                                        incomingMessage.AHPort});
                        /* Now increment the auction house count */
                        auctionHouseCount++;
                        return "All the connection settings are " +
                                "now completed. Note: All agents have been " +
                                "notified of " +
                                "your creation.";
                    }
                }
                /*
                 * Force the auction house to create an account before making
                 *  any other transaction.
                 */
                else if (!portToAuctionHouse.containsKey(socket.getPort())) {
                    return "You have not configured an account with the Bank." +
                            " Please input: <bank create account> before " +
                            "making any transaction";
                    /* if the auction house wants to check an agents ID */
                } else if (incomingMessage.message.contains("checkID")) {
                    String[] temp = incomingMessage.message.split(" ");
                    String tempID = temp[1];
                    return agentToBalance.containsKey(tempID) ? "checkID " +
                            tempID
                            + " valid" : "checkID " + tempID + " invalid";
                    /* If auction house asks bank to block the funds */
                } else if (incomingMessage.message.contains("block funds")) {
                    List<String> info = parseString(incomingMessage.message);
                    if (info.size() != 3) {
                        /*Message should have agentID, itemID, and bid*/
                        System.err.println("Block messages should have three " +
                                "components of information");
                    } else {
                        String agentID = info.get(0);
                        String itemID = info.get(1);
                        int bid = Integer.parseInt(info.get(2));
                        if (blockFunds(agentID, bid)) {
                            return "blocked funds of " + agentID + " " +
                                    itemID + " " + bid;
                        } else {
                            return "bid was invalid, " + agentID;
                        }
                    }
                    /* If the auction House asks to release agents funds */
                } else if (incomingMessage.message.contains("release funds")) {
                    List<String> info = parseString(incomingMessage.message);
                    if (info.size() != 2) {
                        /*Message should have agentID and bid*/
                        System.err.println("Message should have 2 components");
                    } else {
                        unblockFunds(info.get(0), Integer.parseInt(info.get(1)));
                        return "released funds of " + info.get(0);

                    }
                    /* If the auction house asks the bank to untransfer funds */
                } else if (incomingMessage.message.contains("untransfer funds")) {

                    List<String> info = parseString(incomingMessage.message);
                    if (info.size() == 2) {
                        untransferFunds(info.get(0), portToAuctionHouse
                                        .get((socket.getPort())),
                                Integer.parseInt(info.get(1)));
                        return "UnTransferred the funds from " + info.get(0);

                    }
                    return "Message doesn't have an agentID and " +
                            "an amount to transfer";
                }
            case "bank":
                /*
                Here a connection has been lost with either an agent or an
                 auction house. Make sure to erase the corresponding bank
                 account
                 */
                if (incomingMessage.message.split(" ")[0]
                        .equals("delete")) {
                    /* If a connection to an agent has been lost, remove its
                    bank account information
                     */
                    if (portToAgent.containsKey(incomingMessage.socketPort)) {
                        String toRemoveID =
                                portToAgent.get(incomingMessage.socketPort);
                        agentToBalance.remove(toRemoveID);
                        agentToBlockedFunds.remove(toRemoveID);
                        portToAgent.remove(incomingMessage.socketPort);
                        socketToAgentConnectionHandler.remove(toRemoveID);
                    /* If a connection to an auction house has been lost,
                    remove its
                    bank account information
                     */
                    } else if (portToAuctionHouse.containsKey(incomingMessage
                            .socketPort)) {
                        String toRemoveID =
                                portToAuctionHouse.get(incomingMessage
                                        .socketPort);
                        auctionHouseToBalance.remove(toRemoveID);
                        portToAuctionHouse.remove(incomingMessage.socketPort);
                        socketToAHConnectionHandler.remove(toRemoveID);
                        auctionHousesInfo.remove(toRemoveID);
                        sendMessageToAllAgents("delete "+
                                toRemoveID);
                    }
                }
                /* Default case */
            default:
                break;
        }
        /* Something wrong happened here */
        return "Invalid or Unknown instruction, please type <help> to see all" +
                " the valid commands";
    }


    /**
     * sendMessageToAgent method
     * <p>
     * This method will simply alert all the existing agents of a specific
     * event passed in as argument.
     * @param eventToNotify string representing an event
     */
    private void sendMessageToAllAgents(String eventToNotify) {
        for (String str : socketToAgentConnectionHandler.keySet()) {
            try {
                socketToAgentConnectionHandler.get(str)
                        .sendMessage(eventToNotify);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * sentMessageToAuctionHouse method
     * This method is used to send a message from the bank directly
     * to an auction house.
     * @param ah the auction house to send a message to
     * @param event the message to send to the auction house
     */
    private void sendMessageToAuctionHouse(String ah, String event) {
        try {
            socketToAHConnectionHandler.get(ah).sendMessage(event);
        } catch (IOException e) {
            System.out.println("Could not find the requested auction house to" +
                    " send a message to.\nPlease ensure that you have " +
                    "entered " +
                    "the correct command and that you are connected to an " +
                    "auction house. ");
        }
    }

    /**
     * parseString Method
     * This method is used whenever the bank gets a request related to bids.
     * Needed information from messages will be determined by the presence
     * of integers; that is,
     * ID's and bids will be identified through the integers present.
     * For example, to process the message "block funds agentID itemID
     * bid" this method will get
     * agent ID itemID and bid from the string.
     *
     * @param str string to parse
     * @return list containing the string's necessary info
     */
    private List<String> parseString(String str) {
        String[] splitStr = str.split(" ");
        List<String> actionInfo = new ArrayList<>();
        for (String s : splitStr) {
            for (char c : s.toCharArray()) {
                if (Character.isDigit(c)) {
                    actionInfo.add(s);
                    break;
                }
            }
        }
        return actionInfo;

    }

    /**
     * class for UI interactions with the bank.
     * This is strictly for debugging purposes.
     * Runs on its separate thread. Allows the user to input a few commands
     * to get information on current Bank states.
     */
    public class UInteractions implements Runnable {
        /* Scanner that will be used to read from standard input */
        public Scanner scanner;

        /**
         * UInterations constructor.
         * Simply initializes the scanner to system.in
         */
        private UInteractions() {
            scanner = new Scanner(System.in);
        }

        /**
         * run Method
         * When the UInteractions Thread is started, run will be called. In
         * run, the thread will infinitely wait for user interactions and
         * commands to parse.
         */
        @Override
        public void run() {
            while (scanner.hasNextLine()) {
                String str = scanner.nextLine();
                /* If the user asks for Bank information */
                if (str.equals("bank info")) {
                    System.out.println("Agent Information:");
                    if (agentToBalance.size() == 0) {
                        System.out.println("No Agent is currently connected " +
                                "to the bank");
                    } else {
                        System.out.println("Balances are:");
                        System.out.println(agentToBalance);
                        System.out.println("Blocked Funds are:");
                        System.out.println(agentToBlockedFunds);
                    }
                    System.out.println();
                    System.out.println("Auction House Information:");
                    if (socketToAHConnectionHandler.size() == 0) {
                        System.out.println("No Auction House is currently " +
                                "connected to the bank");
                    } else {
                        System.out.println(auctionHouseToBalance);
                    }
                    /* If the user wants to make an auction house exit */
                }else  if (str.contains("exit") && str.contains("AH-")){
                    sendMessageToAuctionHouse(str.split(" ")[0]
                            ,"exit");
                }
                else  if (str.contains("exit")){
                    System.exit(1);
                }
                else {
                    System.out.println("Invalid Instruction");
                }
            }
        }
    }
}

