package org.openflow.example;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class CompleteFutureDemo {
    public static void main(String args[]) throws InterruptedException, ExecutionException{
        Executor executor = null;
        CompletableFuture<String> resultCompletableFuture = CompletableFuture.supplyAsync(new Supplier<String>() {  
            @Override  
            public String get() {  
                try {  
                    TimeUnit.SECONDS.sleep(3);  
                } catch (InterruptedException e) {  
                    // TODO Auto-generated catch block  
                    e.printStackTrace();  
                }  
                return "hello";  
            }  
        }, executor);  
        System.out.println(resultCompletableFuture.get());  
    }
}
