package com.tfl.billing;

import com.oyster.OysterCard;
import com.tfl.billing.legacyinteraction.DBHelper;
import com.tfl.external.Customer;
import com.tfl.external.CustomerDatabase;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DBHelperTest {
    private DBHelper dbTest = new DBHelper();
    private CustomerDatabase customerDatabase = CustomerDatabase.getInstance();

    @Test
    public void getCustomersReturnsIdenticalCustomerList() {
        assertEquals(dbTest.getCustomers(), customerDatabase.getCustomers());
    }

    @Test
    public void isRegisteredIdReturnsSameBoolean() {
        Customer customer1 = dbTest.getCustomers().get(0);
        Customer customer2 = new Customer("Test1", new OysterCard());

        assertTrue(dbTest.isRegisteredId(customer1.cardId()) && customerDatabase.isRegisteredId(customer1.cardId()));
        assertFalse(dbTest.isRegisteredId(customer2.cardId()) || customerDatabase.isRegisteredId(customer2.cardId()));
    }

}