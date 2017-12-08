package com.tfl.billing.interfaces;

import com.tfl.external.Customer;

import java.util.List;
import java.util.UUID;

// Interface to handle client-database interactions that are customer-related

public interface Database {

    List<Customer> getCustomers();

    boolean isRegisteredId(UUID cardId);

    void commitCustomerToDB(Customer customer);

    Customer createCustomer(String fullName);
}
