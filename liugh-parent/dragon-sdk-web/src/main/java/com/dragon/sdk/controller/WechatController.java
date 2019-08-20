package com.dragon.sdk.controller;

import aes.AesException;
import aes.WXBizMsgCrypt;
import com.clover.common.annotation.Pass;
import com.clover.common.controller.BaseController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 前端控制器
 *
 * @author liugh
 * @since 2019-05-09
 */
@RestController
@RequestMapping("/wx")
public class WechatController extends BaseController {
  private static final Logger logger = LoggerFactory.getLogger(WechatController.class);
  private WXBizMsgCrypt wxcpt = null;
  private static final String appId = "wx3c9f8397d921793c";
  private static final String token = "hello123321hello";
  private static final String aesKey = "ElkKszOBbef1UfDf0Ezqn8O4aGV18Aj9nzeWK7qcRhV";

  @RequestMapping(value = "/demo",method = RequestMethod.GET)
  @Pass
  public String accept(String signature, String timestamp, String nonce, String echostr) {
    try {
      wxcpt = new WXBizMsgCrypt(token, aesKey, appId);
    } catch (AesException e) {
      e.printStackTrace();
      logger.error(e.getMessage());
    }
    return "";
  }
}
