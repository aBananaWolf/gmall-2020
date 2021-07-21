package com.atguigu.gmall.item.config;

import com.atguigu.gmall.item.annotation.GmallCache;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author wyl
 * @create 2020-06-28 12:24
 */
@Aspect
@Component
@EnableAspectJAutoProxy
public class DistributeLockConfig {
    @Autowired
    private RedissonClient client;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private ObjectMapper objectMapper;

    @Pointcut("@annotation(com.atguigu.gmall.item.annotation.GmallCache)")
    public void pointcut() {
    }

    @Around("pointcut()")
    public Object DistributeLock(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Object result = null;
        // 强转为方法签名
        MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
        // 获取注解
        GmallCache annotation = signature.getMethod().getAnnotation(GmallCache.class);
        // 缓存键
        String key = annotation.cacheKey();
        // 前缀
        String prefix = annotation.prefix();
        // redis最终缓存键
        String finalKey = null;
        if (!"".equals(key))
            finalKey = prefix + key;
        else
            finalKey = prefix + proceedingJoinPoint.getArgs()[0];
        // 判断是否为List
        boolean isList = false;
        // 返回的类型
        Type genericReturnType = signature.getMethod().getGenericReturnType();

        // 缓存命中
        String cacheHit = redisTemplate.opsForValue().get(finalKey);
        if (cacheHit != null) {
            return parseJson(signature, cacheHit);
        }

        int cacheTime = annotation.cacheTime();
        int randomTime = annotation.randomTime();
        int finalCacheTime = new Random().nextInt(randomTime) + cacheTime;

        // 获取分布式锁
        RLock lock = client.getLock(proceedingJoinPoint.getTarget().getClass().getAnnotation(GmallCache.class).lockName());
        lock.lock();
        // 再次检测，加速期间可能已经有线程获取
        cacheHit = redisTemplate.opsForValue().get(finalKey);
        if (cacheHit != null) {
            return parseJson(signature, cacheHit);
        }
        try {
            result = proceedingJoinPoint.proceed();
            redisTemplate.opsForValue().set(finalKey, objectMapper.writeValueAsString(result), finalCacheTime, TimeUnit.MINUTES);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }

        return result;
    }

    private Object parseJson(MethodSignature signature, String cacheHit) throws com.fasterxml.jackson.core.JsonProcessingException {
        return objectMapper.readValue(cacheHit, new TypeReference<Object>() {
            @Override
            public Type getType() {
                return signature.getMethod().getGenericReturnType();
            }
        });
    }
}
