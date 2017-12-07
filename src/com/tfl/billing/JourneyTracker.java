package com.tfl.billing;

import com.oyster.ScanListener;
import com.tfl.billing.helpers.UnknownOysterCardException;
import com.tfl.billing.interfaces.CardReader;
import com.tfl.billing.journeyelements.JourneyEnd;
import com.tfl.billing.journeyelements.JourneyEvent;
import com.tfl.billing.journeyelements.JourneyStart;
import com.tfl.external.Customer;

import java.util.*;

/**
 * Created by tomaszczernuszenko on 07/12/2017.
 */
public class JourneyTracker implements ScanListener{

    private static List<JourneyEvent> eventLog; //You can only have one instance of the eventlog to ensure no eventlog conflicts
    private static Set<UUID> currentlyTravelling;
    private static DBHelper dbHelper;
    private static PaymentsControl paymentsControl;

    static {
        eventLog = new ArrayList<>();
        currentlyTravelling = new HashSet<>();
        dbHelper = new DBHelper();
        paymentsControl = new PaymentsControl(new JourneyManager(), new FareCalculator());
    }

    public void chargeAccounts() {
        List<Customer> customers = dbHelper.getCustomers();
        for (Customer customer : customers) {
            paymentsControl.charge(customer);
        }
    }

    public void connect(CardReader... cardReaders) {
        for (CardReader cardReader : cardReaders) {
            cardReader.register(this);
        }
    }

    @Override
    public void cardScanned(UUID cardId, UUID readerId) {
        if (currentlyTravelling.contains(cardId)) {
            eventLog.add(new JourneyEnd(cardId, readerId));
            currentlyTravelling.remove(cardId);
        } else {
            if (dbHelper.isRegisteredId(cardId)) {
                currentlyTravelling.add(cardId);
                eventLog.add(new JourneyStart(cardId, readerId));
            } else {
                throw new UnknownOysterCardException(cardId);
            }
        }
        paymentsControl.notifyChangedEventLog(Collections.unmodifiableList(eventLog));
    }

    private List<JourneyEvent> getJourneyEventsFor(Customer customer) {
        List<JourneyEvent> customerJourneyEvents = new ArrayList<>();

        for (JourneyEvent journeyEvent : eventLog) {
            if (journeyEvent.cardId().equals(customer.cardId())) {
                try {
                    customerJourneyEvents.add(journeyEvent.clone());
                } catch (Exception e){
                    System.out.println("JourneyEvent could not be cloned");
                    break;
                }
            }
        }
        return Collections.unmodifiableList(customerJourneyEvents);
    }

}
