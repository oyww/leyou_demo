package com.leyou.page.service;

import com.leyou.item.pojo.*;
import com.leyou.page.client.BrandClient;
import com.leyou.page.client.CategoryClient;
import com.leyou.page.client.GoodsClient;
import com.leyou.page.client.SpecificationClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class PageService {
    @Autowired
    private BrandClient brandClient;
    @Autowired
    private CategoryClient categoryClient;
    @Autowired
    private GoodsClient goodsClient;
    @Autowired
    private SpecificationClient specificationClient;
    @Autowired
    private TemplateEngine templateEngine;
    public Map<String, Object> loadModelData(Long spuId) {
        Map<String, Object> model = new HashMap<>();
        //查询spu
        Spu spu = goodsClient.querySpuById(spuId);
        //查询skus
        List<Sku> skus = spu.getSkus();
        //查询详情
        SpuDetail detail = spu.getSpuDetail();
        //查询brand
        Brand brand = brandClient.queryBrandById(spu.getBrandId());
        //查询商品分类
        List<Category> categories = categoryClient.CategoryByIds(Arrays.asList(spu.getCid1(), spu.getCid1(), spu.getCid3()));
        //查询规格参数
        List<SpecGroup> specs = specificationClient.quetyGroupByCid(spu.getCid3());

//        model.put("spu",spu);
        model.put("title",spu.getTitle());
        model.put("subTitle",spu.getSubTitle());
        model.put("skus",skus);
        model.put("detail",detail);
        model.put("brand",brand);
        model.put("categories",categories);
        model.put("specs",specs);
        return model;
    }
    public void createHtml(Long spuId){
        //上下文
        Context context=new Context();
        context.setVariables(loadModelData(spuId));
        //输出流
        File dest = new File("D:\\Users\\OYWW\\IdeaProjects\\leyou\\ly-page\\src\\main\\resources\\upload",spuId+".html");
        if(dest.exists()){
            dest.delete();//存在就删除，说明是修改
        }
        try(PrintWriter writer = new PrintWriter(dest,"utf-8")) {
            //生成HTML
            templateEngine.process("item",context,writer);
        } catch (Exception e) {
            log.error("[静态化页面服务] 静态化页面异常",e);
        }
    }

    public void deleteHtml(Long spuId) {
        File dest = new File("D:\\Users\\OYWW\\IdeaProjects\\leyou\\ly-page\\src\\main\\resources\\upload",spuId+".html");
        if(dest.exists()){
            dest.delete();//避免发生异常
        }
    }
}
