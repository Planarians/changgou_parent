package com.changgou.order.service;

import java.util.Map;

public interface CartService {
    void add(String skuId, Integer num, String username);


    /***
     * 查询用户的购物车数据
     * @return
     */
    Map list(String username);
}
