package com.itheima.mapper;

import com.itheima.pojo.Clazz;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface ClazzMapper {
    
    //     动态条件查询
    List<Clazz> list(String name, LocalDate begin, LocalDate end);

    // 查询所有班级信息
    @Select("select * from clazz")
    List<Clazz> findAll();

    @Insert("insert into Clazz values (null,#{name},#{room},#{beginDate}," +
            "#{endDate},#{masterId}, #{subject},#{createTime},#{updateTime})")
    void insert(Clazz clazz);

    @Select("select * from clazz where id = #{id}")
    Clazz getInfo(Integer id);

    // 更新班级信息
    void update(Clazz clazz);

    // 删除班级
    @Delete("delete from clazz where id = #{id}")
    void deleteById(Integer id);
}
