package com.changgou.web.order.controller;

import com.changgou.entity.Result;
import com.changgou.entity.StatusCode;
import com.changgou.order.config.TokenDecode;
import com.changgou.order.feign.CartFeign;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Map;

@Controller
@RequestMapping("/wcart")
public class CartController {

    @Resource
    private CartFeign cartFeign;

    @Resource
    private TokenDecode tokenDecode;

    //查询
    @GetMapping("/list")
    public String list(Model model) {
        Map map = cartFeign.list();
        model.addAttribute("items", map);
        return "cart";
    }


    //添加
    @GetMapping("/add")
    @ResponseBody
    public Result<Map> add(String id, Integer num) {
        cartFeign.add(id, num);
        Map map = cartFeign.list();
        return new Result<>(true, StatusCode.OK, "添加购物车成功", map);
    }
}
