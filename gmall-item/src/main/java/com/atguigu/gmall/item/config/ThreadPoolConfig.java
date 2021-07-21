package com.atguigu.gmall.item.config;

import com.atguigu.gmall.item.exception.ItemCompletionFutureException;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author wyl
 * @create 2020-06-29 18:43
 */
@Configuration
public class ThreadPoolConfig {
    @Bean
    public ThreadPoolExecutor threadFactory() {
        return new ItemModuleThreadPoolExecutor(
                        Runtime.getRuntime().availableProcessors() * 100,
                        Runtime.getRuntime().availableProcessors() * 100,
                        0, TimeUnit.SECONDS,new ArrayBlockingQueue<>(2000),
                        new ThreadPoolExecutor.CallerRunsPolicy()
                );
    }

    @Slf4j
    static class ItemModuleThreadPoolExecutor extends ThreadPoolExecutor {

        public ItemModuleThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
            super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,new ItemDefaultThreadFactory(), handler);
        }

        @Override
        protected void afterExecute(Runnable r, Throwable t) {
            log.info("thread execution failure：" + r.toString() , t);
        }
    }

    static class ItemDefaultThreadFactory implements ThreadFactory {
        private static final AtomicInteger poolNumber = new AtomicInteger(1);
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        ItemDefaultThreadFactory() {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() :
                    Thread.currentThread().getThreadGroup();
            namePrefix = "pool-" +
                    poolNumber.getAndIncrement() +
                    "-item-getAndRender-thread-";
        }

        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r,
                    namePrefix + threadNumber.getAndIncrement(),
                    0);
            if (t.isDaemon())
                t.setDaemon(false);
            if (t.getPriority() != Thread.NORM_PRIORITY)
                t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }
    }
}
