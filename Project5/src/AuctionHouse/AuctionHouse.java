/**
 * Authors: Pavan Singara, Kunj Bhavsar and Anmol Singh Gill
 * CS351L : Final Project
 */

/**
 * This Auction House class is a runnable program that connects to the Bank and
 * the Agents that are currently connected to the Bank.
 *
 * The Auction House contains three paintings that it sells at any given moment.
 * The Agents can communicate with the Auction House in order to bid on these
 * items and the Auction House
 * will then communicate with the bank in order to update their account
 * information
 * The Bank will communicate with the auction house to the determine if the agent
 * has transferred the appropriate amount of
 * money for a given item that they have won.
 *
 * The Auction House has its own InBoundConnectionsHandler that connects the
 * Auction House to Agents
 * and it has an OutBoundConnectionsHandler to connect it to the Bank
 */

package AuctionHouse;

import Communications.InBoundConnectionsHandler;
import Communications.OutBoundConnectionsHandler;
import Message.Message;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;


public class AuctionHouse {
    private OutBoundConnectionsHandler bankConnection;
    private ServerSocket serverSocket;
    private int socketPortNumber;
    private String socketAddress;
    private List<Item> itemsToSell;
    private List<Item> itemsCurrentlySelling;

    /*
    These HashMaps are used to keep track of Agents that the
    AuctionHouse has been communicating
     */
    private HashMap<Integer, String> portToAgent = new HashMap<>();
    private HashMap<String, Integer> agentToPort = new HashMap<>();
    private HashMap<String, InBoundConnectionsHandler>
            socketToAgentConnectionHandler = new HashMap<>();

    /**
     * This is the Auction House constructor which instantiates the
     * starting behavior for an auction house.
     * see start()
     */
    private AuctionHouse() {
        /* Initialize a new auction house - bank communication */
        bankConnection = new OutBoundConnectionsHandler("Bank");
        bankConnection.setAuctionHouse(this);
        serverSocket = null;

        itemsToSell = new ArrayList<>();
        itemsCurrentlySelling = new ArrayList<>();

        start();
    }

    /**
     * This method is called upon the creation of an auction house.
     * Upon startup, the auction house will read from a text file to get
     * painting names in order to auction them
     * It will then pick 3 random items to sell
     *
     * see pickItemsToSell()
     */
    private void start(){
        /*
         * open an account with the bank with funds of zero dollars
         * give the bank its host and port address
         * other startup functions
         *
         */
        try{
            InputStream in = AuctionHouse.class.getResourceAsStream("items.txt");
            BufferedReader bufferedReader = new BufferedReader(new
                    InputStreamReader(in));

            String st;
            int itemId = 0;
            while ((st = bufferedReader.readLine()) != null) {
                Item item = new Item(st, 0 , itemId,
                        this);
                itemsToSell.add(item);
                itemId++;
            }

        }catch(IOException e){
            e.printStackTrace();
        }

        //pick three items to sell
        Collections.shuffle(itemsToSell);
        pickItemsToSell(3);
    }

    /**
     * Return a list of item names for the auction house to sell
     * In addition, starts the threads for the Items chosen
     * @param numberOfItems how many items to put into the returned list
     */
    private void pickItemsToSell(int numberOfItems){
        for(int i = 0; i < numberOfItems; i++){
            itemsCurrentlySelling.add(itemsToSell.get(0));
            startItemThread(itemsToSell.get(0));
            itemsToSell.remove(itemsToSell.get(0));
        }
    }

    /**
     * This is the main method for the Auction House class used to
     * keep track of incoming connections.
     *
     * In addition, the main method is where the UI thread is
     * started and where an instance of an Auction House is created.
     *
     * @param args command line arguments
     */
    public static void main(String[] args){
        /* set up a new Auction House */
        AuctionHouse ah = new AuctionHouse();
        /* Set up the variables */
        String[] bankInfo = null;
        boolean wrongBankInfo = true;
        int portNumber = -1;
        String hostName;

        /*
         * First thing to do, select a port number on which the Auction House
         * will listen for agents to connect!
         */
        System.out.println("Welcome to the Auction House interface!");
        /* Read in the user's input */
        BufferedReader stdIn =
                new BufferedReader(new InputStreamReader(System.in));

        boolean correctAuctionHousePort = false;
        while(!correctAuctionHousePort) {
            try{
                System.out.print("Please input a port on which you would " +
                        "like the " +
                        "Auction House to wait for Agent connections: ");
                ah.socketPortNumber = Integer.parseInt(stdIn.readLine());
                if (ah.socketPortNumber > 65535 || ah.socketPortNumber < 1){
                    System.out.println("Port Number out of range");
                    continue;
                }
                correctAuctionHousePort = true;
            }catch (Exception e){
                System.out.println("Invalid port number");
            }
        }
        /*
         * Now we need to set up the communications with
         * the agent. This will be the server side of the AuctionHouse. It will
         * be very similar to the Bank's listening on connections.
         */
        Socket socket;
        try (ServerSocket tmpServerSocket = new ServerSocket(
                ah.socketPortNumber)) {
            ah.serverSocket = tmpServerSocket;
            ah.socketAddress = ""+ InetAddress.getLocalHost().getHostAddress();

            /*
             * Before allowing agent connections, we need to connect to the
             * bank!
             */
            System.out.println("Please provide the Bank's address " +
                    "and port number" +
                    ": <IP address/host name> <PORT>");

            /*
             * The following piece of code will make sure that the user has given
             *  valid information concerning the Bank's address. Note that the
             * user is simply asked to input an address (as a String) and a port
             * number (as an int - thus should be convertible to an int). If none
             *  of these conditions are met, ask the user for valid input
             */
            while (wrongBankInfo) {
                /* read the input */
                bankInfo = stdIn.readLine().split(" ");
                /* If the input does not have a length of 2, break */
                if (bankInfo.length != 2) {
                    System.out.println("Invalid Bank information, " +
                            "please provide a " +
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
                            System.out.println("Port Number out of range, " +
                                    "please " +
                                    "input a port number between 1 and 65535");
                            /*
                             * If all the conditions are met, set the
                             * wrongBankInfo
                             * flag to be false - this will break out of the loop
                             */
                        } else {
                            wrongBankInfo = false;
                        }
                        /*
                         * If the port number could not be converted to an int,
                         *  retry
                         */
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid port number, please input " +
                                "a number " +
                                "between 1 and 65535");
                    }
                }
            }
            /* Valid credentials given, now set the hostname and continue */
            hostName = bankInfo[0];
            ah.bankConnection.setHostAndPort(hostName, portNumber);
            ah.bankConnection.OutGoingBlockingQueue.put(new Message(
                    "auction_house", "create account",
                    ah.socketAddress, ""+ah.socketPortNumber,
                    0));
            /* Start the bank Communications thread */
            ah.startBankCommunicationsThread();
            /* Initialize the UI interactions */
            ah.startUInteractions(stdIn);

            System.out.println("Configuration completed! The Auction House " +
                    "is now waiting for Agent " +
                    "connections " +
                    "on: ");
            System.out.println(InetAddress.getLocalHost().getHostAddress() +
                    ", "+ ah.serverSocket.getLocalPort());
            /* Constantly read input from the command line */
            while ((socket = ah.serverSocket.accept()) != null) {
                new Thread(new InBoundConnectionsHandler(socket, ah)).start();
                System.out.println("New Connection Created");
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("Could not listen on port " +
                    ah.socketPortNumber);
            System.out.println("This port is either closed or already taken. " +
                    "Please make sure to input a valid, open port.");
            System.exit(1);
        }
    }

    /**
     * startUInteractions method
     * This method starts the command line interactions processing
     * @param stdIn bufferedReader from standard input
     */
    private void startUInteractions(BufferedReader stdIn) {
        UInteractions ui = new UInteractions(stdIn);
        new Thread(ui).start();
    }

    /**
     * this method initializes the thread that will handle the communications
     * between the agent and the bank
     */
    private void startBankCommunicationsThread() {
        (new Thread(bankConnection)).start();
    }

    /**
     * This method starts an Item thread
     * @param item item thread to be started
     */
    private void startItemThread(Item item){
        new Thread(item).start();
    }

    /**
     * This method is ONLY for messages to be sent to either the
     * bank or the agent without having received messages prior to the
     * sending. For instance, the auction house will want to send a message
     * to the bank asking if agentXX has enough money in its bank account. Or
     * the bank will want to notify and agent that
     *
     * @param destination a String representation of your desired destination
     * @param message the message you want to send
     * @param agentId the ID of the agent to send. If you are not sending
     *                anything to an agent then you can put anything
     * @throws InterruptedException handling exception
     */
    private void processMessageToSend(String destination, String message,
                                      String agentId)
            throws InterruptedException {
        switch (destination.toLowerCase()) {
            /*
             * Every message that needs to be sent to the bank starts with
             * the bank keyword, followed by a valid instruction
             */
            case "bank":
                if (message != null) {
                    bankConnection.OutGoingBlockingQueue.put(
                            new Message("auction_house",
                                    message, socketAddress,""+
                                    socketPortNumber, 0));
                } else {
                    System.out.println("Please give a valid command for the " +
                            "Bank to process");
                }
                break;
            case "agent":
                if(message != null){
                    try {
                        socketToAgentConnectionHandler.get(agentId)
                                .sendMessage(message);
                    } catch (Exception e){
                        System.out.println("Wrong Agent ID received");
                    }
                }
                break;
            default:
                System.out.println("Wrong Input Argument. Please follow the " +
                        "instructions");
        }
    }

    /**
     * This is where incoming messages from the InBoundConnectionsHandler
     * are processed
     *
     * Messages from the Agent:
     * ID - this is used internally in order to get the agent ID for a specific
     * agent
     * bid - This is used in the UI by an agent to bid on one of the items.
     * This takes the form of "bid <itemID> <dollarAmount>"
     * If the agent can bid on the item with the information given to the
     * Auction House
     * then a message is sent to the bank in order to see if they can block
     * funds for the item
     * items selling - this returns a list of the items selling with their
     * current bidding status, item
     * id, item name, and their minimum bidding status.
     *
     */
    public synchronized String processIncomingMessage(Message incomingMessage,
                                                      Socket socket,
                                                      InBoundConnectionsHandler
                                                              connectionsHandler)
            throws InterruptedException {
        switch (incomingMessage.source.toLowerCase()){
            case "agent":
                if(incomingMessage.message.contains("ID")){
                    try {
                        if(portToAgent.containsKey(socket.getPort())){
                            return "Agent ID already exists";
                        }
                        String[] temp = incomingMessage.message
                                .split(" ");
                        String tempID = temp[1];
                        portToAgent.putIfAbsent(socket.getPort(), tempID);
                        agentToPort.putIfAbsent(tempID, socket.getPort());
                        socketToAgentConnectionHandler.putIfAbsent(tempID,
                                connectionsHandler);
                        processMessageToSend("bank",
                                "checkID " + tempID, "");
                        return "Checking with the Bank if agent ID/Account " +
                                "exists";
                    } catch (Exception e){
                        return "invalid user id";
                    }
                }
                else if (!socketToAgentConnectionHandler
                        .containsValue(connectionsHandler)){
                    return "Please inform the auction house of your agent " +
                            "ID first using the command:\n<auction_house " +
                            "ID agentID>";
                }
                else if(incomingMessage.message.contains("bid")){
                    /* "bid itemId dollarAmount" */
                    List<Integer> values = parseLine(incomingMessage.message);
                    /*check for valid ID*/
                    int index = 0;
                    for(Item item: itemsCurrentlySelling){
                        if(values.get(0).equals(item.getId())){
                            index++;
                        }
                    }
                    /* These if statements are for robustness of the code */
                    if(index == 0){
                        return "Not a valid Item ID";
                    }

                    if(values.size() != 2){
                        return "Not the correct information given.\n"
                                + "Try: auction_house <itemID> <dollarAmount>";
                    }
                    Item itemBidding = findItem(values.get(0));
                    if(!itemBidding.isBidding()){
                        return "You cannot bid on that item anymore";
                    }
                    if(values.get(1) < itemBidding.getMinBid()){
                        return "That is not enough money. Please bid using " +
                                "the min bid amount.";
                    }
                    /*
                     * If the item exists and there is enough money then we
                     * can bid on the item
                     */
                    if(itemBidding != null){
                        if(itemBidding.getBiddingStatus() < values.get(1)){
                            //the item has been outbid
                            processMessageToSend("bank",
                                    "block funds " +
                                            portToAgent.get(socket.getPort()) +
                                            " " + values.get(0) +
                                            " " + values.get(1),
                                    portToAgent.get(socket.getPort()));
                            return "Requesting for sufficient funds";
                        }
                        else{
                            return "Not a large enough bid";
                        }
                    }
                    else{
                        System.out.println("Wrong ID");
                    }
                }
                else if(incomingMessage.message.contains("items selling")){

                    String messageToSend = "ItemID   Item Name             " +
                            "             Current Bid   Minimum Bid \n";
                    int numberOfSpaces;
                    for(Item item: itemsCurrentlySelling){
                        numberOfSpaces = 20;
                        for(int i = 0; i < numberOfSpaces; i++){
                            messageToSend += " ";
                        }
                        messageToSend += item.getId();
                        numberOfSpaces = 9 - Integer.toString(item.getId()).
                                length();
                        for(int i = 0; i < numberOfSpaces; i++){
                            messageToSend += " ";
                        }

                        messageToSend += item.getName();
                        numberOfSpaces = 35 - item.getName().length();
                        for(int i = 0; i < numberOfSpaces; i++){
                            messageToSend += " ";
                        }

                        messageToSend += item.getBiddingStatus();
                        numberOfSpaces = 14 - Integer.toString(item
                                .getBiddingStatus()).length();
                        for(int i = 0; i < numberOfSpaces; i++){
                            messageToSend += " ";
                        }

                        messageToSend += item.getMinBid() + "\n";
                    }

                    return messageToSend;
                }
                break;
            default:
                break;
        }
        return "Unrecognized Command";
    }

    /**
     * This is where the messages from the bank are processed. See the
     * OutBoundsConnectionHandler.
     *
     * Messages from the bank:
     *
     * blocked - this takes the form "blocked <agentID> <itemID> <biddingAmount>
     *     If the auction house receive a message from the bank saying that they
     *     blocked the funds of an agent,
     *     then the auction house knows that they have made a successful bid on
     *     that item. If this is the case, then several checks are made on
     *     the item to make sure that it is
     *     still a valid bid. If it is a valid bid, then it will interrupt
     *     (or start) the timer for the item, update the bidding status of
     *     the item, update the minimum bid of the item, update the current
     *     agent ID with the highest ID of the item, and send a message to
     *     the agent telling them that their bid was successful and on what
     *     item.
     *
     * exit - The bank can terminate the Auction House as well
     *
     * checkID - this checks the ID of an agent to see if it exists before
     * setting it up with the Auction House (internal)
     *
     * bid was invalid - Used to relay the information to an Agent that they
     * could not place a bid for any reason
     *
     * transferred - this is used to tell the Auction House that an agent has
     *      been transferred money If the Agent is bidding on a current item,
     *      and that item is in the selling process, then checks are made
     *      on the amount of money that was sent to the Auction House to see
     *      if they can buy an of the items.
     *
     *      First, a check is made to see all of the items that they are
     *      currently able to buy from this Auction House
     *      Then, this list is sorted in increasing order of cost.
     *      It then sells the first item (lowest costing) to the Agent and
     *      checks  again to see if they have
     *      enough left over money to buy any other items that they are
     *      eligible to buy. If they can, then they are sold those items.
     *      If they have any money left over after checking for items to
     *      sell, then a message is sent to their bank in order to refund
     *      them the money that they sent. In addition, a message is sent to
     *      the agent telling them that they are being refunded money because
     *      they sent too much.
     *
     * @param incomingMessage Message object representing the incoming message.
     * @throws InterruptedException handling InterruptedException
     */
    public synchronized void processBankInformation(Message incomingMessage)
            throws InterruptedException {
        if(incomingMessage.message.contains("blocked")){
            /*"blocked agentId itemId biddingAmount"*/

            List<Integer> values = parseLine(incomingMessage.message);
            /*
             * item being bid on needs to have a new status and should be
             * notified
             */
            if(findItem(values.get(1)) != null){
                Item itemBidding = findItem(values.get(1));
                /*item bidding set the agent with highest bid*/
                if(itemBidding.isAbleToInterrupt()){
                    /*
                     * notify the current highest bidder that they have been
                     * out bid
                     */
                    itemBidding.getInput().put("interrupt");
                    processMessageToSend("agent",
                            "You have been outbid on the Item: " +
                                    itemBidding.getName() + " with ID " +
                                    itemBidding.getId(),
                            "A-" + itemBidding
                                    .getAgentIDWithHighestBid());
                    processMessageToSend("bank",
                            "release funds A-" +
                                    itemBidding.getAgentIDWithHighestBid()
                                    + " " + itemBidding.getBiddingStatus(), "");
                    /*set the item's stored variables as the updated bid*/
                    itemBidding.setAgentIDWithHighestBid(values.get(0));
                    itemBidding.setBiddingStatus(values.get(2));
                    itemBidding.setMinBid(values.get(2) + 1);
                }
                else{
                    itemBidding.setAgentIDWithHighestBid(values.get(0));
                    itemBidding.setBiddingStatus(values.get(2));
                    itemBidding.setMinBid(values.get(2) + 1);
                    itemBidding.getInput().put("start");
                }

                /* add item id */
                processMessageToSend("agent",
                        "successful bid on " + itemBidding.getName() +
                                " with itemID " + itemBidding.getId(),
                        "A-" + values.get(0));
            } else{
                System.out.println("Item does not exist");
            }
        }
        else if (incomingMessage.message.equals("exit")){
            terminateAuctionHouse();
        }
        else if(incomingMessage.message.contains("checkID")){
            String[] temp = incomingMessage.message.split(" ");
            if(temp[2].equals("valid")){
                /* send message to agent saying the id was valid */
                processMessageToSend("agent",
                        "Successfully set up your ID.", temp[1]);
            }
        }
        else if(incomingMessage.message.contains("bid was invalid")){
            /* "could not block agentId itemId biddingAmount" */
            List<Integer> values = parseLine(incomingMessage.message);
            processMessageToSend("agent",
                    "cannot place bid", "A-" + values.get(0));
        }
        else if(incomingMessage.message.contains("transferred")){
            /* "agentID transferred 100" "A-1 transferred 100" */
            List<Integer> values = parseLine(incomingMessage.message);

            List<Item> sell = new ArrayList<>();

            for(Item item: itemsCurrentlySelling){
                if(values.get(0) == item.getAgentIDWithHighestBid()
                        && !item.isBidding() && !item.isSold()){
                    sell.add(item);
                }
            }
            if(sell.size() == 0){
                /* they transferred money too late or without needing to */
                processMessageToSend("bank",
                        "untransfer funds A-" +
                                values.get(0) + " " + values.get(1), "");
                processMessageToSend("agent", "You " +
                                "cannot transfer money, as "+
                                "you are not bidding on any of those items",
                        "A-" + values.get(0));
            }
            else{
                /* sort by lowest cost */
                List<Item> sortedSell = sortByLowestCost(sell);

                if(values.get(1) > sell.get(0).getBiddingStatus()){
                    int tempFunds = values.get(1);

                    for(Item item: sortedSell){
                        if(tempFunds >= item.getBiddingStatus()){
                            tempFunds -= item.getBiddingStatus();
                            item.getInput().put("sell");
                        }
                    }
                    if(tempFunds > 0){
                        processMessageToSend("bank",
                                "untransfer funds A-" +
                                        values.get(0) + " " + tempFunds,
                                "");
                        processMessageToSend("agent",
                                "You sent too much money. "+
                                        "We are refunding you " + tempFunds +
                                        " dollars", "A-" + values.get(0));
                    }
                }
                else if(values.get(1) == sell.get(0).getBiddingStatus()){
                    sell.get(0).getInput().put("sell");
                }
                else{
                    //not enough money to buy any items
                    processMessageToSend("bank",
                            "untransfer funds A-" +
                                    values.get(0) + " " + values.get(1), "");
                    processMessageToSend("agent",
                            "That was not the correct amount of" +
                                    " money to be sent. " +
                                    "Please resend with sufficient funds",
                            "A-" + values.get(0));
                }
            }
        }
    }

    /**
     * This method is used in the Transferred message from the bank.
     * See processBankInformation()
     *
     * This method creates a new Comparator in order to compare items and
     * sort them from lowest to highest bidding amount.
     * @param toSort list of items to sort
     * @return List of items
     */
    private List<Item> sortByLowestCost(List<Item> toSort){

        toSort.sort(new Comparator<Item>() {
            @Override
            public int compare(Item item1, Item item2) {
                return item1.getBiddingStatus() - item2.getBiddingStatus();
            }
        });
        return toSort;
    }

    /**
     * This method is used in the blocked message from the bank
     * see processBankInformation()
     *
     * This method finds an item that the auction house is currently
     * selling based upon a given Item ID (int)
     * @param itemId item ID of an item currently selling to return
     * @return an Item that is within the currentlyselling list of items
     */
    private Item findItem(int itemId){
        Item toReturn = null;
        for(Item item: itemsCurrentlySelling){
            if(item.getId() == itemId){
                toReturn = item;
            }
        }
        return toReturn;
    }

    /**
     * This method is used within the Item class in order to sell an item
     * First, a message is sent to the Agent that bought the item to notify them
     * Then, we remove the item from the list of currently selling items.
     * Finally, we sell a new item
     * @param item item to be sold
     * @throws InterruptedException handling InterruptedException
     */
    public void sellItem(Item item) throws InterruptedException {
        System.out.println("Selling item: " + item.getName() + " for " +
                item.getBiddingStatus() +
                " to A-" + item.getAgentIDWithHighestBid());
        processMessageToSend("agent",
                "Sold item " + item.getId() + " for " +
                        item.getBiddingStatus(),
                "A-" + item.getAgentIDWithHighestBid());
        itemsCurrentlySelling.remove(item);
        pickItemsToSell(1);
    }

    /**
     * This method is used within the Item class within the Timer class
     * in order to resell an Item
     * If the timer runs out and the bid winner did not transfer any money,
     then the item is resold as a new
     * Item instance.
     * @param item item to be resold
     */
    public void resellItem(Item item){
        int index = 0;
        for(Item items: itemsCurrentlySelling){
            if(item == items){
                index++;
            }
        }
        if(index > 0){
            itemsCurrentlySelling.remove(item);
            Item temp = new Item(item.getName(), 0, item.getId(),
                    this);
            startItemThread(temp);
            itemsCurrentlySelling.add(temp);
        }
    }

    /**
     * This method is mainly used in the Item class in order to send a
     * message to an agent
     * @param message message to be send
     * @param agentID agent ID to which the message should be sent
     * @throws InterruptedException handling Interrupted exception
     */
    public void sendMessageToAgent(String message, String agentID)
            throws InterruptedException {
        processMessageToSend("agent", message, agentID);
    }

    /**
     * This method is mainly used in the Item class in order to send a
     * message to the bank
     * @param message message to send
     * @throws InterruptedException handling interrupted exception
     */
    public void sendMessageToBank(String message) throws InterruptedException {
        processMessageToSend("bank", message, "");
    }

    /**
     * This method is used in order to parse a String and return the integers
     * within that string in order
     *
     * This method is used in the methods which process any message that is
     * sent to the Auction House
     * and is used specifically to extract the information of the integers
     * within the messsages
     *
     * First, we start going through the string and then if we find an
     * integer,  we add it to a temp string
     * If we find a space, we add that temp string to a list as an Integer
     * and restart the temp string
     *
     * Continue this until the message is fully looked at
     * @param s string to be passed in
     * @return List of integers
     */
    private List<Integer> parseLine(String s){

        List<Integer> list = new ArrayList<>();

        String integerToAdd = "";
        for(int i = 0; i < s.length(); i++){
            if(s.charAt(i) == ' '){
                if(!integerToAdd.equals("")){
                    list.add(Integer.parseInt(integerToAdd));
                    integerToAdd = "";
                }
            }
            else{
                if(isInteger(String.valueOf(s.charAt(i)))){
                    integerToAdd += s.charAt(i);
                    if(i == s.length() - 1){
                        list.add(Integer.parseInt(integerToAdd));
                    }
                }
            }
        }

        return list;
    }

    /**
     * This method is used within the parseLine() method in order to  check
     * if a String is able to
     * be turned into an Integer.
     * @param input string to be checked for
     * @return flag indicating true or false.
     */
    private boolean isInteger(String input) {
        try {
            Integer.parseInt(input);
            return true;
        }
        catch(NumberFormatException e) {
            return false;
        }
    }

    /**
     * This is used to shut down the Auction House and checks the items to
     * see if any are currently being bid on at the moment. If not, then it
     * is able to shut down.
     */
    private void terminateAuctionHouse(){
        /* check for bidding activity in order to exit */
        boolean canShutDown = true;

        for(Item item: itemsCurrentlySelling){
            if (!item.isAbleToShutDown()) {
                canShutDown = false;
                break;
            }
        }
        if(canShutDown){
            System.out.println("Exiting the Auction House, bye");
            System.exit(1);
        }
        else{
            System.out.println("Bidding in progress, cannot exit the Auction " +
                    "House");
        }

    }

    /**
     * class for UI interactions with the bank.
     * This is strictly for debugging purposes. Runs on its separate thread.
     */
    public class UInteractions implements Runnable {
        private BufferedReader stdIn;

        /**
         * This is the main constructor for the UInteractions class
         * @param stdIn Buffered reader from standard input
         */
        UInteractions(BufferedReader stdIn) {
            this.stdIn = stdIn;
        }

        /**
         * This method is used for some testing and to terminate
         * the auction house.
         */
        @Override
        public void run() {
            String toProcess;
            /* Read a command from input */
            try {
                while (true) {
                    if ((toProcess = stdIn.readLine()).equals("exit")){
                        terminateAuctionHouse();
                    }
                    String[] userInput = toProcess.split(" ");

                    /*
                    This is for testing purposes
                     */
                    if(userInput[0].equals("connections")){
                        Set<String> temp = agentToPort.keySet();
                        for(String str: temp){
                            System.out.println(str);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
