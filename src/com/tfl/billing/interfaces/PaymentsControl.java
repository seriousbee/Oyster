package com.tfl.billing.interfaces;

import com.tfl.external.Customer;

public interface PaymentsControl {
    void charge(Customer customer);
}
