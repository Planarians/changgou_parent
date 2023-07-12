package com.changgou.page.test.PageService;

import com.changgou.entity.Result;
import com.changgou.goods.feign.CategoryFeign;
import com.changgou.goods.feign.SpuFeign;
import com.changgou.goods.pojo.Category;
import com.changgou.goods.pojo.Goods;
import com.changgou.goods.pojo.Spu;
import com.changgou.page.PageApplication;
import com.changgou.page.service.PageService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;


@Slf4j
@SpringBootTest(classes = PageApplication.class)
@RunWith(SpringRunner.class)
public class Demo {



    @Resource
    private PageService pageService;

    @Resource
    private CategoryFeign categoryFeign;


    @Resource
    private SpuFeign spuFeign;

   @Test
    public void test01(){
       pageService.generateHtml("10000001516600");
   }


   @Test
   public void test001(){
       Result<Goods> spuResult = spuFeign.findSpuById("10000001516600");

       Goods goods = spuResult.getData();
       Spu spu = goods.getSpu();
       System.out.println(spu);
       //log.error(category.toString());
       System.err.print(spu.toString());
   }


    @Test
    public void test02(){
        //pageService.generateHtml("10000012259200");
        Result<Category> categoryResult = categoryFeign.findById(399);
        Category category = categoryResult.getData();
        System.out.println(category);
        //log.error(category.toString());
        System.err.print(category.toString());

    }
}
