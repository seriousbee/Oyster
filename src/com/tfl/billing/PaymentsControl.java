package com.tfl.billing;

import com.tfl.billing.helpers.CostCalculatingUtil;
import com.tfl.billing.helpers.JourneyCosts;
import com.tfl.billing.journeyelements.JourneyEvent;
import com.tfl.external.Customer;
import com.tfl.external.PaymentsSystem;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class PaymentsControl implements com.tfl.billing.interfaces.PaymentsControl {

    private static PaymentsSystem paymentsSystem = PaymentsSystem.getInstance();
    private JourneyManager journeyManager;
    private FareCalculator fareCalculator;

    PaymentsControl(JourneyManager journeyManager, FareCalculator fareCalculator) {
        this.journeyManager = journeyManager;
        this.fareCalculator = fareCalculator;
    }

    void charge(Customer customer, List<Journey> journeys, BigDecimal totalBill) {
        paymentsSystem.charge(customer,journeys,totalBill);
    }

    void notifyChangedEventLog(List<JourneyEvent> events) {
        journeyManager.updateEvents(events);
    }

    @Override
    public void charge(Customer customer) {
        try {
            List<Journey> journeys = journeyManager.generateJourneyList(customer);
            BigDecimal total = fareCalculator.calculateFare(journeys);
            PaymentsSystem.getInstance().charge(customer, journeys, total);
        } catch (Exception e) {
            charge(customer, new ArrayList<>(), CostCalculatingUtil.roundToNearestPenny(JourneyCosts.PEAK_DAILY_CAP_PRICE));
        }
    }
}
