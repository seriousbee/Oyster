package com.tfl.billing;

import com.oyster.OysterCard;
import com.tfl.billing.database.Database;
import com.tfl.billing.journeyelements.JourneyEnd;
import com.tfl.billing.journeyelements.JourneyEvent;
import com.tfl.billing.journeyelements.JourneyStart;
import com.tfl.external.Customer;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class TravelTrackerTest {
/*
    @Rule
    public JUnitRuleMockery context = new JUnitRuleMockery();

    Set<UUID> cardsScanned = context.mock(Set.class);
    List<JourneyEvent> log = context.mock(List.class);
    Database database = context.mock(Database.class);
    CostManager costManager = context.mock(CostManager.class);
    private TravelTracker tracker = new TravelTracker(log, cardsScanned, database, costManager);
    private ArrayList<Customer> testCustomers;

    private void setupCustomers() {
        testCustomers = new ArrayList<Customer>() {{
            for(int i=0;i<10;i++) {
                this.add(new Customer("Customer1", new OysterCard()));
            }
        }};
    }

    @Test
    public void TestChargeAccounts() {
        setupCustomers();
        context.checking(new Expectations() {{
          exactly(testCustomers.size()).of(costManager).chargeCustomerAmount(with(any(Customer.class)),with(log));
        }});

        tracker.chargeAccounts(testCustomers);
    }

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
    }*/
}
