package com.tfl.billing;

import com.tfl.billing.helpers.CostCalculatingUtil;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Date;

import static junit.framework.TestCase.assertFalse;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;


public class CostCalculatingUtilTest {

    CostCalculatingUtil costCalculator;
    Date peakTime;
    Date offPeakTime;

    public CostCalculatingUtilTest(){
        peakTime = new Date();
        offPeakTime = new Date();
    }

    private long hoursToMillis(int hour) {
        return hour * 60 * 60 * 1000;
    }

    @Test
    public void morningPeakTimeIsMarkedAsPeak() {
        peakTime.setTime(hoursToMillis(6));
        assertTrue(CostCalculatingUtil.isPeak(peakTime));
    }

    @Test
    public void anythingBetweenPeakTimesIsMarkedAsOffPeak() {offPeakTime.setTime(hoursToMillis(12));
        assertFalse(CostCalculatingUtil.isPeak(offPeakTime));
    }

    @Test
    public void afternoonPeakTimeIsMarkedAsPeak() {peakTime.setTime(hoursToMillis(18));
        assertTrue(CostCalculatingUtil.isPeak(peakTime));
    }

    @Test
    public void eveningTimeIsMarkedAsOffPeak() {offPeakTime.setTime(hoursToMillis(22));
        assertFalse(CostCalculatingUtil.isPeak(offPeakTime));
    }

    @Test
    public void correctlyRoundsToTheNearestPennyFloor() {
        assertThat(CostCalculatingUtil.roundToNearestPenny(new BigDecimal(1.0100010)), is(new BigDecimal(1.01).setScale(2, BigDecimal.ROUND_HALF_UP)));
    }

    @Test
    public void correctlyRoundsToTheNearestPennyCeil() {
        assertThat(CostCalculatingUtil.roundToNearestPenny(new BigDecimal(1.5190011)), is(new BigDecimal(1.52).setScale(2, BigDecimal.ROUND_HALF_UP)));
    }
}
