package com.tfl.billing;

import com.tfl.external.Customer;

import java.util.List;

public interface TripManager {
    List<Journey> getCustomerJourneys(Customer customer, List<JourneyEvent> eventLog);
}
