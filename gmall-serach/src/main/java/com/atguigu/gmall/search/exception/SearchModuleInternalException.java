package com.atguigu.gmall.search.exception;

/**
 * @author wyl
 * @create 2020-06-25 10:33
 */
public class SearchModuleInternalException extends Exception{
    public SearchModuleInternalException() {
        super();
    }

    public SearchModuleInternalException(String message) {
        super("an exception occurred inside the search module " + message);
    }

    public SearchModuleInternalException(String message, Throwable cause) {
        super(message, cause);
    }

    public SearchModuleInternalException(Throwable cause) {
        super(cause);
    }

    protected SearchModuleInternalException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
