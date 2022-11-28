package com.xfliu.dao.mapper;

import com.xfliu.dao.model.CardTrans;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CardTransMapper {


    List<CardTrans> selectAll(@Param("tableName") String tableName);

    List<CardTrans> selectByCardNo(@Param("tableName") String tableName,@Param("cardNo")String cardNo);
}