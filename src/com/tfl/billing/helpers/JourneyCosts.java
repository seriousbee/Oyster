package com.tfl.billing.helpers;

import java.math.BigDecimal;

// Utility class that stores the fixed pricing values

public class JourneyCosts {
    public static final BigDecimal PEAK_LONG_JOURNEY_PRICE = new BigDecimal(3.80);
    public static final BigDecimal PEAK_SHORT_JOURNEY_PRICE = new BigDecimal(2.90);
    public static final BigDecimal PEAK_DAILY_CAP_PRICE = new BigDecimal(9.00);
    public static final BigDecimal OFF_PEAK_LONG_JOURNEY_PRICE = new BigDecimal(2.70);
    public static final BigDecimal OFF_PEAK_SHORT_JOURNEY_PRICE = new BigDecimal(1.60);
    public static final BigDecimal OFF_PEAK_DAILY_CAP_PRICE = new BigDecimal(7.00);
}
