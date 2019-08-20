package com.dragon.sdk.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.*;

import java.util.Date;

/**
 * thirdparty_game_push_android_data
 *
 * @author dingpengfei 2019-08-16
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@TableName("thirdparty_game_push_android_data")
public class ThirdpartyGamePushAndroidData extends Model<ThirdpartyGamePushAndroidData> {
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * imei
     */
    private String imei;

    /**
     * mac
     */
    private String mac;

    /**
     * androidid
     */
    private String androidid;

    /**
     * 1 激活 2 登录 3 付费
     */
    @TableField("event_type")
    private Integer eventType;

    /**
     * 更新时间
     */
    @TableField("update_time")
    private Date updateTime;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    public ThirdpartyGamePushAndroidData(String imei, String mac, String androidid, Integer eventType) {
        this.imei = imei;
        this.mac = mac;
        this.androidid = androidid;
        this.eventType = eventType;
    }
}
