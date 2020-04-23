package com.vivier_technologies.utils;

public class Packer {

    public static long pack(int hi, int lo) {
        return (((long)hi) << 32) | (lo & 0xffffffffL);
    }

    public static int getHi(long val) {
        return (int)(val >> 32);
    }

    public static int getLo(long val) {
        return (int)val;
    }
}
