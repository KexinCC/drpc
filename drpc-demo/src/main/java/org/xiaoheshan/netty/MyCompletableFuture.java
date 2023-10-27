package org.xiaoheshan.netty;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class MyCompletableFuture {



    static CompletableFuture<Integer> completableFuture = new CompletableFuture<>();
    static int pub = 1;


    public static void main(String[] args) throws ExecutionException, InterruptedException {

        new Thread(() -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            int i = 8;
            completableFuture.complete(i);
            pub = 8;

        }).start();



        System.out.println(pub);

        // get 方法是一个阻塞的方法
        System.out.println(completableFuture.get());




    }
}
