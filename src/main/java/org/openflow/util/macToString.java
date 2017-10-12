package org.openflow.util;

public class macToString {
    /**
     * 将mac数组转成string，例如16:23:aa:22:33
     * @param mac mac数组
     * @return
     */
    public static String toString(byte [] mac){
        String value = "";
        for(int i = 0;i < mac.length; i++){
            String sTemp = Integer.toHexString(0xFF & mac[i]);
            value = value+sTemp+":";
        }
        value = value.substring(0,value.lastIndexOf(":"));
        return value;
    }
}
