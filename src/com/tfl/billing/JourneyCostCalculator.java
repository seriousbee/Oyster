package com.tfl.billing;

import com.tfl.external.Customer;
import com.tfl.external.PaymentsSystem;
import java.math.BigDecimal;
import java.util.*;


public class JourneyCostCalculator implements CostManager {

    private static final BigDecimal PEAK_LONG_JOURNEY_PRICE = new BigDecimal(3.80);
    private static final BigDecimal PEAK_SHORT_JOURNEY_PRICE = new BigDecimal(2.90);
    private static final BigDecimal OFF_PEAK_LONG_JOURNEY_PRICE = new BigDecimal(2.70);
    private static final BigDecimal OFF_PEAK_SHORT_JOURNEY_PRICE = new BigDecimal(1.60);

    BigDecimal getTotalFromJourneyList(List<Journey> journeys, BigDecimal customerTotal) {
        boolean traveledOnPeak=false;

        for (Journey journey : journeys) {
            BigDecimal journeyPrice;
            if(journey.durationSeconds() > 25*60){ //design decision: is 25 minutes long or short
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
        else if(!traveledOnPeak && customerTotal.compareTo(BigDecimal.valueOf(7))==1)
            customerTotal = BigDecimal.valueOf(7);

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

    boolean isPeak(Journey journey) {
        return isPeak(journey.startTime()) || isPeak(journey.endTime());
    }

    boolean isPeak(Date time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        return (hour >= 6 && hour <= 9) || (hour >= 17 && hour <= 19);
    }

    BigDecimal roundToNearestPenny(BigDecimal poundsAndPence) {
        return poundsAndPence.setScale(2, BigDecimal.ROUND_HALF_UP);
    }


}
