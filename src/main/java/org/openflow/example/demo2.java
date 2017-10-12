package org.openflow.example;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class demo2 {
    public static void main(String[] args) throws InterruptedException, ExecutionException{
        ExecutorService exec = Executors.newSingleThreadExecutor();
        Future<Integer> f = exec.submit(new MyCallable());

        System.out.println(f.isDone()); //False

        System.out.println(f.get()); //Waits until the task is done, then prints 1
    }
}


class MyCallable implements Callable<Integer> {

    public Integer call() throws Exception {
        Thread.sleep(10000);
        return 1;
    }

}
