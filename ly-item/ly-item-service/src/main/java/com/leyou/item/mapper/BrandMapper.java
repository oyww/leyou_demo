package com.leyou.item.mapper;

import com.leyou.common.mapper.MyBaseMapper;
import com.leyou.item.pojo.Brand;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface BrandMapper extends MyBaseMapper<Brand> {
    /**
     * 新增商品分类和品牌中间表数据
     * @param cid 商品分类id
     * @param bid 品牌id
     * @return
     */
    @Insert("INSERT INTO tb_category_brand (category_id, brand_id) VALUES (#{cid},#{bid})")
    int insertCategoryBrand(@Param("cid") Long cid, @Param("bid") Long bid);

    @Select("SELECT COUNT(id) FROM tb_brand")
    public Long countNum();

    @Delete("DELETE FROM tb_category_brand WHERE category_id = #{cid} AND brand_id=#{bid}")
    public int deleteCategoryBrandById(@Param("cid") Long cid, @Param("bid") Long bid);
    /*删除cb表不存在的brand
     DELETE FROM tb_category_brand WHERE tb_category_brand.brand_id IN (
SELECT a.brand_id FROM (SELECT brand_id FROM tb_category_brand WHERE brand_id NOT IN (SELECT id FROM tb_brand)) a )
*/
    /**
     *
     * SELECT * FROM tb_brand WHERE id IN (SELECT brand_id FROM tb_category_brand WHERE category_id=#{cid})

     SELECT b.* FROM tb_brand as b INNER JOIN tb_category_brand as cb ON b.id=cb.brand_id WHERE cb.category_id=76
     */
    @Select("SELECT b.* FROM tb_brand as b INNER JOIN tb_category_brand as cb ON b.id=cb.brand_id WHERE cb.category_id=#{cid}")
    //@Select("SELECT b.* FROM tb_brand b LEFT JOIN tb_category_brand cb ON b.id = cb.brand_id WHERE cb.category_id = #{cid}")
    public List<Brand> queryBrandListByCategoryId(@Param("cid") Long cid);

}
