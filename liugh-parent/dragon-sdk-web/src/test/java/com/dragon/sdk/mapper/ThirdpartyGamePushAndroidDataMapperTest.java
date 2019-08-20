package com.dragon.sdk.mapper;

import com.dragon.sdk.entity.ThirdpartyGamePushAndroidData;
import org.junit.Test;

import javax.annotation.Resource;

import static org.junit.Assert.*;

public class ThirdpartyGamePushAndroidDataMapperTest extends BaseTest {
//    @Resource
    private ThirdpartyGamePushAndroidDataMapper thirdpartyGamePushAndroidDataMapper;

//    @Test
    public void insert() {
        ThirdpartyGamePushAndroidData data=new ThirdpartyGamePushAndroidData("imei3","mac2","androidid2",1);
        thirdpartyGamePushAndroidDataMapper.insert(data);
        System.out.println();

    }
}