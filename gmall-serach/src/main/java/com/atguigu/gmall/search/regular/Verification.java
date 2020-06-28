package com.atguigu.gmall.search.regular;

import com.atguigu.gmall.search.constants.RegularLiterals;

/**
 * @author wyl
 * @create 2020-06-24 20:22
 */
public class Verification {
    public static boolean isNoneZeroNumber(String verificationStr) {
        return verificationStr.matches(RegularLiterals.NoneZeroNumber);
    }

}
