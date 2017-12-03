import com.tfl.billing.DBHelper;
import com.tfl.billing.Database;
import com.tfl.billing.JourneyEvent;
import com.tfl.billing.TravelTracker;
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
        Database database = new DBHelper();
        Customer c1 = database.getCustomers().get(0);
        Customer c2 = database.getCustomers().get(1);


        TravelTracker tt = new TravelTracker(new ArrayList<JourneyEvent>(), new HashSet<UUID>(),database);
        tt.cardScanned(c1.cardId(), OysterReaderLocator.atStation(Station.PADDINGTON).id());
        tt.cardScanned(c2.cardId(), OysterReaderLocator.atStation(Station.PADDINGTON).id());
        tt.cardScanned(c2.cardId(), OysterReaderLocator.atStation(Station.VICTORIA_STATION).id());
        tt.cardScanned(c2.cardId(), OysterReaderLocator.atStation(Station.OXFORD_CIRCUS).id());
        tt.cardScanned(c2.cardId(), OysterReaderLocator.atStation(Station.KINGS_CROSS).id());
        tt.cardScanned(c2.cardId(), OysterReaderLocator.atStation(Station.OLD_STREET).id());
        tt.cardScanned(c2.cardId(), OysterReaderLocator.atStation(Station.LIVERPOOL_STREET).id());

        tt.chargeAccounts(database.getCustomers());
    }
}
