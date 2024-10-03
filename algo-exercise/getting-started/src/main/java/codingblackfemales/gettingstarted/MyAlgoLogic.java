package codingblackfemales.gettingstarted;

import codingblackfemales.action.Action;
import codingblackfemales.action.NoAction;
import codingblackfemales.algo.AlgoLogic;
import codingblackfemales.sotw.SimpleAlgoState;
import codingblackfemales.sotw.marketdata.AskLevel;
import codingblackfemales.sotw.marketdata.BidLevel;
import codingblackfemales.util.Util;

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

    public List<Double> getPricesOfTopAskOrders() { // top 10
        return pricesOfTopAskOrders;
    }

    public List<Double> getPricesOfTopBidOrders() { // top 10
        return pricesOfTopBidOrders;
    }

    // for calculating total sum and average
    public List<Double> getQuantitiesOfTopAskOrders() { // top 10
        return quantitiesOfTopAskOrders;
    }

    // for calculating total sum and average
    public List<Double> getQuantitiesOfTopBidOrders() { // top 10
        return quantitiesOfTopBidOrders;
    }

    // for analysing supply and demand for the instrument
    public double getTotalQuantityOfAskOrders() {  // top 10
        return totalQuantityOfAskOrders;
    }

    public double getTotalQuantityOfBidOrders() { // top 10
        return totalQuantityOfBidOrders;
    }



    // LATEST HISTORY OF PREVIOUS TICKS (UP TO 10 MOST RECENT TICKS)

    // multiple tick data for trend spotting
    // lists to store data from most recent data ticks
    // LinkedLists for trend spotting
    private List<Double> latestBestAskPrices = new LinkedList<>();
    private List<Double> latestBestBidPrices = new LinkedList<>();
    private List<Double> latestSpreads = new LinkedList<>();
    private List<Double> latestMidPrices = new LinkedList<>();
    private List<Double> latestTotalQuantitiesOfAskOrders = new LinkedList<>();
    private List<Double> latestTotalQuantitiesOfBidOrders = new LinkedList<>();

    // getters to access data within lists of most recent data ticks
    public List<Double> getLatestBestAskPrices() {
        return latestBestAskPrices;
    }

    public List<Double> getLatestBestBidPrices() {
        return latestBestBidPrices;
    }

    public List<Double> getLatestSpreads() {
        return latestSpreads;
    }

    public List<Double> getLatestMidPrices() {
        return latestMidPrices;
    }

    public List<Double> getLatestTotalQuantitiesOfAskOrders() {
        return latestTotalQuantitiesOfAskOrders;
    }

    public List<Double> getLatestTotalQuantitiesOfBidOrders() {
        return latestTotalQuantitiesOfBidOrders;
    }


    // variable to cap items of data to analyse
    int MAX_ITEMS_OF_DATA = 10;

    // method to populate lists of data capped at 10 items
    public void addDataToAList(List<Double> list, double data) {
        list.add(data);
        if (list.size() > MAX_ITEMS_OF_DATA) {
            list.remove(0);  // remove oldest piece of data
        }
    }

    /*
     * TODO
     * 
     * DATA CAPTURE
     * method to calculate sum of all items in a list
     * method to calculate the average of all items in a list
     * 
     * method to access filled orders
     * method to calculate VWAP of filled orders
     * list to store recent history of VWAP
     *  
     * LOGIC
     * method to create passive bid order - at best bid price
     * method to create passive ask order - at best ask price
     * method to create passive "baity" bid order - one tick size above current best bid price
     * method to create passive "baity" ask order - one tick size below best ask price
     * method to create aggressive bid order - at best ask price
     * method to create aggressive ask order - at best bid price
     * 
     * method to cancel an order
     * method to establish what position (of the queue of buyers) I am in on the bid side
     * method to establish what position (of the queue of sellers) I am in on the ask side
     * 
     * 
     * if / else logic to decide which bid / ask strategy to take and at what quantity
     */





    // method to calculate a percentage change of any given data
    public double calculatePercentageChange(double firstValue, double secondValue) {
        return Math.abs(((double) firstValue - secondValue) / firstValue * 100);
    }

    @Override
    public Action evaluate(SimpleAlgoState state) {

        var orderBookAsString = Util.orderBookToString(state);

        logger.info("[MYALGO] The state of the order book is:\n" + orderBookAsString);

        logger.info("[MYALGO] The tick data for analysis is as follows:\n");

        // gather tick data for analysis
        final AskLevel bestAskOrder = state.getAskAt(0);
        logger.info("[MYALGO] The best ask order is:" + state.getAskAt(0));

        final AskLevel secondBestAskOrder = state.getAskAt(1);
        logger.info("[MYALGO] The 2nd best ask order is:" + state.getAskAt(1));

        final AskLevel thirdBestAskOrder = state.getAskAt(2);
        logger.info("[MYALGO] The 3rd best ask order is:" + state.getAskAt(2));

        final BidLevel bestBidOrder = state.getBidAt(0);
        logger.info("[MYALGO] The best bid order is:" + state.getBidAt(0));

        final BidLevel secondBestBidOrder = state.getBidAt(1);
        logger.info("[MYALGO] The 2nd best bid order is:" + state.getBidAt(1));

        final BidLevel thirdBestBidOrder = state.getBidAt(2);
        logger.info("[MYALGO] The 3rd best bid order is:" + state.getBidAt(2));

        // TODO - CREATE A LIST OF UP TO 10 TOP BEST BIDS TO CALCULATE THEIR TOTAL
        // QUANTITIES
        // AND TO CALCULATE THE AVERAGE QUANTITIES

        bestAskPrice = bestAskOrder.price;
        logger.info("[MYALGO] The best ask price is:" + bestAskPrice);

        bestBidPrice = bestBidOrder.price;
        logger.info("[MYALGO] The best bid price is:" + bestBidPrice);

        theSpread = bestAskPrice - bestBidPrice;
        logger.info("[MYALGO] The bid ask spread is:" + theSpread);

        midPrice = (bestAskPrice + bestBidPrice) / 2;
        logger.info("[MYALGO] The mid price is:" + midPrice);

        bestAskQuantity = bestAskOrder.quantity;
        logger.info("[MYALGO] The quantity of the best ask order is:" + bestAskQuantity);

        bestBidQuantity = bestBidOrder.quantity;
        logger.info("[MYALGO] The quantity of the best bid order is:" + bestBidQuantity);

        addDataToAList(latestBestAskPrices, bestAskPrice);
        addDataToAList(latestBestBidPrices, bestBidPrice);
        addDataToAList(latestSpreads, theSpread);
        addDataToAList(latestBestBidPrices, midPrice);
  

        /********
         *
         * Add your logic here....
         *
         */

        return NoAction.NoAction;
    }
}
