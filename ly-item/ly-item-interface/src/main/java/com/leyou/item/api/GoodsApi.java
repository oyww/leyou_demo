package com.leyou.item.api;

import com.leyou.common.dto.CartDto;
import com.leyou.common.vo.PageResult;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public interface GoodsApi {
    /**
     * 查询商品详情detail byId
     * @param spuId
     * @return
     */
    @GetMapping("spu/detail/{id}")
    SpuDetail queryDetailById(@PathVariable("id")Long spuId);

    /**
     * 根据spu查询下面所有的sku
     * @param spuId
     * @return
     */
    @GetMapping("sku/list")
    List<Sku> querySkuListBySpuId(@RequestParam("id") Long spuId);
    /**
     * 分页查询
     * @param page
     * @param rows
     * @param saleable
     * @param key
     * @return
     */
    @GetMapping("/spu/page")
    PageResult<Spu> querySpuByPage(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "rows", defaultValue = "5") Integer rows,
            @RequestParam(value = "saleable", required = false) Boolean saleable,
            @RequestParam(value = "key", required = false) String key);
    /**
     * 根据spu的id查询spu
     * @param id
     * @return
     */
    @GetMapping("spu/{id}")
    public Spu querySpuById(@PathVariable("id") Long id);

    @GetMapping("sku/{id}")
    public Sku querySkuById(@PathVariable("id")Long id);
    @GetMapping("/sku/list/ids")
    List<Sku> querySkuListByIds(@RequestParam("ids") List<Long> ids);
    @PostMapping("stock/decrease")
    void decreaseStock(@RequestBody List<CartDto> cartDTOS);
}
