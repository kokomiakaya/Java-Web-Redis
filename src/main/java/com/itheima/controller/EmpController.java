package com.itheima.controller;

import com.itheima.pojo.Emp;
import com.itheima.pojo.EmpQueryParam;
import com.itheima.pojo.PageResult;
import com.itheima.pojo.Result;
import com.itheima.service.EmpService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RequestMapping("/emps")
@RestController
public class EmpController {
    @Autowired
    private EmpService empService;

//    @GetMapping
//    public Result page(@RequestParam(name = "page",defaultValue = "1")Integer page,
//                       @RequestParam(defaultValue = "10") Integer pageSize){
//        log.info("查询员工信息,page = {},pageSize = {}",page,pageSize);
//        PageResult pageResult = empService.page(page,pageSize);
//        return Result.success(pageResult);
//    }

//    @GetMapping
//    public Result page(@RequestParam(name = "page",defaultValue = "1")Integer page,
//                       @RequestParam(defaultValue = "10") Integer pageSize,
//                       String name, Integer gender,
//                       @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
//                       @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end){
//        log.info("查询请求参数：{},{},{},{},{}",page,pageSize,name,gender,begin,end);
//
//        PageResult pageResult = empService.page(page,pageSize,name,gender,begin,end);
//        return Result.success(pageResult);
//
//    }

    @GetMapping
    public Result page(EmpQueryParam empQueryParam){
        log.info("查询请求参数：{}",empQueryParam);

        PageResult pageResult = empService.page(empQueryParam);
        return Result.success(pageResult);

    }

    // 保存员工
    @PostMapping
    public Result save(@RequestBody Emp emp){
        log.info("请求参数emp:{}",emp);
        empService.save(emp);
        return Result.success();
    }

    // 删除员工
    @DeleteMapping
    public Result delete(@RequestParam List<Integer> ids){
        log.info("批量删除部门:ids = {}",ids);
        empService.deleteByIds(ids);
        return Result.success();
    }

}
