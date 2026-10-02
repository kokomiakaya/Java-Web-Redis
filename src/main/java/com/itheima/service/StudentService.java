package com.itheima.service;

import com.itheima.pojo.ClazzCountOption;
import com.itheima.pojo.PageResult;
import com.itheima.pojo.Student;

import java.util.List;
import java.util.Map;

public interface StudentService {

    // 条件查询
    PageResult page(String name, Integer degree, Integer clazzId, Integer page, Integer pageSize);

    // 根据ID查询信息
    Student getInfo(Integer id);

    // 新增学员
    void save(Student student);

    // 修改学员信息
    void update(Student student);

    // 支持批量删除学员
    void delete(List<Integer> ids);

    // 违纪处理
    void violationHandle(Integer id, Integer score);


}
