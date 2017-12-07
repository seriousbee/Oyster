import com.tfl.billing.TravelTracker;
import com.tfl.billing.database.DBHelper;
import com.tfl.billing.journeyelements.JourneyEvent;
import com.tfl.external.Customer;
import com.tfl.underground.OysterReaderLocator;
import com.tfl.underground.Station;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;

/**
 * Created by tomaszczernuszenko on 16/11/2017.
 */
public class Main {


    public static void main(String[] args){
        DBHelper helper = new DBHelper();

        Customer c1 = helper.createCustomer("Ala Makota");
        Customer c2 = helper.createCustomer("Jan Niezbedny");
        Customer c3 = helper.createCustomer("Anna Staranna");

        helper.commitCustomerToDB(c1);
        helper.commitCustomerToDB(c2);
        helper.commitCustomerToDB(c3);

        TravelTracker tt = new TravelTracker(new ArrayList<JourneyEvent>(), new HashSet<UUID>(),helper,new JourneyCostCalculator());
        tt.cardScanned(c1.cardId(), OysterReaderLocator.atStation(Station.PADDINGTON).id());
        tt.cardScanned(c2.cardId(), OysterReaderLocator.atStation(Station.PADDINGTON).id());
        tt.cardScanned(c3.cardId(), OysterReaderLocator.atStation(Station.GOODGE_STREET).id());
        tt.cardScanned(c1.cardId(), OysterReaderLocator.atStation(Station.BOND_STREET).id());
        tt.cardScanned(c2.cardId(), OysterReaderLocator.atStation(Station.VICTORIA_STATION).id());
        tt.cardScanned(c1.cardId(), OysterReaderLocator.atStation(Station.BAKER_STREET).id());
        tt.cardScanned(c2.cardId(), OysterReaderLocator.atStation(Station.OXFORD_CIRCUS).id());
        tt.cardScanned(c2.cardId(), OysterReaderLocator.atStation(Station.KINGS_CROSS).id());
        tt.cardScanned(c3.cardId(), OysterReaderLocator.atStation(Station.WESTMINSTER).id());
        tt.cardScanned(c2.cardId(), OysterReaderLocator.atStation(Station.OLD_STREET).id());
        tt.cardScanned(c2.cardId(), OysterReaderLocator.atStation(Station.LIVERPOOL_STREET).id());
        tt.cardScanned(c1.cardId(), OysterReaderLocator.atStation(Station.COVENT_GARDEN).id());
        tt.cardScanned(c1.cardId(), OysterReaderLocator.atStation(Station.COVENT_GARDEN).id());
        tt.cardScanned(c1.cardId(), OysterReaderLocator.atStation(Station.COVENT_GARDEN).id());
        tt.cardScanned(c1.cardId(), OysterReaderLocator.atStation(Station.COVENT_GARDEN).id());
        tt.cardScanned(c1.cardId(), OysterReaderLocator.atStation(Station.COVENT_GARDEN).id());
        tt.cardScanned(c1.cardId(), OysterReaderLocator.atStation(Station.COVENT_GARDEN).id());
        tt.cardScanned(c1.cardId(), OysterReaderLocator.atStation(Station.COVENT_GARDEN).id());
        tt.cardScanned(c1.cardId(), OysterReaderLocator.atStation(Station.COVENT_GARDEN).id());
        tt.cardScanned(c1.cardId(), OysterReaderLocator.atStation(Station.COVENT_GARDEN).id());
        tt.cardScanned(c1.cardId(), OysterReaderLocator.atStation(Station.COVENT_GARDEN).id());
        tt.chargeAccounts(helper.getCustomers());

    }
}
