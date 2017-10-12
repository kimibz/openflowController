package org.openflow.util;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;

public class StringByteSerializer {
    /**
     * 从ByteBuffer中读取信息，返回string
     * @param data
     * @param length
     * @return
     */
    public static String readFrom(ByteBuffer data, int length) {
        byte[] stringBytes = new byte[length];
        data.get(stringBytes);
        // 找到一个0的位置
        int index = 0;
        for (byte b : stringBytes) {
            if (0 == b)
                break;
            ++index;
        }
        return new String(Arrays.copyOf(stringBytes, index),
                Charset.forName("ascii"));
    }
    /**
     * 从byte数组内读取信息，并返回string
     * @param stringBytes
     * @param length
     * @return
     */
    public static String readFromByte(byte[] stringBytes, int length) {
        int index = 0;
        for (byte b : stringBytes) {
            if (0 == b)
                break;
            ++index;
        }
        return new String(Arrays.copyOf(stringBytes, index),
                Charset.forName("ascii"));
    }
    /**
     * 将string value写入bytebuffer
     * @param data
     * @param length
     * @param value
     */
    public static void writeTo(ByteBuffer data, int length, String value) {
        try {
            byte[] name = value.getBytes("ASCII");
            if (name.length < length) {
                data.put(name);
                for (int i = name.length; i < length; ++i) {
                    data.put((byte) 0);
                }
            } else {
                data.put(name, 0, length-1);
                data.put((byte) 0);
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
    //bytebuffer转16进制string
    public static String ByteBufferToHexString(ByteBuffer buffer){
        buffer.flip();
        byte[] src= new byte[buffer.remaining()];
        buffer.get(src, 0, src.length);//将bytebuffer内的数据写入byte数组
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }
    //byte数组转16进制string
    public static String byteToHexString(byte[] src){
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }
}
