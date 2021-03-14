package com.leyou.order.web;

import com.leyou.common.enums.ExceptionEnums;
import com.leyou.common.exception.LyException;
import com.leyou.order.dto.OrderDto;
import com.leyou.order.enums.PayState;
import com.leyou.order.pojo.Order;
import com.leyou.order.pojo.TestNotNull;
import com.leyou.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("order")
public class OrderController {
    @Autowired
    private OrderService orderService;
    @PostMapping("test")
    public void testNotNull(TestNotNull testNotNull){
        System.err.println("testNotNull = [" + testNotNull + "]");
    }

    /**
     * 创建订单
     * @param orderDto
     * @return
     */
    @PostMapping
    public ResponseEntity<Long> createOrder(@RequestBody OrderDto orderDto){
        Long orderId=orderService.createOrder(orderDto);
        if (orderId == null) {
            throw new LyException(ExceptionEnums.CREATE_ORDER_ERROR);
        }
        return ResponseEntity.ok(orderId);
    }

    /**
     * 根据订单号查询订单信息，只有订单号和总金额
     * 给付款页面使用
     * @param orderId
     * @return
     */
    @GetMapping("{orderId}")
    public ResponseEntity<Order> getOderInfo(@PathVariable("orderId")Long orderId){
        return ResponseEntity.ok(orderService.getOder(orderId));

    }
    @GetMapping("url/{id}")
    public ResponseEntity<String> createPayUrl(@PathVariable("id")Long orderId){
        return ResponseEntity.ok(orderService.createPayUrl(orderId));
    }
    @GetMapping("/state/{id}")
    private ResponseEntity<Integer> queryOrderStateById(@PathVariable("id")Long orderId){
        return ResponseEntity.ok(orderService.queryOrderStateById(orderId).getValue());
    }
}
