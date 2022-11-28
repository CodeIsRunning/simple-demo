package com.xfliu.dao.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@TableName(value = "student")
public class Student {
    @TableId(value = "id", type = IdType.INPUT)
    private Integer id;


    @NotBlank(message = "测试")
    @TableId(value = "name", type = IdType.INPUT)
    private String name;

    @TableField(value = "sex")
    private Integer sex;

    public static final String COL_ID = "id";

    public static final String COL_NAME = "name";

    public static final String COL_SEX = "sex";
}