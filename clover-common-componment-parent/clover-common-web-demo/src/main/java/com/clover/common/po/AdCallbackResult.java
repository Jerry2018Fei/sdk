package com.clover.common.po;

import com.clover.common.util.ComUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author dingpengfei
 * @date 2019-05-19 19:06
 */
@Data@NoArgsConstructor@AllArgsConstructor
public class AdCallbackResult {
    private String msg;
    private Integer code;
    private Integer ret;

  public static void main(String[] args) {

  }
}
