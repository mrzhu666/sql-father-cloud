package org.mrzhuyk.sqlfather.user;


import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

public class MainTest {
    public static void main(String[] args) throws InterruptedException {
        ThreadPoolExecutor executorService = new ThreadPoolExecutor(3, 10, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>(100));
        StopWatch watch = new StopWatch("MainTest");
        watch.start();
        CountDownLatch countDownLatch = new CountDownLatch(5);
        //CompletableFuture<String> future = CompletableFuture.supplyAsync(()->"String");
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> "hello!")
            .thenCombine(CompletableFuture.supplyAsync(
                () -> "world!"), (s1, s2) -> s1 + s2)
            .thenCompose(s -> CompletableFuture.supplyAsync(() -> s + "nice!"));
        
        future.join();
        
        for (int i = 0; i < 5; i++) {
            int finalI = i;
            // 线程池提交任务
            executorService.submit(() -> {
                try {
                    System.out.println("当前线程 " + Thread.currentThread().getName() + ",---【任务" + finalI + "】开始执行---");
                    // 模拟从数据库查询数据并对数据进行处理
                    List<String> dataFromDB = getDataFromDB();
                    for (String str : dataFromDB) {
                        try {
                            System.out.println("当前线程 " + Thread.currentThread().getName() + ",【任务" + finalI + "】开始处理数据=" + str);
                            TimeUnit.SECONDS.sleep(1);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    System.out.println("当前线程 " + Thread.currentThread().getName() + ",---【任务" + finalI + "】执行完成---");
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();
        watch.stop();
        System.out.println(watch.prettyPrint());
    }
    
    public static List<String> getDataFromDB() {
        return Arrays.asList("1", "2", "3");
    }
    
}
