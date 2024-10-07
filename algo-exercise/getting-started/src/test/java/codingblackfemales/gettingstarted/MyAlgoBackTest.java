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

    @Test
    public void testExampleBackTest() throws Exception {
        
        //create a sample market data tick....
        send(createTick());

        // Places 3 passive child orders joining the best bid on the buy side, each for a quantity of 100
        assertEquals(3, container.getState().getChildOrders().size());
        assertEquals(Side.BUY, container.getState().getChildOrders().get(0).getSide());
        assertEquals(98, container.getState().getChildOrders().get(0).getPrice());
        assertEquals(100, container.getState().getChildOrders().get(0).getQuantity());


        //when: market data moves towards us
        send(createTick2());
 

        //All three passive orders execute and the filled quantity is 300
        long filledQuantity = container.getState().getChildOrders().stream().map(ChildOrder::getFilledQuantity).reduce(Long::sum).get();
        //and: check that our algo state was updated to reflect our fills when the market data
        assertEquals(300, filledQuantity);

        // List<ChildOrder> filledChildOrders = state.getChildOrders()
        //                                                     .stream()
        //                                                     .filter(childOrder -> childOrder.getFilledQuantity() > 0)
        //                                                     .collect(Collectors.toList());
                                
        // when: market prices increase
        createBullishMarketTick1();
    }

}
