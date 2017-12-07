package com.tfl.billing;

import com.oyster.ScanListener;
import com.tfl.billing.database.DBHelper;
import com.tfl.billing.helpers.CostCalculatingUtil;
import com.tfl.billing.helpers.JourneyCosts;
import com.tfl.billing.helpers.UnknownOysterCardException;
import com.tfl.billing.journeyelements.JourneyEnd;
import com.tfl.billing.journeyelements.JourneyEvent;
import com.tfl.billing.journeyelements.JourneyStart;
import com.tfl.external.Customer;
import com.tfl.external.PaymentsSystem;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Created by tomaszczernuszenko on 07/12/2017.
 */
public class JourneyTracker implements ScanListener{

    private static List<JourneyEvent> eventLog;
    private static Set<UUID> currentlyTravelling;
    private static DBHelper helper;

    static {
        eventLog = new ArrayList<>();
        currentlyTravelling = new HashSet<>();
        helper = new DBHelper();
    }

    public void chargeAccounts() {
        List<Customer> customers = helper.getCustomers();

        for (Customer customer : customers) {
            FareCalculator fareCalculator = new FareCalculator();
            List<Journey> customerJourneys = new ArrayList<>();
            try {
                customerJourneys = fareCalculator.generateJourneyList(getJourneyEventsFor(customer));
            } catch (Exception e){
                PaymentsSystem.getInstance().charge(customer, new ArrayList<>(), CostCalculatingUtil.roundToNearestPenny(JourneyCosts.PEAK_DAILY_CAP_PRICE));
            }

            BigDecimal total = fareCalculator.calculateFare(customerJourneys);
            PaymentsSystem.getInstance().charge(customer, customerJourneys, total);
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
            if (helper.isRegisteredId(cardId)) {
                currentlyTravelling.add(cardId);
                eventLog.add(new JourneyStart(cardId, readerId));
            } else {
                throw new UnknownOysterCardException(cardId);
            }
        }
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
        return customerJourneyEvents;
    }

}
