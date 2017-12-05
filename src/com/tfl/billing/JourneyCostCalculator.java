package com.tfl.billing;

import com.tfl.external.Customer;
import com.tfl.external.PaymentsSystem;
import java.math.BigDecimal;
import java.util.*;


public class JourneyCostCalculator implements CostManager {

    BigDecimal getTotalFromJourneyList(List<Journey> journeys, BigDecimal customerTotal) {
        for (Journey journey : journeys) {
            BigDecimal journeyPrice = JourneyCosts.OFF_PEAK_JOURNEY_PRICE;
            if (peak(journey)) {
                journeyPrice = JourneyCosts.PEAK_JOURNEY_PRICE;
            }
            customerTotal = customerTotal.add(journeyPrice);
        }
        return roundToNearestPenny(customerTotal);
    }

    // It is now possible to pre-calculate a journey cost and assert that the return value is the same
    @Deprecated
    BigDecimal getTotalForCustomer(List<Journey> customerJourneys) {
        return roundToNearestPenny(getTotalFromJourneyList(customerJourneys,new BigDecimal(0)));
    }

    @Override
    public void chargeCustomerAmount(Customer customer, List<JourneyEvent> eventLog) {
        TripManager journeyManager = new JourneyManager(eventLog);
        List<Journey> customerJourneys = journeyManager.getJourneys(customer);

        BigDecimal total = getTotalFromJourneyList(customerJourneys,new BigDecimal(0)); // Dangerous method atm because relies on this method order otherwise NPE is thrown
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
