package com.dragon.sdk.enums;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author dingpengfei
 * @date 2019-07-07 21:36
 */
@NoArgsConstructor@AllArgsConstructor
public enum ClientEventTypeEnum {
    /**
     * 激活
     */
    ACTIVE("active",1),
    /**
     * 登录
     */
    LOGIN("login",2),
    /**
     * 付费
     */
    PAY("pay",3)
    ;
    private String name;
    private Integer value;
}
