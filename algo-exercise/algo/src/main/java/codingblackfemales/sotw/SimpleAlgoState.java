package codingblackfemales.sotw;

import codingblackfemales.sotw.marketdata.AskLevel;
import codingblackfemales.sotw.marketdata.BidLevel;

import java.util.List;

public interface SimpleAlgoState {

    public String getSymbol(); // e.g. MSFT is symbol for Microsoft

    // how many orders there are on either side of the book - like a .size() method
    // includes orders that are not from my algo
    public int getBidLevels(); 
    public int getAskLevels(); // 

    public BidLevel getBidAt(int index); // get the bid at a given index
    public AskLevel getAskAt(int index); // get the ask at a given index

    public List<ChildOrder> getChildOrders(); // all orders created by myAlgoLogic including those which have been cancelled or filled

    public List<ChildOrder> getActiveChildOrders(); // all orders created by myAlgoLogic which are still active

    public long getInstrumentId();
}
