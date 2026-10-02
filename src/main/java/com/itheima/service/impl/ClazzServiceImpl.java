package com.itheima.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.exception.BusinessException;
import com.itheima.mapper.ClazzMapper;
import com.itheima.mapper.DeptMapper;
import com.itheima.mapper.EmpMapper;
import com.itheima.mapper.StudentMapper;
import com.itheima.pojo.Clazz;
import com.itheima.pojo.PageResult;
import com.itheima.pojo.Result;
import com.itheima.service.ClazzService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ClazzServiceImpl implements ClazzService {

    @Autowired
    private ClazzMapper clazzMapper;

    @Autowired
    private StudentMapper studentMapper;

    // 条件分页查询
    @Override
    public PageResult page(String name, LocalDate begin, LocalDate end, Integer page, Integer pageSize) {
        PageHelper.startPage(page,pageSize);
        List<Clazz> dataList = clazzMapper.list(name,begin,end);
        Page<Clazz> p = (Page<Clazz>)dataList;
        return new PageResult(p.getTotal(),p.getResult());
    }

    // 查询所有班级信息
    @Override
    public List<Clazz> findAll() {
        return  clazzMapper.findAll();
    }

    // 新增班级
    @Override
    public void save(Clazz clazz) {
        // 1.设置操作和更新时间
        clazz.setCreateTime(LocalDateTime.now());
        clazz.setUpdateTime(LocalDateTime.now());
        // 2.插入数据
        clazzMapper.insert(clazz);
    }

    @Override
    public Clazz getInfo(Integer id) {
        return clazzMapper.getInfo(id);
    }

    @Override
    public void update(Clazz clazz) {
        // 1.设置操作时间
        clazz.setUpdateTime(LocalDateTime.now());

        // 2.执行操作
        clazzMapper.update(clazz);
    }

    @Override
    public void deleteById(Integer id) {
        // 1.查询该班级下是否有学员
        Integer count = studentMapper.countByClazzId(id);
        if (count > 0){
            throw new BusinessException("班级下有学员，不能直接删除~");
        }else {
            // 2.如果没有学员，直接删除
            clazzMapper.deleteById(id);
        }
    }


}
