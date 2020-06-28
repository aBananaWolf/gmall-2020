package cn.com;

import com.sun.org.apache.xpath.internal.operations.Quo;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

import static java.util.stream.Collectors.toList;

/**
 * @author wyl
 * @create 2020-06-09 10:05
 */
class Shop {

    /**
     * 商店名称
     */
    private String name;
    private Random random = new Random();


    public Shop(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    /**
     * (阻塞式)通过名称查询价格
     * @param product
     * @return
     */
    public String getPrice(String product) {

        double price = calculatePrice(product);
        //随机得到一个折扣码
        Discount.Code code = Discount.Code.values()[
                random.nextInt(Discount.Code.values().length)];
        return String.format("%s:%.2f:%s", name, price, code);
    }

    /**
     * 计算价格(模拟一个产生价格的方法)
     * @param product
     * @return
     */
    private double calculatePrice(String product) {
        delay();
        //数字*字符=数字(产生价格的方法)
        return random.nextDouble() * product.charAt(0) * product.charAt(1);
    }


    /**
     * 模拟耗时操作,阻塞1秒
     */
    private void delay() {
        try {
            Thread.sleep(1_000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}


class Discount {

    public enum Code {
        NONE(0), SILVER(5), GOLD(10), PLATINUM(15), DIAMOND(20);

        private final int percentage;

        Code(int percentage) {
            this.percentage = percentage;
        }
    }

    /**
     * 根据一个Quote返回一个折扣信息
     * @param quote
     * @return
     */
    public static String applyDiscount(Quote quote) {
        return quote.getShopName() + " price is " + Discount.apply(quote.getPrice(), quote.getDiscountCode());
    }

    /**
     * 根据价格和折扣计算折扣后的价格
     * @param price
     * @param code
     * @return
     */
    private static double apply(double price, Code code) {
        try {
            Thread.sleep(1_000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }//模拟Discount服务的响应延迟
        return price * (100 - code.percentage) / 100;
    }

}

class Quote {

    private final String shopName;
    private final double price;
    private final Discount.Code discountCode;

    public Quote(String shopName, double price, Discount.Code discountCode) {
        this.shopName = shopName;
        this.price = price;
        this.discountCode = discountCode;
    }

    public static Quote parse(String s) {
        String[] split = s.split(":");
        String shopName = split[0];
        double price = Double.parseDouble(split[1]);
        Discount.Code discountCode = Discount.Code.valueOf(split[2]);
        return new Quote(shopName, price, discountCode);
    }

    public String getShopName() {
        return shopName;
    }

    public double getPrice() {
        return price;
    }

    public Discount.Code getDiscountCode() {
        return discountCode;
    }
}

class ClientTest {

    List<Shop> discountShops;

    {
        discountShops = Arrays.asList(new Shop("淘宝"),
                new Shop("天猫"),
                new Shop("京东"),
                new Shop("亚马逊"),
                new Shop("拼多多"),
                new Shop("唯品会"));
    }


    /**
     *
     * @param product 商品名称
     * @return 根据名字返回每个商店的商品价格
     */
    public List<String> findPrice(String product) {
        List<String> list = discountShops.stream()
                // 高延迟，价格
                .map(discountShop -> discountShop.getPrice(product))
                .map(Quote::parse)
                //                .collect(toList());
                // 高延迟，折扣
                .map(Discount::applyDiscount)
                .collect(toList());


        return list;
    }

    public List<String> findPrice2(String product, ThreadPoolExecutor threadPoolExecutor) {
        List<CompletableFuture<String>> futures = discountShops.stream()
                .map(discountShop -> CompletableFuture.supplyAsync(() -> discountShop.getPrice(product), threadPoolExecutor))
                .map(future -> future.thenApply(Quote::parse))
                .map(future -> future.thenCompose(quote ->
                        CompletableFuture.supplyAsync(
                                () -> Discount.applyDiscount(quote)
                        )
                ))
                .collect(toList());


        return futures.stream()
                .map(future -> future.join())
                .collect(toList());
    }

}


public class MyTest {
    static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void calculation() {
        if (threadLocal.get() == null) {
            threadLocal.set(System.currentTimeMillis());
        } else {
            System.out.println("Done ：" + (System.currentTimeMillis() - threadLocal.get()));
        }
    }


    public static void mai(String[] args) {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                Runtime.getRuntime().availableProcessors() * 100,
                Runtime.getRuntime().availableProcessors() * 100,
                0, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );

        calculation();
        //        new ClientTest().findPrice("天猫").forEach(System.out::println);
        new ClientTest().findPrice2("天猫", threadPoolExecutor).forEach(System.out::println);
        calculation();

//        new Callable<>()
    }
}