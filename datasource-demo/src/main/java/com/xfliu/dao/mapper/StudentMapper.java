package com.xfliu.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xfliu.dao.model.Student;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface StudentMapper extends BaseMapper<Student> {
}