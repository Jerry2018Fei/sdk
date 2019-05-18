package com.dragon.sdk.entity;

import com.baomidou.mybatisplus.activerecord.Model;
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
    private String imei;
    /**
     * ip
     */
    private String ip;
    /**
     * 入库时间
     */
    private Date createTime;
    /**
     * 是否已经失效
     */
    private Integer deleteFlag;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}
