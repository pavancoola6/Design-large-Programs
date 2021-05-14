# Distributed Auction 
Authors: Pavan Singara, Kunj Bhavsar and Anmol Singh Gill
CS351L : Final Project

# Bank 

## Running the Bank
To run the jar file make sure to add port id (example: 5000 or 4444) to the command line argument. This class acts as a server for auction houses and agents. This class should be ran first, and it takes only one command line argument; the argument will be the port number that the bank should run on. The number can be either 4444 or 5000. However, these port number are only
suggestions, though still, the port number can't be too big or too small and should be an open port. It should also be an integer. 


## Bank handling of Bids and Money Transfers 

Let's go over some of the bidding logic. For starters, it is handled in the bank's processIncomingMessage method. As a summary, an agent will bid to auction house, that auction house will ask the bank to block funds.The bank will inform the auction house of status of the bid. Blocked funds will be released whenever an agent loses a bid or fails to transfer funds on time. We have effectively implemented the "Auction Rules", described in the pdf, step by step.

Whenever an agent has won a bid, it can transfer funds. This is done by removing money from the agent's blocked funds. So, if the agent is not bidding and tries to transfer money, it will be stopped. The same applies if the agent tries to transfer too much money when it doesn't have enough. While the clients and UI are handles on separate threads, processIncomingMessage is synchronized, meaning that account manipulation will only be done by one client at a time. The bank also
handles any matters related to account set up and creation. For example, it checks if agent ID's already exist for an auction house. That way, the auction house can easily set up connections with agents. The bank tracks connected clients via their sockets mapped to an InBoundConnectionsHandler, allowing messages to directly be sent to clients, if needed. 

Accounts are stored in maps that map ID's to their balances. Any auction houses connected to the bank are also stored in a map, allowing agents to request for current connections.One final note: the untranferFunds method may seem out of place at first. However, it is there to stop agents from say, bidding 10 dollars on an item, and trying to only transfer 9 dollars.

Changing balances:
The default balances for the agent and auction houses are 100 and 0, respectively. Here are the locations of these constants.
line 47 and line 48 in Bank.java

# Auction House 

## Running The AuctionHouse

To Run the Auction House you can run the command line program with no command line arguments. You will then be prompted to list a port number that you would like to run on. Pick something like 5001 or 5002, but make sure that your number does not match the number of other AuctionHouses or the Bank. Finally, you will be prompted to give the IP address of the Bank and the port number of the bank. The port number will probably be something like 5000 and the IP is given by the computer that the Bank is being run on. If the Bank is being run on the same computer as the Auction House that you are attempting to start then all that is necessary to type is localhost.

## Bidding 

### Item

The Item class is how the Auction House defines things to sell and is what the Agents are trying to bid on. This class has a subclass which is named Timer and keeps track of the timing between bids or waits to check if the Agent has sent the required funds to purchase an Item one bidding is no longer in progress.

There are different states that the Item can be in at any given time which are defined by the booleans bidding and sold in the run function of the Item thread. The first stage of the item is the bidding progress which is given when bidding is equal to true. During this time it will wait to start the bidding. Once the bidding has started (defined by the first bid placed on an item), it will then wait for interruptions. Interruptions can occur from outbidding someone else OR YOURSELF (see Logic additional rules). For the second state of the item, it will be in the selling state. During this time, the Item will either sell the item, or start the selling process. If the item exits the selling process without actually being sold, then a message will be sent to the bank to release the blocked funds of the agent and a message will be sent to the agent specifying that they did not pay the Auction House quickly enough and have lost the bid. Finally, if the Item exits the selling state and was sold then it will send a message to the Agent stating that the Agent has won that Item.

### Logic 
Initially, the Auction House sells three items with random minimum selling values between 5 and 15. If the Auction House receives a request to bid on an item, several checks are made in order to make sure that they input is robust. These include does the item they are bidding on exist, is that item still in the bidding process, did they send enough money to reach the minimum bid, etc. If the Auction House decides that it is a legal bid, a message is then sent to the Bank in order to request that the Bank block their funds for the amount that they are bidding on. The Bank then replies to the Auction House with either a “blocked” or “bid was invalid” message. If the bid was invalid, then a message is sent to the agent telling them that they were not able to bid on the item. If their funds were blocked, then the item is then sent a message to either start a timer or to interrupt an ongoing timer. This timer lasts for 30 seconds and waits for bidding to occur. In addition to updating the Item’s Timer it also updates the Item’s bidding status (current highest bid in dollars), agentID for the highest bidding agent, the minimum bid, and send a message to the Agent that bid on the item saying that their bid was successful. Finally, if the timer was interrupted instead of started then a message is sent to the current highest bidder that they were outbid on that item. If there are no bids within 30 seconds, then a message is sent to the Agent with the current highest bid stating that it is time to pay for the item. It is then their responsibility to transfer the funds to the Auction House that is selling them the item through the bank. If they send enough money to buy more than one item that they are eligible to buy, then they will receive both Items. If they send more money than needed, then they are refunded that amount and notified that they have been refunded for a specific amount of money. Finally, if they do not transfer the money within the given time (30 seconds) then the item is resold with a new minimum value between 5-15. If an Item is sold, then a new item will be listed immediately afterward for a random minimum values between 5 and 15.

Some important facts include that you can outbid yourself at any time. We decided to keep this in the program in the case that someone decided that they wanted to bid higher in order to maintain their place as highest bidder.


# Agent 

The Agent class is one of the client parts of the ditributed auction program. It creates outbound connections with the bank and auction houses and uses their services. In short the Agent class is the UI for this project. This class is dynamic as it creates a new agent every time we run it. As the agent.java file is executed the main method starts by asking to connect to the bank. We can connect to the bank using the IP address and the port number on which the bank waits for connections. As soon as the agent is connected to the bank, an outbound communication thread with the bank is created. After establishing the communication with the bank a while loop is started which waits for user inputs until the program is exited. 

The very first element that the agent does is connect to the Bank. The reader will note that for every part of this project a connection to the Bank is essential. When the connection with the bank is set, the bank provides the agent with an account and a unique agentID. We have chosen the initial amount of each Agent’s bank account to be 100. This can be easily modified in the code (line 69 in Bank.java). At this point, we have also decided not to let the user deposit money in an Agent account. Consequently, once an Agent reaches an account balance of 0, it will not be able to conduct any additional bids.  

The agent can also connect to auction houses. We have decided (for simplicity purposes) that connections to auction houses will be automatic. That is, as soon as an auction house is created after the agent is running, the bank will send a message to the Agent and the connection Agent-AuctionHouse will be automatically made. A similar process is implemented for Auction Houses that have been created before the user/agent connects to the bank. However, we have also decided to implement a way for the user to manually connect to an Auction House in case errors or unexpected behavior might occur. 

Finally, one of the most important aspects of the Agent is the bidding system, explained in more detail in the Auction House and Bank parts of the Readme. The agent will be able to bid on several items as proposed by the current auction houses it is connected to. When an agent makes a bid, it will have to check with the bank if it has sufficient funds in its bank account. Whenever the bid is placed and won, the agent will have 30 seconds to transfer the money from the bank to the auction house’s account. To do so, the user will have to ask the bank to transfer money. Note that we have also implemented cases: 

* The Agent is reimbursed if it transfers more money than the original bid :euro:
* The transfer is blocked if the transfer amount is lower than the current bid :no_entry_sign:
* Two or more items can be won simultaneously if the agent transfers the sum of both bids to the auction houses' account :pound:

We realized that using the agent for the first time might not necessarily be a straightforward task. Consequently, we have incorporated in our agent implementation a handy “help” menu that displays all the possible commands and their related description. At any time during the simulation, the user can simply type the command “help” to figure out which command to use and what steps to take in order to communicate with the bank or with an auction house. 


# Communication

As mentioned in our Design document, all the communications implementations of the distributed auction program are conducted via sockets. Using sockets seemed intimidating at first, but after basing our code and implementations on the KnockKnock Client-Server example given by Oracle, setting up the communications in our design made much more sense. 

We have divided our communications in two major elements: InBoundConnections (Server-side) and OutBoundConnections (Client-side). Both these elements are two fully separate classes implemented in our code. These two connections, however, work hand in hand. An outBoundConnectionsHandler will request a new connection to the InboundConnectionsHandler. When this request is accepted, a communication channel will be created between both entities. 

This section of the readme is purely informative and does not affect the way the user should interact and run our program as all of the communications will be handled internally. However, there are certain elements that might be important to repeat:

* The Bank needs to run first. Without a Bank, the Agents and Auction Houses are pointless. It will be impossible to set up a bidding system :warning:
* The Bank and Auction House servers will need to listen for incoming connections on open, available and unique ports. Otherwise the request will be rejected. :construction:
* Connections between an agent and an auction house will be automatic. That means that at all times, an agent should be connected to all existing auction house (or at least all the auction houses that are currently connected to the Bank) :repeat:
* In case the automatic connections should fail, we have provided a way to set up a manual connection by inputing *auction_house connect* in the agent's CLI. :books:
* For any additional information, especially while controlling and running the agent, type *help* in the agent's CLI to display a help menu :question:

## InboundConnectionsHandler

Two parts of our project will act as Servers: The Bank and the Auction Houses. The Bank will act as a Server for all of its communications. The Auction Houses will only act as Servers only for its communications with Agents. The inbound communications very much resemble the KnockKnockServer example given by Oracle: Listen for new connections, when a new connection arrives, start a new thread to handle that connection. Consequently, every single connection from Bank to Auction House or Agent and from Auction House to Agent, will be launched on a separate independent thread. As mentioned in the design document, inbound communications can be reduced to a single purpose: infinitely wait for incoming message from a client. When a message is read, process that message by sending it back to Bank or Auction House (depending on the current connection), and send a response back on the received message to the client. In a few lines, we have defined the purpose of the InBoundConnectionsHandler. It is, indeed, a rather straightforward implementation. 

## OutBoundConnectionsHandler

Two parts of our project will act as Clients: the Agents and the Auction Houses. The Agents will act as a client for all of their communication while the Auction Houses will act as client only for their communications with the Bank. For the Agent, a new OutBoundConnectionsHandler thread in the Agent is started every time it connects to a new Auction House. Consequently, an Agent will have many different instances of OutBoundConnnectionsHandler, each running on an independent, separate thread. It will have one handler for the Bank, and several for the Auction Houses. As mentioned earlier, we have decided to automatically connect an Agent to an Auction House, whenever this Auction House appears. Thus, each time an Auction House is created and connects to the Bank, the Agent will instantiate a new instance of OutBoundConnectionsHandler and request a connection to the Auction House. For connections to the Bank, both the Agent and the Auction House will have to manually enter the Bank’s IP address and port number in order to request a new connection between the Auction House/Agent OutBoundConnectionsHandler and the Bank’s listening InBoundConnectionsHandler. 

# Bugs

We have tried to test our code as much as possible to avoid any sort of bug or exception. Nevertheless, it is not assures that the user might not find a bug here and there while running the code. 

An important aspect for the Reader to note is the way we have implemented our exit system for each element of this project. The Bank, Auction House and Agent all have the possibility to take "exit" as an input argument. This argument should (hence the name) exit the program that is currently running. For instance, if the Agent receives an "exit" as command line argument, it will quit. This behavior also affects the Bank and Auction House. However, we have added several options to this implementation. First, it is impossible for an Agent and Auction House to exit if a bid is currently in progress. We have made sure that if the user inputs "exit" on either the Agent or the Auction House and a bid is currently in progress, none of them will exit. Instead, they will ask the user to complete the bid and exit afterwards. 

When an Agent or Auction House exits, the Bank is notified of a loss of connection and will adapt its information accordingly but will continue running. However, if a bank exits, all the existing connections to the bank will be forced to exit, independently of whether a bid is currently in process or not. We have decided to adopt such an implementation as we have recognized that the Bank is a clear point of centralization of the project and therefore, if this point fails, the whole program should fail. 

Please feel free to ask for any additional questions regarding our project!

