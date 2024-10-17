package codingblackfemales.gettingstarted;

import codingblackfemales.algo.AlgoLogic;
import codingblackfemales.sotw.ChildOrder;
import codingblackfemales.sotw.SimpleAlgoState;
import messages.order.Side;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import java.util.List;
import java.util.stream.Collectors;


/**
 * This test plugs together all of the infrastructure, including the order book (which you can trade against)
 * and the market data feed.
 *
 * If your algo adds orders to the book, they will reflect in your market data coming back from the order book.
 *
 * If you cross the srpead (i.e. you BUY an order with a price which is == or > askPrice()) you will match, and receive
 * a fill back into your order from the order book (visible from the algo in the childOrders of the state object.
 *
 * If you cancel the order your child order will show the order status as cancelled in the childOrders of the state object.
 *
 */
public class MyAlgoBackTest extends AbstractAlgoBackTest {

    @Override
    public AlgoLogic createAlgoLogic() {
        return new MyAlgoLogic();
    }

    double delta = 0.0001;

    //UNIT TESTING - TODO - COMMENT BACK IN LATER! AND COPY ACROSS 
    // @Test
    // public void testGetBestAskPriceInCurrentTick() throws Exception {
    //     MyAlgoLogic myAlgoLogic = new MyAlgoLogic();
    //     // Create a sample market data tick
    //     send(unitTestingTick());
    //     // Manually set the state as the algo logic evaluates the tick
    //     SimpleAlgoState state = container.getState();
    //     // Invoke the evaluate method, which will internally update the data
    //     myAlgoLogic.evaluate(state);
    //     assertEquals(100, myAlgoLogic.getBestAskPriceInCurrentTick(), delta);
    // }

    // TODO - ADD ALL UNIT TESTS HERE USING createTick()
    // TODO - write more tests here for unit testing all methods against a single tick of data

    //BACKTESTING FOR ALGO BEHAVIOUR
    @Test
    public void testExampleBackTest() throws Exception {
        

        // ****************  TODO -  overhaul BackTests for bullish market conditions - asserts after every **********
        //create a sample market data tick....
        send(unitTestingTick()); 

        // Places 4 passive child orders joining the best bid on the buy side, each for a quantity of 100
        assertEquals(3, container.getState().getChildOrders().size());
        assertEquals(Side.BUY, container.getState().getChildOrders().get(0).getSide());
        assertEquals(Side.BUY, container.getState().getChildOrders().get(1).getSide());
        assertEquals(Side.BUY, container.getState().getChildOrders().get(2).getSide());
        assertEquals(96, container.getState().getChildOrders().get(0).getPrice());
        assertEquals(97, container.getState().getChildOrders().get(1).getPrice());
        assertEquals(98, container.getState().getChildOrders().get(2).getPrice());
        assertEquals(100, container.getState().getChildOrders().get(0).getQuantity());
        assertEquals(100, container.getState().getChildOrders().get(1).getQuantity());
        assertEquals(100, container.getState().getChildOrders().get(2).getQuantity());



        // When: market data moves towards us...
        send(createTick2());
 

        //... top Bid orders executes and filled quantity is 100
        long filledQuantity = container.getState().getChildOrders().stream().map(ChildOrder::getFilledQuantity).reduce(Long::sum).get();
        assertEquals(100, filledQuantity);

        // List<ChildOrder> filledChildOrders = state.getChildOrders()
        //                                                     .stream()
        //                                                     .filter(childOrder -> childOrder.getFilledQuantity() > 0)
        //                                                     .collect(Collectors.toList());
                                
        // when: market prices increase
        createBullishMarketTick1();

    }
}
