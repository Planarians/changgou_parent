package com.changgou.user.feign;

import com.changgou.user.pojo.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient("User")
public interface UserFeign {


    @GetMapping("/user/load/{username}")
    User findUserInfo(@PathVariable("username") String username);
}
