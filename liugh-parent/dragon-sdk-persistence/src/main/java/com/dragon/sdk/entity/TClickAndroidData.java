package com.dragon.sdk.entity;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.*;

import java.io.Serializable;
import java.util.Date;

/**
 * @author dingpengfei
 * @date 2019-05-18 17:51
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@TableName("t_click_android_data")
public class TClickAndroidData extends Model<TClickAndroidData> {
  /** 主键 */
  @TableId("id")
  private Long id;

  /** mac */
  @TableField("mac")
  private String mac;

  /** ua */
  @TableField("ua")
  private String ua;

  /** uuid */
  @TableField("uuid")
  private String uuid;
  /** androidid */
  @TableField("androidid")
  private String androidid;
  /** openudid */
  @TableField("openudid")
  private String openudid;


  /** callback_url */
  @TableField("callback_url")
  private String callbackUrl;
  /** adid */
  @TableField("adid")
  private String adid;
  /** cid */
  @TableField("cid")
  private String cid;
  /** imei */
  @TableField("imei")
  private String imei;


  /** ip */
  @TableField("ip")
  private String ip;
  /** 入库时间 */
  @TableField("create_time")
  private Date createTime;

  @TableField("update_time")
  private Date updateTime;
  /** 是否已经回调给头条 */
  @TableField("is_send")
  private Integer isSend;
  /** 是否已经失效 */
  @TableField("delete_flag")
  private Integer deleteFlag;

  @Override
  protected Serializable pkVal() {
    return this.id;
  }

  public TClickAndroidData(
      String mac,
      String ua,
      String uuid,
      String androidid,
      String openudid,
      String callbackUrl,
      String imei,
      String ip,
      String adid,
      String cid) {
    this.mac = mac;
    this.ua = ua;
    this.uuid = uuid;
    this.androidid = androidid;
    this.openudid = openudid;
    this.callbackUrl = callbackUrl;
    this.imei = imei;
    this.ip = ip;
    this.createTime = new Date();
    this.deleteFlag = 0;
    this.isSend = -1;
    this.adid = adid;
    this.cid = cid;
  }
}
