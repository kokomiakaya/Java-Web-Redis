package com.itheima.service;

import com.itheima.pojo.JobOption;

import java.util.List;
import java.util.Map;

public interface ReportService {
    // 统计员工职位
    JobOption getEmpJobData();

    // 统计员工性别
    List<Map> getEmpGenderData();
}
