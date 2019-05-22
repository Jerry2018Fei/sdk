package com.dragon.sdk.entity;

import com.alibaba.fastjson.serializer.ToStringSerializer;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
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
@TableName("toutiao_ad_data")
public class TouTiaoAdData extends Model<TouTiaoAdData> {
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
  /** os */
  @TableField("os")
  private Integer os;
  /** callback_url */
  @TableField("callback_url")
  private String callbackUrl;

  /** imei */
  @TableField("imei")
  private String imei;

  @TableField("idfa")
  private String idfa;
  /** ip */
  @TableField("ip")
  private String ip;
  /** 入库时间 */
  @TableField("create_time")
  private Date createTime;
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

  public TouTiaoAdData(
      String mac,
      String ua,
      String uuid,
      String androidid,
      String openudid,
      Integer os,
      String callbackUrl,
      String imei,
      String ip,
      String idfa) {
    this.mac = mac;
    this.ua = ua;
    this.uuid = uuid;
    this.androidid = androidid;
    this.openudid = openudid;
    this.os = os;
    this.callbackUrl = callbackUrl;
    this.imei = imei;
    this.ip = ip;
    this.createTime = new Date();
    this.deleteFlag = 0;
    this.isSend = 0;
    this.idfa = idfa;
  }
}
