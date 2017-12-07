package com.tfl.billing;

public class JourneyCostCalculatorTest {
/*
    Date peakTime;
    Date offPeakTime;
    Customer c;
    Customer c1;
    Customer c2;
    JourneyCostCalculator costCalculator;

    public JourneyCostCalculatorTest() {
        peakTime = new Date();
        offPeakTime = new Date();
        c = CustomerDatabase.getInstance().getCustomers().get(0);
        c1 = CustomerDatabase.getInstance().getCustomers().get(1);
        c2 = CustomerDatabase.getInstance().getCustomers().get(2);
        costCalculator = new JourneyCostCalculator();
    }

    private long hoursToMillis(int hour) {
        return hour*60*60*1000;
    }

    private Journey createSingleJourneyFor (Customer customer, Station from, Station to){
        JourneyStart journeyStart = new JourneyStart(customer.cardId(), OysterReaderLocator.atStation(from).id());
        JourneyEnd journeyEnd = new JourneyEnd  (customer.cardId(), OysterReaderLocator.atStation(to).id());
        return new Journey(journeyStart, journeyEnd);
    }

    private Journey createSingleJourneyFor (Customer customer, Station from, DateTime startTime, Station to, DateTime endTime){
        JourneyStart journeyStart = new JourneyStart(customer.cardId(), OysterReaderLocator.atStation(from).id(), startTime);
        JourneyEnd journeyEnd = new JourneyEnd  (customer.cardId(), OysterReaderLocator.atStation(to).id(), endTime);
        return new Journey(journeyStart, journeyEnd);
    }

    @Test
    public void shortOffPeakJourneyChargedCorrectly() {
        DateTime startTime = new DateTime();
        DateTime endTime = new DateTime();
        startTime = startTime.hourOfDay().setCopy(11);
        endTime = endTime.hourOfDay().setCopy(11);
        BigDecimal expected = JourneyCosts.OFF_PEAK_SHORT_JOURNEY_PRICE;
        List<Journey> shortPeakJourney = Arrays.asList(createSingleJourneyFor(c, Station.PADDINGTON, startTime, Station.VICTORIA_STATION, endTime));
        assertThat(costCalculator.getTotalFromJourneyList(shortPeakJourney), is(costCalculator.roundToNearestPenny(expected)));
    }

    @Test
    public void shortPeakJourneyChargedCorrectly() {
        DateTime startTime = new DateTime();
        DateTime endTime = new DateTime();
        startTime = startTime.hourOfDay().setCopy(7);
        endTime = endTime.hourOfDay().setCopy(7);
        BigDecimal expected = JourneyCosts.PEAK_SHORT_JOURNEY_PRICE;
        List<Journey> shortPeakJourney = Arrays.asList(createSingleJourneyFor(c, Station.PADDINGTON, startTime, Station.VICTORIA_STATION, endTime));
        assertThat(costCalculator.getTotalFromJourneyList(shortPeakJourney), is(costCalculator.roundToNearestPenny(expected)));
    }

    @Test
    public void longOffPeakJourneyChargedCorrectly() {
        DateTime startTime = new DateTime();
        DateTime endTime = new DateTime();
        startTime = startTime.hourOfDay().setCopy(11);
        endTime = endTime.hourOfDay().setCopy(12);
        BigDecimal expected = JourneyCosts.OFF_PEAK_LONG_JOURNEY_PRICE;
        List<Journey> shortPeakJourney = Arrays.asList(createSingleJourneyFor(c, Station.PADDINGTON, startTime, Station.VICTORIA_STATION, endTime));
        assertThat(costCalculator.getTotalFromJourneyList(shortPeakJourney), is(costCalculator.roundToNearestPenny(expected)));
    }

    @Test
    public void longPeakJourneyChargedCorrectly() {
        DateTime startTime = new DateTime();
        DateTime endTime = new DateTime();
        startTime = startTime.hourOfDay().setCopy(16);
        endTime = endTime.hourOfDay().setCopy(18);
        BigDecimal expected = JourneyCosts.PEAK_LONG_JOURNEY_PRICE;
        List<Journey> shortPeakJourney = Arrays.asList(createSingleJourneyFor(c, Station.PADDINGTON, startTime, Station.VICTORIA_STATION, endTime));
        assertThat(costCalculator.getTotalFromJourneyList(shortPeakJourney), is(costCalculator.roundToNearestPenny(expected)));
    }

    @Test
    public void customerWhoDidNotTapOutIsChargedAPenaltyFare() {
        BigDecimal expected = BigDecimal.valueOf(9);
        //TODO: fill out
    }

    @Test
    public void offPeakCapAppliedCorrectly() {
        List<Journey> shortPeakJourney = new ArrayList<>();
        DateTime startTime = new DateTime();
        DateTime endTime = new DateTime();
        startTime = startTime.hourOfDay().setCopy(11);
        endTime = endTime.hourOfDay().setCopy(11);

        for(int i=0;i<=59;i+=6){
            startTime = startTime.minuteOfHour().setCopy(i);
            endTime = endTime.minuteOfHour().setCopy(i+5);
            shortPeakJourney.add(createSingleJourneyFor(c, Station.PADDINGTON, startTime, Station.VICTORIA_STATION, endTime));
        }

        BigDecimal expected = JourneyCosts.OFF_PEAK_DAILY_CAP_PRICE;
        assertThat(costCalculator.getTotalFromJourneyList(shortPeakJourney), is(costCalculator.roundToNearestPenny(expected)));
    }

    @Test
    public void peakCapAppliedCorrectly() {

        List<Journey> shortPeakJourney = new ArrayList<>();
        DateTime startTime = new DateTime();
        DateTime endTime = new DateTime();
        startTime = startTime.hourOfDay().setCopy(7);
        endTime = endTime.hourOfDay().setCopy(7);

        for(int i=0;i<=59;i+=6){
            startTime = startTime.minuteOfHour().setCopy(i);
            endTime = endTime.minuteOfHour().setCopy(i+5);
            shortPeakJourney.add(createSingleJourneyFor(c, Station.PADDINGTON, startTime, Station.VICTORIA_STATION, endTime));
        }

        BigDecimal expected = JourneyCosts.PEAK_DAILY_CAP_PRICE;
        assertThat(costCalculator.getTotalFromJourneyList(shortPeakJourney), is(costCalculator.roundToNearestPenny(expected)));
    }

    @Test
    public void morningPeakTimeIsMarkedAsPeak() {
        peakTime.setTime(hoursToMillis(6));
        assertTrue(JourneyCostCalculator.isPeak(peakTime));
    }

    @Test
    public void anythingBetweenPeakTimesIsMarkedAsOffPeak() {
        offPeakTime.setTime(hoursToMillis(12));
        assertFalse(JourneyCostCalculator.isPeak(offPeakTime));
    }

    @Test
    public void afternoonPeakTimeIsMarkedAsPeak() {
        peakTime.setTime(hoursToMillis(18));
        assertTrue(JourneyCostCalculator.isPeak(peakTime));
    }

    @Test
    public void eveningTimeIsMarkedAsOffPeak() {
        offPeakTime.setTime(hoursToMillis(22));
        assertFalse(JourneyCostCalculator.isPeak(offPeakTime));
    }

    @Test
    public void correctlyRoundsToTheNearestPennyFloor(){
        assertThat(costCalculator.roundToNearestPenny(new BigDecimal(1.0100010)), is(new BigDecimal(1.01).setScale(2,BigDecimal.ROUND_HALF_UP)));
    }

    @Test
    public void correctlyRoundsToTheNearestPennyCeil(){
        assertThat(costCalculator.roundToNearestPenny(new BigDecimal(1.5190011)), is(new BigDecimal(1.52).setScale(2,BigDecimal.ROUND_HALF_UP)));
    }*/

}
