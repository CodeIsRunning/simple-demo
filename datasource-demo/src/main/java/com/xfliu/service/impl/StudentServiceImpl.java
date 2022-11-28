package com.xfliu.service.impl;

import com.xfliu.service.StudentService;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xfliu.dao.model.Student;
import com.xfliu.dao.mapper.StudentMapper;

@Service
public class StudentServiceImpl extends ServiceImpl<StudentMapper, Student> implements StudentService {


}

