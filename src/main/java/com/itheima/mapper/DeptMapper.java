package com.itheima.mapper;

import com.itheima.pojo.Dept;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface  DeptMapper {

    @Select("select id, name, create_time, update_time from dept")
    public List<Dept> findAll();

    @Delete("delete from dept where id = #{deptId}")
    public void deleteById(Integer deptId);

    @Insert("insert into dept(name, create_time, update_time) values(#{name}, #{createTime}, #{updateTime})")
    public void save(Dept dept);

    @Select("select id, name, create_time, update_time from dept where id = #{deptId}")
    public Dept getInfo(Integer deptId);

    @Update("update dept set name = #{name}, update_time = #{updateTime} where id = #{id}")
    public void update(Dept dept);
}
