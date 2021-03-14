package com.leyou.page.mq;

import com.leyou.page.service.PageService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ItemListener {
    @Autowired
    private PageService pageService;
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "page.item.insert.queue",durable = "true"),
            exchange = @Exchange(name = "ly.item.exchange",type = ExchangeTypes.TOPIC),
            key = {"item.insert","item.update"}
    ))
    public void listenInsertOrUpDate(Long spuId) {//新增或更新索引消息处理
        if (spuId == null) {
            return;
        }
        //处理消息，进行页面新增或修改
        pageService.createHtml(spuId);//不用处理异常，有异常让消息回滚
    }
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "page.item.delete.queue",durable = "true"),
            exchange = @Exchange(name = "ly.item.exchange",type = ExchangeTypes.TOPIC),
            key = {"item.delete"}
    ))
    public void listendelete(Long spuId) {//删除索引消息处理
        if (spuId == null) {
            return;
        }
        //处理消息，对索引库进行新增或修改
        pageService.deleteHtml(spuId);//不用处理异常，有异常让消息回滚
    }
}
