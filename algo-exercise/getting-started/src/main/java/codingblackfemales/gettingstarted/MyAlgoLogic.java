package codingblackfemales.gettingstarted;

import codingblackfemales.action.Action;
import codingblackfemales.action.CancelChildOrder;
import codingblackfemales.action.CreateChildOrder;
import codingblackfemales.action.NoAction;
import codingblackfemales.algo.AlgoLogic;
import codingblackfemales.sotw.ChildOrder;
import codingblackfemales.sotw.ChildFill;
import codingblackfemales.sotw.SimpleAlgoState;
import codingblackfemales.sotw.marketdata.AbstractLevel;
import codingblackfemales.sotw.marketdata.AskLevel;
import codingblackfemales.sotw.marketdata.BidLevel;
import codingblackfemales.util.Util;
import messages.order.Side;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ObjectUtils.Null;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyAlgoLogic implements AlgoLogic {

    private static final Logger logger = LoggerFactory.getLogger(MyAlgoLogic.class);

    private int tickCount = 0;

    // variables to store data from the current tick
    private AbstractLevel bestBidOrderInCurrentTick;
    private double bestBidPriceInCurrentTick;
    private double bestBidQuantityInCurrentTick;


    private AbstractLevel bestAskOrderInCurrentTick;
    private double bestAskPriceInCurrentTick;
    private double bestAskQuantityInCurrentTick;

    private double theSpreadInCurrentTick;
    private double midPriceInCurrentTick;
    private double relativeSpreadInCurrentTick;


    private double totalQuantityOfAskOrdersInCurrentTick; // from top 10 orders
    private double totalQuantityOfBidOrdersInCurrentTick; // from top 10 orders

    // lists to store data from multiple orders in the current tick
    private List<AbstractLevel> topBidOrdersInCurrentTick = new ArrayList<>(); // top 10 ask orders
    private List<Double> pricesOfTopBidOrdersInCurrentTick = new ArrayList<>(); // from top 10 ask orders
    private List<Double> quantitiesOfTopBidOrdersInCurrentTick = new ArrayList<>(); // from top 10 ask orders

    private List<AbstractLevel> topAskOrdersInCurrentTick = new ArrayList<>(); // top 10 ask orders
    private List<Double> pricesOfTopAskOrdersInCurrentTick = new ArrayList<>(); // from top 10 ask orders
    private List<Double> quantitiesOfTopAskOrdersInCurrentTick = new ArrayList<>(); // from top 10 ask orders

    // getters to retrieve data from the current tick
    public AbstractLevel getBestBidOrderInCurrentTick() { // TODO TEST METHOD
        return bestBidOrderInCurrentTick;
    }


    public double getBestBidPriceInCurrentTick() {
        return bestBidPriceInCurrentTick;
    }

    public double getBestBidQuantityInCurrentTick() {
        return bestBidQuantityInCurrentTick;
    }

    public AbstractLevel getBestAskOrderInCurrentTick() { // TODO TEST METHOD
        return bestAskOrderInCurrentTick;
    }
    
    public double getBestAskPriceInCurrentTick() {
        return bestAskPriceInCurrentTick;
    }

    public double getBestAskQuantityInCurrentTick() {
        return bestAskQuantityInCurrentTick;
    }


    public double getTheSpreadInCurrentTick() {
        return theSpreadInCurrentTick;
    }

    public double getMidPriceInCurrentTick() {
        return midPriceInCurrentTick;
    }

    public double getRelativeSpreadInCurrentTick() {
        return relativeSpreadInCurrentTick;
    }

    public List<AbstractLevel> getTopBidOrdersInCurrentTick() {
        return topBidOrdersInCurrentTick;
    }

    public List<Double> getPricesOfTopBidOrdersInCurrentTick() { // top 10
        return pricesOfTopBidOrdersInCurrentTick;
    }

    public List<Double> getQuantitiesOfTopBidOrdersInCurrentTick() { // top 10
        return quantitiesOfTopBidOrdersInCurrentTick;
    }

    public List<AbstractLevel> getTopAskOrdersInCurrentTick() {
        return topAskOrdersInCurrentTick;
    }

    public List<Double> getPricesOfTopAskOrdersInCurrentTick() { // top 10
        return pricesOfTopAskOrdersInCurrentTick;
    }

    public List<Double> getQuantitiesOfTopAskOrdersInCurrentTick() { // top 10
        return quantitiesOfTopAskOrdersInCurrentTick;
    }



    // for analysing supply and demand for the instrument

    private double setTotalQuantityOfBidOrdersInCurrentTick() { // top 10
        return totalQuantityOfBidOrdersInCurrentTick = sumOfAllInAListOfDoubles(getQuantitiesOfTopBidOrdersInCurrentTick());
    }

    public double getTotalQuantityOfBidOrdersInCurrentTick() { // top 10
        return totalQuantityOfBidOrdersInCurrentTick;
    }

    private double setTotalQuantityOfAskOrdersInCurrentTick() { // top 10
        return totalQuantityOfAskOrdersInCurrentTick = sumOfAllInAListOfDoubles(getQuantitiesOfTopAskOrdersInCurrentTick());
    }

    public double getTotalQuantityOfAskOrdersInCurrentTick() { // top 10
        return totalQuantityOfAskOrdersInCurrentTick;
    }


    // Historical data from most recent ticks (up to the 10 most recent ticks)
    private List<Double> historyOfBestBidPrice = new LinkedList<>();
    private List<Double> historyOfBestAskPrice = new LinkedList<>();
    private List<Double> historyOfTheSpread = new LinkedList<>();
    private List<Double> historyOfMidPrice = new LinkedList<>();
    private List<Double> historyOfRelativeSpread = new LinkedList<>();
    private List<Double> historyOfTotalQuantityOfBidOrders = new LinkedList<>();
    private List<Double> historyOfTotalQuantityOfAskOrders = new LinkedList<>();

    // getters to access lists of historical data
    public List<Double> getHistoryOfBestBidPrice() {
        return historyOfBestBidPrice;
    }

    public List<Double> getHistoryOfBestAskPrice() {
        return historyOfBestAskPrice;
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

    public List<Double> getHistoryOfTotalQuantityOfBidOrders() {
        return historyOfTotalQuantityOfBidOrders;
    }

    public List<Double> getHistoryOfTotalQuantityOfAskOrders() {
        return historyOfTotalQuantityOfAskOrders;
    }



    // variable to cap items of data to analyse
    int MAX_ITEMS_OF_DATA = 10;

    // method to populate lists of numerical data capped at 10 items
    private void addDataToAList(List<Double> list, double data) {
        list.add(data);
        if (list.size() > MAX_ITEMS_OF_DATA) {
            list.remove(0); // remove oldest piece of data
        }
    }

    // methods to populate lists of orders on both sides
    private void addToListOfOrders(List<AbstractLevel> listOfOrders, AbstractLevel order) {
        listOfOrders.add(order);
    }


    // method to calculate sum of all doubles in a list
    private double sumOfAllInAListOfDoubles(List<Double> list) { // TODO - unit test
        return list.stream().reduce(Double::sum).get();
    }

    // method to calculate average of all doubles in a list
    private double averageOfDoublesInAList(List<Double> list) { // TODO - unit test
        return sumOfAllInAListOfDoubles(list) / list.size();
    }

    // method to calculate a percentage change of any given data
    private double calculatePercentageChange(double firstValue, double secondValue) { // TODO - unit test
        return (((double) secondValue - firstValue) / firstValue * 100);
    }


    // Create filtered lists of all child BUY orders
    private List<ChildOrder> allChildBidOrdersList = new ArrayList<>(); 
    List<String> allChildBidOrdersListToString = new ArrayList<>(); // TODO - delete when no longer needed, using for logging statements for now
    List<String> allChildOrdersListToString = new ArrayList<>(); // TODO - delete when no longer needed, using for logging statements for now
    private List<ChildOrder> unfilledChildBidOrdersList = new ArrayList<>(); 
    List<String> unfilledChildBidOrdersListToString = new ArrayList<>(); // TODO - delete when no longer needed, using for logging statements for now
    private List<ChildOrder> filledAndPartFilledChildBidOrdersList = new ArrayList<>();
    List<String> filledAndPartFilledChildBidOrdersListToString = new ArrayList<>(); // for calculating and updating entryPrice and for updating total shares owned
    // private List<ChildOrder> partFilledChildBuyOrdersList = new ArrayList<>();// TODO - for potentially cancelling what's remaining 
    // List<String> listOfPartFilledChildBuyOrdersToString = new ArrayList<>(); // TODO - delete when no longer needed, using for logging statements for now
    private long totalFilledBidQuantity = 0;

    private boolean haveUnfilledBidOrders = false;
    private boolean haveFilledBidOrders = false;
    private ChildOrder unfilledChildBidOrderWithLowestPrice= null;
    private String unfilledChildBidOrderWithLowestPriceToString = ""; // TODO - delete when no longer needed, using for logging statements for now

    
    public List<ChildOrder> getAllChildBidOrdersList() { // TODO - unit test
        return allChildBidOrdersList;
    }

    private Set<ChildOrder> bidOrdersMarkedAsUnfilled = new HashSet<>();

    public List<ChildOrder> getUnfilledChildBidOrdersList() { // TODO - unit test
        return unfilledChildBidOrdersList;
    }

    public ChildOrder getUnfilledChildBidOrderWithLowestPrice() { // TODO - unit test
        return unfilledChildBidOrderWithLowestPrice;
    }

    // HashSet to prevent duplication in list of filled and part filled orders list
    private Set<ChildOrder> bidOrdersMarkedAsFilledOrPartFilled = new HashSet<>();

    // List of filled orders as an ArrayList to preserve the sequential order of filled and part filled orders
    // and maintain state across ticks to track changes over time
    public List<ChildOrder> getFilledAndPartFilledChildBidOrdersList() { // TODO - unit test
        return filledAndPartFilledChildBidOrdersList;
    }

    // public List<ChildOrder> getPartFilledChildBuyOrdersList () { // TODO - unit test
    //     return partFilledChildBuyOrdersList;
    // }

    // Create filtered lists of all child ASK orders
    private List<ChildOrder> allChildAskOrdersList = new ArrayList<>();
    List<String> allChildAskOrdersListToString = new ArrayList<>();
    private List<ChildOrder> unfilledChildAskOrdersList = new ArrayList<>(); // TODO
    List<String> unfilledChildAskOrdersListToString = new ArrayList<>(); // TODO - delete when no longer needed, using for logging statements for now
    private List<ChildOrder> filledAndPartFilledChildAskOrdersList = new ArrayList<>(); // TODO
    List<String> filledAndPartFilledChildAskOrdersListToString = new ArrayList<>(); // TODO - delete when no longer needed, using for logging statements for now
    private long totalFilledAskQuantity = 0;

    private boolean haveUnfilledAskOrders = false;
    private boolean haveFilledAskOrders = false;
    private ChildOrder unfilledChildAskOrderWithHighestPrice= null;
    private String unfilledChildAskOrderWithHighestPriceToString = ""; // TODO - delete when no longer needed, using for logging statements for now

    public List<ChildOrder> getUnfilledChildAskOrdersList() { // TODO - unit test
        return unfilledChildAskOrdersList;
    }

    // HashSet to prevent duplication in list of filled and part filled orders list
    private Set<ChildOrder> askOrdersMarkedAsFilledOrPartFilled = new HashSet<>();

    // List of filled orders as an ArrayList to preserve the sequential order of filled and part filled orders
    // and maintain state across ticks to track changes over time
    public List<ChildOrder> getFilledAndPartFilledChildAskOrdersList() { // TODO - unit test
        return filledAndPartFilledChildAskOrdersList;
    }



    private long childBidOrderQuantity;
    private long passiveChildBidOrderPrice;

    private long passiveChildAskOrderPrice;
    private long passiveChildAskOrderQuantity;

    private long averageEntryPrice; // VWAP of all child fills on the BID / BUY side
    private long totalExpenditure; // use to calculate and update profit and loss over time
    private long totalRevenue; // use to calculate and update profit and loss over time
    private long totalProfitOrLoss;
    private long numOfSharesOwned;
    private double stopLoss; // to be updated with the entryPrice and/or VWAP
    private long profitOrLossOnThisTrade = passiveChildAskOrderPrice - averageEntryPrice; //  SCRAP THIS??

    // TODO - cap on spending after initial investment? Stretch task

    private void setTotalExpenditure() {
        totalExpenditure = getFilledAndPartFilledChildBidOrdersList().stream()
            .mapToLong(order -> order.getFilledQuantity() * order.getPrice())
            .sum();
    }
    
    
    public long getTotalExpenditure() { //TODO test this method
        return totalExpenditure;
    }
    

    private void setTotalRevenue() {
        totalRevenue = getFilledAndPartFilledChildAskOrdersList().stream()
            .mapToLong(order -> order.getFilledQuantity() * order.getPrice())
            .sum();
    }
    
    public long getTotalRevenue() { //TODO test this method
        return totalRevenue;
    }
    
    private void setTotalProfitOrLoss() {
        totalProfitOrLoss = getTotalRevenue() - getTotalExpenditure();
    }
    
    public long getTotalProfitOrLoss() { // top 10 // TODO - TEST THIS METHOD
        return totalProfitOrLoss;
    }

    private void setTotalFilledBidQuantity() {
        totalFilledBidQuantity = getFilledAndPartFilledChildBidOrdersList().stream()
        .mapToLong(ChildOrder::getFilledQuantity)
        .sum();
    }

    public long getTotalFilledBidQuantity() {
        return totalFilledBidQuantity;
    }

    private void setAverageEntryPrice() {  
        averageEntryPrice = getFilledAndPartFilledChildBidOrdersList().stream()
            .mapToLong(order -> order.getFilledQuantity() * order.getPrice())
            .sum() / getTotalFilledBidQuantity();
    }

    public double getAverageEntryPrice() { // top 10 // TODO - TEST THIS METHOD
        return averageEntryPrice;
    }

    private void setStopLoss() {
        stopLoss = getAverageEntryPrice() * 0.99;
    }

    public double getStopLoss() { // top 10 // TODO - TEST THIS METHOD
        return stopLoss;
    }

    private void setTotalFilledAskQuantity() {
        totalFilledAskQuantity = getFilledAndPartFilledChildAskOrdersList().stream()
        .mapToLong(ChildOrder::getFilledQuantity)
        .sum();
    }

    public long getTotalFilledAskQuantity() { // TODO - TEST THIS METHOD
        return totalFilledAskQuantity;
    }


    private void setNumOfSharesOwned() {
        numOfSharesOwned = getTotalFilledBidQuantity() - getTotalFilledAskQuantity();
    }

    public long getNumOfSharesOwned(){  // TODO - TEST THIS METHOD
        return numOfSharesOwned;
    }


       // set and get childOrder quantity // TODO - CHANGE THIS TAKING INTO ACCOUNT PERCENTAGE FACTOR
    private void setChildBidOrderQuantity() {
        childBidOrderQuantity = (long) Math.round((getTotalQuantityOfBidOrdersInCurrentTick() * 0.1)); // set POV to 10%
    }

    public long getChildBidOrderQuantity() { //TODO - unit test this method
        return childBidOrderQuantity;
    }

    private void setPassiveChildBidOrderPrice() {
        passiveChildBidOrderPrice = (long) (getBestBidPriceInCurrentTick() - 2 + tickCount);
    }

    public long getPassiveChildBidOrderPrice() { //TODO - unit test this method
        return passiveChildBidOrderPrice;
    }

    private void setPassiveChildAskOrderQuantity() {
        passiveChildAskOrderQuantity = (long) (getTotalFilledBidQuantity() * 0.33); // set POV to 10%
    }


    public long getPassiveChildAskOrderQuantity() { //TODO - unit test this method
        return passiveChildAskOrderQuantity;
    }

    private void setPassiveChildAskOrderPrice() {
        passiveChildAskOrderPrice = (long) (getBestAskPriceInCurrentTick() + 2 + tickCount); // todo
    }

    public long getPassiveChildAskOrderPrice() { //TODO - unit test this method
        return passiveChildAskOrderPrice;
    }
    // ******************************EVALUATE METHOD
    // CALL***************************************
    // ******************************EVALUATE METHOD
    // CALL**************************************
    @Override
    public Action evaluate(SimpleAlgoState state) {

        var orderBookAsString = Util.orderBookToString(state);

        logger.info("[MYALGO] The state of the order book is:\n" + orderBookAsString);

        // gather tick data for analysis

        bestAskOrderInCurrentTick = state.getAskAt(0);
        bestBidOrderInCurrentTick = state.getBidAt(0);

        bestAskPriceInCurrentTick = bestAskOrderInCurrentTick.price;
        bestAskQuantityInCurrentTick = bestAskOrderInCurrentTick.quantity;

        bestBidPriceInCurrentTick = bestBidOrderInCurrentTick.price;
        bestBidQuantityInCurrentTick = bestBidOrderInCurrentTick.quantity;

        theSpreadInCurrentTick = bestAskPriceInCurrentTick - bestBidPriceInCurrentTick;
        midPriceInCurrentTick = (bestAskPriceInCurrentTick + bestBidPriceInCurrentTick) / 2;
        
        // Maths round to limit to 2dp
        relativeSpreadInCurrentTick = Math.round((theSpreadInCurrentTick / midPriceInCurrentTick * 100) * 100 / 100);


        // Loop to populate lists of data about the top ask orders in the current tick
        int maxAskOrders = Math.min(state.getAskLevels(), 10); // up to a max of 10 ask orders
        getTopAskOrdersInCurrentTick().clear();
        getPricesOfTopAskOrdersInCurrentTick().clear();
        getQuantitiesOfTopAskOrdersInCurrentTick().clear();
        for (int i = 0; i < maxAskOrders; i++) {
            AbstractLevel askOrder = state.getAskAt(i);
            addToListOfOrders(topAskOrdersInCurrentTick, askOrder);
            addDataToAList(pricesOfTopAskOrdersInCurrentTick, askOrder.price);
            addDataToAList(quantitiesOfTopAskOrdersInCurrentTick, askOrder.quantity);
        }
        setTotalQuantityOfAskOrdersInCurrentTick();

        // Loop to populate lists of data about the top bid orders in the current tick
        int maxBidOrders = Math.min(state.getBidLevels(), MAX_ITEMS_OF_DATA); // up to a max of 10 bid orders
        topBidOrdersInCurrentTick.clear();
        pricesOfTopBidOrdersInCurrentTick.clear();
        quantitiesOfTopBidOrdersInCurrentTick.clear();
        for (int i = 0; i < maxBidOrders; i++) {
            AbstractLevel bidOrder = state.getBidAt(i);
            addToListOfOrders(topBidOrdersInCurrentTick, bidOrder);
            addDataToAList(pricesOfTopBidOrdersInCurrentTick, bidOrder.price);
            addDataToAList(quantitiesOfTopBidOrdersInCurrentTick, bidOrder.quantity);
        }
        setTotalQuantityOfBidOrdersInCurrentTick();

        // add data to historical data of most recent ticks
        addDataToAList(getHistoryOfBestAskPrice(), getBestAskPriceInCurrentTick());
        addDataToAList(getHistoryOfTotalQuantityOfAskOrders(), getTotalQuantityOfAskOrdersInCurrentTick());

        addDataToAList(getHistoryOfBestBidPrice(), getBestBidPriceInCurrentTick());
        addDataToAList(getHistoryOfTotalQuantityOfBidOrders(), getTotalQuantityOfBidOrdersInCurrentTick());

        addDataToAList(getHistoryOfTheSpread(), getTheSpreadInCurrentTick());
        addDataToAList(getHistoryOfRelativeSpread(), getRelativeSpreadInCurrentTick());
        addDataToAList(getHistoryOfMidPrice(), getMidPriceInCurrentTick());

        
        

        // Populate and update filtered lists of MyAlgo's child orders on the BID/BUY side
        allChildBidOrdersList.clear();
        allChildBidOrdersListToString.clear();
        allChildBidOrdersList = state.getChildOrders().stream()
            .filter(order -> order.getSide() == Side.BUY)
            .peek(order-> allChildBidOrdersListToString // TODO DELETE LATER ONLY FOR OUTPUT DURING DEVELOPMENT FOR BACK TESTS
            .add("CHILD BID Id:" + order.getOrderId() + " [" + order.getQuantity() + "@" + order.getPrice() + "]")) // TODO DELETE LATER URING DEVELOPMENT FOR BACK TESTS
            .collect(Collectors.toList());  


        // list of unfilled child bid orders
        unfilledChildBidOrdersList.clear();
        unfilledChildBidOrdersListToString.clear();
        unfilledChildBidOrdersList = state.getChildOrders().stream()
            .filter(order -> order.getSide() == Side.BUY && order.getFilledQuantity() == 0)
            .peek(order-> unfilledChildBidOrdersListToString // TODO DELETE LATER ONLY FOR OUTPUT DURING DEVELOPMENT FOR BACK TESTS
            .add("UNFILLED BID Id:" + order.getOrderId() + " [" + order.getQuantity() + "@" + order.getPrice() + "]")) // TODO DELETE LATER URING DEVELOPMENT FOR BACK TESTS
            .collect(Collectors.toList());  

        // if there are unfilled child bid orders 
        if (!unfilledChildBidOrdersList.isEmpty()) {
            haveUnfilledBidOrders = true;
            //update the bid with the lowest price
            unfilledChildBidOrderWithLowestPrice = unfilledChildBidOrdersList.stream()
                .min((order1, order2) -> Long.compare(order1.getPrice(), order2.getPrice()))
                .orElse(null);  // handle the case when min() returns an empty Optional
        }

        // TODO delete entire if/else statement for string creation later - for now, useful for development and debugging purposes only
        if (unfilledChildBidOrderWithLowestPrice != null) {
            unfilledChildBidOrderWithLowestPriceToString = "unfilledChildBidOrderWithLowestPrice Id:" 
                + unfilledChildBidOrderWithLowestPrice.getOrderId() + " [" 
                + unfilledChildBidOrderWithLowestPrice.getQuantity() + "@" 
                + unfilledChildBidOrderWithLowestPrice.getPrice() + "]";
        } else {
            unfilledChildBidOrderWithLowestPriceToString = "No unfilled child bid orders found";
        }
        
        filledAndPartFilledChildBidOrdersList = state.getChildOrders().stream()
        .filter(order -> order.getSide() == Side.BUY && order.getFilledQuantity() > 0)
        .filter(order -> !bidOrdersMarkedAsFilledOrPartFilled.contains(order))  // Only add if not processed
        .peek(order -> bidOrdersMarkedAsFilledOrPartFilled.add(order))  // Mark as processed
        .peek(order-> filledAndPartFilledChildBidOrdersListToString // TODO DELETE LATER ONLY FOR OUTPUT DURING DEVELOPMENT FOR BACK TESTS
        .add("FILL/PARTFILL BID Id:" + order.getOrderId() + " [" + order.getQuantity() + "@" + order.getPrice() + "] filledQuantity: " + order.getFilledQuantity())) // TODO DELETE LATER URING DEVELOPMENT FOR BACK TESTS
        .collect(Collectors.toList());

        // if there are filled BID Orders
        if (!filledAndPartFilledChildBidOrdersList.isEmpty()) { 
            haveFilledBidOrders = true;
            setTotalFilledBidQuantity(); // update total bid 
            setAverageEntryPrice(); // update the average entry price
            setStopLoss(); // update the stop loss
            setTotalExpenditure(); // update the total expenditure
        }

        // Populate and update filtered lists of MyAlgo's child orders on the ASK/SELL side
        allChildAskOrdersList.clear();
        allChildAskOrdersListToString.clear();
        allChildAskOrdersList = state.getChildOrders().stream()
            .filter(order -> order.getSide() == Side.SELL)
            .peek(order-> allChildAskOrdersListToString // TODO DELETE LATER ONLY FOR OUTPUT DURING DEVELOPMENT FOR BACK TESTS
            .add("CHILD ASK Id:" + order.getOrderId() + " [" + order.getQuantity() + "@" + order.getPrice() + "]")) // TODO DELETE LATER URING DEVELOPMENT FOR BACK TESTS
            .collect(Collectors.toList());  

        // list of unfilled child ask orders
        unfilledChildAskOrdersList.clear();
        unfilledChildAskOrdersListToString.clear();
        unfilledChildAskOrdersList = state.getChildOrders().stream()
            .filter(order -> order.getSide() == Side.SELL && order.getFilledQuantity() == 0)
            .peek(order -> unfilledChildAskOrdersListToString // TODO DELETE LATER URING DEVELOPMENT FOR BACK TESTS
            .add("UNFILLED ASK Id:" + order.getOrderId() + " [" + order.getQuantity() + "@" + order.getPrice() + "]"))  // TODO DELETE LATER ONLY FOR OUTPUT DURING DEVELOPMENT FOR BACK TESTS
            .collect(Collectors.toList());


        // if there are unfilled child ask orders update the ask with the highest price
        if (!unfilledChildAskOrdersList.isEmpty()) {
            haveUnfilledAskOrders = true;
                unfilledChildAskOrderWithHighestPrice = unfilledChildAskOrdersList.stream()
                    .max((order1, order2) -> Long.compare(order1.getPrice(), order2.getPrice()))
                    .orElse(null);  // handle the case when max() returns an empty Optional
            }

        // TODO delete entire if/else statement for string creation later - for now, useful for development and debugging purposes only
        if (unfilledChildAskOrderWithHighestPrice != null) {
            unfilledChildAskOrderWithHighestPriceToString = "unfilledChildAskOrderWithHighestPrice Id:" 
                + unfilledChildAskOrderWithHighestPrice.getOrderId() + " [" 
                + unfilledChildAskOrderWithHighestPrice.getQuantity() + "@" 
                + unfilledChildAskOrderWithHighestPrice.getPrice() + "]";
        } else {
            unfilledChildAskOrderWithHighestPriceToString = "No unfilled child ask orders found";
        }
        
        filledAndPartFilledChildAskOrdersList = state.getChildOrders().stream()
        .filter(order -> order.getSide() == Side.SELL && order.getFilledQuantity() > 0)
        .filter(order -> !askOrdersMarkedAsFilledOrPartFilled.contains(order))  // Only add if not processed
        .peek(order -> askOrdersMarkedAsFilledOrPartFilled.add(order))  // Mark as processed
        .peek(order-> filledAndPartFilledChildAskOrdersListToString // TODO DELETE LATER ONLY FOR OUTPUT DURING DEVELOPMENT FOR BACK TESTS
        .add("FILL/PARTFILL ASK Id:" + order.getOrderId() + " [" + order.getQuantity() + "@" + order.getPrice() + "] filledQuantity: " + order.getFilledQuantity())) // TODO DELETE LATER URING DEVELOPMENT FOR BACK TESTS
        .collect(Collectors.toList());

        // if there are filled ASK Orders
        if (!filledAndPartFilledChildAskOrdersList.isEmpty()) { 
            haveFilledAskOrders = true;
            setTotalFilledAskQuantity();
            setTotalRevenue(); // update the total revenue
        }
    
        setChildBidOrderQuantity(); // TODO - rethink the logic of these
     


        logger.info("[MYALGO tickCount : " + (tickCount) + " \n");

        // logger.info("allChildOrdersToString is: " + allChildOrdersToString);
        logger.info("unfilledChildBidOrdersListToString is: " + unfilledChildBidOrdersListToString); // showing it's filled
        logger.info("unfilledChildBidOrderWithLowestPriceToString is: " + unfilledChildBidOrderWithLowestPriceToString);
        logger.info("filledAndPartFilledChildBidOrdersListToString is: " + filledAndPartFilledChildBidOrdersListToString + "\n");  
        logger.info("haveFilledBidOrders is: " + haveFilledBidOrders);
        logger.info("unfilledChildAskOrdersListToString is: " + unfilledChildAskOrdersListToString);  
        logger.info("unfilledChildAskOrderWithHighestPriceToString is: " + unfilledChildAskOrderWithHighestPriceToString);  
        
        logger.info("getTotalExpenditure() is: " + getTotalExpenditure());
        logger.info("getTotalRevenue() is: " + getTotalRevenue());
        logger.info("getTotalProfitOrLoss() is: " + getTotalProfitOrLoss());
        logger.info("getTotalFilledBidQuanity() is: " + getTotalFilledBidQuantity());
        logger.info("getAverageEntryPrice() is: " + getAverageEntryPrice());
        logger.info("getStopLoss() is: " + getStopLoss());
        logger.info("getTotalFilledAskQuanity() is: " + getTotalFilledAskQuantity());
        logger.info("getNumOfSharesOwned() is: " + getNumOfSharesOwned());

        
        


        logger.info("[MYALGO CURRENT TICK DATA: \n");

        logger.info("[MYALGO the topAskOrdersInCurrentTick are: " + getTopAskOrdersInCurrentTick().toString());
        logger.info("[MYALGO bestAskOrderInCurrentTick is : " + getBestAskOrderInCurrentTick());

        logger.info("[MYALGO the topBidOrdersInCurrentTick are: " + getTopBidOrdersInCurrentTick().toString());
        logger.info("[MYALGO bestBidOrderInCurrentTick is : " + getBestBidOrderInCurrentTick());


        logger.info("[MYALGO relativeSpreadInCurrentTick is: " + getRelativeSpreadInCurrentTick() + "\n");

        logger.info("[MYALGO the totalQuantityOfAskOrdersInCurrentTick is: "
                + getTotalQuantityOfAskOrdersInCurrentTick() + "\n");
        logger.info("[MYALGO the totalQuantityOfBidOrdersInCurrentTick is: "
                + getTotalQuantityOfBidOrdersInCurrentTick() + "\n");

        // logger.info("[MYALGO HISTORICAL TICK DATA : \n ");
        // logger.info("[MYALGO getHistoryOfBestAskPrice() is: " + getHistoryOfBestAskPrice().toString());
        // logger.info("[MYALGO getHistoryOfTotalQuantityOfAskOrders() is: "
        //         + getHistoryOfTotalQuantityOfAskOrders().toString());
        // logger.info("[MYALGO getHistoryOfBestBidPrice() is: " + getHistoryOfBestBidPrice().toString());
        // logger.info("[MYALGO getHistoryOfTotalQuantityOfBidOrders() is: "
        //         + getHistoryOfTotalQuantityOfBidOrders().toString());
        // logger.info("[MYALGO getHistoryOfRelativeSpread() is: " + getHistoryOfRelativeSpread().toString()+ "\n");

        // logger.info("[MYALGO my current entryPrice is: " + getEntryPrice());
        // logger.info("[MYALGO my current totalProfit is: " + getTotalProfit());
        // logger.info("[MYALGO my current stopLoss is: " + getStopLoss() + "\n");

        logger.info("[MYALGO ENTERING MY ALGO\'S BUY / SELL LOGIC \n");

         // up to a max of 3 ask orders
        if (allChildAskOrdersList.size() >= 3) {
            logger.info("Condition 'allChildBidOrdersList.size() >= 3' met. TickCount is: " + tickCount);
            tickCount += 1;
            return NoAction.NoAction;
            
        // If I have filled bid orders 
        } else if ((haveFilledBidOrders == true)) { // adapt later for when spread is wide
            setPassiveChildAskOrderQuantity(); //  TODO - rethink the logic of these
            setPassiveChildAskOrderPrice(); //  TODO - rethink the logic of these
            setNumOfSharesOwned();
            setTotalProfitOrLoss();
            logger.info("Condition 'haveFilledBidOrders == true' met. TickCount is: " + tickCount);
            tickCount += 1;
            // place passive ask orders
            return new CreateChildOrder(Side.SELL, getPassiveChildAskOrderQuantity(), getPassiveChildAskOrderPrice());

        // check if an unfilled bid order becomes too uncompetitive, if so, cancel it
        } else if ((haveUnfilledBidOrders == true) 
            && (unfilledChildBidOrderWithLowestPrice.getPrice() < (bestBidPriceInCurrentTick - 5))) {
                    logger.info("[MYALGO]: Cancelling bid order " 
                            + unfilledChildBidOrderWithLowestPrice.getOrderId() + " "
                            + unfilledChildBidOrderWithLowestPrice.getQuantity()
                            + "@" + unfilledChildBidOrderWithLowestPrice.getPrice()
                            + " because it has become too uncompetitive. TickCount is: " + tickCount);     
                    tickCount += 1;
                    return new CancelChildOrder(unfilledChildBidOrderWithLowestPrice); // TODO - backtest to test that we get cancelled orders

        // check if an unfilled ask order becomes too uncompetitive, if so, cancel it
        } else if ((haveUnfilledAskOrders == true)
                && (unfilledChildAskOrderWithHighestPrice.getPrice() > (bestAskPriceInCurrentTick + 5))) {
                    logger.info("[MYALGO]: Cancelling ask order "
                        + unfilledChildAskOrderWithHighestPrice.getOrderId() + " "
                        + unfilledChildAskOrderWithHighestPrice.getQuantity()
                        + "@" + unfilledChildAskOrderWithHighestPrice.getPrice()
                        + " because it has become too uncompetitive. TickCount is: " + tickCount);
                        tickCount += 1;
                    return new CancelChildOrder(unfilledChildAskOrderWithHighestPrice); // TODO - backtest to test that we get cancelled orders
        
        // up to a max of 3 bid orders
        } else if (allChildBidOrdersList.size() >= 5) {
            logger.info("Condition 'allChildBidOrdersList.size() >= 3' met. TickCount is: " + tickCount);
            tickCount += 1;
            return NoAction.NoAction;
        } else {
            setPassiveChildBidOrderPrice();
            setChildBidOrderQuantity(); // TODO - rethink the logic of these
            logger.info("[MYALGO]: Placing passive bid orders.  TickCount is: " + tickCount);
            tickCount += 1; 
            return new CreateChildOrder(Side.BUY, getChildBidOrderQuantity(), (long) getPassiveChildBidOrderPrice());
        }

        
    }
}
        



// ******************* TODO - take some logic from below to future iterations of algo logic ********
//         // If I have filled orders and spread is wide, place passive sell order

//         // If I have no active orders, place 3 child orders to join the best bid
//         if (state.getChildOrders().size() < 3) {
//             // entryQuantity = // TODO - further abstract to antoher method to calculate order quantity
//             entryPrice = (long) bestBidPriceInCurrentTick + evaluateMethodCallCount; // TODO - further abstract to antoher method to calculate order entryPrice
//             logger.info("[MYALGO] Currently have: " + state.getChildOrders().size()
//             + " children, want 3, joining best bid with: " + 100 + " @ " + entryPrice);
//             // BEFORE RETURN STATEMENT
//             // add to evaluate call count
//             // calculate profitOrLossOnThisTrade
//             // update totalProfit
//             // update entryPrice
//             // update stopLoss
//             evaluateMethodCallCount += 1; // use this logic to adjust quantities and prices
//             //private entryPrice
//             return new CreateChildOrder(Side.BUY, 100, (long) entryPrice );
//         } else {

//             // if (filledQuantity > 0) {

//             // entryPrice = filledChildOrders.get(0).getPrice();

//             if (bestBidPriceInCurrentTick >= entryPrice * 1.01) {
//                 long profitOnThisTrade = (long) (totalChildFilledQuantity * 0.25)
//                         * (entryPrice - (long) bestBidPriceInCurrentTick);
//                 totalProfit = profitOnThisTrade;

//                 logger.info("[MYALGO] profitOnThisTrade is: " + profitOnThisTrade);
//                 logger.info("[MYALGO] totalProfit is: " + totalProfit);
//                 return new CreateChildOrder(Side.SELL, (long) (totalChildFilledQuantity * 0.25),
//                         (long) bestBidPriceInCurrentTick);
//             }
//         }
//         logger.info("[MYALGO] Currently have: " + state.getChildOrders().size() + " child orders. No action");

//         // BEFORE RETURN STATEMENT
//         // add to evaluate call count
//         // calculate profitOrLossOnThisTrade
//         // update totalProfit
//         // update entryPrice
//         // update stopLoss
//         evaluateMethodCallCount += 1;
//         return NoAction.NoAction;
//     }
// }
