package com.tfl.billing;

import com.tfl.external.Customer;
import com.tfl.external.CustomerDatabase;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;


public class JourneyCostCalculatorTest {
    JourneyCostCalculator testCalc = new JourneyCostCalculator();
    Date peakTime = new Date();
    Date offPeakTime = new Date();
    Customer c = CustomerDatabase.getInstance().getCustomers().get(0);

    @Rule
    public JUnitRuleMockery context = new JUnitRuleMockery();

    private long getTimeInMillis(int hour) {
        return hour*60*60*1000;
    }

    @Test
    public void getJourneyEventsTest() throws Exception {
        List<JourneyEvent> events = context.mock(List.class);

    }

    @Test
    public void getJourneysTest() throws Exception {
    }

    @Test
    public void getTotalTest() throws Exception {
    }

    @Test
    public void peakTest() {
        peakTime.setTime(getTimeInMillis(6));
        offPeakTime.setTime(getTimeInMillis(12));

        assertTrue(testCalc.peak(peakTime));
        assertFalse(testCalc.peak(offPeakTime));

        peakTime.setTime(getTimeInMillis(18));
        offPeakTime.setTime(getTimeInMillis(22));

        assertTrue(testCalc.peak(peakTime));
        assertFalse(testCalc.peak(offPeakTime));

    }

    @Test
    public void correctlyRoundstoTheNearestPenny(){
        System.out.println();

        assertThat(testCalc.roundToNearestPenny(new BigDecimal(1.5110011)), is(new BigDecimal(1.51).setScale(2,BigDecimal.ROUND_HALF_UP)));
        assertThat(testCalc.roundToNearestPenny(new BigDecimal(1.0100010)), is(new BigDecimal(1.01).setScale(2,BigDecimal.ROUND_HALF_UP)));
    }

}