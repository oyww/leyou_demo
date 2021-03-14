package com.leyou.cart.web;

import com.leyou.cart.pojo.Cart;
import com.leyou.cart.pojo.LocalCart;
import com.leyou.cart.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class CartController {

    @Autowired
    private CartService cartService;

    /**
     * 添加购物车
     *
     * @return
     */
    @PostMapping
    public ResponseEntity<Void> addCart(@RequestBody Cart cart) {
        this.cartService.addCart(cart);
        return ResponseEntity.ok().build();
    }

    /**
     * 获得购物车列表
     * @return
     */
    @GetMapping
    public ResponseEntity<List<Cart>> queryCartList() {
        try {
            //加入购物车马上获得可能还没提交成功
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        List<Cart> carts = this.cartService.queryCartList();
        if (carts == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
          return ResponseEntity.ok(carts);
    }

    /**
     * 购物车商品数量增减
     * @param skuId
     * @param num
     * @return
     */
    @PutMapping
    public ResponseEntity<Void> updateNum(@RequestParam("skuId") Long skuId,
                                          @RequestParam("num") Integer num) {
        this.cartService.updateNum(skuId, num);
        return ResponseEntity.ok().build();
    }

    /**
     * 删除购物车商品
     * @param skuId
     * @return
     */
    @DeleteMapping("{skuId}")
    public ResponseEntity<Void> deleteCart(@PathVariable("skuId") String skuId) {
        this.cartService.deleteCart(skuId);
        return ResponseEntity.ok().build();
    }

    /**
     * 登录后合并本地购物车
     * @param localCartList
     * @return
     */
    @PostMapping("mergecart")
    public ResponseEntity<Void> mergeCart( @RequestBody  List<Cart> localCartList){
        cartService.mergeCart(localCartList);
        try {
            //等待数据存储成功
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok().build();
    }
}