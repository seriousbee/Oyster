package com.tfl.billing;

import com.tfl.billing.helpers.CostCalculatingUtil;
import com.tfl.billing.helpers.JourneyCosts;
import com.tfl.billing.legacyinteraction.DBHelper;
import com.tfl.billing.legacyinteraction.PaymentsController;
import com.tfl.external.Customer;
import com.tfl.underground.OysterReaderLocator;
import com.tfl.underground.Station;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Date;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

//source: https://stackoverflow.com/questions/1092219/assertcontains-on-strings-in-junit


/**
 * Created by tomaszczernuszenko on 07/12/2017.
 */
@RunWith(Theories.class)
public class SmokeTesting {

    private static DBHelper dbHelper;

    //source: https://stackoverflow.com/questions/1119385/junit-test-for-system-out-println
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    JourneyTracker tracker;
    PaymentsController paymentsController;


    @BeforeClass
    public static void beforeAll(){
        dbHelper = new DBHelper();
    }

    @Before
    public void beforeEach(){
        System.setOut(new PrintStream(outContent));
        tracker = new JourneyTracker();
        paymentsController = new PaymentsController();
    }

    @After
    public void after(){
        System.setOut(new PrintStream(outContent));
    }

    @Theory
    public void customerCorrectlyAddedToTheDB(String name) {
        Customer c = dbHelper.createCustomer(name);
        dbHelper.commitCustomerToDB(c);
        assertTrue(dbHelper.isRegisteredId(c.cardId()));
    }

    public static @DataPoints String[] names = {"Adam Testowy", "Jan Niezbedny", "Lorem Ipsum", "ABCD", "1234567890"};


    @Test
    public void notTravelingPersonIsNotCharged(){
        Customer nonTraveler = dbHelper.createCustomer("Adam Testowy");
        dbHelper.commitCustomerToDB(nonTraveler);
        paymentsController.chargeAllAccounts();
        String expected = "Customer: Adam Testowy - " + nonTraveler.cardId() + "Journey Summary:Total charge £: 0.00";
        Assert.assertThat(outContent.toString().replace("\n", "").replace("\r", ""), containsString(expected));
    }

    @Test
    public void customCustomerAppearsOnlyOnceOnReceipt(){
        Customer nonTraveler = dbHelper.createCustomer("Adam Testowy");
        dbHelper.commitCustomerToDB(nonTraveler);

        tracker.cardScanned(nonTraveler.cardId(), OysterReaderLocator.atStation(Station.PADDINGTON).id());
        paymentsController.chargeAllAccounts();

        //source: https://stackoverflow.com/questions/767759/occurrences-of-substring-in-a-string
        int numberOfCustomerOccurrences = outContent.toString().split(nonTraveler.cardId().toString(), -1).length-1;

        assertEquals(1, numberOfCustomerOccurrences);
    }

    @Test
    public void unfinishedJourneysArePenalised(){
        Customer barrierJumper = dbHelper.createCustomer("Adam Testowy");
        dbHelper.commitCustomerToDB(barrierJumper);

        tracker.cardScanned(barrierJumper.cardId(), OysterReaderLocator.atStation(Station.PADDINGTON).id());
        paymentsController.chargeAllAccounts();

        String expected = "Customer: Adam Testowy - " + barrierJumper.cardId() + "Journey Summary:Total charge £: " + CostCalculatingUtil.roundToNearestPenny(JourneyCosts.PEAK_DAILY_CAP_PRICE);
        Assert.assertThat(outContent.toString().replace("\n", "").replace("\r", ""), containsString(expected));
    }

    @Test
    public void customerChargedCorrectAmountForSingleJourney(){
        Customer customer = dbHelper.createCustomer("Adam Testowy");
        dbHelper.commitCustomerToDB(customer);

        tracker.cardScanned(customer.cardId(), OysterReaderLocator.atStation(Station.PADDINGTON).id());
        tracker.cardScanned(customer.cardId(), OysterReaderLocator.atStation(Station.PADDINGTON).id());

        paymentsController.chargeAllAccounts();

        String expected;
        if(CostCalculatingUtil.isPeak(new Date()))
            expected = "Total charge £: " + CostCalculatingUtil.roundToNearestPenny(JourneyCosts.PEAK_SHORT_JOURNEY_PRICE);
        else
            expected = "Total charge £: " + CostCalculatingUtil.roundToNearestPenny(JourneyCosts.OFF_PEAK_SHORT_JOURNEY_PRICE);

        Assert.assertThat(outContent.toString(), containsString(expected));
    }

    //there is a slight chance this test will flicker if run on a border between peak and off peak, but it is minimal
    @Test
    public void correctCapAppliedForCustomer(){
        Customer customer = dbHelper.createCustomer("Adam Testowy");
        dbHelper.commitCustomerToDB(customer);

        //create 7 journeys
        for(int i=0; i<14; i++){
            tracker.cardScanned(customer.cardId(), OysterReaderLocator.atStation(Station.PADDINGTON).id());
        }

        paymentsController.chargeAllAccounts();

        String expected;
        if(CostCalculatingUtil.isPeak(new Date()))
            expected = "Total charge £: " + CostCalculatingUtil.roundToNearestPenny(JourneyCosts.PEAK_DAILY_CAP_PRICE);
        else
            expected = "Total charge £: " + CostCalculatingUtil.roundToNearestPenny(JourneyCosts.OFF_PEAK_DAILY_CAP_PRICE);

        Assert.assertThat(outContent.toString(), containsString(expected));
    }


}
