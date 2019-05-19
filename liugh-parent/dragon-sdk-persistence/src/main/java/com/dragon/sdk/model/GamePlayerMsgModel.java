package com.dragon.sdk.model;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.dragon.sdk.entity.Admin;
import com.dragon.sdk.util.excel.annotation.ExcelField;
import lombok.*;

import java.io.Serializable;
import java.util.Date;

/**
 * @author dingpengfei
 * @date 2019-05-18 17:51
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString

public class GamePlayerMsgModel  {

  /** imei */
  @ExcelField(value = "imei",title = "IMEI",sort = 3,fieldType = String.class)
  private String imei;
  /** ip */
  @ExcelField(value = "ip",title = "ip",sort = 2,fieldType = String.class)
  private String ip;
  /** 设备码 */
  @ExcelField(value = "deviceId",title = "设备码",sort = 1,fieldType = String.class)
  private String deviceId;
  /** 入库时间 */
  @ExcelField(value = "createTime",title = "注册时间",sort = 0,fieldType = Date.class)

  private Date createTime;

}
