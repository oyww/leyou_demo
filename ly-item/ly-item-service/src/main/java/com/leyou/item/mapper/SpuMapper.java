package com.leyou.item.mapper;

import com.leyou.item.pojo.Spu;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

/**
 * @Author: 98050
 * Time: 2018-08-14 22:14
 * Feature:
 */
@org.apache.ibatis.annotations.Mapper
public interface SpuMapper extends Mapper<Spu> {
    @Select("SELECT COUNT(id) FROM tb_spu")
    public Long countNum();
}

