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
@TableName("game_call_data")
public class GameCallData extends Model<GameCallData> {
  /** 主键 */
  @TableId("id")
  private Long id;

  /** 类型 1 安卓 2 ios */
  @TableField("type")
  private Integer type;

  /** event_type 1 激活 2 登录 3 付费 */
  @TableField("event_type")
  private Integer eventType;

  /** mac */
  @TableField("mac")
  private String mac;

  /** androidid */
  @TableField("androidid")
  private String androidid;

  /** imei */
  @TableField("imei")
  private String imei;

  @TableField("idfa")
  private String idfa;

  /** 入库时间 */
  @TableField("update_time")
  private Date updateTime;

  public GameCallData(Integer type, String idfa, String imei, String mac,String androidid, Integer eventType) {
    this.type=type;
    this.idfa=idfa;
    this.imei=imei;
    this.mac=mac;
    this.androidid=androidid;
    this.eventType=eventType;
    this.updateTime=new Date();
  }

  @Override
  protected Serializable pkVal() {
    return this.id;
  }
}
