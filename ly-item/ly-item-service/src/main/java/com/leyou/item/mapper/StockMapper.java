package com.leyou.item.mapper;

import com.leyou.common.mapper.MyBaseMapper;
import com.leyou.item.pojo.Stock;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * @Author: 98050
 * Time: 2018-08-17 16:10
 * Feature:
 */
@org.apache.ibatis.annotations.Mapper
public interface StockMapper extends MyBaseMapper<Stock> {
    @Update("UPDATE tb_stock SET stock = stock - #{num} WHERE sku_id = #{id} AND stock >= #{num}")
    int decreaseStock(@Param("id") Long id, @Param("num") Integer num);

}
