package com.vivier_technologies.sequencer.eventstore;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class MemoryMappedEventStoreTest {

    @Test
    public void testSemantics() throws IOException {
        MemoryMappedEventStore es = new MemoryMappedEventStore(null, null);
        es.open();
        es.close();
    }

}