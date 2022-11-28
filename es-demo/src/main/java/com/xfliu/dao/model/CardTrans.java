package com.xfliu.dao.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.Date;


@Data
public class CardTrans {
    private String cardNo;

    /**
     * 唯一id
     */
    private String pid;

    /**
     * 卡状态
     */
    private Integer cardStatus;

    /**
     * 核酸结果时间
     */
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date resultTime;

    /**
     * 入库时间更新时间戳
     */
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date updateTimeTrans;

    /**
     * 拉取时间
     */
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;


}