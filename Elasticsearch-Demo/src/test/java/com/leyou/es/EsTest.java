package com.leyou.es;

import com.leyou.es.repository.ItemRepository;
import com.leyou.pojo.Item;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.metrics.avg.InternalAvg;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EsTest {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    ElasticsearchTemplate template;
    @Test
    public void testCreate(){
        //创建索引
        template.createIndex(Item.class);
        //映射关系
        template.putMapping(Item.class);
    }
    @Test
    //单个新增
    public void index() {
        Item item = new Item(1L, "小米手机7", " 手机",
                "小米", 3499.00, "http://image.leyou.com/13123.jpg");
        itemRepository.save(item);
    }
    @Test
    //批量新增
    public void indexList() {
        List<Item> list = new ArrayList<>();
        list.add(new Item(1L, "小米手机7", "手机", "小米", 3299.00, "http://image.leyou.com/13123.jpg"));
        list.add(new Item(2L, "坚果手机R1", "手机", "锤子", 3699.00, "http://image.leyou.com/13123.jpg"));
        list.add(new Item(3L, "华为META10", "手机", "华为", 4499.00, "http://image.leyou.com/13123.jpg"));
        list.add(new Item(4L, "小米Mix2S", "手机", "小米", 4299.00, "http://image.leyou.com/13123.jpg"));
        list.add(new Item(5L, "荣耀V10", "手机", "华为", 2799.00, "http://image.leyou.com/13123.jpg"));
        // 接收对象集合，实现批量新增
        itemRepository.saveAll(list);
    }
    //查找并排序
    @Test
    public void query(){
        Iterable<Item> pricesSort = itemRepository.findAll(Sort.by("price").ascending());
        System.err.println("--------------------------------");
        pricesSort.forEach(item -> System.err.println("item = " + item));
        Iterable<Item> items = itemRepository.findAll(Sort.by("price").descending());
//        for (Item item : items) {
//            System.err.println("item="+item);
//        }

        items.forEach(item -> System.err.println("item="+item));
    }
    @Test
    public void queryByPriceBetween(){
        List<Item> between = itemRepository.findByPriceBetween(2800, 4000);
        between.stream().forEach(item -> System.err.println("item.getPrice() = " + item.getPrice()));
    }
    @Test
    public void search(){
        // 构建查询条件
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        // 添加基本分词查询
        queryBuilder.withQuery(QueryBuilders.matchQuery("title","小米"));
        //排序(排序-》根据什么字段排序-》升降序排)
        queryBuilder.withSort(SortBuilders.fieldSort("price").order(SortOrder.ASC));
        // 搜索，获取结果
        Page<Item> items = itemRepository.search(queryBuilder.build());
        // 总条数
        long total = items.getTotalElements();
        System.err.println("总条数 " + total);
        items.forEach(item -> System.err.println("item = " + item));
    }
    @Test//结果过滤
    public void testSource()throws Exception{
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        //结果过滤(第一个参数包含，第二个参数不包含)
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{"id","title"},null));
        queryBuilder.withQuery(QueryBuilders.matchQuery("title","小米"));
        //分页
        int size = 2,currentPage=0;
        queryBuilder.withPageable(PageRequest.of(currentPage,size));
        Page<Item> items = itemRepository.search(queryBuilder.build());
        long total = items.getTotalElements();//总条数
        int totalPages = items.getTotalPages();//总页数
        System.err.println("总页数 = " + totalPages);
        System.err.println("总条数 = " + total);
        System.err.println("当前页 = " + items.getNumber());

        items.forEach(item -> System.err.println("item = " + item));

    }
    @Test
    public void testQueryByBrand()throws Exception{
        List<Item> items = itemRepository.findByBrandContains("小米");
        items.stream().forEach(item -> System.err.println("item = " + item));
    }
    @Test
    public void searchByPage(){
        //构建查询条件对象
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        //使用QueryBiulds封装查询条件，matchQuery分词分析查询
        queryBuilder.withQuery(QueryBuilders.matchQuery("title","手机"));
        //分页
        int page = 0;
        int size = 2;
        queryBuilder.withPageable(PageRequest.of(page,size));
        //使用ElasticsearchRepository子类执行查询
        Page<Item> items = itemRepository.search(queryBuilder.build());
        //获得总条数
        System.err.println("数据总条数："+items.getTotalElements());
        //获得总页数
        System.err.println("总页数："+items.getTotalPages());
        //获得每页大小
        System.err.println("每页大小："+items.getSize());
        //获得当前页数
        System.err.println("当前页："+items.getNumber());
        //遍历当前页结果
        items.forEach(System.err::println);
//        items.forEach(item -> System.err.println("item = " + item));
    }
    @Test
    public void testSearchAndSort()throws Exception{
        //构建查询条件对象
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        //添加基本查询条件(完全匹配才能查询)
        queryBuilder.withQuery(QueryBuilders.termQuery("category","手机"));
        //排序
        queryBuilder.withSort(SortBuilders.fieldSort("price").order(SortOrder.DESC));
        //执行查询
        Page<Item> items = itemRepository.search(queryBuilder.build());
        //获得总条数
        System.err.println(items.getTotalElements());
        //获得总页数
        System.err.println(items.getTotalPages());
        //获得每页大小
        System.err.println(items.getSize());
        //获得当前页数
        System.err.println(items.getNumber());
        //遍历当前页结果
        items.forEach(item -> System.err.println("item = " + item));
    }
    @Test
    public void testAgg()throws Exception{
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        //不查询任何结果,不包含不排除任何
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{""},null));
        //添加聚合，指定聚合类型、名称和聚合字段
        queryBuilder.addAggregation(AggregationBuilders.terms("brands").field("brand"));
        //执行查询，把结果强转为AggregatedPage类型
        AggregatedPage<Item> items = (AggregatedPage<Item>) itemRepository.search(queryBuilder.build());
        //推荐使用ElasticsearchTemplate不用强转
        //AggregatedPage<Item> items1 = templates.queryForPage(queryBuilder.build(), Item.class);

        //-----------------------------------------------
        System.err.println("\n");
        Aggregations aggs = items.getAggregations();//解析聚合
        StringTerms brandsAgg = aggs.get("brands");//获得指定名字的聚合，不用强转
        List<StringTerms.Bucket> brandsAggBuckets = brandsAgg.getBuckets();
        brandsAggBuckets.forEach(bucket -> {
            System.err.println("DocCount() = " + bucket.getDocCount());
            System.err.println("KeyAsString() = " + bucket.getKeyAsString());
        });
        System.err.println("\n");
        //--------------------------------------------------
        // 解析从结果中按聚合名取出聚合，强转为字段包装类型的聚合（StringTerms）
        StringTerms brands = (StringTerms) items.getAggregation("brands");
        //聚合中取出桶
        List<StringTerms.Bucket> buckets = brands.getBuckets();
        //遍历桶获取key和文档数量(包含相同的key)
       buckets.forEach(bucket -> {
           System.err.println("bucket.getDocCount() = " + bucket.getDocCount());
           System.err.println("bucket.getKeyAsString() = " + bucket.getKeyAsString());
       });
       /* for (StringTerms.Bucket bucket : buckets) {
            // 3.4、获取桶中的key，即品牌名称
            System.err.println(bucket.getKeyAsString());
            // 3.5、获取桶中的文档数量
            System.err.println(bucket.getDocCount());
        }*/

    }
    @Test
    public void testAvg()throws Exception{
        //创建查询条件对象
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        //不查询任何结果
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{""},null));
        //添加并嵌套聚合，key  value形式
        queryBuilder.addAggregation(AggregationBuilders.terms("brands").field("brand")
        .subAggregation(AggregationBuilders.avg("priceAvg").field("price")));
        //执行查询，把结果强转为AggregatedPage带聚合信息类型
        AggregatedPage<Item> items = (AggregatedPage<Item>) itemRepository.search(queryBuilder.build());
        //--------------不强转-----------------
        Aggregations aggregations = items.getAggregations();
        StringTerms brands1 = (StringTerms) items.getAggregation("brands");//传递参数强转一步到位
        StringTerms populate = aggregations.get("brands");
        List<StringTerms.Bucket> bucketList = populate.getBuckets();
        for (StringTerms.Bucket bucket : bucketList) {
            System.err.println("bucket.getKeyAsString() = " + bucket.getKeyAsString());
            System.err.println("bucket.getDocCount() = " + bucket.getDocCount());
        }
        //-----------------------------------------------------
        //取出String类型的聚合
        StringTerms brands = (StringTerms)items.getAggregation("brands");
        //从聚合中取出桶
        List<StringTerms.Bucket> buckets = brands.getBuckets();
        for (StringTerms.Bucket bucket : buckets) {
            // 桶中的key，即品牌名称   获取桶中的文档数量
            System.err.println(bucket.getKeyAsString() + "，共" + bucket.getDocCount() + "台");
            //桶中获取子聚合结果,强转InternalAvg
            InternalAvg avg = (InternalAvg) bucket.getAggregations().asMap().get("priceAvg");
            System.err.println("平均售价：" + avg.getValue());
        }


    }
    @Test
    public void testAgg2()throws Exception{
        //构建查询条件对象
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        String aggName="populateBrand";//抽取变量
        //添加聚合：聚合类型 名称 字段
        queryBuilder.addAggregation(AggregationBuilders.terms(aggName).field("brand"));
        //查询并返回带聚合的结果
        AggregatedPage<Item> items = template.queryForPage(queryBuilder.build(), Item.class);
        //解析聚合
        Aggregations agg = items.getAggregations();
        //获得指定名称聚合
        StringTerms aggregation = agg.get(aggName);
        //获得桶,不需要参数
        List<StringTerms.Bucket> buckets = aggregation.getBuckets();
        System.err.println("总条数 " + items.getTotalElements());
        for (StringTerms.Bucket bucket : buckets) {
            System.err.println("getKeyAsString = " + bucket.getKeyAsString());
            System.err.println("getDocCount = " + bucket.getDocCount());
            System.err.println("getKey = " + bucket.getKey());

        }
    }
    @Test
    public void testAgg3()throws Exception{
        //构建
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        //添加聚合
        queryBuilder.addAggregation(AggregationBuilders.terms("brands").field("brand"));
        AggregatedPage<Item> items = (AggregatedPage<Item>)itemRepository.search(queryBuilder.build());
        //解析聚合、
        StringTerms brand = (StringTerms)items.getAggregation("brands");
        //获得桶
        List<StringTerms.Bucket> buckets = brand.getBuckets();
        for (StringTerms.Bucket bucket : buckets) {
            System.err.println("key = " + bucket.getKeyAsString());
            System.err.println("doc = " + bucket.getDocCount());
        }
    }

}
