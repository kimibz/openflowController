package org.openflow.example;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class lamdaDemo {

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        List<String> names = Arrays.asList("peter", "anna", "mike", "xenia");
        Collections.sort(names, (String a, String b) -> b.compareTo(a));  
        System.out.println(names.toString());
        
    }

}
