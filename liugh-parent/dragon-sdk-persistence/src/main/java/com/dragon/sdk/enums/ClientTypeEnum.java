package com.dragon.sdk.enums;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author dingpengfei
 * @date 2019-07-07 21:36
 */
@NoArgsConstructor@AllArgsConstructor
public enum ClientTypeEnum {
    /**
     * android
     */
    ANDROID("android",1),
    /**
     * ios
     */
    IOS("ios",2)
    ;
    private String name;
    private Integer value;




}

