package org.openflow.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReadTxT {
    /**
     * 逐行读取文本
     * @param filePath 文件地址
     * @return
     * @throws IOException
     */
    public static List<String> readF1(String filePath) throws IOException { 
        List<String> list = new ArrayList<String>();
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
        for (String line = br.readLine(); line != null; line = br.readLine()) {
            list.add(line);
//          System.out.println(line);               
        }
        br.close();
        return list;
    }
    /**
     * 逐行写入文本
     * @param word 写入内容
     * @param filePath 文件地址
     */
    public static void writeF1(String word,String filePath){
        FileWriter fw = null;
        try {
            fw = new FileWriter(filePath,true);
            fw.write(word);
            fw.close();
        } catch (IOException e1) {
            e1.printStackTrace();
            System.out.println("写入失败");
            System.exit(-1);
        }
    }
    //例子：1234-4567 将“-”去除，（1234，4567）存入并返回map
    public static Map<String,String> splitToMap(String word,Map<String, String> map){
        String temp[]=word.split("-");
        if(temp.length==2){
        map.put(temp[0],temp[1]);
        return map;
        }
        else{
            return null;
        }
    }
}
