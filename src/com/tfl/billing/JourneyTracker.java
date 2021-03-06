package com.tfl.billing;

import com.oyster.ScanListener;
import com.tfl.billing.helpers.CostCalculatingUtil;
import com.tfl.billing.helpers.JourneyCosts;
import com.tfl.billing.helpers.UnknownOysterCardException;
import com.tfl.billing.interfaces.CardReader;
import com.tfl.billing.interfaces.Database;
import com.tfl.billing.interfaces.PaymentSystem;
import com.tfl.billing.journeyelements.JourneyEnd;
import com.tfl.billing.journeyelements.JourneyEvent;
import com.tfl.billing.journeyelements.JourneyStart;
import com.tfl.billing.legacyinteraction.DBHelper;
import com.tfl.billing.legacyinteraction.PaymentsHelper;
import com.tfl.external.Customer;
import com.tfl.external.PaymentsSystem;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

// Driver class that represents the entire network. All the other classes are triggered from
// calls to methods from this class as it tracks all the events that happen on the network.

public class JourneyTracker implements ScanListener {

    //You can only have one instance of the eventLog and currentlyTravelling to ensure no eventlog conflicts
    private static List<JourneyEvent> eventLog;
    private static Set<UUID> currentlyTravelling;
    private static Database dbHelper;
    private static PaymentSystem paymentsHelper;

    static {
        eventLog = new ArrayList<>();
        currentlyTravelling = new HashSet<>();
        dbHelper = new DBHelper();
        paymentsHelper = new PaymentsHelper();
    }

    private JourneyAssembler journeyAssembler;

    public JourneyTracker() {
        journeyAssembler = new JourneyAssembler();
    }

    public void chargeAccounts() {
        List<Customer> customers = dbHelper.getCustomers();

        for (Customer customer : customers) {
            FareCalculator fareCalculator = new FareCalculator();
            List<Journey> customerJourneys;

            try {
                customerJourneys = journeyAssembler.generateJourneyList(journeyAssembler.getJourneyEventsFor(customer, getCopyOfEventLog()));
            } catch (Exception e) {
                paymentsHelper.charge(customer, new ArrayList<>(), CostCalculatingUtil.roundToNearestPenny(JourneyCosts.PEAK_DAILY_CAP_PRICE));
                continue;
            }

            BigDecimal total = fareCalculator.calculateFare(customerJourneys);
            paymentsHelper.charge(customer, customerJourneys, total);
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
    }

    public List<JourneyEvent> getCopyOfEventLog() {
        return Collections.unmodifiableList(eventLog);
    }

}
