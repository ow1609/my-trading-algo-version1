package codingblackfemales.gettingstarted;

import codingblackfemales.action.Action;
import codingblackfemales.action.CreateChildOrder;
import codingblackfemales.action.NoAction;
import codingblackfemales.algo.AlgoLogic;
import codingblackfemales.sotw.ChildOrder;
import codingblackfemales.sotw.ChildFill;
import codingblackfemales.sotw.SimpleAlgoState;
import codingblackfemales.sotw.marketdata.AskLevel;
import codingblackfemales.sotw.marketdata.BidLevel;
import codingblackfemales.util.Util;
import messages.order.Side;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyAlgoLogic implements AlgoLogic {

    private static final Logger logger = LoggerFactory.getLogger(MyAlgoLogic.class);

    // variables to store data from the current tick
    public double bestAskPriceInCurrentTick;
    public double bestBidPriceInCurrentTick;
    public double theSpreadInCurrentTick;
    public double midPriceInCurrentTick;
    public double relativeSpreadInCurrentTick;

    public double bestAskQuantityInCurrentTick;
    public double bestBidQuantityInCurrentTick;

    public double totalQuantityOfAskOrdersInCurrentTick; // from top 10 orders
    public double totalQuantityOfBidOrdersInCurrentTick; // from top 10 orders

    // lists to store data from multiple orders in the current tick
    private List<AskLevel> topAskOrdersInCurrentTick = new ArrayList<>(); // top 10 ask orders
    private List<Double> pricesOfTopAskOrdersInCurrentTick = new ArrayList<>(); // from top 10 ask orders
    private List<Double> quantitiesOfTopAskOrdersInCurrentTick = new ArrayList<>(); // from top 10 ask orders

    private List<BidLevel> topBidOrdersInCurrentTick = new ArrayList<>(); // top 10 ask orders
    private List<Double> pricesOfTopBidOrdersInCurrentTick = new ArrayList<>(); // from top 10 ask orders
    private List<Double> quantitiesOfTopBidOrdersInCurrentTick = new ArrayList<>(); // from top 10 ask orders

    // getters to retrieve data from the current tick
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


    public List<AskLevel> getTopAskOrdersInCurrentTick() {
        return topAskOrdersInCurrentTick;
    }

    public List<Double> getPricesOfTopAskOrdersInCurrentTick() { // top 10
        return pricesOfTopAskOrdersInCurrentTick;
    }

    public List<Double> getQuantitiesOfTopAskOrdersInCurrentTick() { // top 10
        return quantitiesOfTopAskOrdersInCurrentTick;
    }

    public List<BidLevel> getTopBidOrdersInCurrentTick() {
        return topBidOrdersInCurrentTick;
    }

    public List<Double> getPricesOfTopBidOrdersInCurrentTick() { // top 10
        return pricesOfTopBidOrdersInCurrentTick;
    }

    public List<Double> getQuantitiesOfTopBidOrdersInCurrentTick() { // top 10
        return quantitiesOfTopBidOrdersInCurrentTick;
    }

    // for analysing supply and demand for the instrument
    public double setTotalQuantityOfAskOrdersInCurrentTick() { // top 10
        return totalQuantityOfAskOrdersInCurrentTick = sumOfAllInAListOfDoubles(quantitiesOfTopAskOrdersInCurrentTick);
    }

    public double getTotalQuantityOfAskOrdersInCurrentTick() { // top 10
        return totalQuantityOfAskOrdersInCurrentTick;
    }

    public double setTotalQuantityOfBidOrdersInCurrentTick() { // top 10
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
        childBidOrderQuantity = (long)(totalQuantityOfBidOrdersInCurrentTick * 0.1); // set POV to 10%
    }

    public void setChildAskOrderQuantity() {
        childAskOrderQuantity = (long)(totalQuantityOfAskOrdersInCurrentTick * 0.1); // set POV to 10%
    }

    

    public long childBuyOrderPrice;
    public long childSellOrderPrice;
    public long entryPrice;
    public long totalProfit;
    public double stopLoss = entryPrice * 0.99;
    public long profitOrLossOnThisTrade = childSellOrderPrice - entryPrice;

    public double getTotalProfit() { // top 10 // TODO - TEST THIS METHOD
        return totalProfit;
    }

    public double getEntryPrice() { // top 10 // TODO - TEST THIS METHOD
        return entryPrice;
    }

    public double getStopLoss() { // top 10 // TODO - TEST THIS METHOD
        return stopLoss;
    }


    public int evaluateMethodCallCount = 0;

    public long totalChildFilledQuantity;

    public double getTotalChildFilledQuantity() { // top 10 // TODO - TEST THIS METHOD
        return totalChildFilledQuantity;
    }



    // ******************************EVALUATE METHOD CALL***************************************
    // ******************************EVALUATE METHOD CALL**************************************
    @Override
    public Action evaluate(SimpleAlgoState state) {

        var orderBookAsString = Util.orderBookToString(state);

        logger.info("[MYALGO] The state of the order book is:\n" + orderBookAsString);

        // gather tick data for analysis

        final AskLevel bestAskOrder = state.getAskAt(0);
        final BidLevel bestBidOrder = state.getBidAt(0);

        bestAskPriceInCurrentTick = bestAskOrder.price;
        bestAskQuantityInCurrentTick = bestAskOrder.quantity;

        bestBidPriceInCurrentTick = bestBidOrder.price;
        bestBidQuantityInCurrentTick = bestBidOrder.quantity;

        theSpreadInCurrentTick = bestAskPriceInCurrentTick - bestBidPriceInCurrentTick;
        midPriceInCurrentTick = (bestAskPriceInCurrentTick + bestBidPriceInCurrentTick) / 2;
        relativeSpreadInCurrentTick = theSpreadInCurrentTick / midPriceInCurrentTick * 100;
        


        // Loop to populate lists of data about the top ask orders in the current tick
        int maxAskOrders = Math.min(state.getAskLevels(), 10); // up to a max of 10 ask orders
        getTopAskOrdersInCurrentTick().clear();
        getPricesOfTopAskOrdersInCurrentTick().clear();
        getQuantitiesOfTopAskOrdersInCurrentTick().clear();
        for (int i = 0; i < maxAskOrders; i++) {
            AskLevel askOrder = state.getAskAt(i);
            addToListOfAskOrders(getTopAskOrdersInCurrentTick(), askOrder);
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
            BidLevel bidOrder = state.getBidAt(i);
            addToListOfBidOrders(topBidOrdersInCurrentTick, bidOrder);
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
        
        List<String> childBidOrdersNotFilledList = new ArrayList<>();
        List<String> childBidOrdersFilledList = new ArrayList<>();
        List<String> childBidOrdersPartiallyFilledList = new ArrayList<>();


        List<String> childAskOrdersNotFilledList = new ArrayList<>();
        List<String> childAskOrdersFilledList = new ArrayList<>();
        List<String> childAskOrdersPartiallyFilledList = new ArrayList<>();

        logger.info("[MYALGO childBidOrdersNotFilledList is : " + childBidOrdersNotFilledList.toString());
        logger.info("[MYALGO childBidOrdersFilledList is " + childBidOrdersFilledList);
        logger.info("[MYALGO childBidOrdersPartiallyFilledList is " + childBidOrdersPartiallyFilledList);

        logger.info("[MYALGO childAskOrdersNotFilledList is " + childAskOrdersNotFilledList);
        logger.info("[MYALGO childAskOrdersFilledList is " + childAskOrdersFilledList);
        logger.info("[MYALGO childAskOrdersPartiallyFilledList is " + childAskOrdersPartiallyFilledList);

    
        // for (int i = 0; i < state.getChildOrders().size() ; i++) {
        //     ChildOrder childOrder = state.getChildOrders().get(i);
        //     long id = childOrder.getOrderId();
        //     Side side = childOrder.getSide();
        //     long quantity = childOrder.getQuantity();
        //     long price = childOrder.getPrice();
        //     long filledQuantity = childOrder.getFilledQuantity();
        //     if (filledQuantity == 0 && side == Side.BUY) {
        //         childBidOrdersNotFilledList.add("UNFILLED CHILD id:" + id + " BID[" + quantity + "@" + price + "]");
        //     } else if (filledQuantity == quantity && side == Side.BUY) {
        //         childBidOrdersFilledList.add("FILLED CHILD id:" + id + " BID[" + quantity + "@" + price + "]");
        //     } else if (filledQuantity < 0 && filledQuantity != quantity && side == Side.BUY) {
        //         childBidOrdersPartiallyFilledList.add("PART FILLED CHILD id:" + id + " BID[" + quantity + "@" + price + "] FILLED QUANTITY is: " + filledQuantity);
        //     } else if (filledQuantity == 0 && side == Side.SELL) {
        //         childAskOrdersNotFilledList.add("UNFILLED CHILD id:" + id + " ASK[" + quantity + "@" + price + "]");
        //     } else if (filledQuantity == quantity && side == Side.SELL) {
        //         childAskOrdersFilledList.add("FILLED CHILD  id:" + id + " ASK[" + quantity + "@" + price + "]");
        //     } else if (filledQuantity == quantity && side == Side.SELL) {
        //         childAskOrdersPartiallyFilledList.add("PART FILLED CHILD id:" + id + " ASK[" + quantity + "@" + price + "] FILLED QUANTITY is: " + filledQuantity);
        //     }
        // }
        

        // // Visibility of filled orders
        // List<ChildOrder> filledChildOrdersCount = state.getChildOrders()
        // .stream()
        // .filter(order -> order.getFilledQuantity() > 0)
        // .collect(Collectors.toList());
        
        long totalChildFilledQuantity = state.getChildOrders()
                                    .stream()
                                    .map(ChildOrder::getFilledQuantity)
                                    .reduce(Long::sum)
                                    .orElse(0L);;

        List<String> childOrdersToString = new ArrayList<>();

        for (int i = 0; i < state.getChildOrders().size() ; i++) {
            ChildOrder childOrder = state.getChildOrders().get(i);
            long id = childOrder.getOrderId();
            Side side = childOrder.getSide();
            long quantity = childOrder.getQuantity();
            long price = childOrder.getPrice();
            long filledQuantity = childOrder.getFilledQuantity();
            childOrdersToString.add("CHILDORDER Id:" + id + " Side:" + side + " [" + quantity + "@" + price + "]") ;
        }

        logger.info("childOrdersToString is: " + childOrdersToString);



        logger.info("[MYALGO Until now, the evaluate method has been called : " + evaluateMethodCallCount + " times.");
        logger.info("[MYALGO This is evaluate method call number : " + (evaluateMethodCallCount + 1) + " \n");

        logger.info("[MYALGO THE CURRENT TICK DATA is: \n");

        logger.info("[MYALGO bestAskPriceInCurrentTick is: " + getBestAskPriceInCurrentTick());
        logger.info("[MYALGO bestAskQuantityInCurrentTick is: " + getBestAskQuantityInCurrentTick());
        logger.info("[MYALGO bestBidPriceInCurrentTick is: " + getBestBidPriceInCurrentTick());
        logger.info("[MYALGO bestBidQuantityInCurrentTick is: " + getBestBidQuantityInCurrentTick() + "\n");
   
        logger.info("[MYALGO theSpreadInCurrentTick is: " + getTheSpreadInCurrentTick());
        logger.info("[MYALGO midPriceInCurrentTick is: " + getMidPriceInCurrentTick());
        logger.info("[MYALGO relativeSpreadInCurrentTick is: " + getRelativeSpreadInCurrentTick()+ "\n");

        logger.info("[MYALGO the topAskOrdersInCurrentTick are: " + getTopAskOrdersInCurrentTick().toString());
        logger.info("[MYALGO the pricesOfTopAskOrdersInCurrentTick  are: " + getPricesOfTopAskOrdersInCurrentTick().toString());
        logger.info("[MYALGO the quantitiesOfTopAskOrdersInCurrentTick  are: " + getQuantitiesOfTopAskOrdersInCurrentTick().toString());
        logger.info("[MYALGO the totalQuantityOfAskOrdersInCurrentTick are: " + getTotalQuantityOfAskOrdersInCurrentTick()+ "\n");


        logger.info("[MYALGO the topBidOrdersInCurrentTick are: " + getTopBidOrdersInCurrentTick().toString());
        logger.info("[MYALGO the pricesOfTopBidOrdersInCurrentTick are: " + getPricesOfTopBidOrdersInCurrentTick().toString());
        logger.info("[MYALGO the quantitiesOfTopBidOrdersInCurrentTick are: " + getQuantitiesOfTopBidOrdersInCurrentTick().toString());
        logger.info("[MYALGO the totalQuantityOfBidOrdersInCurrentTick are: " + getTotalQuantityOfBidOrdersInCurrentTick()+ "\n");

        logger.info("[MYALGO THE HISTORICAL TICK DATA is: \n ");
        logger.info("[MYALGO getHistoryOfBestAskPrice() is: " + getHistoryOfBestAskPrice().toString());
        logger.info("[MYALGO getHistoryOfTotalQuantityOfAskOrders() is: " + getHistoryOfTotalQuantityOfAskOrders().toString());
        logger.info("[MYALGO getHistoryOfBestBidPrice() is: " + getHistoryOfBestBidPrice().toString());
        logger.info("[MYALGO getHistoryOfTotalQuantityOfBidOrders() is: " + getHistoryOfTotalQuantityOfBidOrders().toString());
        logger.info("[MYALGO getHistoryOfTheSpread() is: " + getHistoryOfTheSpread().toString());
        logger.info("[MYALGO getHistoryOfRelativeSpread() is: " + getHistoryOfRelativeSpread().toString());
        logger.info("[MYALGO getHistoryOfMidPrice() is: " + getHistoryOfMidPrice().toString() + "\n");

        logger.info("[MYALGO my current entryPrice is: " + getEntryPrice());
        logger.info("[MYALGO my current totalProfit is: " + getTotalProfit());
        logger.info("[MYALGO my current stopLoss is: " + getStopLoss() + "\n");


        logger.info("[MYALGO ENTERING MY ALGO\'S BUY / SELL LOGIC \n");


        // If I have filled orders and spread is wide, place passive sell order


        // If I have no active orders, place 3 child orders to join the best bid
        if (state.getChildOrders().size() < 3) {
            logger.info("[MYALGO] Currently have: " + state.getChildOrders().size() + " children, want 3, joining best bid with: " + 100 + " @ " + bestBidPriceInCurrentTick);
            entryPrice = (long) bestBidPriceInCurrentTick;

             // BEFORE RETURN STATEMENT
            // add to evaluate call count
            // calculate profitOrLossOnThisTrade
            // update totalProfit
            // update entryPrice
            // update stopLoss
            evaluateMethodCallCount += 1;
            return new CreateChildOrder(Side.BUY, 100, (long)bestBidPriceInCurrentTick);
        } else {
            
            // if (filledQuantity > 0) {
                logger.info("[MYALGO] totalChildFilledQuantity is: " + totalChildFilledQuantity);
                logger.info("[MYALGO] totalChildFilledQuantity * 0.25 is: " + (totalChildFilledQuantity * 0.25));

                // entryPrice = filledChildOrders.get(0).getPrice();
                logger.info("[MYALGO] entryPrice is: " + entryPrice);
                logger.info("[MYALGO] bestBidPrice is: " + bestBidPriceInCurrentTick);
                

                if (bestBidPriceInCurrentTick >= entryPrice * 1.01) {
                    long profitOnThisTrade = (long)(totalChildFilledQuantity * 0.25) * (entryPrice - (long)bestBidPriceInCurrentTick);
                    totalProfit = profitOnThisTrade;

                    logger.info("[MYALGO] profitOnThisTrade is: " + profitOnThisTrade);
                    logger.info("[MYALGO] totalProfit is: " + totalProfit);
                    return new CreateChildOrder(Side.SELL, (long)(totalChildFilledQuantity * 0.25), (long)bestBidPriceInCurrentTick);
                }
            }
            logger.info("[MYALGO] Currently have: " + state.getChildOrders().size() + " child orders. No action");
            
            // BEFORE RETURN STATEMENT
            // add to evaluate call count
            // calculate profitOrLossOnThisTrade
            // update totalProfit
            // update entryPrice
            // update stopLoss
            evaluateMethodCallCount += 1;
            return NoAction.NoAction;
        }
    }

