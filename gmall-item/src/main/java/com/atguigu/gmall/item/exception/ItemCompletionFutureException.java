package com.atguigu.gmall.item.exception;

/**
 * @author wyl
 * @create 2020-06-29 19:35
 */
public class ItemCompletionFutureException extends RuntimeException {
    public ItemCompletionFutureException() {
        super();
    }

    public ItemCompletionFutureException(String message) {
        super("this could be a malicious attack , ItemModule completionFutureException：" + message);
    }

    public ItemCompletionFutureException(String message, Throwable cause) {
        super(message, cause);
    }

    public ItemCompletionFutureException(Throwable cause) {
        super(cause);
    }

    protected ItemCompletionFutureException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
