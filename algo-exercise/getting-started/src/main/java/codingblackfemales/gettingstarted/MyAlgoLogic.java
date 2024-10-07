package codingblackfemales.gettingstarted;

import codingblackfemales.action.Action;
import codingblackfemales.action.CreateChildOrder;
import codingblackfemales.action.NoAction;
import codingblackfemales.algo.AlgoLogic;
import codingblackfemales.sotw.ChildOrder;
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
    public double relativeSpread;

    public double bestAskQuantity;
    public double bestBidQuantity;

    public double totalQuantityOfAskOrders; // from top 10 orders
    public double totalQuantityOfBidOrders; // from top 10 orders

    // lists to store data from multiple orders in the current tick
    private List<AskLevel> topAskOrders = new ArrayList<>(); // top 10 ask orders
    private List<Double> pricesOfTopAskOrders = new ArrayList<>(); // from top 10 ask orders
    private List<Double> quantitiesOfTopAskOrders = new ArrayList<>(); // from top 10 ask orders

    private List<BidLevel> topBidOrders = new ArrayList<>(); // top 10 ask orders
    private List<Double> pricesOfTopBidOrders = new ArrayList<>(); // from top 10 ask orders
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

    public double getRelativeSpread() {
        return relativeSpread;
    }

    public double getBestAskQuantity() {
        return bestAskQuantity;
    }

    public double getBestBidQuantity() {
        return bestBidQuantity;
    }

    public List<AskLevel> getTopAskOrders() {
        return topAskOrders;
    }

    public List<Double> getpricesOfTopAskOrders() { // top 10
        return pricesOfTopAskOrders;
    }

    public List<Double> getQuantitiesOfTopAskOrders() { // top 10
        return quantitiesOfTopAskOrders;
    }

    public List<BidLevel> getTopBidOrders() {
        return topBidOrders;
    }

    public List<Double> getPricesOfTopBidOrders() { // top 10
        return pricesOfTopBidOrders;
    }

    public List<Double> getQuantitiesOfTopBidOrders() { // top 10
        return quantitiesOfTopBidOrders;
    }

    // for analysing supply and demand for the instrument
    public double setTotalQuantityOfAskOrders() { // top 10
        return totalQuantityOfAskOrders = sumOfAllInAListOfDoubles(quantitiesOfTopAskOrders);
    }

    public double getTotalQuantityOfAskOrders() { // top 10
        return totalQuantityOfAskOrders;
    }

    public double setTotalQuantityOfBidOrders() { // top 10
        return totalQuantityOfBidOrders = sumOfAllInAListOfDoubles(quantitiesOfTopBidOrders);
    }

    public double getTotalQuantityOfBidOrders() { // top 10
        return totalQuantityOfBidOrders;
    }



    // Historical data from most recent ticks (up to the 10 most recent ticks)

    private List<Double> historyOfBestAskPrice = new LinkedList<>();
    private List<Double> historyOfBestBidPrice = new LinkedList<>();
    private List<Double> historyOfTheSpread = new LinkedList<>();
    private List<Double> historyOfMidPrice = new LinkedList<>();
    private List<Double> historyOfRelativeSpread = new LinkedList<>();
    private List<Double> historyOfTotalQuantityOfAskOrders = new LinkedList<>();
    private List<Double> historyOfTotalQuantityOfBidOrders = new LinkedList<>();

    // getters to access lists of historical data
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

    public List<Double> getHistoryOfRelativeSpread() {
        return historyOfRelativeSpread;
    }

    public List<Double> getHistoryOfTotalQuantityOfAskOrders() {
        return historyOfTotalQuantityOfAskOrders;
    }

    public List<Double> getHistoryOfTotalQuantityOfBidOrders() {
        return historyOfTotalQuantityOfBidOrders;
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

    //method to calculate sum of all doubles in a list
    public double sumOfAllInAListOfDoubles(List<Double> list) {
        return list.stream().reduce(Double::sum).get();
    }

    //method to calculate average of all doubles in a list
    public double averageOfDoublesInAList(List<Double> list) {
        return (list.stream().reduce(Double::sum).get()) / list.size();
    }


    // method to calculate a percentage change of any given data
    public double calculatePercentageChange(double firstValue, double secondValue) {
        return Math.abs(((double) firstValue - secondValue) / firstValue * 100);
    }

    public long childBidOrderQuantity;

    public long childAskOrderQuantity;

    // set childOrder quantity
    public void setChildBidOrderQuantity() {
        childBidOrderQuantity = (long)(totalQuantityOfBidOrders * 0.1); // set POV to 10%
    }

    public void setChildAskOrderQuantity() {
        childAskOrderQuantity = (long)(totalQuantityOfAskOrders * 0.1); // set POV to 10%
    }

    

  
    public long entryPrice;
    public long totalProfit;
    public double stopLoss = entryPrice * 0.99;

    public double getTotalProfit() { // top 10
        return totalProfit;
    }



    @Override
    public Action evaluate(SimpleAlgoState state) {

        var orderBookAsString = Util.orderBookToString(state);

        logger.info("[MYALGO] The state of the order book is:\n" + orderBookAsString);

        // gather tick data for analysis

        final AskLevel bestAskOrder = state.getAskAt(0);
        final BidLevel bestBidOrder = state.getBidAt(0);

        bestAskPrice = bestAskOrder.price;
        bestAskQuantity = bestAskOrder.quantity;

        bestBidPrice = bestBidOrder.price;
        bestBidQuantity = bestBidOrder.quantity;

        theSpread = bestAskPrice - bestBidPrice;
        midPrice = (bestAskPrice + bestBidPrice) / 2;
        relativeSpread = theSpread / midPrice * 100;
        


        // Loop to populate lists of data about the top ask orders in the current tick
        int maxAskOrders = Math.min(state.getAskLevels(), 10); // up to a max of 10 ask orders
        topAskOrders.clear();
        pricesOfTopAskOrders.clear();
        quantitiesOfTopAskOrders.clear();
        for (int i = 0; i < maxAskOrders; i++) {
            AskLevel askOrder = state.getAskAt(i);
            addToListOfAskOrders(topAskOrders, askOrder);
            addDataToAList(pricesOfTopAskOrders, askOrder.price);
            addDataToAList(quantitiesOfTopAskOrders, askOrder.quantity);
        }
        setTotalQuantityOfAskOrders();


        // Loop to populate lists of data about the top bid orders in the current tick
        int maxBidOrders = Math.min(state.getBidLevels(), MAX_ITEMS_OF_DATA); // up to a max of 10 bid orders
        topBidOrders.clear();
        pricesOfTopBidOrders.clear();
        quantitiesOfTopBidOrders.clear();
        for (int i = 0; i < maxBidOrders; i++) {
            BidLevel bidOrder = state.getBidAt(i);
            addToListOfBidOrders(topBidOrders, bidOrder);
            addDataToAList(pricesOfTopBidOrders, bidOrder.price);
            addDataToAList(quantitiesOfTopBidOrders, bidOrder.quantity);
        }
        setTotalQuantityOfBidOrders();
        
        // add data to historical data of most recent ticks
        addDataToAList(historyOfBestAskPrice, bestAskPrice);
        addDataToAList(historyOfBestBidPrice, bestBidPrice);
        addDataToAList(historyOfTheSpread, theSpread);
        addDataToAList(historyOfMidPrice, midPrice);
        addDataToAList(historyOfRelativeSpread, relativeSpread);


        setChildBidOrderQuantity();
        setChildAskOrderQuantity();


        // If I have no active orders, place 3 child orders to join the best bid
        if (state.getActiveChildOrders().size() < 3) {
            logger.info("[MYALGO] Currently have: " + state.getChildOrders().size() + " children, want 3, joining best bid with: " + 100 + " @ " + bestBidPrice);
            entryPrice = (long) bestBidPrice;
            return new CreateChildOrder(Side.BUY, 100, (long)bestBidPrice);
        } else {
            long filledQuantity = state.getChildOrders().stream().map(ChildOrder::getFilledQuantity).reduce(Long::sum).get();
            if (filledQuantity > 0) {
                if (bestBidPrice >= entryPrice * 1.01) {
                    long profitOnThisTrade = (long)(filledQuantity * 0.25) * (entryPrice - (long)bestBidPrice);
                    totalProfit += profitOnThisTrade;
                    logger.info("[MYALGO] profitOnThisTrade is: " + profitOnThisTrade);
                    logger.info("[MYALGO] totalProft is: " + totalProfit);

                    return new CreateChildOrder(Side.SELL, (long)(filledQuantity * 0.25), (long)bestBidPrice);
                }
            }
            logger.info("[MYALGO] Currently have: " + state.getChildOrders().size() + " child orders. No action");
            return NoAction.NoAction;
        }
    }
}
