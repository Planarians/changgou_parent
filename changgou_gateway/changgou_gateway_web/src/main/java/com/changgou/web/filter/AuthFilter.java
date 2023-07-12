package com.changgou.web.filter;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @program: changgou_parent
 * @description:
 * @author: Mr.Wang
 * @create: 2023-07-10 16:02
 **/

@Component
public class AuthFilter implements GlobalFilter, Ordered {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpRequest serverHttpRequest = exchange.getRequest();
        ServerHttpResponse serverHttpResponse = exchange.getResponse();

        //todo 0 if request login -> access
        String path = serverHttpRequest.getURI().getPath();
        if ("/api/oauth/login".equals(path)) {
            return chain.filter(exchange);
        }
        //todo 1 get JTI from cookie


        HttpCookie httpCookie = serverHttpRequest.getCookies().getFirst("uid");
        if(httpCookie==null){
            serverHttpResponse.setStatusCode(HttpStatus.UNAUTHORIZED);
            return serverHttpResponse.setComplete();
        }
        String jti = httpCookie.getValue();
        //todo 2 if JTI isn't existed  -> 401
        if (StringUtils.isBlank(jti)) {
            serverHttpResponse.setStatusCode(HttpStatus.UNAUTHORIZED);
            return serverHttpResponse.setComplete();
        }
        //todo 3 if JTI existed -> get value(JWT) in redis by key

        String jwt = stringRedisTemplate.opsForValue().get(jti);
        //todo 4 if value is null ->401

        if (StringUtils.isBlank(jwt)) {
            serverHttpResponse.setStatusCode(HttpStatus.UNAUTHORIZED);
            return serverHttpResponse.setComplete();
        }

        //todo 5 redis is not null -> access
        serverHttpRequest.mutate().header("Authorization", "Bearer" + jwt);
        return chain.filter(exchange);

    }

    @Override
    public int getOrder() {
        return -1;
    }
}
