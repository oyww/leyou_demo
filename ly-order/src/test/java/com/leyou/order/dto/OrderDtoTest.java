package com.leyou.order.dto;

import org.junit.Test;

import static org.junit.Assert.*;

public class OrderDtoTest {
    @Test
    public void testNotNull()throws Exception{
        OrderDto orderDto = new OrderDto(null,null,null);
        System.out.println("orderDto = " + orderDto);
    }
    @Test
    public void test()throws Exception{
        System.out.println("");
    }

}