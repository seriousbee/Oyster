package com.tfl.billing;

import org.joda.time.DateTime;

import java.util.UUID;

public class JourneyEvent {

    private final UUID cardId;
    private final UUID readerId;
    private final long time;

    public JourneyEvent(UUID cardId, UUID readerId) {
        this.cardId = cardId;
        this.readerId = readerId;
        this.time = System.currentTimeMillis();
    }

    public JourneyEvent(UUID cardId, UUID readerId, DateTime dateTime) {
        this.cardId = cardId;
        this.readerId = readerId;
        this.time = dateTime.getMillis();
    }

    public UUID cardId() {
        return cardId;
    }

    public UUID readerId() {
        return readerId;
    }

    public long time() {
        return time;
    }
}
