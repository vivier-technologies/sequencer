package com.vivier_technologies.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PackerTest {

    @Test
    public void testPacking() {
        long l = Packer.pack(234, 5678);
        assertEquals(234, (int)(l >> 32));
        assertEquals(5678, (int)l);
        assertEquals(234, Packer.getHi(l));
        assertEquals(5678, Packer.getLo(l));
    }

}