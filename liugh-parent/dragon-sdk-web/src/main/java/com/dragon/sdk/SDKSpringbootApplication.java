package com.dragon.sdk;/*
 * Copyright (C) 2018 royal Inc., All Rights Reserved.
 */

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @Description: 指定项目为springboot，由此类当作程序入口，自动装配 web 依赖的环境
 * @author liugh
 * @since 2019-05-09
 *
 */
@SpringBootApplication(scanBasePackages={"com.dragon.sdk","com.clover.common"})
@MapperScan({"com.dragon.sdk.mapper","com.clover.common.mapper"})
@EnableCaching
@EnableScheduling
@EnableAsync
public class SDKSpringbootApplication {
	public static void main(String[] args) {
        SpringApplication.run(SDKSpringbootApplication.class, args);
    }
}
