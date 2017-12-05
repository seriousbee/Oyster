package com.tfl.billing;

import com.tfl.external.Customer;

import java.util.List;

public interface TripManager {
    List<Journey> getJourneys(Customer customer);
}
