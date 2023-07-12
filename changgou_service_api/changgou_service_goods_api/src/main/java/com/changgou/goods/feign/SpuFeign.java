package com.changgou.goods.feign;

import com.changgou.entity.Result;
import com.changgou.goods.pojo.Goods;
import com.changgou.goods.pojo.Spu;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "goods")
public interface SpuFeign {

    @GetMapping("/spu/{id}")
    public Result<Goods> findSpuById(@PathVariable("id") String id);

    @GetMapping("/spu/jgfukyfyu/{id}")
    public Spu findByspuId(@PathVariable String id);
}
