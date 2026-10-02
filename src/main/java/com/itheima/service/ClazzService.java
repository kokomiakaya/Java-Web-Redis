package com.itheima.service;

import com.itheima.pojo.Clazz;
import com.itheima.pojo.PageResult;

import java.time.LocalDate;
import java.util.List;

public interface ClazzService {

    /**
     * 条件分页查询
     * @param name
     * @param begin
     * @param end
     * @param page
     * @param pageSize
     * @return
     */
    PageResult page(String name, LocalDate begin, LocalDate end, Integer page, Integer pageSize);


    // 查询所有班级信息
    List<Clazz> findAll();

    // 新增班级
    void save(Clazz clazz);

    // 根据ID查询班级
    Clazz getInfo(Integer id);

    // 修改班级信息
    void update(Clazz clazz);

    // 删除班级
    void deleteById(Integer id);

}
