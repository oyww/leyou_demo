package com.leyou.item.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.dto.CartDto;
import com.leyou.common.enums.ExceptionEnums;
import com.leyou.common.exception.LyException;
import com.leyou.common.vo.PageResult;
import com.leyou.item.mapper.*;
import com.leyou.item.pojo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.*;
import java.util.stream.Collectors;
@Slf4j
@Service
public class GoodsService {
    @Autowired
    private SpuMapper spuMapper;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private BrandService brandService;
    @Autowired
    private SkuMapper skuMapper;
    @Autowired
    private StockMapper stockMapper;
    @Autowired
    private SpuDetailMapper spuDetailMapper;
    @Autowired
    private AmqpTemplate amqpTemplate;
    public PageResult<Spu> querySpuByPage(Integer page, Integer rows, Boolean saleable, String key) {
        //分页
        if(rows==-1){
            rows = spuMapper.countNum().intValue();
        }
        PageHelper.startPage(page,rows);
        //过滤
        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        //搜索字段过滤
        if(StringUtils.isNotBlank(key)){
            criteria.andLike("title","%"+key+"%");
        }
        //上下架过滤
        if(null!=saleable){
            criteria.andEqualTo("saleable",saleable);
        }
        //排序（页面不用，可以设置默认）
        example.setOrderByClause("last_update_time DESC");
        List<Spu> spus = spuMapper.selectByExample(example);
        if(CollectionUtils.isEmpty(spus)){
            throw new LyException(ExceptionEnums.GOODS_NOT_FOND);
        }
        PageInfo<Spu> pageInfo = new PageInfo(spus);
        //解析分类和品牌名称
        loadCategoryAndBrandName(spus);
        // 返回结果
        return new PageResult<Spu>(pageInfo.getTotal(), spus);
    }

    private void loadCategoryAndBrandName(List<Spu> spus) {
        for (Spu spu : spus) {
            //处理分类名称
//            List<String> list = categoryService.queryNameByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3())).stream().map(Category::getName).collect(Collectors.toList());
            List<String> cnames = categoryService.queryCategoryByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3())).stream().map(Category::getName).collect(Collectors.toList());
            spu.setCname(StringUtils.join(cnames,"/"));

            //处理品牌名称
            spu.setBname(brandService.queryBrandById(spu.getBrandId()).getName() );
        }


    }
    @Transactional
    public void saveGoods(Spu spu) {
        //新增spu
        spu.setId(null);
        spu.setCreateTime(new Date());
        spu.setLastUpdateTime(spu.getCreateTime());
        spu.setSaleable(true);
        spu.setValid(false);
        int result = spuMapper.insert(spu);
        if(result!=1){
            log.error("[商品服务] 新增商品失败");
            throw new LyException(ExceptionEnums.GOODS_SAVE_ERROR);
        }
        //新增detail
        SpuDetail spuDetail = spu.getSpuDetail();
        //上面保存spu后就有id
        spuDetail.setSpuId(spu.getId());
        spuDetailMapper.insert(spuDetail);
        //保存sku和stock
        saveSkuAndStock(spu);
        try {//包起来以免发消息的异常影响原业务逻辑
            //发送通知，消息内容商品id
            amqpTemplate.convertAndSend("item.insert",spu.getCid1());
        } catch (AmqpException e) {
            e.printStackTrace();
        }

    }

    private void saveSkuAndStock(Spu spu) {
        int result;List<Stock> stocks=new ArrayList<>();

        //新增sku
        List<Sku> skus = spu.getSkus();
        for (Sku sku : skus) {
            sku.setCreateTime(new Date());
            sku.setLastUpdateTime(sku.getCreateTime());
            sku.setSpuId(spu.getId());

            result = skuMapper.insert(sku);
            if(result!=1){
                throw new LyException(ExceptionEnums.GOODS_SAVE_ERROR);
            }
            //新增库存
            Stock stock = new Stock();
            stock.setSkuId(sku.getId());
            stock.setStock(sku.getStock());
            /*result=stockMapper.insert(stock);
            if(result!=1){
                throw new LyException(ExceptionEnums.GOODS_SAVE_ERROR);
            }*/
            stocks.add(stock);
        }
        stockMapper.insertList(stocks);
    }

    public SpuDetail queryDetailById(Long spuId) {

        SpuDetail spuDetail = spuDetailMapper.selectByPrimaryKey(spuId);
        if(null==spuDetail){
            throw new LyException(ExceptionEnums.GOODS_DETAIL_NOT_FOND);
        }
        return spuDetail;
    }

    public List<Sku> querySkuListBySpuId(Long spuId) {
        Sku sku=new Sku();
        sku.setSpuId(spuId);
        List<Sku> skuList = skuMapper.select(sku);
        if(CollectionUtils.isEmpty(skuList)){
            throw new LyException(ExceptionEnums.GOODS_SKU_NOT_FOND);
        }
        //查询库存
        /*for (Sku s : skuList) {
            Stock stock = stockMapper.selectByPrimaryKey(s.getId());
            if(stock==null){
                throw new LyException(ExceptionEnums.GOODS_STOCK_NOT_FOND);
            }
            s.setStock(stock.getStock());
        }*/
        //查询库存 JDK8流stream操作
        //获得skuId集合
        List<Long> skuIds = skuList.stream().map(Sku::getId).collect(Collectors.toList());
        //通过id批量查询stock
        List<Stock> stockList = stockMapper.selectByIdList(skuIds);
        //使用批量stock的id和库存打包map
        Map<Long, Long> idStockMap = stockList.stream().collect(Collectors.toMap(Stock::getSkuId, Stock::getStock));
        //通过get（id值等于键值）方式给sku的stock批量赋值
        skuList.forEach(s ->s.setStock(idStockMap.get(s.getId())));
        skuList.forEach(s ->s.getCreateTime());
        return skuList;
    }

    @Transactional
    public void updateGoods(Spu spu) {
        if(spu.getId()==null){
            throw new LyException(ExceptionEnums.GOODS_ID_BE_NULL);
        }
        Sku sku = new Sku();
        sku.setSpuId(spu.getId());
        //查询sku
        List<Sku> skuList = skuMapper.select(sku);
        if(!CollectionUtils.isEmpty(skuList)){
            //删除sku
            skuMapper.delete(sku);
            //删除stock
            List<Long> ids = skuList.stream().map(Sku::getId).collect(Collectors.toList());
            stockMapper.deleteByIdList(ids);
        }
        //修改spu
        spu.setValid(null);
        spu.setSaleable(null);
        spu.setCreateTime(null);
        spu.setLastUpdateTime(new Date());
        //根据非空字段更新数据库
        int result = spuMapper.updateByPrimaryKeySelective(spu);
        if(1!=result){
            throw new LyException(ExceptionEnums.GOODS_UPDATE_ERROR);
        }
        //修改detail
        result = spuDetailMapper.updateByPrimaryKeySelective(spu.getSpuDetail());
        if(1!=result){
            throw new LyException(ExceptionEnums.GOODS_UPDATE_ERROR);
        }
        //更新sku和stock
        saveSkuAndStock(spu);
        try {//包起来以免发消息的异常影响原业务逻辑
            //发送通知，消息内容商品id
            amqpTemplate.convertAndSend("item.update",spu.getId());
        } catch (AmqpException e) {
            e.printStackTrace();
        }

    }

    public Spu querySpuById(Long id) {
        //查询spu
        Spu spu = spuMapper.selectByPrimaryKey(id);
        if(spu==null){
            throw new LyException(ExceptionEnums.GOODS_NOT_FOND);
        }
        //查询spu下的sku list
        spu.setSkus(querySkuListBySpuId(id));
        //查询detail
        spu.setSpuDetail(queryDetailById(id));
        return spu;
    }

    public Sku querySkuById(Long id) {
        return skuMapper.selectByPrimaryKey(id);
    }

    public List<Sku> querySkuListByIds(String ids) {
        List<Long> idList = null;
        try {
            idList = Arrays.asList(ids.split(",")).stream().map(s -> Long.parseLong(s)).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("[商品服务] 购物车中的商品根据id查询sku参数有误：{}",ids);
            throw new LyException(ExceptionEnums.BAD_REQUEST_PARAMS);
        }
        List<Sku> skuList = null;
        try {
            skuList = skuMapper.selectByIdList(idList);
        } catch (Exception e) {
            log.error("[商品服务] 购物车中的商品根据id查询sku不存在");
            throw new LyException(ExceptionEnums.GOODS_SKU_NOT_FOND);
        }
        return skuList;
    }

    public List<Sku> querySkuBySpuIds(List<Long> ids) {
        return null;
    }

    @Transactional
    public void decreaseStock(List<CartDto> cartDTOS) {
        // 不能用if判断来实现减库存，当线程很多的时候，有可能引发超卖问题
        // 加锁也不可以  性能太差，只有一个线程可以执行，当搭了集群时synchronized只锁住了当前一个tomcat
        for (CartDto cartDTO : cartDTOS) {
            int count = stockMapper.decreaseStock(cartDTO.getSkuId(), cartDTO.getNum());
            if(count != 1){
                log.error("[商品服务] 库存不足，无法执行减库存操作");
                throw new LyException(ExceptionEnums.STOCK_NOT_ENOUGH);
            }
        }
    }
}

