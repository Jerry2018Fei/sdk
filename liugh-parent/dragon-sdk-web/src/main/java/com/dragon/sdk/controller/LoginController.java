package com.dragon.sdk.controller;

import com.alibaba.fastjson.JSONObject;
import com.dragon.sdk.annotation.Log;
import com.dragon.sdk.annotation.Pass;
import com.dragon.sdk.annotation.ValidationParam;
import com.dragon.sdk.config.ResponseHelper;
import com.dragon.sdk.config.ResponseModel;
import com.dragon.sdk.entity.Admin;
import com.dragon.sdk.service.IAdminService;
import com.dragon.sdk.util.ComUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import java.util.Map;

/**
 *  登录接口
 * @author liugh
 * @since 2019-05-09
 */
@RestController
@Api(value="身份认证模块")
public class LoginController {
    @Resource
    private IAdminService adminService;


    @ApiOperation(value="手机密码登录", notes="body体参数,不需要Authorization",produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "requestJson", value = "{\"mobile\":\"13888888888\",\"password\":\"123456\"}"
                    , required = true, dataType = "String",paramType="body")
    })
    @PostMapping("/login")
    @Log(action="SignIn",modelName= "Login",description="前台密码登录接口")
    @Pass
    public ResponseModel<Map<String, Object>> login(
            @ValidationParam("mobile,password")@RequestBody JSONObject requestJson) throws Exception{
        return ResponseHelper.buildResponseModel(adminService.checkMobileAndPasswd(requestJson));
    }

    @ApiOperation(value="短信验证码登录", notes="body体参数,不需要Authorization",produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "requestJson", value = "{\"mobile\":\"13888888888\",\"captcha\":\"5780\"}"
                    , required = true, dataType = "String",paramType="body")
    })
    @PostMapping("/login/captcha")
    @Log(action="SignInByCaptcha",modelName= "Login",description="前台短信验证码登录接口")
    @Pass
    public ResponseModel<Map<String, Object>> loginBycaptcha(
            @ValidationParam("mobile,captcha")@RequestBody JSONObject requestJson) throws Exception{
        return ResponseHelper.buildResponseModel( adminService.checkMobileAndCatcha(requestJson));
    }



    @ApiOperation(value="手机验证码注册", notes="body体参数,不需要Authorization",produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "requestJson", value = "{\"username\":\"liugh\",\"mobile\":\"13888888888\",</br>" +
                    "\"captcha\":\"5780\",\"password\":\"123456\",</br>\"rePassword\":\"123456\",\"job\":\"java开发\"," +
                    "</br>\"unit(可不传)\":\"xxx公司\"}"
                    , required = true, dataType = "String",paramType="body")
    })
    @PostMapping("/register")
    @Log(action="register",modelName= "Login",description="注册接口")
    @Pass
    public ResponseModel<Admin> register(@ValidationParam("username,password,rePassword,mobile,captcha,job")
                                       @RequestBody JSONObject requestJson)throws Exception {
        return ResponseHelper.buildResponseModel( adminService.checkAndRegisterUser(requestJson));
    }


    @ApiOperation(value="忘记密码", notes="body体参数,不需要Authorization",produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "requestJson", value = "{\"mobile\":\"17765071662\",\"captcha\":\"5780\",</br>" +
                    "\"password\":\"123456\",\"rePassword\":\"123456\"}"
                    , required = true, dataType = "String",paramType="body")
    })
    @PostMapping("/forget/password")
    @Pass
    public ResponseModel<Admin> resetPassWord (@ValidationParam("mobile,captcha,password,rePassword")
                                               @RequestBody JSONObject requestJson ) throws Exception{
        return ResponseHelper.buildResponseModel(adminService.updateForgetPasswd(requestJson));
    }

    /**
     * 检查用户是否注册过
     * @param mobile
     * @return
     * @throws Exception
     */
    @GetMapping("/check/mobile")
    @Pass
    @ApiIgnore
    public ResponseModel loginBycaptcha(@RequestParam("mobile") String mobile) throws Exception{
        Admin admin = adminService.getUserByMobile(mobile);
        return ResponseHelper.buildResponseModel(!ComUtil.isEmpty(admin));
    }
}
