package com.itheima.mapper;

import com.itheima.pojo.EmpExpr;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper // 表示这是一个Mapper接口
public interface EmpExprMapper {

    public void insertBatch(List<EmpExpr> exprList);

    void deleteByEmpIds(List<Integer> ids);
}
