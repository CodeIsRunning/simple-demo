package com.xfliu;


import com.xfliu.dao.mapper.UserMapper;
import com.xfliu.dao.model.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MybatisDemoApplicationTests {

    @Resource
    UserMapper userMapper;

    @Test
    public void contextLoads() {
        userMapper.insert(new User().setId(1).setName("张三"));
    }

}
