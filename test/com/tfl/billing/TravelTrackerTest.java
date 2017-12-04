package com.tfl.billing;

import com.oyster.OysterCard;
import com.tfl.external.Customer;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.hamcrest.Matcher;

import java.math.BigDecimal;
import java.util.*;

public class TravelTrackerTest {

    @Rule
    public JUnitRuleMockery context = new JUnitRuleMockery();

    Set<UUID> cardsScanned = context.mock(Set.class);
    List<JourneyEvent> log = context.mock(List.class);
    Database database = context.mock(Database.class);
    private TravelTracker tracker = new TravelTracker(log, cardsScanned, database);
    private ArrayList<Customer> testCustomers;


    static final BigDecimal OFF_PEAK_JOURNEY_PRICE = new BigDecimal(2.40);
    static final BigDecimal PEAK_JOURNEY_PRICE = new BigDecimal(3.20);

    private void setupCustomers() {
        testCustomers = new ArrayList<Customer>() {
            {
                for(int i=0;i<10;i++) {
                    this.add(new Customer("Customer1", new OysterCard()));
                }
            }
        };
    }


//    Needs fixing
    @Test
    public void TestChargeAccounts() {
        CostManager costManager = context.mock(CostManager.class);
        setupCustomers();

        context.checking(new Expectations() {{
            exactly(testCustomers.size()).of(log).iterator();
            exactly(testCustomers.size()).of(costManager).chargeCustomerAmount(with(any(Customer.class)),with(log));
        }});

        tracker.chargeAccounts(testCustomers);
    }

    //Fixed
    @Test
    public void CardScannedWhileCurrentlyTravellingTest() {
        UUID test = UUID.randomUUID();
        UUID reader = UUID.randomUUID();

        context.checking(new Expectations() {{
            oneOf(cardsScanned).contains(test); will(returnValue(true));
            oneOf(log).add(with(any(JourneyEnd.class)));
            oneOf(cardsScanned).remove(test);
        }});

        tracker.cardScanned(test, reader);

    }

    //Fixed
    @Test
    public void CardScannedWhileNotCurrentlyTravellingTest() {
        UUID test = UUID.randomUUID();
        UUID reader = UUID.randomUUID();

        context.checking(new Expectations() {{
            oneOf(cardsScanned).contains(test); will(returnValue(false));
            oneOf(database).isRegisteredId(test); will(returnValue(true));

            oneOf(cardsScanned).add(test);
            oneOf(log).add(with(any(JourneyStart.class)));
        }});

        tracker.cardScanned(test, reader);
    }

    @Test
    public void ConnectRegistersOneCard() {
        CardReader reader = context.mock(CardReader.class);
        context.checking(new Expectations() {{
            oneOf(reader).register(tracker);
        }});
        tracker.connect(reader);
    }

    @Test
    public void ConnectRegistersMultipleCards() {
        CardReader reader = context.mock(CardReader.class);

        context.checking(new Expectations() {{
            exactly(3).of(reader).register(tracker);
        }});

        tracker.connect(reader,reader,reader);
    }
}
