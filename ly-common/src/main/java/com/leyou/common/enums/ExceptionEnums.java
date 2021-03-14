package com.leyou.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public enum ExceptionEnums {
    PRICE_CANNOT_BE_NULL(400,"价格不能为空"),
    GOODS_ID_BE_NULL(400,"商品Id不能为空"),
    FILE_UPLOAD_ERROR(500,"文件上传失败"),
    CATEGORY_NOT_FOND(404,"商品分类不存在"),
    SPECGROUP_NOT_FOND(404,"商品规格组不存在"),
    SPECPARAM_NOT_FOND(404,"商品规格参数不存在"),
    GOODS_NOT_FOND(404,"商品不存在"),
    GOODS_SKU_NOT_FOND(404,"商品SKU不存在"),
    GOODS_STOCK_NOT_FOND(404,"商品库存不存在"),
    BRAND_NOT_FOND(404,"品牌不存在"),
    GOODS_DETAIL_NOT_FOND(404,"商品详情不存在"),
    BRAND_SAVE_FAIL(500,"品牌新增失败"),
    BRAND_UPDATE_ERROR(500,"品牌更新错误"),
    GOODS_SAVE_ERROR(500,"商品新增错误"),
    GOODS_UPDATE_ERROR(500,"商品更新错误"),
    MESSAGE_SEND_ERROR(500,"消息发送失败"),
    BAD_REQUEST_PARAMS(400,"错误的请求参数"),
    INVALID_VERIFY_CODE(400,"无效的验证码"),
    INVALID_USERNAME_PASSWORD(400,"用户名或密码错误"),
    INVALID_REQUST(400,"无效的请求"),
    CREATE_ORDER_ERROR(500,"创建订单失败"),
    CLEARN_CARTS_ERROR(500,"购物车清空失败"),
    STOCK_NOT_ENOUGH(500,"库存不足"),
    ORDER_NOT_FPND(400,"订单号不存在"),
    ORDER_DETAIL_NOT_FOUNT(400,"订单详情不存在"),
    ORDER_STATUS_NOT_FOUND(400,"订单状态不存在"),
    WX_PAY_ORDER_FAIL(500,"微信下单失败！" ),
    ORDER_STATUS_ERROE(500,"订单状态异常！" ),
    INVALID_ORDER_PARAM(400,"订单参数异常！" ),
    UPDATE_ORDER_STATUS_ERROR(400,"订单状态更新失败！" );
    ;
    private int status;
    private String msg;
}
