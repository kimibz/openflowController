package org.openflow.example;

import java.util.NoSuchElementException;
import java.util.Optional;

public class optional_demo {
    public static void main(String args[]){
        Optional<String> name = Optional.of("Sanaulla");
//        Optional<String> someNull = Optional.of(null);
        Optional empty = Optional.ofNullable(null);
      //isPresent方法用来检查Optional实例中是否包含值
        if (name.isPresent()) {
          //在Optional实例内调用get()返回已存在的值
          System.out.println(name.get());//输出Sanaulla
        }
        try {
            //在空的Optional实例上调用get()，抛出NoSuchElementException
            System.out.println(empty.get());
          } catch (NoSuchElementException ex) {
            System.out.println(ex.getMessage());
          }
      //ifPresent方法接受lambda表达式作为参数。
      //lambda表达式对Optional的值调用consumer进行处理。
      name.ifPresent((value) -> {
        System.out.println("The length of the value is: " + value.length());
      });
    }
}
