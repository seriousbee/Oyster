package com.tfl.billing;

import com.oyster.OysterCardReader;
import com.oyster.ScanListener;
import com.tfl.external.Customer;
import com.tfl.external.CustomerDatabase;
import com.tfl.external.PaymentsSystem;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class TravelTracker implements ScanListener {

    private static final BigDecimal PEAK_LONG_JOURNEY_PRICE = new BigDecimal(3.80);
    private static final BigDecimal PEAK_SHORT_JOURNEY_PRICE = new BigDecimal(2.90);
    private static final BigDecimal OFF_PEAK_LONG_JOURNEY_PRICE = new BigDecimal(2.70);
    private static final BigDecimal OFF_PEAK_SHORT_JOURNEY_PRICE = new BigDecimal(1.60);

    private final List<JourneyEvent> eventLog = new ArrayList<>();
    private final Set<UUID> currentlyTravelling = new HashSet<>();

    //call this method to see how much everyone has paid
    public void chargeAccounts() {
        CustomerDatabase customerDatabase = CustomerDatabase.getInstance();

        List<Customer> customers = customerDatabase.getCustomers();
        for (Customer customer : customers) {
            totalJourneysFor(customer);
        }
    }

    //TODO: split the method into smalelr parts
    private void totalJourneysFor(Customer customer) {
        List<JourneyEvent> customerJourneyEvents = new ArrayList<>();
        for (JourneyEvent journeyEvent : eventLog) {
            if (journeyEvent.cardId().equals(customer.cardId())) {
                customerJourneyEvents.add(journeyEvent);
            }
        }

        List<Journey> journeys = new ArrayList<>();

        JourneyEvent start = null;
        for (JourneyEvent event : customerJourneyEvents) {
            if (event instanceof JourneyStart) {
                start = event;
            }
            if (event instanceof JourneyEnd && start != null) {
                journeys.add(new Journey(start, event));
                start = null;
            }
        }

        BigDecimal customerTotal = new BigDecimal(0);
        boolean traveledOnPeak=false;

        for (Journey journey : journeys) {
            BigDecimal journeyPrice;
            if(journey.durationSeconds() > 25*60){ //TODO: is 25 minutes long or short
                journeyPrice = OFF_PEAK_LONG_JOURNEY_PRICE;
                if (isPeak(journey)) {
                    journeyPrice = PEAK_LONG_JOURNEY_PRICE;
                    traveledOnPeak = true;
                }
            } else {
                journeyPrice = OFF_PEAK_SHORT_JOURNEY_PRICE;
                if (isPeak(journey)) {
                    journeyPrice = PEAK_SHORT_JOURNEY_PRICE;
                    traveledOnPeak = true;
                }
            }
            customerTotal = customerTotal.add(journeyPrice);
        }

        //TODO: capping needs testing!!!
        if(traveledOnPeak && customerTotal.compareTo(BigDecimal.valueOf(9))==1)
            customerTotal = BigDecimal.valueOf(9);
        if(!traveledOnPeak && customerTotal.compareTo(BigDecimal.valueOf(7))==1)
            customerTotal = BigDecimal.valueOf(7);

        PaymentsSystem.getInstance().charge(customer, journeys, roundToNearestPenny(customerTotal));
    }

    //TODO: why are we using BigDecimal in the first place?
    private BigDecimal roundToNearestPenny(BigDecimal poundsAndPence) {
        return poundsAndPence.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    private boolean isPeak(Journey journey) {
        return isPeak(journey.startTime()) || isPeak(journey.endTime());
    }


    private boolean isPeak(Date time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        return (hour >= 6 && hour <= 9) || (hour >= 17 && hour <= 19);
    }

    //TODO: WTF is this?
    public void connect(OysterCardReader... cardReaders) {
        for (OysterCardReader cardReader : cardReaders) {
            cardReader.register(this);
        }
    }

    //TODO: rename the method. It indicates that it will return a boolean
    @Override
    public void cardScanned(UUID cardId, UUID readerId) {
        if (currentlyTravelling.contains(cardId)) {
            eventLog.add(new JourneyEnd(cardId, readerId));
            currentlyTravelling.remove(cardId);
        } else {
            if (CustomerDatabase.getInstance().isRegisteredId(cardId)) {
                currentlyTravelling.add(cardId);
                eventLog.add(new JourneyStart(cardId, readerId));
            } else {
                throw new UnknownOysterCardException(cardId);
            }
        }
    }

}
