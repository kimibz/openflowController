package org.openflow.util;

public class U16 {
    public static int f(short i) {
        return (int)i & 0xffff;
    }

    public static short t(int l) {
        return (short) l;
    }
    public static void main(String args[]){
        System.out.println(f((short) 0xfff));
        System.out.println(t(2));
    }
}
