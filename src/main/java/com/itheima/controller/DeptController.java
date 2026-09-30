package com.itheima.controller;

import com.itheima.pojo.Dept;
import com.itheima.pojo.Result;
import com.itheima.service.DeptService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.print.DocFlavor;
import java.util.List;

/*
 部门管理控制器
 */

@Slf4j
@RequestMapping("/depts")
@RestController
public class DeptController {
    @Autowired
    private DeptService deptService;

//    @RequestMapping(value = "/depts",method = RequestMethod.GET)
//    public Result findAll(){
//        List<Dept> deptList = deptService.findAll();
//        return Result.success(deptList);
//    }

    // 查询所有部门
    @GetMapping
    public Result findAll(){
        log.info("查询部门列表");
        List<Dept> deptList = deptService.findAll();
        return Result.success(deptList);
    }


    // /depts?id=1
    // 删除部门
    @DeleteMapping
    public Result delete(@RequestParam("id") Integer deptId){
        log.info("根据id删除部门,id:{} ",deptId);
        deptService.deleteById(deptId);
        return Result.success();
    }


    // 新增部门
    @PostMapping
    public Result save(@RequestBody Dept dept){
        log.info("新增部门,dept:{} ",dept);
        deptService.save(dept);
        return Result.success();
    }

    // /depts/id
    @GetMapping("/{id}")
    public Result getInfo(@PathVariable("id") Integer deptId){
        log.info("根据ID查询,id:{} ",deptId);
        Dept dept =  deptService.getInfo(deptId);
        return Result.success(dept);
    }

    @PutMapping
    public Result update(@RequestBody Dept dept){
        log.info("修改部门,dept: {}",dept);
        deptService.update(dept);
        return Result.success();
    }
}
