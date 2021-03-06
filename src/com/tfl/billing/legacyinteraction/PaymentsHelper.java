package com.tfl.billing.legacyinteraction;

import com.tfl.billing.Journey;
import com.tfl.billing.JourneyTracker;
import com.tfl.billing.interfaces.PaymentSystem;
import com.tfl.external.Customer;
import com.tfl.external.PaymentsSystem;

import java.math.BigDecimal;
import java.util.List;

// Helper that allows client-database interactions that are payment-related.
// This is where the SystemPricing class from the external.jar gets called.

public class PaymentsHelper implements PaymentSystem {

    public void charge(Customer customer, List<Journey> journeys, BigDecimal totalBill) {
        PaymentsSystem.getInstance().charge(customer, journeys, totalBill);
    }

    public void chargeAllAccounts() {
        JourneyTracker tracker = new JourneyTracker();
        tracker.chargeAccounts();
    }
}
