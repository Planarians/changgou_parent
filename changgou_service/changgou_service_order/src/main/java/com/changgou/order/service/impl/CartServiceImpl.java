package com.changgou.order.service.impl;

import com.changgou.goods.feign.SkuFeign;
import com.changgou.goods.feign.SpuFeign;
import com.changgou.goods.pojo.Sku;
import com.changgou.goods.pojo.Spu;
import com.changgou.order.pojo.OrderItem;
import com.changgou.order.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CartServiceImpl implements CartService {

    private static final String CART = "Cart_";

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private SkuFeign skuFeign;

    @Autowired
    private SpuFeign spuFeign;

//    @Override
//    public void add(String skuId, Integer num, String username) {
//
//        OrderItem orderItem = (OrderItem) redisTemplate.opsForHash().get(username, skuId);
//
//        if (Objects.isNull(orderItem)) {
//            Sku sku = skuFeign.(skuId);
//            if (Objects.isNull(sku)) {
//                throw new RuntimeException("sku doesn't exist ");
//            }
//            BeanUtils.copyProperties(sku, orderItem);
//            orderItem.setNum(orderItem.getNum() + num);
//            orderItem.setMoney(orderItem.getPrice() * orderItem.getNum());
//            orderItem.setPayMoney(orderItem.getPrice() * orderItem.getNum());
//            orderItem.setWeight((num * sku.getWeight()));
//
//            Result<Goods> goodsResult = spuFeign.findSpuById(sku.getSpuId());
//            Goods goods = goodsResult.getData();
//            Spu spu = goods.getSpu();
//            goods.
//                    orderItem.setImage(spu.getImage());
//            orderItem.setMoney
//
//        } else {
//            orderItem.setNum(orderItem.getNum() + num);
//            orderItem.setMoney(orderItem.getPrice() * orderItem.getNum());
//            orderItem.setPayMoney(orderItem.getPrice() * orderItem.getNum());
//
//        }
//
//
//        redisTemplate.opsForHash().put(username, skuId, orderItem);
//
//
////        skuFeign.findSkuListBySpuId()
////                spuFeign.findSpuById()
//    }
//    }
//    }


    /**
     * 添加购物车
     * @param skuId
     * @param num
     */
    @Override
    public void add(String skuId, Integer num,String username) {

        /**
         * 1）查询redis中的数据
         * 2）如果redis中已经有了，则追加数量，重新计算金额
         * 3）如果没有，将商品添加到缓存
         */
        OrderItem orderItem = (OrderItem) redisTemplate.boundHashOps(CART+username).get(skuId);
        if (orderItem != null){
            //存在，刷新购物车
            orderItem.setNum(orderItem.getNum()+num);
            orderItem.setMoney(orderItem.getNum()*orderItem.getPrice());
            orderItem.setPayMoney(orderItem.getNum()*orderItem.getPrice());
        }else{
            //不存在，新增购物车
            Sku sku  = skuFeign.findSkuById(skuId);
            Spu spu = spuFeign.findByspuId(sku.getSpuId());

            //将SKU转换成OrderItem
            orderItem = this.sku2OrderItem(sku,spu,num);
        }

        //存入redis
        redisTemplate.boundHashOps(CART+username).put(skuId,orderItem);


    }

    @Override
    public Map list(String username) {
        Map resultMap =new HashMap();


        List<OrderItem> orderItemList=redisTemplate.opsForHash().values(CART+username);
        resultMap.put("orderItemList",orderItemList);

        int totalNum=orderItemList.stream().mapToInt(value -> value.getNum()).sum();
        int totalPrice=orderItemList.stream().mapToInt(value -> value.getMoney()).sum();
        resultMap.put("totalNum",totalNum);
        resultMap.put("totalPrice",totalPrice);
        return resultMap;

    }

    //sku转换为orderItem
    private OrderItem sku2OrderItem(Sku sku, Spu spu, Integer num) {
        OrderItem orderItem = new OrderItem();
        orderItem.setSpuId(sku.getSpuId());
        orderItem.setSkuId(sku.getId());
        orderItem.setName(sku.getName());
        orderItem.setPrice(sku.getPrice());
        orderItem.setNum(num);
        orderItem.setMoney(num*orderItem.getPrice());       //单价*数量
        orderItem.setPayMoney(num*orderItem.getPrice());    //实付金额
        orderItem.setImage(sku.getImage());
        orderItem.setWeight(sku.getWeight()*num);           //重量=单个重量*数量

        //分类ID设置
        orderItem.setCategoryId1(spu.getCategory1Id());
        orderItem.setCategoryId2(spu.getCategory2Id());
        orderItem.setCategoryId3(spu.getCategory3Id());
        return orderItem;
    }

}