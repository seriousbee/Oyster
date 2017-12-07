package com.tfl.billing.legacyinteraction;

import com.tfl.billing.Journey;
import com.tfl.billing.JourneyTracker;
import com.tfl.external.Customer;
import com.tfl.external.PaymentsSystem;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by tomaszczernuszenko on 07/12/2017.
 */
public class PaymentSystemHelper {

    private static PaymentsSystem paymentsSystem;

    static {
        paymentsSystem = PaymentsSystem.getInstance();
    }

    public static void billSingleCustomer(Customer customer, List<Journey> customerJourneys, BigDecimal total){
        paymentsSystem.charge(customer, customerJourneys, total);
    }

    public static void chargeAllAccounts(){
        JourneyTracker tracker = new JourneyTracker();
        tracker.chargeAccounts();
    }



}
