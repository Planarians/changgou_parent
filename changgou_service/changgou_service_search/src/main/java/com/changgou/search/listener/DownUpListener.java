package com.changgou.search.listener;

import com.changgou.goods.feign.SkuFeign;
import com.changgou.goods.pojo.Sku;
import com.changgou.search.config.RabbitMQConfig;
import com.changgou.search.pojo.SkuInfo;
import com.changgou.search.repository.SearchRepository;
import com.changgou.search.service.ESManagerService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DownUpListener {

    @Autowired
    private ESManagerService esManagerService;

    @Resource
    private SkuFeign skuFeign;
    @Resource
    private SearchRepository searchRepository;

    @RabbitListener(bindings = @QueueBinding(value =
    @Queue(name = RabbitMQConfig.SEARCH_ADD_QUEUE),
            exchange = @Exchange(name = RabbitMQConfig.GOODS_UP_EXCHANGE, type = ExchangeTypes.DIRECT)

    ))
    public void upMsg(String msg) {

        List<Sku> skuList = skuFeign.findSkuListBySpuId(msg);
        if (skuList == null || skuList.size()<=0){
            throw new RuntimeException("当前没有数据被查询到,无法导入索引库");
        }
        List<SkuInfo> skuInfos=skuList.stream().filter(c->
                c.getStatus().equals("1")).map(c->{
            SkuInfo skuInfo=new SkuInfo();
            BeanUtils.copyProperties(c,skuInfo);
            skuInfo.setId(Long.parseLong(c.getId()));
            skuInfo.setPrice(c.getPrice().longValue());
            return skuInfo;
        }).collect(Collectors.toList());
        searchRepository.saveAll(skuInfos);
    }




    @RabbitListener(bindings = @QueueBinding(value =
    @Queue(name = RabbitMQConfig.SEARCH_DEL_QUEUE),
            exchange = @Exchange(name = RabbitMQConfig.GOODS_DOWN_EXCHANGE, type = ExchangeTypes.DIRECT)

    ))
    public void downMsg(String msg) {

        List<Sku> skuList = skuFeign.findSkuListBySpuId(msg);
        if (skuList == null || skuList.size()<=0){
            throw new RuntimeException("当前没有数据被查询到,无法导入索引库");
        }
        List<SkuInfo> skuInfos=skuList.stream().map(c->{
            SkuInfo skuInfo=new SkuInfo();
            skuInfo.setId(Long.parseLong(c.getId()));
            return skuInfo;
        }).collect(Collectors.toList());
        searchRepository.deleteAll(skuInfos);
    }
}
