import com.tfl.billing.FareCalculator;
import com.tfl.billing.JourneyTracker;
import com.tfl.billing.legacyinteraction.DBHelper;
import com.tfl.external.Customer;
import com.tfl.underground.OysterReaderLocator;
import com.tfl.underground.Station;

/**
 * Created by tomaszczernuszenko on 16/11/2017.
 */
public class Main {


    public static void main(String[] args){
        DBHelper helper = new DBHelper();

        Customer c1 = helper.createCustomer("Ala Makota");
        Customer c2 = helper.createCustomer("Jan Niezbedny");
        Customer c3 = helper.createCustomer("Anna Staranna");
        Customer nonTraveler = helper.createCustomer("Adam Testowy");

        helper.commitCustomerToDB(c1);
        helper.commitCustomerToDB(c2);
        helper.commitCustomerToDB(c3);
        helper.commitCustomerToDB(nonTraveler);

        JourneyTracker jt = new JourneyTracker();
        
        jt.cardScanned(c1.cardId(), OysterReaderLocator.atStation(Station.PADDINGTON).id());
        jt.cardScanned(c2.cardId(), OysterReaderLocator.atStation(Station.PADDINGTON).id());
        jt.cardScanned(c3.cardId(), OysterReaderLocator.atStation(Station.GOODGE_STREET).id());
        jt.cardScanned(c1.cardId(), OysterReaderLocator.atStation(Station.BOND_STREET).id());
        jt.cardScanned(c2.cardId(), OysterReaderLocator.atStation(Station.VICTORIA_STATION).id());
        jt.cardScanned(c1.cardId(), OysterReaderLocator.atStation(Station.BAKER_STREET).id());
        jt.cardScanned(c2.cardId(), OysterReaderLocator.atStation(Station.OXFORD_CIRCUS).id());
        jt.cardScanned(c2.cardId(), OysterReaderLocator.atStation(Station.KINGS_CROSS).id());
        jt.cardScanned(c3.cardId(), OysterReaderLocator.atStation(Station.WESTMINSTER).id());
        jt.cardScanned(c2.cardId(), OysterReaderLocator.atStation(Station.OLD_STREET).id());
        jt.cardScanned(c2.cardId(), OysterReaderLocator.atStation(Station.LIVERPOOL_STREET).id());
        jt.cardScanned(c1.cardId(), OysterReaderLocator.atStation(Station.COVENT_GARDEN).id());
        jt.cardScanned(c1.cardId(), OysterReaderLocator.atStation(Station.COVENT_GARDEN).id());
        jt.cardScanned(c1.cardId(), OysterReaderLocator.atStation(Station.COVENT_GARDEN).id());
        jt.cardScanned(c1.cardId(), OysterReaderLocator.atStation(Station.COVENT_GARDEN).id());
        jt.cardScanned(c1.cardId(), OysterReaderLocator.atStation(Station.COVENT_GARDEN).id());


        jt.chargeAccounts();

    }
}
