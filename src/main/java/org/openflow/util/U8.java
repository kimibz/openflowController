package org.openflow.util;

public class U8 {
    public static short f(byte i) {
        return (short) ((short)i & 0xff);
    }

    public static byte t(short l) {
        return (byte) l;
    }
    public static void main(String args[]){
        System.out.println(f((byte) 10000101));
        System.out.println(t((short) 144440));
    }
}
