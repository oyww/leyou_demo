package com.leyou.search.service;

import com.leyou.search.pojo.Goods;
import com.leyou.search.repository.GoodsRepository;
import org.elasticsearch.index.query.QueryBuilders;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;
@RunWith(SpringRunner.class)
@SpringBootTest
public class SearchServiceTest {
    @Autowired
    private GoodsRepository goodsRepository;
    @Test
    public void searchByPage(){
        // 构建查询条件
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        // 添加基本分词查询
        queryBuilder.withQuery(QueryBuilders.termQuery("all", "手机"));
        // 分页：
        int page = 5;
        int size = 20;
        queryBuilder.withPageable(PageRequest.of(page,size));

        // 搜索，获取结果
        Page<Goods> goodss = this.goodsRepository.search(queryBuilder.build());
        // 总条数
        long total = goodss.getTotalElements();
        System.err.println("总条数 = " + total);
        // 总页数
        long totalPages = (int) (total - 1) / size + 1;
        System.err.println("总页数 = " + goodss.getTotalPages());
        // 当前页
        System.err.println("当前页：" + goodss.getNumber());
        // 每页大小
        System.err.println("每页大小：" + goodss.getSize());

        for (Goods goods : goodss) {
            System.err.println(goods);
        }
    }
    @Test
    public void searchByPage2(){
        //构建查询条件对象
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        //使用QueryBiulds封装查询条件
        queryBuilder.withQuery(QueryBuilders.termQuery("all","手机"));
        //分页
        int page = 2;
        int size = 2;
        queryBuilder.withPageable(PageRequest.of(page,size));
        //使用ElasticsearchRepository子类执行查询
        Page<Goods> items = goodsRepository.search(queryBuilder.build());
        //***************
//        queryBuilder.withQuery(QueryBuilders.termQuery("category","手机"));
        //分页
        queryBuilder.withPageable(PageRequest.of(page,size));
        //使用ElasticsearchRepository子类执行查询
        //获得总条数
        System.err.println("数据总条数："+items.getTotalElements());
        //获得总页数
        int totalPages = (int) ((items.getTotalElements()+size-1)/size);
//        System.err.println("总页数："+ totalPages);
        System.err.println("总页数："+items.getTotalPages());
        //获得每页大小
//        System.err.println("每页大小："+ size);
        System.err.println("每页大小："+items.getSize());
        //获得当前页数
//        System.err.println("当前页："+ (page+1));
        System.err.println("当前页："+items.getNumber());
        //********************
        //遍历当前页结果
        items.forEach(item -> System.err.println("item = " + item));
    }


}