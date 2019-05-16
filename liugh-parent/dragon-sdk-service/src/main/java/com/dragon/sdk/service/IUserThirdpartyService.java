package com.dragon.sdk.service;

import com.dragon.sdk.entity.Admin;
import com.dragon.sdk.entity.UserThirdparty;
import com.baomidou.mybatisplus.service.IService;
import com.dragon.sdk.model.ThirdPartyUser;

/**
 * <p>
 * 第三方用户表 服务类
 * </p>
 *
 * @author dingpengfei
 * @since 2019-05-09
 */
public interface IUserThirdpartyService extends IService<UserThirdparty> {

    Admin insertThirdPartyUser(ThirdPartyUser param, String password)throws Exception;

}
