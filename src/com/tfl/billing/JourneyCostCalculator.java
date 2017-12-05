package com.tfl.billing;

import com.tfl.external.Customer;
import com.tfl.external.PaymentsSystem;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class JourneyCostCalculator implements CostManager {

    private List<Journey> customerJourneys;

    List<JourneyEvent> getJourneyEvents(Customer customer, List<JourneyEvent> eventLog) {
        List<JourneyEvent> customerJourneyEvents = new ArrayList<>();
        for (JourneyEvent journeyEvent : eventLog) {
            if (journeyEvent.cardId().equals(customer.cardId())) {
                customerJourneyEvents.add(journeyEvent);
            }
        }
        return customerJourneyEvents;
    }

    List<Journey> getJourneys(List<JourneyEvent> customerJourneyEvents) {
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
        return journeys;
    }


    BigDecimal getTotal(List<Journey> journeys, BigDecimal customerTotal) {
        for (Journey journey : journeys) {
            BigDecimal journeyPrice = JourneyCosts.OFF_PEAK_JOURNEY_PRICE;
            if (peak(journey)) {
                journeyPrice = JourneyCosts.PEAK_JOURNEY_PRICE;
            }
            customerTotal = customerTotal.add(journeyPrice);
        }
        return customerTotal;
    }

    // It is now possible to pre-calculate a journey cost and assert that the return value is the same
    BigDecimal getTotalForCustomer(Customer customer, List<JourneyEvent> eventLog) {
        List<JourneyEvent> customerEvents = getJourneyEvents(customer, eventLog);
        customerJourneys = getJourneys(customerEvents);
        return roundToNearestPenny(getTotal(customerJourneys,new BigDecimal(0)));
    }

    @Override
    public void chargeCustomerAmount(Customer customer, List<JourneyEvent> eventLog) {
        BigDecimal total = getTotalForCustomer(customer,eventLog); // Dangerous method atm because relies on this method order otherwise NPE is thrown
        PaymentsSystem.getInstance().charge(customer, customerJourneys, total);
    }

    boolean peak(Journey journey) {
        return peak(journey.startTime()) || peak(journey.endTime());
    }

    boolean peak (Date time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        return (hour >= 6 && hour <= 9) || (hour >= 17 && hour <= 19);
    }

    BigDecimal roundToNearestPenny(BigDecimal poundsAndPence) {
        return poundsAndPence.setScale(2, BigDecimal.ROUND_HALF_UP);
    }


}
