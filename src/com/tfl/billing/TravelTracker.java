package com.tfl.billing;

import com.oyster.ScanListener;
import com.tfl.external.Customer;

import java.math.BigDecimal;
import java.util.*;

public class TravelTracker implements ScanListener {

    private static final BigDecimal PEAK_LONG_JOURNEY_PRICE = new BigDecimal(3.80);
    private static final BigDecimal PEAK_SHORT_JOURNEY_PRICE = new BigDecimal(2.90);
    private static final BigDecimal OFF_PEAK_LONG_JOURNEY_PRICE = new BigDecimal(2.70);
    private static final BigDecimal OFF_PEAK_SHORT_JOURNEY_PRICE = new BigDecimal(1.60);
    static final BigDecimal OFF_PEAK_JOURNEY_PRICE = new BigDecimal(2.40);
    static final BigDecimal PEAK_JOURNEY_PRICE = new BigDecimal(3.20);

    private final List<JourneyEvent> eventLog;
    private final Set<UUID> currentlyTravelling;
    private final Database database;


    public TravelTracker(List<JourneyEvent> events, Set<UUID> currentlyTravelling, Database database) {
        this.eventLog =  events;
        this.currentlyTravelling = currentlyTravelling;
        this.database = database;
    }

//  Injected dependency for customerList, DBHelper is an adapter for the CustomerDatabase dependency
//  This decreases coupling also allowing for dependency injection
//  No logic was changed, only broke methods apart and created helper classes
    public void chargeAccounts(List<Customer> customers) {
        CostManager costManager = new JourneyCostCalculator();
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
