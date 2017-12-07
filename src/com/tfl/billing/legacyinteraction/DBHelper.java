package com.tfl.billing.legacyinteraction;

import com.oyster.OysterCard;
import com.tfl.billing.interfaces.Database;
import com.tfl.external.Customer;
import com.tfl.external.CustomerDatabase;

import java.util.List;
import java.util.UUID;

public class DBHelper implements Database {

    private static CustomerDatabase database;

    static {
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

    public void commitCustomerToDB(Customer customer){
        while(!isRegisteredId(customer.cardId())){
            database.getCustomers().add(customer);
        }
    }

    public Customer createCustomer(String fullName){
        UUID uuid = UUID.randomUUID();
        while (isRegisteredId(uuid)){
            uuid = UUID.randomUUID();
        }
        return new Customer(fullName, new OysterCard(uuid.toString()));
    }
}
