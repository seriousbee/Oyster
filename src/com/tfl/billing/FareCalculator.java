package com.tfl.billing;

import com.tfl.billing.helpers.CostCalculatingUtil;
import com.tfl.billing.helpers.JourneyCosts;
import com.tfl.billing.journeyelements.JourneyEnd;
import com.tfl.billing.journeyelements.JourneyEvent;
import com.tfl.billing.journeyelements.JourneyStart;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


public class FareCalculator {


    public BigDecimal getTotal(List<JourneyEvent> customerEvents) {
        List<Journey> journeys = null;

        try {
            journeys = generateJourneyList(customerEvents);
        } catch (Exception e) {
            return JourneyCosts.PEAK_LONG_JOURNEY_PRICE;
        }

        return CostCalculatingUtil.roundToNearestPenny(calculateFare(journeys));
    }


    private BigDecimal calculateFare(List<Journey> journeys){
        boolean traveledOnPeak=false;

        BigDecimal customerTotal = BigDecimal.ZERO;
        for (Journey journey : journeys) {
            BigDecimal journeyPrice;
            if(journey.durationSeconds() > 25*60){
                journeyPrice = JourneyCosts.OFF_PEAK_LONG_JOURNEY_PRICE;
                if (CostCalculatingUtil.isPeak(journey)) {
                    journeyPrice = JourneyCosts.PEAK_LONG_JOURNEY_PRICE;
                    traveledOnPeak = true;
                }
            } else {
                journeyPrice = JourneyCosts.OFF_PEAK_SHORT_JOURNEY_PRICE;
                if (CostCalculatingUtil.isPeak(journey)) {
                    journeyPrice = JourneyCosts.PEAK_SHORT_JOURNEY_PRICE;
                    traveledOnPeak = true;
                }
            }
            customerTotal = customerTotal.add(journeyPrice);
        }

        customerTotal = applyCapIfEligible(traveledOnPeak, customerTotal);

        return CostCalculatingUtil.roundToNearestPenny(customerTotal);

    }

    private BigDecimal applyCapIfEligible(boolean traveledOnPeak, BigDecimal customerTotal){

        if(traveledOnPeak && customerTotal.compareTo(BigDecimal.valueOf(9))==1)
            customerTotal = BigDecimal.valueOf(9);
        else if(!traveledOnPeak && customerTotal.compareTo(BigDecimal.valueOf(7))==1)
            customerTotal = BigDecimal.valueOf(7);
        return customerTotal;

    }

    public List<Journey> generateJourneyList(List<JourneyEvent> customerJourneyEvents) throws Exception{
        List<Journey> journeys = new ArrayList<>();

        JourneyEvent start = null;
        for (JourneyEvent event : customerJourneyEvents) {
            if (event instanceof JourneyStart) {
                if (start != null) //the customer has not completed their earlier journey.
                    throw new Exception("Customer started a journey without finishing the previous one ");
                start = event;
            }
            if (event instanceof JourneyEnd && start != null) {
                journeys.add(new Journey(start, event));
                start = null;
            }
        }
        if (start != null)
            throw new Exception("Customer started a journey without finishing the previous one ");
        return journeys;
    }
}
