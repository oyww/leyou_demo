package com.leyou.order.dto;

import com.leyou.common.dto.CartDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
/**
 * dto: orderDataTransferObject
 * 三个字段正好对应前端页面http://api.leyou.com/api/order-service/order 的三个字段
 * carts又是一个集合  包含了商品信息，所以又定义一个cartDTO
 */
public class OrderDto {
    @NotNull
    private Long addressId;
    @NotNull
    private Integer paymentType;
    @NotNull
    private List<CartDto> carts;
}
