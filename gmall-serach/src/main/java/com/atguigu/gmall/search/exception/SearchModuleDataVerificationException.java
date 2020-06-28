package com.atguigu.gmall.search.exception;

import java.text.ParseException;

/**
 * @author wyl
 * @create 2020-06-24 20:55
 */
public class SearchModuleDataVerificationException extends Exception {
    public SearchModuleDataVerificationException() {
        super();
    }

    public SearchModuleDataVerificationException(String message) {
        super("search module data validation exception ：" + message);
    }

    public SearchModuleDataVerificationException(String message, Throwable cause) {
        super(message, cause);
    }

    public SearchModuleDataVerificationException(Throwable cause) {
        super(cause);
    }

    protected SearchModuleDataVerificationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
