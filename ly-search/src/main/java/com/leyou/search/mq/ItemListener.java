package com.leyou.search.mq;

import com.leyou.search.service.SearchService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ItemListener {
    @Autowired
    private SearchService searchService;
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "search.item.insert.queue",durable = "true"),
            exchange = @Exchange(name = "ly.item.exchange",type = ExchangeTypes.TOPIC),
            key = {"item.insert","item.update"}
    ))
    public void listenInsertOrUpDate(Long spuId) {//新增或更新索引消息处理
        if (spuId == null) {
            return;
        }
        //处理消息，对索引库进行新增或修改
        searchService.createOrUpDate(spuId);//不用处理异常，有异常让消息回滚
    }
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "search.item.delete.queue",durable = "true"),
            exchange = @Exchange(name = "ly.item.exchange",type = ExchangeTypes.TOPIC),
            key = {"item.delete"}
    ))
    public void listendelete(Long spuId) {//删除索引消息处理
        if (spuId == null) {
            return;
        }
        //处理消息，对索引库进行新增或修改
        searchService.deleteIndex(spuId);//不用处理异常，有异常让消息回滚
    }
}
