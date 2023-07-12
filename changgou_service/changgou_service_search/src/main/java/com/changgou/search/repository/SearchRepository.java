package com.changgou.search.repository;

import com.changgou.search.pojo.SkuInfo;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @program: changgou_parent
 * @description:
 * @author: Mr.Wang
 * @create: 2023-07-07 16:09
 **/
public interface SearchRepository extends ElasticsearchRepository<SkuInfo, Long> {




}
