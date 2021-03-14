package com.leyou.item.service;

import com.leyou.common.dto.CartDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
@RunWith(SpringRunner.class)
@SpringBootTest
public class GoodsServiceTest {
    @Autowired
    private GoodsService goodsService;
    @Test
    public void decreaseStock() throws Exception {
        List<CartDto> cartDtoList = Arrays.asList(new CartDto(2600242l, 2), new CartDto(2600248l, 2));
        goodsService.decreaseStock(cartDtoList);
    }

}