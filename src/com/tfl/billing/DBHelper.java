package com.tfl.billing;

import com.tfl.external.Customer;
import com.tfl.external.CustomerDatabase;

import java.util.List;
import java.util.UUID;

public class DBHelper implements Database {

    CustomerDatabase database;

    public DBHelper() {
        database = CustomerDatabase.getInstance();
    }

    @Override
    public List<Customer> getCustomers() {
        return database.getCustomers();
    }

    @Override
    public boolean isRegisteredId(UUID cardId) {
        return database.isRegisteredId(cardId);
    }
}
