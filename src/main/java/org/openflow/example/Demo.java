package org.openflow.example;

import java.io.UnsupportedEncodingException;

import org.openflow.util.StringByteSerializer;

public class Demo {

    public static void main(String[] args) throws UnsupportedEncodingException {
        // TODO Auto-generated method stub
        final String NAME_WIDTH = "-15";
        final String VALUE_WIDTH = "-20";
        final String FORMAT_STRING = "%1$" + NAME_WIDTH + "s%2$"
                + VALUE_WIDTH + "s%3$s\n";
        byte[] exa = {(byte)0xff,(byte)3};
        StringByteSerializer.readFromByte(exa, 2);
        System.out.println(StringByteSerializer.byteToHexString(exa));
        System.out.println(String.format(FORMAT_STRING, "\toption", "type [default]", "usage"));
    }

}
