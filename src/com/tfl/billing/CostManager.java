package com.tfl.billing;

import com.tfl.external.Customer;

import java.util.List;

public interface CostManager {
    void chargeCustomerAmount(Customer customer, List<JourneyEvent> eventLog);
}