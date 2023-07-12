package com.changgou.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.search.pojo.SkuInfo;
import com.changgou.search.service.SearchService;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.MultiBucketsAggregation;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @program: changgou_parent
 * @description:
 * @author: Mr.Wang
 * @create: 2023-07-07 10:09
 **/


@Service
public class SearchServiceImpl implements SearchService {


    @Resource
    private ElasticsearchTemplate elasticsearchTemplate;

//
//    public <T> PageResult<T> parseResponse(SearchResponse response, Class<T> clazz) {
//        SearchHits searchHits = response.getHits();
//        long total = searchHits.getTotalHits();//.value;
//        SearchHit[] hits = searchHits.getHits();
//        List<T> ts = new ArrayList<>();
//        for (SearchHit hit : hits) {
//            String json = hit.getSourceAsString();
//            //System.out.println(json);
//
//            T t = JSON.parseObject(json, clazz);
//            // 放入集合
//            ts.add(t);
//        }
//        return new PageResult(total, ts);
//    }


//    private List<String> getBuckKeyListN(AggregatedPage aggregatedPage, String aggName) {
//        Aggregations aggreations = aggregatedPage.getAggregations();
//        Terms brandTerms = aggreations.get(aggName);
//        List<? extends Terms.Bucket> buckets = brandTerms.getBuckets();
//        List<String> list = buckets.stream().map(MultiBucketsAggregation.Bucket::getKeyAsString).collect(Collectors.toList());
//        return list;
//    }

//
//    private List<String> getBuckKeyList(AggregatedPage aggregatedPage, String aggName) {
//        StringTerms brandTerms = (StringTerms) aggregatedPage.getAggregation(aggName);
//        Aggregation aggregation = aggregatedPage.getAggregation(aggName);
//        aggregation
//        List<? extends Terms.Bucket> buckets = brandTerms.getBuckets();
//        List<String> list = brandTerms.getBuckets().stream().map(bucket -> bucket.getKeyAsString()).collect(Collectors.toList());
//        return list;
//    }


    public Map<String, Set<String>> formartSpec(List<String> specList){
        Map<String,Set<String>> resultMap = new HashMap<>();
        if (specList!=null && specList.size()>0){
            for (String specJsonString : specList) {  //"{'颜色': '黑色', '尺码': '250度'}"
                //将获取到的json转换为map
                Map<String,String> specMap = JSON.parseObject(specJsonString, Map.class);
                for (String specKey : specMap.keySet()) {
                    Set<String> specSet = resultMap.get(specKey);
                    if (specSet == null){
                        specSet = new HashSet<String>();
                    }
                    //将规格信息存入set中
                    specSet.add(specMap.get(specKey));
                    //将set存入map
                    resultMap.put(specKey,specSet);
                }
            }
        }
        return resultMap;
    }
    private List<String> getBuckKeyList(AggregatedPage aggregatedPage, String aggName) {
        // StringTerms brandTerms = (StringTerms) aggregatedPage.getAggregation(aggName);
        Aggregations aggregations = aggregatedPage.getAggregations();
        Terms brandTerms = aggregations.get(aggName);
        List<? extends Terms.Bucket> buckets = brandTerms.getBuckets();
        List<String> list = buckets.stream().map(MultiBucketsAggregation.Bucket::getKeyAsString).collect(Collectors.toList());
        return list;
    }


    private BoolQueryBuilder getBoolQueryBuilder(Map<String, String> paramMap) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        if (StringUtils.isNotBlank(paramMap.get(paramMap.get("keywords")))) {
            boolQuery.must((QueryBuilders.matchQuery("name", paramMap.get("keywords"))));
        } else {
            boolQuery.must(QueryBuilders.matchAllQuery());
        }
        if (StringUtils.isNotBlank(paramMap.get("brand"))) {
            boolQuery.filter(QueryBuilders.termQuery("brandName", paramMap.get("brand")));
        }

        //根据规格 多个
        for (String key : paramMap.keySet()) {
            if (key.startsWith("spec_")) {
                boolQuery.filter(QueryBuilders.termQuery("specMap." + key.substring(5) + ".keyword", paramMap.get(key)));
            }
        }
        if (StringUtils.isNotBlank(paramMap.get(paramMap.get("price")))) {
            String[] prices = paramMap.get("price").split("-");
            if (prices.length == 2) {
                boolQuery.filter(QueryBuilders.rangeQuery("price").lte(prices[1]));//<500
            }
            boolQuery.filter(QueryBuilders.rangeQuery("price").gte(prices[0]));
        }


        return boolQuery;
    }


    @Override
    public Map search(Map<String, String> paramMap) {

        BoolQueryBuilder boolQueryBuilder = getBoolQueryBuilder(paramMap);
        BoolQueryBuilder boolQuery = boolQueryBuilder;


        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
        nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("skuBrand").field("BrandName").size(20));
        nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("skuSpec").field("spec.keyword").size(20));
        String pageNum = paramMap.get("pageNum");
        if (StringUtils.isBlank(pageNum)) {
            pageNum = "1";
        }
        String pageSize = paramMap.get("pageSize");
        if (StringUtils.isBlank(pageSize)) {
            pageSize = "30";
        }
        nativeSearchQueryBuilder.withPageable(PageRequest.of(Integer.parseInt(pageNum) - 1, Integer.parseInt(pageSize)));
        if (StringUtils.isNotBlank(paramMap.get("sortField"))) {

            if (paramMap.get("sortRule") == "ASC") {
                nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort(paramMap.get("sortField")).order(SortOrder.ASC));

            } else {
                nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort(paramMap.get("sortField")).order(SortOrder.DESC));

            }

        }

//        HighlightBuilder highlighterBuilder = new  HighlightBuilder().preTags("<font style='color:red'>").postTags("</font>").field("name");
//        nativeSearchQueryBuilder.withHighlightBuilder(highlighterBuilder);


        HighlightBuilder.Field highlightBuilder = new HighlightBuilder.Field("name").preTags("<font style='color:red'>").postTags("</font>");
        nativeSearchQueryBuilder.withHighlightFields(highlightBuilder);



        nativeSearchQueryBuilder.withQuery(boolQuery);

        AggregatedPage<SkuInfo> aggregatedPage = elasticsearchTemplate.queryForPage(nativeSearchQueryBuilder.build(), SkuInfo.class, new SearchResultMapper() {
            @Override
            public <T> AggregatedPage<T> mapResults(SearchResponse searchResponse, Class<T> aClass, Pageable pageable) {

                List<SkuInfo> skuInfoList = new ArrayList<>();
                SearchHits searchHits = searchResponse.getHits();
                SearchHit[] hits = searchHits.getHits();


                for (SearchHit hit : hits) {
                    HighlightField highlightField = hit.getHighlightFields().get("name");
                    String sourceAsJson = hit.getSourceAsString();
                    SkuInfo skuInfo = JSON.parseObject(sourceAsJson, SkuInfo.class);
                    if (!Objects.isNull(highlightField)) {
                        String highName = highlightField.fragments()[0].string();
                        skuInfo.setName(highName);

                    }
                    skuInfoList.add(skuInfo);
                }
                return new AggregatedPageImpl<T>((List<T>) skuInfoList, pageable, searchHits.getTotalHits(), searchResponse.getAggregations());


            }
        });
        Map map = new HashMap();
        // sum number
        map.put("total", aggregatedPage.getTotalElements());
        map.put("totalPages", aggregatedPage.getTotalPages());
        map.put("rows", aggregatedPage.getContent());
        //todo 返回map结果数据

//        StringTerms brandTerms = (StringTerms) aggregatedPage.getAggregation("skuBrand");
//        List<String> brandList = brandTerms.getBuckets().stream().map(bucket -> bucket.getKeyAsString()).collect(Collectors.toList());
//        map.put("brandList", brandList);

        //    List<String> brandList = getBuckKeyList(aggregatedPage, "skuSpec");

        StringTerms specTerms = (StringTerms) aggregatedPage.getAggregation("skuSpec");
        List<String> specList = specTerms.getBuckets().stream().map(bucket -> bucket.getKeyAsString()).collect(Collectors.toList());

        map.put("specList", specList);

        List<String> brandList = getBuckKeyList(aggregatedPage, "skuBrand");

//
//        StringTerms brandTerms = (StringTerms) aggregatedPage.getAggregation("skuBrand");
//        List<String> brandList = brandTerms.getBuckets().stream().map(bucket -> bucket.getKeyAsString()).collect(Collectors.toList());

        map.put("brandList", brandList);


        map.put("pageNum", pageNum);


        return map;
    }
}
