/**
 * Authors: Pavan Singara, Kunj Bhavsar and Anmol Singh Gill
 * CS351L : Final Project
 */

 /**
 * This class is used in the Auction House to sell Items
 * These Items are Threads that wait for bidding status' to be sent to them from
 * the Auction House that they belong to. They can then use some Auction House
 * methods in order to give the Auction House messages to send to either an
 * Agent or the Bank.
 */

package AuctionHouse;

import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Item implements Runnable{

    private final String name;
    private int biddingStatus;
    private int minBid;
    private int id;
    private AuctionHouse auctionHouse;
    private int agentIDWithHighestBid;
    private boolean bidding;
    private boolean sold;
    private boolean didSell;
    private boolean ableToShutDown = true;
    private boolean ableToInterrupt = false;
    private Timer timer;
    private Thread t;
    private BlockingQueue<String> input = new LinkedBlockingQueue<>();

    /**
     * This is the constructor for Item class
     * @param name the name of the item
     * @param biddingStatus how much money the current highest bid on the item
     *                      is
     * @param id the id number for an item
     * @param auctionHouse the auction house that the item belongs to
     */
    public Item(String name, int biddingStatus, int id, AuctionHouse
            auctionHouse){
        this.biddingStatus = biddingStatus;
        this.name = name;
        this.id = id;
        this.auctionHouse = auctionHouse;
        bidding = true;
        didSell = false;
        sold = false;
        agentIDWithHighestBid = -1;

        /* random min bid from 5 to 15 */
        Random rand = new Random();
        minBid = rand.nextInt(11) + 5;

        /* 30 seconds */
        timer = new Timer(true, this, 30);
    }

    /**
     * This method is overridden from Runnable and starts upon .start()
     * In this method, it will wait for messages to be put into its blocking
     * queue.
     * For the first stage of the item, it will be in bidding progress as
     *      bidding will be true. During this time it will wait to start the
     *      bidding and wait for interruptions Interruptions can occur from
     *      out bidding someone
     * For the second stage of the item, it will be in the selling process
     *      During this time, the Item will either sell the item, or start
     *      the selling process If the item exits the selling process without
     *      actually being sold, then a message will be sent to the bank to
     *      release the blocked funds of the agent and a message will be sent
     *      to the agent specifying that they did not pay the Auction House
     *      quickly enough and have lost the bid.
     *
     * In the instance of the timer, if the time ran out to give the auction
     * house their money then it will resell the item
     */
    public void run(){
        try {
            /* once notified of the first bid, start the timer */
            while (bidding) {
                if(!input.isEmpty()){
                    String message = input.take();
                    if(message.equals("start")){
                        ableToShutDown = false;
                        ableToInterrupt = true;
                        System.out.println("starting timer");
                        t = new Thread(timer);
                        t.start();
                    }
                    else if(message.equals("interrupt")){
                        System.out.println("Interrupted");
                        t.interrupt();
                        /* 30 seconds */
                        t = new Thread(new Timer(true, this,
                                30));
                        t.start();
                    }
                }
            }
            auctionHouse.sendMessageToAgent("You have won the bid " +
                            "for " + this.name + "." + "\nIt is time to " +
                            "transfer funds!\n" +
                            "You have 30 seconds to transfer the funds to " +
                            "this Auction House or the item will be resold",
                    "A-" + agentIDWithHighestBid);
            /* The bidding is now done. We wait for funds. */
            while(!sold){
                if(!input.isEmpty()){
                    String message = input.take();
                    if(message.equals("sell")){
                        t.interrupt();
                        auctionHouse.sellItem(this);
                        sold = true;
                        didSell = true;
                        /* exit we are done */
                    }
                    else if(message.equals("start2")){
                        /* 30 seconds */
                        t = new Thread(new Timer(true, this,
                                30));
                        t.start();
                    }
                    /*
                     * if it doesnt receive sell after 30 seconds then it is
                     * handled in the timer
                     */
                }
            }
            if(!didSell){
                auctionHouse.sendMessageToAgent("You did not transfer " +
                                "the money quickly enough. The item " +
                                name+ " with itemID of " + id + " is being resold",
                        "A-" + agentIDWithHighestBid);
                auctionHouse.sendMessageToBank("release funds A-" +
                        agentIDWithHighestBid + " " + biddingStatus);
            }

        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    /**
     * These are the main setters for the item class
     * Including:
     * setting the bidding status
     * setting the agent id that is currently bidding on the item
     * setting the minimum bid amount
     */

    public void setBiddingStatus(int biddingStatus) {
        this.biddingStatus = biddingStatus;
    }

    public void setAgentIDWithHighestBid(int agentIDWithHighestBid){
        this.agentIDWithHighestBid = agentIDWithHighestBid;
    }

    public void setMinBid(int minBid){
        this.minBid = minBid;
    }

    /**
     * These are the main getters for the Item class
     * Including:
     * getting the agent ID with the highest bid
     * getting the highest bid amount
     * getting the minimum amount to bid
     * getting the name of the item
     * getting the ID of the item
     * getting the blocking queue to communicate with the item
     * getting whether or not the item is able to shut down
     * getting if the item is able to become interrupted
     * getting if the item is currently in the bidding process
     * getting if the item is currently in the selling process
     * @return
     */

    public int getAgentIDWithHighestBid(){
        return agentIDWithHighestBid;
    }

    public int getBiddingStatus(){
        return this.biddingStatus;
    }

    public int getMinBid(){
        return this.minBid;
    }

    public String getName(){
        return this.name;
    }

    public int getId(){
        return this.id;
    }

    public BlockingQueue<String> getInput() {
        return input;
    }

    public boolean isAbleToShutDown() {
        return ableToShutDown;
    }

    public boolean isAbleToInterrupt(){
        return ableToInterrupt;
    }

    public boolean isBidding(){
        return bidding;
    }

    public boolean isSold(){
        return sold;
    }

    /**
     * This Timer class is a Thread that each Item uses in order to keep
     * track of the bidding status
     * If the Timer is not interrupted that means that it can tell the Item
     * that a specific behavior
     * should be executed. Otherwise, the Timer will exit upon interruption
     * and the Item will start a new Timer.
     */
    private class Timer implements Runnable{

        protected boolean running;
        protected Item item;
        protected int seconds;

        /**
         * This is the main constructor for the Timer class which instantiates
         * needed variables
         * @param running flag indicating if the timer is currently running
         * @param item item on which the timer is running
         * @param seconds number of seconds allowed for the timer
         */
        public Timer(boolean running, Item item, int seconds){
            this.running = running;
            this.item = item;
            this.seconds = seconds;
        }

        /**
         * This run method is overridden from Runnable and is called upon
         * .start() The timer will start and will sleep based on the amount
         * of seconds specified. If the timer is interrupted it will exit the
         * timer. If the timer is not interrupted it will then make checks to
         * change the Item that it was handed The first instance of a timer
         * that has not been interrupted will set bidding to false The second
         * instance of a timer that has not been interrupted will set sold to
         * be true
         */
        public void run(){
            try {
                if (running) {
                    ableToShutDown = false;
                    System.out.println("in timer...");
                    Thread.sleep(seconds * 1000);
                    System.out.println("exiting sleep...");
                    if(!bidding && !didSell){
                        /* resell item */
                        System.out.println("Done with the 20 seconds and no bid");
                        auctionHouse.resellItem(item);
                        sold = true;
                    }
                    else if(bidding){
                        System.out.println("Starting the 20 seconds");
                        bidding = false;
                        input.put("start2");
                    }
                }
            }catch (InterruptedException e){
                System.out.println("exiting timer");
                running = false;
            }
        }
    }
}
