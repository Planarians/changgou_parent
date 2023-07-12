package com.changgou.web.order;

import com.changgou.interceptor.FeignInterceptor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients(basePackages = {"com.changgou.order.feign"})
public class WebOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebOrderApplication.class,args);
    }

    @Bean
    public FeignInterceptor feignInterceptor(){
        return  new FeignInterceptor();
    }
}
