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
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ObjectUtils.Null;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyAlgoLogic implements AlgoLogic {

    private static final Logger logger = LoggerFactory.getLogger(MyAlgoLogic.class);

    // variables to store data from the current tick
    private AbstractLevel bestAskOrderInCurrentTick;
    private AbstractLevel bestBidOrderInCurrentTick;
    private double bestAskPriceInCurrentTick;
    private double bestBidPriceInCurrentTick;
    private double theSpreadInCurrentTick;
    private double midPriceInCurrentTick;
    private double relativeSpreadInCurrentTick;

    private double bestAskQuantityInCurrentTick;
    private double bestBidQuantityInCurrentTick;

    private double totalQuantityOfAskOrdersInCurrentTick; // from top 10 orders
    private double totalQuantityOfBidOrdersInCurrentTick; // from top 10 orders

    // lists to store data from multiple orders in the current tick
    private List<AbstractLevel> topAskOrdersInCurrentTick = new ArrayList<>(); // top 10 ask orders
    private List<Double> pricesOfTopAskOrdersInCurrentTick = new ArrayList<>(); // from top 10 ask orders
    private List<Double> quantitiesOfTopAskOrdersInCurrentTick = new ArrayList<>(); // from top 10 ask orders

    private List<AbstractLevel> topBidOrdersInCurrentTick = new ArrayList<>(); // top 10 ask orders
    private List<Double> pricesOfTopBidOrdersInCurrentTick = new ArrayList<>(); // from top 10 ask orders
    private List<Double> quantitiesOfTopBidOrdersInCurrentTick = new ArrayList<>(); // from top 10 ask orders

    // getters to retrieve data from the current tick
    
    public AbstractLevel getBestAskOrderInCurrentTick() { // TODO TEST METHOD
        return bestAskOrderInCurrentTick;
    }
    
    public AbstractLevel getBestBidOrderInCurrentTick() { // TODO TEST METHOD
        return bestBidOrderInCurrentTick;
    }
    
    public double getBestAskPriceInCurrentTick() {
        return bestAskPriceInCurrentTick;
    }

    public double getBestAskQuantityInCurrentTick() {
        return bestAskQuantityInCurrentTick;
    }

    public double getBestBidPriceInCurrentTick() {
        return bestBidPriceInCurrentTick;
    }

    public double getBestBidQuantityInCurrentTick() {
        return bestBidQuantityInCurrentTick;
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

    public List<AbstractLevel> getTopAskOrdersInCurrentTick() {
        return topAskOrdersInCurrentTick;
    }

    public List<Double> getPricesOfTopAskOrdersInCurrentTick() { // top 10
        return pricesOfTopAskOrdersInCurrentTick;
    }

    public List<Double> getQuantitiesOfTopAskOrdersInCurrentTick() { // top 10
        return quantitiesOfTopAskOrdersInCurrentTick;
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

    // for analysing supply and demand for the instrument
     // private for security

    private double setTotalQuantityOfAskOrdersInCurrentTick() { // top 10
        return totalQuantityOfAskOrdersInCurrentTick = sumOfAllInAListOfDoubles(quantitiesOfTopAskOrdersInCurrentTick);
    }

    public double getTotalQuantityOfAskOrdersInCurrentTick() { // top 10
        return totalQuantityOfAskOrdersInCurrentTick;
    }

    // private for security
    private double setTotalQuantityOfBidOrdersInCurrentTick() { // top 10
        return totalQuantityOfBidOrdersInCurrentTick = sumOfAllInAListOfDoubles(quantitiesOfTopBidOrdersInCurrentTick);
    }

    public double getTotalQuantityOfBidOrdersInCurrentTick() { // top 10
        return totalQuantityOfBidOrdersInCurrentTick;
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
    private double sumOfAllInAListOfDoubles(List<Double> list) {
        return list.stream().reduce(Double::sum).get();
    }

    // method to calculate average of all doubles in a list
    private double averageOfDoublesInAList(List<Double> list) {
        return sumOfAllInAListOfDoubles(list) / list.size();
    }

    // method to calculate a percentage change of any given data
    private double calculatePercentageChange(double firstValue, double secondValue) {
        return Math.abs(((double) firstValue - secondValue) / firstValue * 100);
    }

    private long childBidOrderQuantity;

    private long childAskOrderQuantity;

    // set childOrder quantity
    private void setChildBidOrderQuantity() {
        childBidOrderQuantity = (long) (totalQuantityOfBidOrdersInCurrentTick * 0.1); // set POV to 10%
    }

    private void setChildAskOrderQuantity() {
        childAskOrderQuantity = (long) (totalQuantityOfAskOrdersInCurrentTick * 0.1); // set POV to 10%
    }

    private long childBuyOrderPrice;
    private long childSellOrderPrice;
    private long entryPrice;
    private long totalProfit;
    private double stopLoss = entryPrice * 0.99;
    private long profitOrLossOnThisTrade = childSellOrderPrice - entryPrice;

    public double getTotalProfit() { // top 10 // TODO - TEST THIS METHOD
        return totalProfit;
    }

    public double getEntryPrice() { // top 10 // TODO - TEST THIS METHOD
        return entryPrice;
    }

    public double getStopLoss() { // top 10 // TODO - TEST THIS METHOD
        return stopLoss;
    }

    private int evaluateMethodCallCount = 0;

    private long totalChildFilledQuantity;

    public double getTotalChildFilledQuantity() { // top 10 // TODO - TEST THIS METHOD
        return totalChildFilledQuantity;
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
            addToListOfOrders(getTopAskOrdersInCurrentTick(), askOrder);
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

        setChildBidOrderQuantity();
        setChildAskOrderQuantity();




        // Create a list of all child orders as strings
        List<String> allChildOrdersListToString = new ArrayList<>();
        List<ChildOrder> unfilledChildBuyOrdersList = new ArrayList<>(); 
        List<String> unfilledChildBuyOrdersListToString = new ArrayList<>();
        List<ChildOrder> filledAndPartFilledChildBuyOrdersList = new ArrayList<>();
        List<String> filledAndPartFilledChildBuyOrdersListToString = new ArrayList<>(); // for calculating and updating entryPrice and for updating total shares owned
        List<ChildOrder> listOfPartFilledChildBuyOrders = new ArrayList<>();// TODO
        List<String> listOfPartFilledChildBuyOrdersToString = new ArrayList<>();// TODO
        List<ChildOrder> listOfCompletelyFilledChildBuyOrders = new ArrayList<>(); // TODO
        List<ChildOrder> listOfUnfilledChildAskOrders = new ArrayList<>(); // TODO

        boolean haveFilledBuyOrders = false;
        ChildOrder unfilledChildBuyOrderWithLowestPrice= null;
        String unfilledChildBuyOrderWithLowestPriceToString = "";

        unfilledChildBuyOrdersList = state.getChildOrders().stream()
            .filter(order -> order.getSide() == Side.BUY && order.getFilledQuantity() == 0)
            .peek(order-> unfilledChildBuyOrdersListToString // TODO DELETE LATER ONLY FOR OUTPUT DURING DEVELOPMENT FOR BACK TESTS
            .add(" Id:" + order.getOrderId() + " [" + order.getQuantity() + "@" + order.getPrice() + "]")) // TODO DELETE LATER URING DEVELOPMENT FOR BACK TESTS
            .collect(Collectors.toList());  

        if (!unfilledChildBuyOrdersList.isEmpty()) {
            unfilledChildBuyOrderWithLowestPrice = unfilledChildBuyOrdersList.stream()
                .min((order1, order2) -> Long.compare(order1.getPrice(), order2.getPrice()))
                .orElse(null);  // handle the case when min() returns an empty Optional
        }

        if (unfilledChildBuyOrderWithLowestPrice != null) {
            unfilledChildBuyOrderWithLowestPriceToString = "unfilledChildBuyOrderWithLowestPrice Id:" 
                + unfilledChildBuyOrderWithLowestPrice.getOrderId() + " [" 
                + unfilledChildBuyOrderWithLowestPrice.getQuantity() + "@" 
                + unfilledChildBuyOrderWithLowestPrice.getPrice() + "]";
        } else {
            unfilledChildBuyOrderWithLowestPriceToString = "No unfilled child buy orders found";
        }


        logger.info("[MYALGO EVALUATE METHOD CALL NUMBER : " + (evaluateMethodCallCount + 1) + " \n");

        // logger.info("allChildOrdersToString is: " + allChildOrdersToString);
        logger.info("unfilledChildBuyOrdersListToString is: " + unfilledChildBuyOrdersListToString); // showing it's filled
        logger.info("unfilledChildBuyOrderWithLowestPriceToString is: " + unfilledChildBuyOrderWithLowestPriceToString); // showing it's filled


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

        logger.info("[MYALGO HISTORICAL TICK DATA : \n ");
        logger.info("[MYALGO getHistoryOfBestAskPrice() is: " + getHistoryOfBestAskPrice().toString());
        logger.info("[MYALGO getHistoryOfTotalQuantityOfAskOrders() is: "
                + getHistoryOfTotalQuantityOfAskOrders().toString());
        logger.info("[MYALGO getHistoryOfBestBidPrice() is: " + getHistoryOfBestBidPrice().toString());
        logger.info("[MYALGO getHistoryOfTotalQuantityOfBidOrders() is: "
                + getHistoryOfTotalQuantityOfBidOrders().toString());
        logger.info("[MYALGO getHistoryOfRelativeSpread() is: " + getHistoryOfRelativeSpread().toString());

        // logger.info("[MYALGO my current entryPrice is: " + getEntryPrice());
        // logger.info("[MYALGO my current totalProfit is: " + getTotalProfit());
        // logger.info("[MYALGO my current stopLoss is: " + getStopLoss() + "\n");

        logger.info("[MYALGO ENTERING MY ALGO\'S BUY / SELL LOGIC \n");

         //make sure we have an exit condition...
        if (state.getChildOrders().size() > 3) {
            return NoAction.NoAction;
        // when a buy order becomes too uncompetitive, cancel it
        } else if ((unfilledChildBuyOrdersList.size() > 0) 
            && (unfilledChildBuyOrderWithLowestPrice.getPrice() < (bestBidPriceInCurrentTick - 10))) {
                    logger.info("[MYALGO]: Cancelling " 
                                + unfilledChildBuyOrderWithLowestPriceToString 
                                + " because it has become too uncompetitive");     
                    
                    evaluateMethodCallCount += 1;
                    return new CancelChildOrder(unfilledChildBuyOrderWithLowestPrice);
        } else {
            evaluateMethodCallCount += 1; // use this logic to adjust quantities and prices
            return new CreateChildOrder(Side.BUY, 100, (long) bestBidPriceInCurrentTick);
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
