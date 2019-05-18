package com.dragon.sdk.controller;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.dragon.sdk.annotation.AccessLimit;
import com.dragon.sdk.config.ResponseHelper;
import com.dragon.sdk.config.ResponseModel;
import com.dragon.sdk.entity.GamePlayerMsg;
import com.dragon.sdk.service.IGamePlayerMsgService;
import com.dragon.sdk.util.ComUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 前端控制器
 *
 * @author liugh
 * @since 2019-05-09
 */
@RestController
@RequestMapping("/gamePlayerMsg")
public class GamePlayerMsgController {

  @Resource IGamePlayerMsgService gamePlayerMsgService;

  @GetMapping(value = "/pageList")
  //  @RequiresPermissions(value = {"user:list"})
  // 拥有超级管理员或管理员角色的用户可以访问这个接口,换成角色控制权限,改变请看MyRealm.class
  // @RequiresRoles(value = {Constant.RoleType.SYS_ASMIN_ROLE,Constant.RoleType.ADMIN},logical =
  // Logical.OR)
  @AccessLimit(perSecond = 1, timeOut = 500) // 5秒钟生成一个令牌
  public ResponseModel<Page<GamePlayerMsg>> findList(
      @RequestParam(name = "pageIndex", defaultValue = "1", required = false) Integer pageIndex,
      @RequestParam(name = "pageSize", defaultValue = "10", required = false) Integer pageSize,
      @RequestParam(value = "imei", defaultValue = "", required = false) String imei) {
    Wrapper<GamePlayerMsg> wrapper ;
    wrapper =
        ComUtil.isEmpty(imei)
            ? new EntityWrapper<>()
            : new EntityWrapper<GamePlayerMsg>().like("imei", imei);
    return ResponseHelper.buildResponseModel(
        gamePlayerMsgService.selectPage(new Page<>(pageIndex, pageSize), wrapper));
  }
}
