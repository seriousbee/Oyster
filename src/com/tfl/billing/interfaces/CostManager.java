package com.tfl.billing.interfaces;

import com.tfl.billing.journeyelements.JourneyEvent;
import com.tfl.external.Customer;

import java.util.List;

public interface CostManager {
    void chargeCustomerAmount(Customer customer, List<JourneyEvent> eventLog);
}
