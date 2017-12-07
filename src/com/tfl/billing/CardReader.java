package com.tfl.billing;

import com.oyster.ScanListener;

public interface CardReader {
    void register(ScanListener scanListener);
}
