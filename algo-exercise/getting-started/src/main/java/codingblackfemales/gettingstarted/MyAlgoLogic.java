package codingblackfemales.gettingstarted;

import codingblackfemales.action.Action;
import codingblackfemales.action.CreateChildOrder;
import codingblackfemales.action.NoAction;
import codingblackfemales.algo.AlgoLogic;
import codingblackfemales.sotw.SimpleAlgoState;
import codingblackfemales.sotw.marketdata.AskLevel;
import codingblackfemales.sotw.marketdata.BidLevel;
import codingblackfemales.util.Util;
import messages.order.Side;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyAlgoLogic implements AlgoLogic {

    private static final Logger logger = LoggerFactory.getLogger(MyAlgoLogic.class);

    // variables to store data from the current tick
    public double bestAskPrice;
    public double bestBidPrice;
    public double theSpread;
    public double midPrice;

    public double bestAskQuantity;
    public double bestBidQuantity;

    public double totalQuantityOfAskOrders; // from top 10 orders
    public double totalQuantityOfBidOrders; // from top 10 orders

    // lists to store data from multiple orders in the current tick
    private List<AskLevel> topAskOrdersOfCurrentTick = new ArrayList<>(); // top 10 ask orders
    private List<BidLevel> topBidOrdersOfCurrentTick = new ArrayList<>(); // top 10 ask orders

    private List<Double> pricesOfTopAskOrders = new ArrayList<>(); // from top 10 ask orders
    private List<Double> pricesOfTopBidOrders = new ArrayList<>(); // from top 10 ask orders
    private List<Double> quantitiesOfTopAskOrders = new ArrayList<>(); // from top 10 ask orders
    private List<Double> quantitiesOfTopBidOrders = new ArrayList<>(); // from top 10 ask orders

    // getters to retrieve data from the current tick
    public double getBestAskPrice() {
        return bestAskPrice;
    }

    public double getBestBidPrice() {
        return bestBidPrice;
    }

    public double getTheSpread() {
        return theSpread;
    }

    public double getMidPrice() {
        return midPrice;
    }

    public double getBestAskQuantity() {
        return bestAskQuantity;
    }

    public double getBestBidQuantity() {
        return bestBidQuantity;
    }

    public Object getTopAskOrdersOfCurrentTick() {
        return topAskOrdersOfCurrentTick;
    }

    public Object getTopBidOrdersOfCurrentTick() {
        return topBidOrdersOfCurrentTick;
    }

    public List<Double> getPricesOfTopAskOrders() { // top 10
        return pricesOfTopAskOrders;
    }

    public List<Double> getPricesOfTopBidOrders() { // top 10
        return pricesOfTopBidOrders;
    }

    public List<Double> getQuantitiesOfTopAskOrders() { // top 10
        return quantitiesOfTopAskOrders;
    }

    public List<Double> getQuantitiesOfTopBidOrders() { // top 10
        return quantitiesOfTopBidOrders;
    }

    // for analysing supply and demand for the instrument
    public double getTotalQuantityOfAskOrders() { // top 10
        return totalQuantityOfAskOrders;
    }

    public double getTotalQuantityOfBidOrders() { // top 10
        return totalQuantityOfBidOrders;
    }

    // LATEST HISTORY OF PREVIOUS TICKS (UP TO 10 MOST RECENT TICKS)

    // multiple tick data for trend spotting
    // lists to store data from most recent data ticks
    // LinkedLists for trend spotting
    private List<Double> historyOfBestAskPrice = new LinkedList<>(); // variable naming "historical"
    private List<Double> historyOfBestBidPrice = new LinkedList<>();
    private List<Double> historyOfTheSpread = new LinkedList<>();
    private List<Double> historyOfMidPrice = new LinkedList<>();
    private List<Double> historyOfTotalQuantitiesOfAskOrders = new LinkedList<>();
    private List<Double> historyOfTotalQuantitiesOfBidOrders = new LinkedList<>();

    // getters to access data within lists of most recent data ticks
    public List<Double> getHistoryOfBestAskPrice() {
        return historyOfBestAskPrice;
    }

    public List<Double> getHistoryOfBestBidPrice() {
        return historyOfBestBidPrice;
    }

    public List<Double> getHistoryOfTheSpread() {
        return historyOfTheSpread;
    }

    public List<Double> getHistoryOfMidPrice() {
        return historyOfMidPrice;
    }

    public List<Double> getHistoryOfTotalQuantitiesOfAskOrders() {
        return historyOfTotalQuantitiesOfAskOrders;
    }

    public List<Double> getistoryOfTotalQuantitiesOfBidOrders() {
        return historyOfTotalQuantitiesOfBidOrders;
    }

    // variable to cap items of data to analyse
    int MAX_ITEMS_OF_DATA = 10;

    // method to populate lists of numerical data capped at 10 items
    public void addDataToAList(List<Double> list, double data) {
        list.add(data);
        if (list.size() > MAX_ITEMS_OF_DATA) {
            list.remove(0); // remove oldest piece of data
        }
    }

    // methods to populate lists of orders on both sids  
    public void addToListOfAskOrders(List<AskLevel> listOfAskOrders, AskLevel askOrder) {
        listOfAskOrders.add(askOrder);
    }

    public void addToListOfBidOrders(List<BidLevel> listOfBidOrders, BidLevel bidOrder) {
        listOfBidOrders.add(bidOrder);
    }

    /*
     * TODO DATA CAPTURE
     * 
     * method to calculate sum of all items in a list
     * method to calculate the average of all items in a list
     * 
     * method to access filled orders
     * method to calculate VWAP of filled orders
     * list to store recent history of VWAP
     */

    // method to calculate a percentage change of any given data
    public double calculatePercentageChange(double firstValue, double secondValue) {
        return Math.abs(((double) firstValue - secondValue) / firstValue * 100);
    }

    public long childOrderQuantity;

    // set childOrder quantity
    public long setChildOrderQuantity() {
        // TODO - write logic to decide / change quantity depending on conditions e.g.
        // bestAskQuantity
        return childOrderQuantity;
    }

    @Override
    public Action evaluate(SimpleAlgoState state) {

        var orderBookAsString = Util.orderBookToString(state);

        logger.info("[MYALGO] The state of the order book is:\n" + orderBookAsString);

        logger.info("[MYALGO] The tick data for analysis is as follows:\n");

        // gather tick data for analysis

        // Loop to add to a list of the current top ask orders
        int maxAskOrders = Math.min(state.getAskLevels(), 10); // up to a max of 10 ask orders
        topAskOrdersOfCurrentTick.clear();
        for (int i = 0; i < maxAskOrders; i++) {
            AskLevel askOrder = state.getAskAt(i);
            addToListOfAskOrders(topAskOrdersOfCurrentTick, askOrder);
        }

        // Loop to populate a list of the top bid orders in the current tick
        int maxBidOrders = Math.min(state.getBidLevels(), MAX_ITEMS_OF_DATA); // up to a max of 10 bid orders
        topBidOrdersOfCurrentTick.clear();
        for (int i = 0; i < maxBidOrders; i++) {
            BidLevel bidOrder = state.getBidAt(i);
            addToListOfBidOrders(topBidOrdersOfCurrentTick, bidOrder);
        }

        // TODO
        final AskLevel bestAskOrder = state.getAskAt(0);
        logger.info("[MYALGO] The top best ask orders in the current tick are: " + getTopAskOrdersOfCurrentTick());

        final BidLevel bestBidOrder = state.getBidAt(0);
        logger.info("[MYALGO] The top best bid orders in the current tick are: " + getTopBidOrdersOfCurrentTick());

        long loggerCheck1 = (topAskOrdersOfCurrentTick.get(0).price);
        logger.info("[MYALGO] The price of the first item in the list of best asks is: " + loggerCheck1);

        long loggerCheck2 = (topAskOrdersOfCurrentTick.get(0).quantity);
        logger.info("[MYALGO] The quantity of the first item in the list of best asks is: " + loggerCheck2);


        // TODO - CREATE A LIST OF UP TO 10 TOP BEST BIDS TO CALCULATE THEIR TOTAL
        // QUANTITIES
        // AND TO CALCULATE THE AVERAGE QUANTITIES

        bestAskPrice = bestAskOrder.price;
        logger.info("[MYALGO] The best ask price is: " + bestAskPrice);

        bestAskQuantity = bestAskOrder.quantity;
        logger.info("[MYALGO] The best ask quantity is: " + bestAskQuantity);

        bestBidPrice = bestBidOrder.price;
        logger.info("[MYALGO] The best bid price is: " + bestBidPrice);

        bestBidQuantity = bestBidOrder.quantity;
        logger.info("[MYALGO] The best bid quantity is: " + bestBidQuantity);

        theSpread = bestAskPrice - bestBidPrice;
        logger.info("[MYALGO] The spread is: " + theSpread);

        midPrice = (bestAskPrice + bestBidPrice) / 2;
        logger.info("[MYALGO] The mid price is: " + midPrice);

        bestAskQuantity = bestAskOrder.quantity;
        logger.info("[MYALGO] The quantity of the best ask order is: " + bestAskQuantity);

        bestBidQuantity = bestBidOrder.quantity;
        logger.info("[MYALGO] The quantity of the best bid order is: " + bestBidQuantity);

        addDataToAList(historyOfBestAskPrice, bestAskPrice);
        addDataToAList(historyOfBestBidPrice, bestBidPrice);
        addDataToAList(historyOfTheSpread, theSpread);
        addDataToAList(historyOfMidPrice, midPrice);

        logger.info("[MYALGO] The history of the best Ask price is: " + historyOfBestAskPrice);
        logger.info("[MYALGO] The history of the best Bid price is: " + historyOfBestBidPrice);
        logger.info("[MYALGO] The history of the spread is: " + historyOfTheSpread);
        logger.info("[MYALGO] The history of the mid price is: " + historyOfMidPrice);


        /*
         * TODO LOGIC
         * 
         * 
         * 
         * 
         * method to cancel an order
         * method to establish what position (of the queue of buyers) I am in on the bid
         * side
         * method to establish what position (of the queue of sellers) I am in on the
         * ask side
         * 
         * 
         * if / else or case logic to decide which bid / ask strategy to take and at
         * what quantity
         */

        // TODO - BUILD UP ALGO LOGIC ITERATIVELY

        // FIRSTLY - BUY WHEN LESS THAN
        // CASE STATEMENTS

        /*
         * if (conditional statement: execute PASSIVE BUY ORDER @ BEST BID PRICE) {
         * return new CreateChildOrder(Side.BUY, childOrderQuantity, bestBidPrice);
         * } else if (conditional statement: execute PASSIVE SELL ORDER @ BEST ASK
         * PRICE) {
         * return new CreateChildOrder(Side.ASK, childOrderQuantity, bestAskPrice);
         * } else if (conditional statement: execute PASSIVE "BAITY" BUY ORDER @ 1 TICK
         * SIZE ABOVE BEST BID PRICE) {
         * return new CreateChildOrder(Side.BUY, childOrderQuantity, (bestBidPrice +
         * 1)); // assuming the tick size is 1
         * } else if (conditional statement: execute PASSIVE "BAITY" ASK ORDER @ 1 TICK
         * SIZE BELOW BEST ASK PRICE) {
         * return new CreateChildOrder(Side.ASK, childOrderQuantity, (bestAskPrice -
         * 1)); // assuming the tick size is 1
         * } else if (conditional statement: execute AGGRESSIVE BID ORDER @ BEST ASK
         * PRICE) {
         * return new CreateChildOrder(Side.BUY, childOrderQuantity, bestAskPrice);
         * } else if (conditional statement: execute AGGRESSIVE SELL ORDER @ BEST BID
         * PRICE) {
         * return new CreateChildOrder(Side.ASK, childOrderQuantity, bestBidPrice);
         * } else {
         * return NoAction.NoAction;
         */

        // if I have no orders, create an order of quantity 1 and price 1
        if (state.getChildOrders().size() == 0) {
            return new CreateChildOrder(Side.BUY, 1, 1);
        } else {
            return NoAction.NoAction;
        }
    }
}
