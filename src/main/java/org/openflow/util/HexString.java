package org.openflow.util;

public class HexString {
    /**
     * 转换mac数组
     * @param bytes
     * @return "0f:ca:fe:de:ad:be:ef"
     */
    public static String toHexString(byte[] bytes) {
        int i;
        String ret = "";
        String tmp;
        for(i=0; i< bytes.length; i++) {
            if(i> 0)
                ret += ":";
            tmp = Integer.toHexString(U8.f(bytes[i]));
            if (tmp.length() == 1)
                ret += "0";
            ret += tmp; 
        }
        return ret;
    }
    
    public static String toHexString(long val) {
        char arr[] = Long.toHexString(val).toCharArray();
        String ret = "";
        // prepend the right number of leading zeros
        int i = 0;
        for (; i < (16 - arr.length); i++) {
            ret += "0";
            if ((i % 2) == 1)
                ret += ":";
        }
        for (int j = 0; j < arr.length; j++) {
            ret += arr[j];
            if ((((i + j) % 2) == 1) && (j < (arr.length - 1)))
                ret += ":";
        }
        return ret;
    }
    
    
    /**
     * 这个有点BUG，建议使用macToString类里的方法来返回mac的string
     * mac string 转成数组
     * @param values "0f:ca:fe:de:ad:be:ef"
     * @return [15, 5 ,2, 5, 17] 
     */
    
    public static byte[] fromHexString(String values) {
        String[] octets = values.split(":");
        byte[] ret = new byte[octets.length];
        int i;
        
        for(i=0;i<octets.length; i++)
            ret[i] = Integer.valueOf(octets[i], 16).byteValue();
        return ret;
    }
    
    public static long toLong(String values) {
        return Long.parseLong(values.replaceAll(":", ""),16);
    }

}
