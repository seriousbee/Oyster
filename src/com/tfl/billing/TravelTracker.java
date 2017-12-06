package com.tfl.billing;

import com.oyster.ScanListener;
import com.tfl.external.Customer;

import java.util.List;
import java.util.Set;
import java.util.UUID;


public class TravelTracker implements ScanListener {

    private final List<JourneyEvent> eventLog;
    private final Set<UUID> currentlyTravelling;
    private final Database database;
    private final CostManager costManager;


    public TravelTracker(List<JourneyEvent> events, Set<UUID> currentlyTravelling, Database database, CostManager costManager) {
        this.eventLog =  events;
        this.currentlyTravelling = currentlyTravelling;
        this.database = database;
        this.costManager = costManager;
    }

//  Injected dependency for customerList, DBHelper is an adapter for the CustomerDatabase dependency
//  This decreases coupling also allowing for dependency injection
//  No logic was changed, only broke methods apart and created helper classes
    public void chargeAccounts(List<Customer> customers) {
        for (Customer customer : customers) {
            costManager.chargeCustomerAmount(customer, eventLog);
        }
    }

//  Abstracted using interface and adapter so a mock object could be used.
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
            if (database.isRegisteredId(cardId)) {
                currentlyTravelling.add(cardId);
                eventLog.add(new JourneyStart(cardId, readerId));
            } else {
                throw new UnknownOysterCardException(cardId);
            }
        }
    }
}
