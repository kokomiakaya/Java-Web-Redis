package com.itheima.mapper;

import com.itheima.pojo.Student;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Mapper
public interface StudentMapper {

    // 根据ID统计班级中的学员人数
    @Select("select count(*) from student where clazz_id = #{id}")
    Integer countByClazzId(Integer id);

    // 条件分页查询
    List<Student> list(String name, Integer degree, Integer clazzId);

    // 根据ID查询学员信息
    @Select("select * from student where id = #{id}")
    Student getById(Integer id);

    @Insert("insert into student(name, no, gender, phone,id_card, is_college, address, degree, graduation_date,clazz_id, create_time, update_time) VALUES " +
            "(#{name},#{no},#{gender},#{phone},#{idCard},#{isCollege},#{address},#{degree},#{graduationDate},#{clazzId},#{createTime},#{updateTime})")
    void insert(Student student);

    // 修改学员信息
    void update(Student student);

    void delete(List<Integer> ids);

    // 违纪处理
    @Update("update student set violation_count = violation_count + 1 , violation_score = violation_score + #{score} , " +
            "update_time = now() where id = #{id}")
    void updateViolation(Integer id, Integer score);

    List<Map> countStudentDegreeData();

    List<Map<String, Object>> getStudentCount();
}
