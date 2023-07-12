package com.changgou.test;

import com.changgou.GoodsApplication;
import com.changgou.entity.Result;
import com.changgou.goods.controller.CategoryController;
import com.changgou.goods.controller.SpuController;
import com.changgou.goods.pojo.Category;
import com.changgou.goods.pojo.Goods;
import com.changgou.goods.pojo.Spu;
import com.changgou.goods.service.SpuService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;


@Slf4j
@SpringBootTest(classes = GoodsApplication.class)
@RunWith(SpringRunner.class)
public class Demo {


@Resource
private CategoryController categoryController;

@Resource
private SpuController spuController;

@Resource
private SpuService spuService;


    @Test
    public void test02(){
        //pageService.generateHtml("10000012259200");
        Result<Category> categoryResult = categoryController.findById(399);
        Category category = categoryResult.getData();

             //   Category category = categoryResult.getData();
        System.out.println(category);
        log.error(category.toString());
        System.err.print(category.toString());
    }

    @Test
    public void test03(){
        //pageService.generateHtml("10000012259200");
        Result<Goods> result = spuController.findById("10000012259200");

        Goods goods = result.getData();
        Spu spu = goods.getSpu();

        //   Category category = categoryResult.getData();
        System.out.println(spu);
        log.error(spu.toString());
        System.err.print(spu.toString());
    }


    @Test
    public void test04(){
        //pageService.generateHtml("10000012259200");
        Spu spu = spuService.findById("10000012259200");
        //   Category category = categoryResult.getData();
        System.out.println(spu);
        log.error(spu.toString());
        System.err.print(spu.toString());
    }
}
