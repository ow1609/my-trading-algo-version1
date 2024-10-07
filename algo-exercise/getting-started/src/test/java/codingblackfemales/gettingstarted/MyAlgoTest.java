package codingblackfemales.gettingstarted;

import codingblackfemales.algo.AlgoLogic;
import codingblackfemales.sotw.SimpleAlgoState;
import messages.order.Side;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * This test is designed to check your algo behavior in isolation of the order
 * book.
 *
 * You can tick in market data messages by creating new versions of createTick()
 * (ex. createTick2, createTickMore etc..)
 *
 * You should then add behaviour to your algo to respond to that market data by
 * creating or cancelling child orders.
 *
 * When you are comfortable you algo does what you expect, then you can move on
 * to creating the MyAlgoBackTest.
 *
 */
public class MyAlgoTest extends AbstractAlgoTest {

    double delta = 0.0001;

    @Override
    public AlgoLogic createAlgoLogic() {
        // this adds your algo logic to the container classes
        return new MyAlgoLogic();
    }

    @Test
    public void testDispatchThroughSequencer() throws Exception {

        // create a sample market data tick....
        send(createTick());

        // simple assert to check we had 3 orders created
        // assertEquals(container.getState().getChildOrders().size(), 3);
    }



    // MY UNIT TESTS
    @Test
    public void testBestAskPrice() throws Exception {
        MyAlgoLogic myAlgoLogic = new MyAlgoLogic();
        // Create a sample market data tick
        send(createTick());
        // Manually set the state as the algo logic evaluates the tick
        // Assume container.getState() gives you the current state
        SimpleAlgoState state = container.getState();
        // Invoke the evaluate method, which will internally update the bid price
        myAlgoLogic.evaluate(state);
        // TODO change expected to 100
        assertEquals(98.0, myAlgoLogic.getBestAskPrice(), delta);
    }


    @Test
    public void testGetBestBidPrice() throws Exception {
        MyAlgoLogic myAlgoLogic = new MyAlgoLogic();
        // Create a sample market data tick
        send(createTick());
        // Manually set the state as the algo logic evaluates the tick
        // Assume container.getState() gives you the current state
        SimpleAlgoState state = container.getState();
        // Invoke the evaluate method, which will internally update the bid price
        myAlgoLogic.evaluate(state);
        // TODO change expected to 98
        assertEquals(100.0, myAlgoLogic.getBestBidPrice(), delta);
    }


    @Test
    public void testGetTheSpread() throws Exception {
        MyAlgoLogic myAlgoLogic = new MyAlgoLogic();
        // Create a sample market data tick
        send(createTick());
        // Manually set the state as the algo logic evaluates the tick
        // Assume container.getState() gives you the current state
        SimpleAlgoState state = container.getState();
        // Invoke the evaluate method, which will internally update the bid price
        myAlgoLogic.evaluate(state);        
        // TODO change expected to 2
        assertEquals(-2, myAlgoLogic.getTheSpread(), delta);
    }

    @Test
    public void testGetMidPrice() throws Exception {
        MyAlgoLogic myAlgoLogic = new MyAlgoLogic();
        // Create a sample market data tick
        send(createTick());
        // Manually set the state as the algo logic evaluates the tick
        // Assume container.getState() gives you the current state
        SimpleAlgoState state = container.getState();
        // Invoke the evaluate method, which will internally update the bid price
        myAlgoLogic.evaluate(state);
        assertEquals(99, myAlgoLogic.getMidPrice(), delta);
    }

    @Test
    public void testGetBestAskQuantity() throws Exception {
        MyAlgoLogic myAlgoLogic = new MyAlgoLogic();
        // Create a sample market data tick
        send(createTick());
        // Manually set the state as the algo logic evaluates the tick
        // Assume container.getState() gives you the current state
        SimpleAlgoState state = container.getState();
        // Invoke the evaluate method, which will internally update the bid price
        myAlgoLogic.evaluate(state);
        assertEquals(100, myAlgoLogic.getBestAskQuantity(), delta);
    }


    @Test
    public void testGetBestBidQuantity() throws Exception {
        MyAlgoLogic myAlgoLogic = new MyAlgoLogic();
        // Create a sample market data tick
        send(createTick());
        // Manually set the state as the algo logic evaluates the tick
        // Assume container.getState() gives you the current state
        SimpleAlgoState state = container.getState();
        // Invoke the evaluate method, which will internally update the bid price
        myAlgoLogic.evaluate(state);
        assertEquals(101, myAlgoLogic.getBestBidQuantity(), delta);
    }

    @Test
    public void testCreateOneChildOrder() throws Exception {
        // create a sample market data tick....
        send(createTick());
        // simple assert to check we had 1 order created
        assertEquals(1, container.getState().getChildOrders().size());
        assertEquals(Side.BUY, container.getState().getChildOrders().get(0).getSide());
    }



    @Test
    public void testCalculatePercentageChange() throws Exception {
        MyAlgoLogic myAlgoLogic = new MyAlgoLogic();
        // simple assert to check it calculated the absolute percentage change
        assertEquals(25, myAlgoLogic.calculatePercentageChange(100, 75), delta);
        assertEquals(25, myAlgoLogic.calculatePercentageChange(80, 100), delta);
    }

}


    /* TESTS TO WRITE
     * 
     * METHODS FOR CURRENT TICK DATA RETRIEVAL:
     * 
     * getTopAskOrdersOfCurrentTick()
     * getTopBidOrdersOfCurrentTick()
     * getPricesOfTopAskOrders()
     * getPricesOfTopBidOrders()
     * getQuantitiesOfTopAskOrders()
     * getQuantitiesOfTopBidOrders()
     * getTotalQuantityOfAskOrders()
     * getTotalQuantityOfBidOrders()
     * 
     * METHODS FOR DATA RETRIEVAL FROM LATEST HISTORY OF TICKS:
     * 
     * getLatestBestAskPrices()
     * getLatestBestBidPrices()
     * getLatestSpreads()
     * getLatestMidPrices()
     * getLatestTotalQuantitiesOfAskOrders()
     * getLatestTotalQuantitiesOfBidOrders()
     * 
     * METHODS FOR LIST MANIPULATION:
     * 
     * addDataToAList(list, data) - possibly tested intrinsically when testing contents of lists??
     * 
     */