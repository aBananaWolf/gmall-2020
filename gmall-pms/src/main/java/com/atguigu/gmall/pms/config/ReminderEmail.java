package com.atguigu.gmall.pms.config;

import io.seata.tm.api.FailureHandler;
import io.seata.tm.api.GlobalTransaction;
import org.springframework.stereotype.Component;

/**
 * @author wyl
 * @create 2020-06-17 14:25
 */
@Component
public class ReminderEmail implements FailureHandler {
    @Override
    public void onBeginFailure(GlobalTransaction tx, Throwable cause) {

    }

    @Override
    public void onCommitFailure(GlobalTransaction tx, Throwable cause) {

    }

    @Override
    public void onRollbackFailure(GlobalTransaction tx, Throwable cause) {

    }


    public void onRollbackRetrying(GlobalTransaction globalTransaction, Throwable throwable) {

    }
}
