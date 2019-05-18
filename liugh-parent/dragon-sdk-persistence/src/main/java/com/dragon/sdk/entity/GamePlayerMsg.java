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
@TableName("game_player_msg")
public class GamePlayerMsg extends Model<Admin> {
    /**
     * 主键
     */
    @TableId("id")
    private Long id;
    /**
     * imei
     */
    @TableField("imei")
    private String imei;
    /**
     * ip
     */
    @TableField("ip")

    private String ip;
    /**
     * 设备码
     */
    @TableField("device_id")

    private String deviceId;
    /**
     * 入库时间
     */
    @TableField("create_time")

    private Date createTime;
    /**
     * 是否已经匹配过
     */
    @TableField("status")

    private Integer status;
    /**
     * 是否已经删除
     */
    @TableField("delete_flag")

    private Integer deleteFlag;
    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}
