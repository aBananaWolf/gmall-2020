package com.atguigu.com.gmall.sso.test;

import java.util.*;
import java.util.function.Supplier;

/**
 * @author wyl
 * @create 2020-07-25 17:08
 */

public class RandomElementRemoveTestOfList {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();
    public static void main(String[] args) throws InterruptedException {
        // 测试基于元素的随机删除，ArrayList 和 LinkedList 哪个更快
        //        int capacity = 10_0000; // ArrayList 更快
        //        int capacity = 500; // ArrayList 更快
        //        int capacity = 100; // ArrayList 更快
        //        int capacity = 50; // LinkedList 更快

        // 测试基于下标的随机删除，ArrayList 和 LinkedList 哪个更快
        //        int capacity = 500; // ArrayList 更快
        //        int capacity = 200; // ArrayList 更快
        //        int capacity = 100; // LinkedList 更快
        //        int capacity = 50; // LinkedList 更快
        System.out.println(new int[5].length);

        // 容器容量/数据规模
        int capacity = 10_0000;
        // 测试次数
        int testNum = 5;

        double testArrayList = test(testNum, capacity, () -> new ArrayList<>(capacity));
        double testLinkedList = test(testNum, capacity, LinkedList::new);

        System.out.println("当数据规模为 " + capacity + " 个元素，测试次数为 " + testNum + " 时：");
        System.out.println("ArrayList 测试时间为：" + testArrayList + "纳秒");
        System.out.println("LinkedList 测试时间为：" + testLinkedList + "纳秒");
        if (testArrayList < testLinkedList)
            System.out.println("在基于元素的随机删除测试过程中，testArrayList 更快");
        else
            System.out.println("在基于元素的随机删除测试过程中，testLinkedList 更快");


        /*ArrayList<Long> testResultList = new ArrayList<>(testNum);

        for (int i = 0; i < testNum; i++) {
            ArrayList<Integer> arrayList = new ArrayList<>(capacity);
            testResultList.add(test(capacity, arrayList));;
        }
        calculateTestResult(testResultList);

        for (int i = 0; i < testNum; i++) {
            LinkedList<Integer> linkedList = new LinkedList<>();
            testResultList.add(test(capacity, linkedList));
        }
        calculateTestResult(testResultList);*/
    }

    public static double test(int testNum, int capacity, Supplier<List<Integer>> listSuppler) {
        ArrayList<Long> testResultList = new ArrayList<>(testNum);
        String listTypeString = listSuppler.get().getClass().getTypeName();
        for (int i = 0; i < testNum; i++) {
            System.out.println(listTypeString + "进行第 " + (i + 1) + " 次测试" );
            List<Integer> list = new ArrayList<>(capacity);
            testResultList.add(test(capacity, listSuppler.get()));;
        }
        return calculateTestResult(testResultList);
    }
    
    public static long test(int capacity, List<Integer> list) {
        fillList(list, capacity);
        start();
        elementBasedRandomRemove(list, capacity);
        return end();
    }
    
    public static double calculateTestResult(List<Long> testResultList) {
        System.out.println(testResultList.getClass().getTypeName() + " 基于元素的随机删除测试结果：");
        for (int i = 0; i < testResultList.size(); i++) {
            System.out.println("第 " + (i + 1) + " 次结果：" + testResultList.get(i) + " 纳秒");
        }
        double averageTime = testResultList.stream().mapToLong(Long::longValue).average().orElse(0L);
        System.out.println("平均时间：" + averageTime + " 纳秒") ;
        System.out.println();
        return averageTime;
    }

    public static void elementBasedRandomRemove(List<Integer> list, int num) {
       /* // 基于下标的随机删除
        Random random = new Random();
        while (list.size() != 0) {
            list.remove(random.nextInt(list.size()));
        }*/


        // 基于元素的随机删除
        for (int i = 0; i < num; i++) {
            list.remove(new Integer(i));

            // 基于元素的随机删除
            // boolean remove = list.remove(new Integer(i));
            // 基于下标的随机删除
            // Integer remove = list.remove(i);
        }
    }
    public static void fillList(List<Integer> list, int num) {
        for (int i = 0; i < num; i++) {
            list.add(i);
        }
        Collections.shuffle(list);
    }

    public static void start() {
        threadLocal.set(System.nanoTime());
    }

    public static long end() {
        long executionTime = System.nanoTime() - threadLocal.get();
        System.out.println("执行时间为：" + executionTime + " 纳秒");
        threadLocal.remove();
        return executionTime;
    }
}