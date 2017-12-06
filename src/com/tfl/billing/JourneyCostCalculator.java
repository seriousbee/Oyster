package com.tfl.billing;

import com.tfl.external.Customer;
import com.tfl.external.PaymentsSystem;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class JourneyCostCalculator implements CostManager {

    protected BigDecimal getTotalFromJourneyList(List<Journey> journeys) {
        boolean traveledOnPeak=false;
        BigDecimal customerTotal = BigDecimal.ZERO;
        for (Journey journey : journeys) {
            BigDecimal journeyPrice;
            if(journey.durationSeconds() > 25*60){ //design decision: is 25 minutes long or short
                journeyPrice = JourneyCosts.OFF_PEAK_LONG_JOURNEY_PRICE;
                if (isPeak(journey)) {
                    journeyPrice = JourneyCosts.PEAK_LONG_JOURNEY_PRICE;
                    traveledOnPeak = true;
                }
            } else {
                journeyPrice = JourneyCosts.OFF_PEAK_SHORT_JOURNEY_PRICE;
                if (isPeak(journey)) {
                    journeyPrice = JourneyCosts.PEAK_SHORT_JOURNEY_PRICE;
                    traveledOnPeak = true;
                }
            }
            customerTotal = customerTotal.add(journeyPrice);
        }

        customerTotal = applyCap(traveledOnPeak, customerTotal);

        return roundToNearestPenny(customerTotal);
    }

    public BigDecimal applyCap(boolean traveledOnPeak, BigDecimal customerTotal){
        if(traveledOnPeak && customerTotal.compareTo(BigDecimal.valueOf(9))==1)
            customerTotal = BigDecimal.valueOf(9);
        else if(!traveledOnPeak && customerTotal.compareTo(BigDecimal.valueOf(7))==1)
            customerTotal = BigDecimal.valueOf(7);
        return customerTotal;
    }

    // It is now possible to pre-calculate a journey cost and assert that the return value is the same
    @Deprecated
    protected BigDecimal getTotalForCustomer(List<Journey> customerJourneys) {
        return roundToNearestPenny(getTotalFromJourneyList(customerJourneys));
    }

    @Override
    public void chargeCustomerAmount(Customer customer, List<JourneyEvent> eventLog) {
        TripManager journeyManager = new JourneyManager();
        List<Journey> customerJourneys = journeyManager.getCustomerJourneys(customer,eventLog);

        BigDecimal total = getTotalFromJourneyList(customerJourneys); // Dangerous method atm because relies on this method order otherwise NPE is thrown
        PaymentsSystem.getInstance().charge(customer, customerJourneys, total);
    }

    protected boolean isPeak(Journey journey) {
        return isPeak(journey.startTime()) || isPeak(journey.endTime());
    }

    protected boolean isPeak(Date time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        return (hour >= 6 && hour <= 9) || (hour >= 17 && hour <= 19);
    }

    protected BigDecimal roundToNearestPenny(BigDecimal poundsAndPence) {
        return poundsAndPence.setScale(2, BigDecimal.ROUND_HALF_UP);
    }


}
