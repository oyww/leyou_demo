package com.leyou.item.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.leyou.common.enums.ExceptionEnums;
import com.leyou.common.exception.LyException;
import com.leyou.common.vo.PageResult;
import com.leyou.item.mapper.BrandMapper;
import com.leyou.item.pojo.Brand;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class BrandService {

    @Autowired
    private BrandMapper brandMapper;

    public PageResult<Brand> queryBrandByPageAndSort(
            Integer page, Integer rows, String sortBy, Boolean desc, String key) {
        // 开始分页
        if(rows==-1){
            Long countNum = brandMapper.countNum();
            rows=countNum.intValue();
        }
        PageHelper.startPage(page, rows);
        // 过滤
        Example example = new Example(Brand.class);
        if (StringUtils.isNotBlank(key)) {
            example.createCriteria().andLike("name", "%" + key + "%")
                    .orEqualTo("letter", key);
        }
        if (StringUtils.isNotBlank(sortBy)) {
            // 排序
            String orderByClause = sortBy + (desc ? " DESC" : " ASC");
            example.setOrderByClause(orderByClause);
        }
        // 查询
        Page<Brand> pageInfo = (Page<Brand>) brandMapper.selectByExample(example);
        if(CollectionUtils.isEmpty(pageInfo)){
            throw new LyException(ExceptionEnums.BRAND_NOT_FOND);
        }
        //PageInfo info=new PageInfo(list);//解析分页结果，自动把list转成page
        // 返回结果
        return new PageResult<>(pageInfo.getTotal(), pageInfo);
    }
    @Transactional
    public void saveBrand(Brand brand, List<Long> cids) {
        int i = brandMapper.insert(brand);
        if(i!=1){
            throw new LyException(ExceptionEnums.BRAND_SAVE_FAIL);
        }
        for (Long cid : cids) {
            int i1 = brandMapper.insertCategoryBrand(cid, brand.getId());
            if(i1!=1){
                throw new LyException(ExceptionEnums.BRAND_SAVE_FAIL);
            }

        }
    }
    @Transactional
    public void updateBrand(Brand brand, List<Long> categories) {
        int update = brandMapper.updateByPrimaryKeySelective(brand);
        if(update>0){
            for (Long cid : categories) {
                brandMapper.deleteCategoryBrandById(cid,brand.getId());
                brandMapper.insertCategoryBrand(cid,brand.getId());
            }
        }else {
            throw new LyException(ExceptionEnums.PRICE_CANNOT_BE_NULL);
        }



    }

    public void deleteByBrandIdInCategoryBrand(Long bid) {

    }

    public void deleteBrand(long l) {
        int i = brandMapper.deleteByPrimaryKey(l);
        if (i!=1){

            throw new LyException(ExceptionEnums.BRAND_NOT_FOND);
        }

    }

    public List<Brand> queryBrandByCategoryId(Long cid) {
        List<Brand> brandList = brandMapper.queryBrandListByCategoryId(cid);
        if(null==brandList){
            throw new LyException(ExceptionEnums.BRAND_NOT_FOND);
        }
        return brandList ;
    }

    public List<Brand> queryBrandByBrandIds(List<Long> ids) {
        List<Brand> brandList = brandMapper.selectByIdList(ids);
        if(null==brandList){
            throw new LyException(ExceptionEnums.BRAND_NOT_FOND);
        }
        return brandList ;
    }

    public Brand queryBrandById(Long brandId) {
        Brand brand = brandMapper.selectByPrimaryKey(brandId);
        if(null==brand){
            throw new LyException(ExceptionEnums.BRAND_NOT_FOND);
        }
        return brand;

    }
}
