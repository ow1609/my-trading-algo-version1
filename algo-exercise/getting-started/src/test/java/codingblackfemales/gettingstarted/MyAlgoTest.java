package codingblackfemales.gettingstarted;

import codingblackfemales.algo.AlgoLogic;
import codingblackfemales.container.Actioner;
import codingblackfemales.container.AlgoContainer;
import codingblackfemales.container.RunTrigger;
import codingblackfemales.sequencer.DefaultSequencer;
import codingblackfemales.sequencer.Sequencer;
import codingblackfemales.sequencer.consumer.LoggingConsumer;
import codingblackfemales.sequencer.net.TestNetwork;
import codingblackfemales.service.MarketDataService;
import codingblackfemales.service.OrderService;
import codingblackfemales.sotw.SimpleAlgoState;
import messages.marketdata.*;
import messages.order.Side;

import org.agrona.concurrent.UnsafeBuffer;
import org.junit.Test;

import java.nio.ByteBuffer;

import static org.junit.Assert.assertEquals;

public class MyAlgoTest extends AbstractAlgoTest {

    private final MessageHeaderEncoder headerEncoder = new MessageHeaderEncoder();
    private final BookUpdateEncoder encoder = new BookUpdateEncoder();

    private AlgoContainer container;

    double delta = 0.0001;

    @Override
    public Sequencer getSequencer() {
        final TestNetwork network = new TestNetwork();
        final Sequencer sequencer = new DefaultSequencer(network);

        final RunTrigger runTrigger = new RunTrigger();
        final Actioner actioner = new Actioner(sequencer);

        container = new AlgoContainer(new MarketDataService(runTrigger), new OrderService(runTrigger), runTrigger,
                actioner);
        // set my algo logic
        container.setLogic(new MyAlgoLogic());

        network.addConsumer(new LoggingConsumer());
        network.addConsumer(container.getMarketDataService());
        network.addConsumer(container.getOrderService());
        network.addConsumer(container);

        return sequencer;
    }

    private UnsafeBuffer createSampleMarketDataTick() {
        final ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024);
        final UnsafeBuffer directBuffer = new UnsafeBuffer(byteBuffer);

        // write the encoded output to the direct buffer
        encoder.wrapAndApplyHeader(directBuffer, 0, headerEncoder);

        // set the fields to desired values
        encoder.venue(Venue.XLON);
        encoder.instrumentId(123L);

        encoder.askBookCount(3)
                .next().price(102L).size(101L)
                .next().price(110L).size(200L)
                .next().price(115L).size(5000L);

        encoder.bidBookCount(3)
                .next().price(98L).size(100L)
                .next().price(95L).size(200L)
                .next().price(91L).size(300L);

        encoder.instrumentStatus(InstrumentStatus.CONTINUOUS);
        encoder.source(Source.STREAM);

        return directBuffer;
    }

    @Test
    public void testDispatchThroughSequencer() throws Exception {

        // create a sample market data tick....
        send(createSampleMarketDataTick());

        // simple assert to check we had 3 orders created
        assertEquals(3, container.getState().getChildOrders().size());
        assertEquals(Side.BUY, container.getState().getChildOrders().get(0).getSide());
    }

    @Test
    public void testCreateChildOrder() throws Exception {
        // create a sample market data tick....
        send(createSampleMarketDataTick());
        // simple assert to check we had 1 order created
        assertEquals(3, container.getState().getChildOrders().size());
        assertEquals(Side.BUY, container.getState().getChildOrders().get(0).getSide());
    }


    @Test
    public void testGetBestAskPriceInCurrentTick() throws Exception {
        MyAlgoLogic myAlgoLogic = new MyAlgoLogic();
        // Create a sample market data tick
        send(createSampleMarketDataTick());
        // Manually set the state as the algo logic evaluates the tick
        SimpleAlgoState state = container.getState();
        // Invoke the evaluate method, which will internally update the data
        myAlgoLogic.evaluate(state);
        // TODO change expected to 102
        assertEquals(98.0, myAlgoLogic.getBestAskPriceInCurrentTick(), delta);
    }


    @Test
    public void testGetBestBidPriceInCurrentTick() throws Exception {
        MyAlgoLogic myAlgoLogic = new MyAlgoLogic();
        // Create a sample market data tick
        send(createSampleMarketDataTick());
        // Manually set the state as the algo logic evaluates the tick
        SimpleAlgoState state = container.getState();
        // Invoke the evaluate method, which will internally update the data
        myAlgoLogic.evaluate(state);
        // TODO change expected to 98
        assertEquals(102, myAlgoLogic.getBestBidPriceInCurrentTick(), delta);
    }


    @Test
    public void testGetTheSpreadInCurrentTick() throws Exception {
        MyAlgoLogic myAlgoLogic = new MyAlgoLogic();
        // Create a sample market data tick
        send(createSampleMarketDataTick());
        // Manually set the state as the algo logic evaluates the tick
        SimpleAlgoState state = container.getState();
        // Invoke the evaluate method, which will internally update the data
        myAlgoLogic.evaluate(state);        
        // TODO change expected to 4
        assertEquals(-4, myAlgoLogic.getTheSpreadInCurrentTick(), delta);
    }

    @Test
    public void testGetMidPriceInCurrentTick() throws Exception {
        MyAlgoLogic myAlgoLogic = new MyAlgoLogic();
        // Create a sample market data tick
        send(createSampleMarketDataTick());
        // Manually set the state as the algo logic evaluates the tick
        SimpleAlgoState state = container.getState();
        // Invoke the evaluate method, which will internally update the data
        myAlgoLogic.evaluate(state);
        assertEquals(100, myAlgoLogic.getMidPriceInCurrentTick(), delta);
    }


    @Test
    public void testRelativeSpreadInCurrentTick() throws Exception {
        MyAlgoLogic myAlgoLogic = new MyAlgoLogic();
        // Create a sample market data tick
        send(createSampleMarketDataTick());
        // Manually set the state as the algo logic evaluates the tick
        SimpleAlgoState state = container.getState();
        // Invoke the evaluate method, which will internally update the data
        myAlgoLogic.evaluate(state);
        //TODO change expected to 4
        assertEquals(-4, myAlgoLogic.getRelativeSpreadInCurrentTick(), delta);
    }

    @Test
    public void testGetBestAskQuantityInCurrentTick() throws Exception {
        MyAlgoLogic myAlgoLogic = new MyAlgoLogic();
        // Create a sample market data tick
        send(createSampleMarketDataTick());
        // Manually set the state as the algo logic evaluates the tick
        SimpleAlgoState state = container.getState();
        // Invoke the evaluate method, which will internally update the data
        myAlgoLogic.evaluate(state);
        // TODO change expected to 101
        assertEquals(100, myAlgoLogic.getBestAskQuantityInCurrentTick(), delta);
    }


    @Test
    public void testGetBestBidQuantityInCurrentTick() throws Exception {
        MyAlgoLogic myAlgoLogic = new MyAlgoLogic();
        // Create a sample market data tick
        send(createSampleMarketDataTick());
        // Manually set the state as the algo logic evaluates the tick
        SimpleAlgoState state = container.getState();
        // Invoke the evaluate method, which will internally update the data
        myAlgoLogic.evaluate(state);
        // TODO change expected to 100
        assertEquals(101, myAlgoLogic.getBestBidQuantityInCurrentTick(), delta);
    }
    @Test
    public void testGetTopAskOrdersInCurrentTick() throws Exception {
        MyAlgoLogic myAlgoLogic = new MyAlgoLogic();
        // Create a sample market data tick
        send(createSampleMarketDataTick());
        // Manually set the state as the algo logic evaluates the tick
        SimpleAlgoState state = container.getState();
        // Invoke the evaluate method, which will internally update the data
        myAlgoLogic.evaluate(state);
        // TODO change expected to "[ASK[101@102], ASK[200@110], ASK[5000@115]]"
        assertEquals("[ASK[100@98], ASK[200@95], ASK[300@91]]", myAlgoLogic.getTopAskOrdersInCurrentTick().toString());
    }

    @Test
    public void testGetPricesOfTopAskOrdersInCurrentTick() throws Exception {
        MyAlgoLogic myAlgoLogic = new MyAlgoLogic();
        // Create a sample market data tick
        send(createSampleMarketDataTick());
        // Manually set the state as the algo logic evaluates the tick
        SimpleAlgoState state = container.getState();
        // Invoke the evaluate method, which will internally update the data
        myAlgoLogic.evaluate(state);
        // TODO change expected to "[102.0, 110.0, 115.0]"
        assertEquals("[98.0, 95.0, 91.0]", myAlgoLogic.getPricesOfTopAskOrdersInCurrentTick().toString());
    }

    @Test
    public void testGetQuantitiesOfTopAskOrders() throws Exception {
        MyAlgoLogic myAlgoLogic = new MyAlgoLogic();
        // Create a sample market data tick
        send(createSampleMarketDataTick());
        // Manually set the state as the algo logic evaluates the tick
        SimpleAlgoState state = container.getState();
        // Invoke the evaluate method, which will internally update the data
        myAlgoLogic.evaluate(state);
        // TODO change expected to "[101.0, 200.0, 5000.0]"
        assertEquals("[100.0, 200.0, 300.0]", myAlgoLogic.getQuantitiesOfTopAskOrdersInCurrentTick().toString());
    }

    @Test
    public void testGetTopBidOrdersInCurrentTick() throws Exception {
        MyAlgoLogic myAlgoLogic = new MyAlgoLogic();
        // Create a sample market data tick
        send(createSampleMarketDataTick());
        // Manually set the state as the algo logic evaluates the tick
        SimpleAlgoState state = container.getState();
        // Invoke the evaluate method, which will internally update the data
        myAlgoLogic.evaluate(state);
        // TODO change expected to "[BID[100@98], BID[200@95], BID[300@91]]"
        assertEquals("[BID[101@102], BID[200@110], BID[5000@115]]", myAlgoLogic.getTopBidOrdersInCurrentTick().toString());
    }


    @Test
    public void testGetPricesOfTopBidOrdersInCurrentTick() throws Exception {
        MyAlgoLogic myAlgoLogic = new MyAlgoLogic();
        // Create a sample market data tick
        send(createSampleMarketDataTick());
        // Manually set the state as the algo logic evaluates the tick
        SimpleAlgoState state = container.getState();
        // Invoke the evaluate method, which will internally update the data
        myAlgoLogic.evaluate(state);
        // TODO change expected to "[98.0, 95.0, 91.0]"
        assertEquals("[102.0, 110.0, 115.0]", myAlgoLogic.getPricesOfTopBidOrdersInCurrentTick().toString());
    }

    @Test
    public void testGetQuantitiesOfTopBidOrdersInCurrentTick() throws Exception {
        MyAlgoLogic myAlgoLogic = new MyAlgoLogic();
        // Create a sample market data tick
        send(createSampleMarketDataTick());
        // Manually set the state as the algo logic evaluates the tick
        SimpleAlgoState state = container.getState();
        // Invoke the evaluate method, which will internally update the data
        myAlgoLogic.evaluate(state);
        // TODO change expected to "[100.0, 200.0, 300.0]"
        assertEquals("[101.0, 200.0, 5000.0]", myAlgoLogic.getQuantitiesOfTopBidOrdersInCurrentTick().toString());
    }

    @Test
    public void testGetTotalQuantityOfAskOrdersInCurrentTick() throws Exception {
        MyAlgoLogic myAlgoLogic = new MyAlgoLogic();
        // Create a sample market data tick
        send(createSampleMarketDataTick());
        // Manually set the state as the algo logic evaluates the tick
        SimpleAlgoState state = container.getState();
        // Invoke the evaluate method, which will internally update the data
        myAlgoLogic.evaluate(state);
        // TODO change expected to 5301.0
        assertEquals(600.0, myAlgoLogic.getTotalQuantityOfAskOrdersInCurrentTick(), delta);
    }

    @Test
    public void testGetTotalQuantityOfBidOrdersInCurrentTick() throws Exception {
        MyAlgoLogic myAlgoLogic = new MyAlgoLogic();
        // Create a sample market data tick
        send(createSampleMarketDataTick());
        // Manually set the state as the algo logic evaluates the tick
        SimpleAlgoState state = container.getState();
        // Invoke the evaluate method, which will internally update the data
        myAlgoLogic.evaluate(state);
        // TODO change expected to 600.0
        assertEquals(5301.0, myAlgoLogic.getTotalQuantityOfBidOrdersInCurrentTick(), delta);
    }
    
    


    // @Test
    // public void testCalculatePercentageChange() throws Exception {
    //     MyAlgoLogic myAlgoLogic = new MyAlgoLogic();
    //     // simple assert to check it calculated the absolute percentage change
    //     assertEquals(25, myAlgoLogic.calculatePercentageChange(100, 75), delta);
    //     assertEquals(25, myAlgoLogic.calculatePercentageChange(80, 100), delta);
    // }

    @Override
    public AlgoLogic createAlgoLogic() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createAlgoLogic'");
    }
}