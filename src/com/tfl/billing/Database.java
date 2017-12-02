package com.tfl.billing;

import com.tfl.external.Customer;
import com.tfl.external.CustomerDatabase;

import java.util.List;
import java.util.UUID;

public interface Database {

    static List<Customer> getCustomers(){
        return CustomerDatabase.getInstance().getCustomers();
    }

    static boolean isRegisteredId(UUID cardId) {
        return CustomerDatabase.getInstance().isRegisteredId(cardId);
    }
}
