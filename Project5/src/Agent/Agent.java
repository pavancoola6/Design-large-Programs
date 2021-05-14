/**
 * Authors: Pavan Singara, Kunj Bhavsar and Anmol Singh Gill
 * CS351L : Final Project
 */

/**
 * The class represents the Agent part of the Distributed Auction House. The
 * Agent is the main point of control for an user. In the part of the Auction
 * House project, the user will interact with several auction houses, places
 * bids and win/lose items, as well as with the bank to manage the agent's
 * account and transfer money. All the interactions will be made via command
 * line arguments. We have incorporate a help menu in order to assist the
 * user in controlling and agent.
 *
 */

package Agent;

import Communications.OutBoundConnectionsHandler;
import Message.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;

public class Agent {
    /* Instance of OutBoundConnectionsHandler - Agent <-> Bank connections */
    private OutBoundConnectionsHandler bankConnection;
    /* Hash Map of an Auction House ID to its communications instance */
    private HashMap<String, OutBoundConnectionsHandler>
            auctionIDtoConnectionsHandler;
    /* String representing the current agent's ID as assigned by the Bank */
    private String agentID;
    /* Flag indicating whether a current bid is in process or now */
    private boolean bidding;

    /**
     * Agent Constructor
     * The constructor simply initializes a new OutBoundConnectionsHandler
     * with the Bank and a new HashMap that will hold the AuctionHouses IDs
     * to their respective connections links with the agent
     */
    public Agent() {
        /* Bank connection instance */
        bankConnection = new OutBoundConnectionsHandler("Bank");
        /* HashMap from auction house ID to its connection instance */
        auctionIDtoConnectionsHandler = new HashMap<>();
    }

    /**
     * main
     * <p>
     * When the Agent class is launched, the user will be asked to input the
     * Bank's information in order to set up an initial connection. The
     * Agent will have its main thread, through which the user will make
     * requests via the command line, and it will have a additional threads that
     * will simply receive messages from the Bank and the Auction houses in
     * case some events occur.
     *
     * @param args command line arguments
     * @throws IOException handling the sockets and object input/output streams
     */
    public static void main(String[] args) throws IOException,
            InterruptedException {
        /* set up a new agent */
        Agent ag = new Agent();
        /*
         * First thing to do, connect to the bank! This is a necessary step
         * in order to get in touch with any auction houses. When running
         * the agent, the user will need to give an ip address and a port
         * number in order to connect to the bank.
         */
        System.out.println("Please provide the Bank's address and port number" +
                ": <IP address/host name> <PORT>");
        /* Read in the user's input */
        BufferedReader stdIn =
                new BufferedReader(new InputStreamReader(System.in));
        /* Set up the variables */
        String[] bankInfo = null;
        boolean wrongBankInfo = true;
        int portNumber = -1;
        String hostName;

        /*
         * The following piece of code will make sure that the user has given
         *  valid information concerning the Bank's address. Note that the
         *  user is simply asked to input an address (as a String) and a port
         *  number (as an int - thus should be convertible to an int). If none
         *  of these conditions are met, ask the user for valid input
         */
        while (wrongBankInfo) {
            /* read the input */
            bankInfo = stdIn.readLine().split(" ");
            /* If the input does not have a length of 2, break */
            if (bankInfo.length != 2) {
                System.out.println("Invalid Bank information, please provide a " +
                        "valid address in the following format: \n  " +
                        "<IP address/host name> <PORT>");
            } else {
                /* Try to convert the given port number to an int */
                try {
                    portNumber = Integer.parseInt(bankInfo[1]);
                    /*
                     * If converting to int was successful, make sure that it
                     *  is within the possible ports range
                     */
                    if (portNumber > 65535 || portNumber < 1) {
                        System.out.println("Port Number out of range, please " +
                                "input a port number between 1 and 65535");
                        /*
                         * If all the conditions are met, set the wrongBankInfo
                         * flag to be false - this will break out of the loop
                         */
                    } else {
                        wrongBankInfo = false;
                    }
                    /*
                     * If the port number could not be converted to an int,
                     * retry
                     */
                } catch (NumberFormatException e) {
                    System.out.println("Invalid port number, please input a " +
                            "number " +
                            "between 1 and 65535");
                }
            }
        }
        /* Valid credentials given, now set the hostname and continue */
        hostName = bankInfo[0];
        ag.bankConnection.setHostAndPort(hostName, portNumber);
        ag.bankConnection.OutGoingBlockingQueue.put(new Message(
                "agent",
                "create account", null, null,
                0));
        ag.bankConnection.setAgent(ag);
        /* Start the bank Communications thread */
        ag.startBankCommunicationsThread();
        String toProcess;
        /* Read a command from input */
        while (true) {
            if ((toProcess = stdIn.readLine()).equals("exit")){
                ag.terminateAgent();
                continue;
            }
            String[] userInput = toProcess.split(" ");

            ag.processMessagesToSend(userInput[0], String.join(" ",
                    Arrays.copyOfRange(userInput,
                            1, userInput.length)));
        }
    }

    /**
     * setBiddingFlag method
     * This method simply sets the bidding flag to true or false, depending
     * if the agent is currently bidding on an item or not.
     * @param flag boolean indicating if bid is in progress.
     */
    public synchronized void setBiddingFlag(boolean flag){
        bidding = flag;
    }

    /**
     * terminateAgent method
     * This method is called when the user inputs exit in the CLI. Note that
     * we will only exit the agent if the bidding flag is set to false.
     * Otherwise the agent will not be allowed to terminate.
     */
    private void terminateAgent(){
        if (bidding){
            System.out.println("Cannot exit the agent, bidding is in progress" +
                    ".\nPlease terminate the bid before exiting");
        }else {
            System.out.println("Exiting the Agent, bye");
            System.exit(1);
        }
    }



    /**
     * setAgentID method
     * method to set up the agent's ID as given by the bank
     * @param agentID string representing the agent's ID
     */
    public void setAgentID(String agentID){
        this.agentID = agentID;
    }

    /**
     * removeAuctionHouse method
     * update the list of auction houses to which the agent is currently
     * connected.
     * @param auctionID string representing the auction House's ID
     */
    public void removeAuctionHouse(String auctionID){
        if (auctionIDtoConnectionsHandler.containsKey(auctionID)){
            auctionIDtoConnectionsHandler.remove(auctionID);
        }
    }

    /**
     * connectAuctionHouse method
     * method to automatically connect to an auction house. Initially, we had
     * set up the program to force users to manually connect to an auction
     * house. This feature is still available, but for ease of use purposes,
     * we have decided to automatically connect an agent to an auction house.
     * @param auctionID string representing the Auction House's ID
     * @param hostName string representing the address of the auction House
     * @param portNumber string representing the port number on which the
     *                   auction house is listening.
     * @throws IOException handling IO exception
     * @throws InterruptedException handling Interrupted exceptions
     */
    public void connectToAuctionHouse(String auctionID, String hostName,
                                      int portNumber) throws IOException,
            InterruptedException {
        auctionIDtoConnectionsHandler.putIfAbsent(auctionID,new
                OutBoundConnectionsHandler("Auction " +
                "House " + auctionID));
        auctionIDtoConnectionsHandler.get(auctionID).setHostAndPort(hostName,
                portNumber);
        auctionIDtoConnectionsHandler.get(auctionID).setAgent(this);
        (new Thread(auctionIDtoConnectionsHandler.get(auctionID))).start();
        processMessagesToSend(auctionID,"ID "+agentID);
    }

    /**
     * startBankCommunications method
     *
     * This method initializes the thread that will handle the communications
     * between the agent and the bank. To do so, we use an instance of
     * OutBoundConnectionsHandler.
     */
    private void startBankCommunicationsThread() {
        (new Thread(bankConnection)).start();
    }

    /**
     * printHelpMenu method
     * This method simply output the help menu when the user asks for help.
     * It's basically a bunch of very ugly print statements.
     */
    private void printHelpMenu(){
        System.out.println("\nWelcome to The Help Panel\n");
        System.out.println("Bank Services:");
        System.out.println("Command to use\t\t\t\t\t\t\tDescription");
        System.out.println("bank account " +
                "info\t\t\t\t\t\tRequest information concerning your " +
                "bank" +
                " account, balance " +
                "and ID");
        System.out.println("bank current AH " +
                "connections\t\t\t\tRequest" +
                " ID, Address and Port # of all Auction Houses " +
                "currently connected to the Bank");
        System.out.println("bank transfer <auctionId> " +
                "<amount>\t\tRequest the Bank to transfer <amount> " +
                "to <auctionId> auction");
        System.out.println("\nAuction House Services:");
        System.out.println("Command to use\t\t\t\t\t\t\tDescription");
        System.out.println("<auctionID> <items selling>\t\t\t\tTo " +
                "request the list of items sold by Auction House" +
                "<auctionID>");
        System.out.println("<auctionID> bid <ItemID> " +
                "<amount>\t\tPlace a bid of <amound> on <ItemID> " +
                "at Auction House <auctionID>");
        System.out.println("auction_house connect\t\t\t\t\tPerform a " +
                "manual connection to an Auction House" +
                ".\n\t\t\t\t\t\t\t\t\t\t" +
                "Note that " +
                "this command is strictly optional and should in " +
                "general cases not be used since connections to " +
                "Auction Houses will be automatic");
        System.out.println("\nAgent Services:");
        System.out.println("Command to use\t\t\t\t\t\t\tDescription");
        System.out.println("bank_port\t\t\t\t\t\t\t\tGet the port " +
                "number on " +
                "which you are connected with the Bank");
        System.out.println("current AH connections\t\t\t\t\tGet the " +
                "name of all the Auction Houses to which you are " +
                "currently connected");
    }


    /**
     * processMessagesToSend method.
     *
     * This method is one of the main method relating to the Agent's
     * functionality. In this method, the user's CLI commands will be parsed
     * and processed. The method is called with a destination string (given
     * by the user in its CL input) and the actual message. The rest will be
     * processed based on the passed in information via the auction house and
     * banks communication channels.
     * @param destination String representing destination where the message
     *                    should be sent
     * @param message String representing the message that should be sent.
     * @throws InterruptedException handling the exception
     */
    private void processMessagesToSend(String destination, String message)
            throws InterruptedException, IOException {
        switch (destination.toLowerCase()) {
            /* If the user types help, display the help menu */
            case "help":
                printHelpMenu();
                break;
            /* If the user wants to communicate with the bank */
            case "bank":
                if (message != null) {
                    bankConnection.OutGoingBlockingQueue.put(new Message(
                            "agent",
                            message, null, null,
                            0));
                } else {
                    System.out.println("Please give a valid command for the " +
                            "Bank to process");
                }
                break;
            /*
             * if the user has a general request for an auction house, for
             * instance, if the user tries to manually connect to an auction
             * house. Note that this feature is optional and should barely
             * be used as agents should now be able to connect automatically
             * to each existing auction house after it connect to the bank.
             */
            case "auction_house":
                if (message.equals("connect")){
                    BufferedReader stdIn2 =
                            new BufferedReader(new InputStreamReader(System.in));
                    System.out.println("Please provide an auction house ID, " +
                            "its address and port " +
                            "number on which the agent should connect: ");
                    String[] info =  stdIn2.readLine().split(" ");
                    try {
                        String auctionID = info[0];
                        String hostName = info[1];
                        int portNumber = Integer.parseInt(info[2]);

                        auctionIDtoConnectionsHandler.putIfAbsent(auctionID,
                                new OutBoundConnectionsHandler(
                                        "Auction " +
                                                "House " + auctionID));
                        auctionIDtoConnectionsHandler.get(auctionID)
                                .setHostAndPort(hostName, portNumber);
                        (new Thread(auctionIDtoConnectionsHandler
                                .get(auctionID))).start();
                    } catch (Exception e){
                        System.out.println("Invalid information given, please" +
                                " double check");
                    }
                }
                else{
                    System.out.println("Invalid Auction House Command");
                }
                break;
            /* Any additional information that the user wants is done here */
            case "bank_port":
                /* User wants the port number of bank connection */
                System.out.println(bankConnection.bankSocketPort);
                break;
            /* User wants list of AH to which it is current connected */
            case "current":
                if (message.equals("AH connections")) {
                    System.out.println("Here are the auction houses you are " +
                            "currently connected to:");
                    if (auctionIDtoConnectionsHandler.keySet().isEmpty()) {
                        System.out.println("You are not currently connected " +
                                "to any Auction House");
                    } else {
                        for (String str : auctionIDtoConnectionsHandler
                                .keySet()) {
                            System.out.println("\t- " + str);
                        }
                    }
                }
                break;
            default:
                /*
                 * Here, we send messages to unique auction houses via the
                 * ah- keyword. This keyword represents the ID of an auction
                 * house in the format AH-int
                 */
                if(destination.toLowerCase().contains("ah-")){
                    try {
                        if ( auctionIDtoConnectionsHandler
                                .containsKey(destination) ) {
                            auctionIDtoConnectionsHandler
                                    .get(destination)
                                    .OutGoingBlockingQueue
                                    .put(new Message("agent",
                                            message, null,
                                            null, 0));
                        } else {
                            System.out.println("No connection with the given " +
                                    "Auction House exists. " +
                                    "Make sure you create a connection " +
                                    "before making any request.");
                        }
                    }catch (Exception e){
                        System.out.println("Invalid instructions for an " +
                                "Auction House.\nPlease input help for " +
                                "more information");
                    }
                }
                else{
                    System.out.println("Wrong Input Argument, please type <help> " +
                            "for all available options.");
                }
                return;
        }
    }
}
