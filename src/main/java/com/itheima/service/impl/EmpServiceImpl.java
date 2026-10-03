package com.itheima.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.mapper.EmpExprMapper;
import com.itheima.mapper.EmpLogMapper;
import com.itheima.mapper.EmpMapper;
import com.itheima.pojo.*;
import com.itheima.service.EmpLogService;
import com.itheima.service.EmpService;
import com.itheima.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

// 员工管理

@Service
public class EmpServiceImpl implements EmpService {

    @Autowired
    private EmpMapper empMapper;
    @Autowired
    private EmpExprMapper empExprMapper;
    @Autowired
    private EmpLogMapper empLogMapper;
    @Autowired
    private EmpLogService empLogService;

//    @Override
//    public PageResult page(Integer page, Integer pageSize) {
//     //1. 设置分页参数
//    PageHelper.startPage(page,pageSize); // 告诉 PageHelper：紧接着执行的 SQL 查询需要进行分页
//
//     //2. 执行查询
//    List<Emp> empList = empMapper.list(); // 执行查询
//    Page<Emp> p = (Page<Emp>) empList; // 获取分页查询结果
//
//    //3. 封装结果
//    return new PageResult(p.getTotal(), p.getResult());
//
//    }

// @Override
// public PageResult page(Integer page, Integer pageSize, String name, Integer gender, LocalDate begin, LocalDate end) {
//  //1. 设置分页参数
//  PageHelper.startPage(page,pageSize); // 告诉 PageHelper：紧接着执行的 SQL 查询需要进行分页
//
//  List<Emp> empList =  empMapper.list(name,gender,begin,end);
//  Page<Emp> p = (Page<Emp>) empList; // 获取分页查询结果
//  return new PageResult(p.getTotal(),p.getResult());
// }

    @Override
    public PageResult page(EmpQueryParam empQueryParam) {
        // 1. 设置分页参数
        PageHelper.startPage(empQueryParam.getPage(), empQueryParam.getPageSize());

        // 2.执行查询
        List<Emp> empList = empMapper.list(empQueryParam);

        // 3.封装分页结果
        Page<Emp> p = (Page<Emp>) empList;
        return new PageResult(p.getTotal(), p.getResult());
    }

//    @Override
//    public PageResult page(Integer page, Integer pageSize) {
//        // 1.查询记录的总数
//        Long total = empMapper.count();
//
//        // 2. 获取起始页和每页要查询的记录
//        Integer start = (page - 1) * pageSize;
//
//        // 3.查询
//        List<Emp> empList = empMapper.list(start,pageSize);
//        return new PageResult(total,empList);
//    }

    // 默认回滚所有异常，包括运行时异常和检查型异常
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void save(Emp emp) {
        try {
            // 1.补全基础属性
            emp.setCreateTime(LocalDateTime.now());
            emp.setUpdateTime(LocalDateTime.now());

            // 2.保存员工基本信息
            empMapper.insert(emp);

            // 人为引入bug，介绍事务
           /* int i = 1/0;*/

            // 3.保存员工的工作经历信息 - 批量保存
            Integer empId = emp.getId();
            // 获取工作经历
            List<EmpExpr> exprList = emp.getExprList();
            if (!CollectionUtils.isEmpty(exprList)) {
                exprList.forEach(empExpr -> empExpr.setEmpId(empId));
                empExprMapper.insertBatch(exprList);
            }
        }finally {

            EmpLog empLog = new EmpLog(null, LocalDateTime.now(), emp.toString());
            empLogService.insertLog(empLog);

        }
    }

    @Transactional
    @Override
    public void deleteByIds(List<Integer> ids) {
        // 1.根据ID批量删除员工基本信息
        empMapper.deleteByIds(ids);

        // 2.根据ID批量删除员工的工作经历
        empExprMapper.deleteByEmpIds(ids);
    }

    @Override
    public Emp getInfo(Integer id) {
        return empMapper.getById(id);

    }

    @Override
    public void update(Emp emp) {
       // 1.根据员工ID更新基本信息
        emp.setUpdateTime(LocalDateTime.now());
        empMapper.updateById(emp);

        // 2.根据员工ID删除工作经历
        empExprMapper.deleteByEmpIds(Arrays.asList(emp.getId()));

        // 3.新增员工的工作经历数据
        Integer empId = emp.getId();
        List<EmpExpr> exprList = emp.getExprList();
        if (!CollectionUtils.isEmpty(exprList)) {
            exprList.forEach(empExpr -> empExpr.setEmpId(empId));
            empExprMapper.insertBatch(exprList);
        }
    }

    // 登录校验功能
//    @Override
//    public LoginInfo login(Emp emp) {
//        Emp empLogin = empMapper.getUsernameAndPassword(emp);
//        if(empLogin != null){
//            LoginInfo loginInfo = new LoginInfo(empLogin.getId(), empLogin.getUsername(), empLogin.getName(), null);
//            return loginInfo;
//        }
//        return null;
//    }

    @Override
    public LoginInfo login(Emp emp) {
        Emp empLogin = empMapper.getUsernameAndPassword(emp);
        if(empLogin != null){
            // 1.生成JWT令牌
            Map<String,Object> dataMap = new HashMap<>();
            dataMap.put("id",empLogin.getId());
            dataMap.put("username",empLogin.getUsername());

            String jwt = JwtUtils.generateJwt(dataMap);
            LoginInfo loginInfo = new LoginInfo(empLogin.getId(), empLogin.getUsername(), empLogin.getName(), jwt);
            return loginInfo;
        }
        return null;
    }

}
