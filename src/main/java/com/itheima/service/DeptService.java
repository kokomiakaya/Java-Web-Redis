package com.itheima.service;


import com.itheima.pojo.Dept;

import java.util.List;

public interface DeptService {

    // 查询所有部门信息
    List<Dept> findAll();


    // 删除部门
    void deleteById(Integer deptId);

    // 增加部门
    void save(Dept dept);

    Dept getInfo(Integer deptId);

    void update(Dept dept);
}
