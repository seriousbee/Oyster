package com.tfl.billing;

import com.tfl.billing.helpers.CostCalculatingUtil;
import com.tfl.billing.helpers.JourneyCosts;

import java.math.BigDecimal;
import java.util.List;

// Class that handles the computing of charges. It should not handle anything related
// to creation of the journey, only the manipulation of their duration and timeframe.

public class FareCalculator {

    public BigDecimal calculateFare(List<Journey> journeys) {
        boolean traveledOnPeak = false;

        BigDecimal customerTotal = BigDecimal.ZERO;
        for (Journey journey : journeys) {
            BigDecimal journeyPrice;
            if (journey.durationSeconds() > 25 * 60) {
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

    private BigDecimal applyCapIfEligible(boolean traveledOnPeak, BigDecimal customerTotal) {

        if (traveledOnPeak && customerTotal.compareTo(JourneyCosts.PEAK_DAILY_CAP_PRICE) == 1)
            customerTotal = BigDecimal.valueOf(9);
        else if (!traveledOnPeak && customerTotal.compareTo(JourneyCosts.OFF_PEAK_DAILY_CAP_PRICE) == 1)
            customerTotal = BigDecimal.valueOf(7);
        return customerTotal;

    }
}

