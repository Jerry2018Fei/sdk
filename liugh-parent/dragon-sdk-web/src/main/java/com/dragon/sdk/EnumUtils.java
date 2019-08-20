package com.dragon.sdk;

import com.dragon.sdk.enums.ClientEventTypeEnum;
import com.dragon.sdk.enums.ClientTypeEnum;

/**
 * @author dingpengfei
 * @date 2019-07-07 21:57
 */
public class EnumUtils {
  public static ClientTypeEnum getClientTypeEnumByValue(Integer value) {
    switch (value) {
      case 1:
        return ClientTypeEnum.ANDROID;
      case 2:
        return ClientTypeEnum.IOS;
      default:
        return null;
    }
  }

  public static ClientEventTypeEnum getClientEventTypeEnumByValue(Integer value) {
    switch (value) {
      case 1:
        return ClientEventTypeEnum.ACTIVE;
      case 2:
        return ClientEventTypeEnum.LOGIN;
      case 3:
        return ClientEventTypeEnum.PAY;
      default:
        return null;
    }
  }
}
