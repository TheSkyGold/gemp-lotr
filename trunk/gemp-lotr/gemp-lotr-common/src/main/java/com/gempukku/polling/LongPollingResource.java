package com.gempukku.polling;

public interface LongPollingResource {
    public boolean wasProcessed();

    public void processIfNotProcessed();
}
