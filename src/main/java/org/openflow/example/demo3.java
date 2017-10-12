package org.openflow.example;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.function.Supplier;

public class demo3 {
    public static void main(String[] args) throws InterruptedException, ExecutionException{
        ExecutorService exec = Executors.newSingleThreadExecutor();
        CompletableFuture<Integer> f = CompletableFuture.supplyAsync(new MySupplier(), exec);
        System.out.println(f.isDone()); // False
        //下面就可以看到，f2这个异步事件处理无缝引用了f这个异步事件处理的结果。整个过程中间不需要像future.get()这样引入了不必要的同步阻塞
        CompletableFuture<Integer> f2 = f.thenApply(new PlusOne());
        System.out.println(f2.get()); // Waits until the "calculation" is done,
    }
}
class MySupplier implements Supplier<Integer> {

    @Override
    public Integer get() {
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            //Do nothing
        }
        return 1;
    }
}

/**

* A (pure) function that adds one to a given Integer
**/
class PlusOne implements Function<Integer, Integer> {

    @Override
    public Integer apply(Integer x) {
        return x + 1;
    }
}