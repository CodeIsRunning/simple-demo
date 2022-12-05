package com.xfliu.dao.model;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class User {
    private Integer id;

    private String name;

    private Integer sex;

    private String address;
}