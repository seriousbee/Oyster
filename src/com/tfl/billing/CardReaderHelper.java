package com.tfl.billing;

import com.oyster.OysterCard;
import com.oyster.OysterCardReader;
import com.oyster.ScanListener;

import java.util.UUID;

public class CardReaderHelper implements CardReader {
    private OysterCardReader reader;

    public CardReaderHelper() {
       reader = new OysterCardReader();
    }

    @Override
    public void register(ScanListener scanListener) {
        reader.register(scanListener);
    }

    @Override
    public void touch(OysterCard card) {
        reader.touch(card);
    }

    @Override
    public UUID id() {
        return reader.id();
    }
}
