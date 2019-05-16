package com.xuecheng.search;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.Map;


/**
 * search搜索测试
 * @author 鲜磊 on 2019/5/14
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestSearch {

    /**
     * 注入客户端
     */
    @Autowired
    RestHighLevelClient client;

    @Autowired
    RestClient restClient;


    //search方法
    private SearchRequest getSearchRequest(){
        //索引库
        SearchRequest searchRequest = new SearchRequest("xc_course");
        //文档类型
        searchRequest.types("doc");
        return searchRequest;
    }

    //查询全部
    @Test
    public void testQueryAll() throws IOException {
        //调用搜索方法
        SearchRequest searchRequest = getSearchRequest();
        //搜索资源builder,构建搜索源
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //查询全部
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        //过滤条件 ,第一个参数代表包含哪些参数,第二个参数代表不包含那些参数
        searchSourceBuilder.fetchSource(new String[]{"name","studymodel","description"},new String[]{});
        //向搜索对象设置搜索源
        searchRequest.source(searchSourceBuilder);
        //执行搜索,es向Http发送请求
        SearchResponse searchResponse = client.search(searchRequest);
        //搜索结果
        SearchHits hits = searchResponse.getHits();
        //总记录数
        long totalHits = hits.getTotalHits();
        //得到匹配的
        SearchHit[] hits1 = hits.getHits();
        //循环打印
        for(SearchHit hit : hits1){
            //获取ID
            String id = hit.getId();
            //转换为Map格式
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            String name= (String) sourceAsMap.get("name");
            String studymodel = (String) sourceAsMap.get("studymodel");
            String description = (String) sourceAsMap.get("description");

            System.out.println(name);
            System.out.println(studymodel);
            System.out.println(description);
        }

    }



    //分页查询
    @Test
    public void testQueryPage() throws IOException {
        //获取执行对象
        SearchRequest searchRequest = getSearchRequest();
        //构建搜索源
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        //查询所有
        SearchSourceBuilder query = sourceBuilder.query(QueryBuilders.matchAllQuery());
        //设置过滤参数
        query.fetchSource(new String[]{"name","studymodel","description"},new String[]{});
        //设置分页参数
        int page = 1;

        int size = 1;
        //起始搜索从0开始 下标0开始
        int from = (page-1)*size;

        //传入分页参数
        sourceBuilder.from(from);
        sourceBuilder.size(size);

        //向搜索对象设置搜索源
        searchRequest.source(sourceBuilder);
        //执行搜索,es向Http发送请求
        SearchResponse search = client.search(searchRequest);
        //搜索结果
        SearchHits hits = search.getHits();
        //总记录数
        long totalHits = hits.getTotalHits();
        //得到最高匹配数的
        SearchHit[] searchHits = hits.getHits();
        //循环遍历获取
        for(SearchHit hit : searchHits){
            //获取id
            String id = hit.getId();
            //转换为map格式
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();

            //获取name
            String name= (String) sourceAsMap.get("name");
            //获取详细说明desc
            String studymodel = (String) sourceAsMap.get("studymodel");
            //课程编号
            String description = (String) sourceAsMap.get("description");

            System.out.println(name);
            System.out.println(studymodel);
            System.out.println(description);
        }
    }


    //精确查询
    @Test
    public void testTermQuery() throws IOException {
        //获取执行对象
        SearchRequest searchRequest = getSearchRequest();
        //构建搜索源
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        //精确查询
        SearchSourceBuilder query = sourceBuilder.query(QueryBuilders.termQuery("name","spring"));
        //设置过滤参数
        query.fetchSource(new String[]{"name","studymodel","description"},new String[]{});
        //设置分页参数
        int page = 1;

        int size = 1;
        //起始搜索从0开始 下标0开始
        int from = (page-1)*size;

        //传入分页参数
        sourceBuilder.from(from);
        sourceBuilder.size(size);

        //向搜索对象设置搜索源
        searchRequest.source(sourceBuilder);
        //执行搜索,es向Http发送请求
        SearchResponse search = client.search(searchRequest);
        //搜索结果
        SearchHits hits = search.getHits();
        //总记录数
        long totalHits = hits.getTotalHits();
        //得到最高匹配数的
        SearchHit[] searchHits = hits.getHits();
        //循环遍历获取
        for(SearchHit hit : searchHits){
            //获取id
            String id = hit.getId();
            //转换为map格式
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();

            //获取name
            String name= (String) sourceAsMap.get("name");
            //获取详细说明desc
            String studymodel = (String) sourceAsMap.get("studymodel");
            //课程编号
            String description = (String) sourceAsMap.get("description");

            System.out.println(name);
            System.out.println(studymodel);
            System.out.println(description);
        }
    }

    //TermQuery ids
    @Test
    public void testMatchQuery() throws IOException {
        //获取执行对象
        SearchRequest searchRequest = getSearchRequest();
        //构建搜索源
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        //关键字查询 matchQuery 分词搜索
        SearchSourceBuilder query = sourceBuilder.query(QueryBuilders.matchQuery("name","spring 开发")
                .operator(Operator.OR));
        //设置过滤参数
        query.fetchSource(new String[]{"name","studymodel","description"},new String[]{});
        //向搜索对象设置搜索源
        searchRequest.source(sourceBuilder);
        //执行搜索,es向Http发送请求
        SearchResponse search = client.search(searchRequest);
        //搜索结果
        SearchHits hits = search.getHits();
        //总记录数
        long totalHits = hits.getTotalHits();
        //得到最高匹配数的
        SearchHit[] searchHits = hits.getHits();
        //循环遍历获取
        for(SearchHit hit : searchHits){
            //获取id
            String id = hit.getId();
            //转换为map格式
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            //获取name
            String name= (String) sourceAsMap.get("name");
            //获取详细说明desc
            String studymodel = (String) sourceAsMap.get("studymodel");
            //课程编号
            String description = (String) sourceAsMap.get("description");

            System.out.println(name);
            System.out.println(studymodel);
            System.out.println(description);
        }
    }


    //精确查询id
    @Test
    public void testTermQueryByIds() throws IOException {
        //获取执行对象
        SearchRequest searchRequest = getSearchRequest();
        //构建搜索源
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        String[] ids = new String[]{"1","2"};
        //关键字查询 matchQuery
        SearchSourceBuilder query = sourceBuilder.query(QueryBuilders.termsQuery("_id",ids));
        //设置过滤参数
        query.fetchSource(new String[]{"name","studymodel","description"},new String[]{});
        //向搜索对象设置搜索源
        searchRequest.source(sourceBuilder);
        //执行搜索,es向Http发送请求
        SearchResponse search = client.search(searchRequest);
        //搜索结果
        SearchHits hits = search.getHits();
        //总记录数
        long totalHits = hits.getTotalHits();
        //得到最高匹配数的
        SearchHit[] searchHits = hits.getHits();
        //循环遍历获取
        for(SearchHit hit : searchHits){
            //获取id
            String id = hit.getId();
            //转换为map格式
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();

            //获取name
            String name= (String) sourceAsMap.get("name");
            //获取详细说明desc
            String studymodel = (String) sourceAsMap.get("studymodel");
            //课程编号
            String description = (String) sourceAsMap.get("description");

            System.out.println(name);
            System.out.println(studymodel);
            System.out.println(description);
        }
    }


    //matchQuery
    @Test
    public void testMatchQueryByShouldMatch() throws IOException {
        //获取执行对象
        SearchRequest searchRequest = getSearchRequest();
        //构建搜索源
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        //关键字查询 matchQuery  计算方式3*0.7 = 2.1  取整2
        SearchSourceBuilder query = sourceBuilder.query(QueryBuilders.matchQuery("description","前台页面开发框架 架构")
                .minimumShouldMatch("70%"));//查询词的关键字占比
        //设置过滤参数
        query.fetchSource(new String[]{"name","studymodel","description"},new String[]{});
        //向搜索对象设置搜索源
        searchRequest.source(sourceBuilder);
        //执行搜索,es向Http发送请求
        SearchResponse search = client.search(searchRequest);
        //搜索结果
        SearchHits hits = search.getHits();
        //总记录数
        long totalHits = hits.getTotalHits();
        //得到最高匹配数的
        SearchHit[] searchHits = hits.getHits();
        //循环遍历获取
        for(SearchHit hit : searchHits){
            //获取id
            String id = hit.getId();
            //转换为map格式
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();

            //获取name
            String name= (String) sourceAsMap.get("name");
            //获取详细说明desc
            String studymodel = (String) sourceAsMap.get("studymodel");
            //课程编号
            String description = (String) sourceAsMap.get("description");

            System.out.println(name);
            System.out.println(studymodel);
            System.out.println(description);
        }
    }

    //multiMatchQuery
    @Test
    public void testMultiMatchQuery() throws IOException {
        //获取执行对象
        SearchRequest searchRequest = getSearchRequest();
        //构建搜索源
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        //关键字查询 multiMatchQuery  boost提升字段权重
        sourceBuilder.query(QueryBuilders.multiMatchQuery("spring 开发","name")
                .minimumShouldMatch("70%").field("name",10));
        //设置过滤参数
        sourceBuilder.fetchSource(new String[]{"name","studymodel","description"},new String[]{});
        //向搜索对象设置搜索源
        searchRequest.source(sourceBuilder);
        //执行搜索,es向Http发送请求
        SearchResponse search = client.search(searchRequest);
        //搜索结果
        SearchHits hits = search.getHits();
        //总记录数
        long totalHits = hits.getTotalHits();
        //得到最高匹配数的
        SearchHit[] searchHits = hits.getHits();
        //循环遍历获取
        for(SearchHit hit : searchHits){
            //获取id
            String id = hit.getId();
            //转换为map格式
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();

            //获取name
            String name= (String) sourceAsMap.get("name");
            //获取详细说明desc
            String studymodel = (String) sourceAsMap.get("studymodel");
            //课程编号
            String description = (String) sourceAsMap.get("description");

            System.out.println(name);
            System.out.println(studymodel);
            System.out.println(description);
        }
    }


    //BoolQuery
    @Test
    public void testBoolQuery() throws IOException {
        //获取执行对象
        SearchRequest searchRequest = getSearchRequest();
        //构建搜索源
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        //设置过滤参数
        sourceBuilder.fetchSource(new String[]{"name","studymodel","description"},new String[]{});
        //精确查询 multiMatchQuery
        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery("spring开发框架","name","description")
                .minimumShouldMatch("50%");
        multiMatchQueryBuilder.field("name",10);

        //查询课程 termQuery
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("studymodel", "201001");

        //bool查询
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(multiMatchQueryBuilder);
        boolQueryBuilder.must(termQueryBuilder);
        //关键字查询 multiMatchQuery  boost提升字段权重
        sourceBuilder.query(boolQueryBuilder);
        //向搜索对象设置搜索源
        searchRequest.source(sourceBuilder);
        //执行搜索,es向Http发送请求
        SearchResponse search = client.search(searchRequest);
        //搜索结果
        SearchHits hits = search.getHits();
        //总记录数
        long totalHits = hits.getTotalHits();
        //得到最高匹配数的
        SearchHit[] searchHits = hits.getHits();
        //循环遍历获取
        for(SearchHit hit : searchHits){
            //获取id
            String id = hit.getId();
            //转换为map格式
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            //获取name
            String name= (String) sourceAsMap.get("name");
            //获取详细说明desc
            String studymodel = (String) sourceAsMap.get("studymodel");
            //课程编号
            String description = (String) sourceAsMap.get("description");

            System.out.println(name);
            System.out.println(studymodel);
            System.out.println(description);
        }
    }


    //Filter
    @Test
    public void testFilter() throws IOException {
        //获取执行对象
        SearchRequest searchRequest = getSearchRequest();
        //构建搜索源
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        //设置过滤参数
        sourceBuilder.fetchSource(new String[]{"name","studymodel","description","price"},new String[]{});
        //精确查询 multiMatchQuery
        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery("spring开发框架","name","description")
                .minimumShouldMatch("50%");
        multiMatchQueryBuilder.field("name",10);


        //bool查询
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(multiMatchQueryBuilder);
        //设置过滤器
        boolQueryBuilder.filter(QueryBuilders.termQuery("studymodel","201001"));
        boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").gte(0).lte(100));

        //关键字查询 multiMatchQuery  boost提升字段权重
        sourceBuilder.query(boolQueryBuilder);
        //向搜索对象设置搜索源
        searchRequest.source(sourceBuilder);
        //执行搜索,es向Http发送请求
        SearchResponse search = client.search(searchRequest);
        //搜索结果
        SearchHits hits = search.getHits();
        //总记录数
        long totalHits = hits.getTotalHits();
        //得到最高匹配数的
        SearchHit[] searchHits = hits.getHits();
        //循环遍历获取
        for(SearchHit hit : searchHits){
            //获取id
            String id = hit.getId();
            //转换为map格式
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();

            //获取name
            String name= (String) sourceAsMap.get("name");
            //获取详细说明desc
            String studymodel = (String) sourceAsMap.get("studymodel");
            //课程编号
            String description = (String) sourceAsMap.get("description");

            System.out.println(name);
            System.out.println(studymodel);
            System.out.println(description);
        }

    }

    //Sort
    @Test
    public void testSort() throws IOException {
        //获取执行对象
        SearchRequest searchRequest = getSearchRequest();
        //构建搜索源
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        //设置过滤参数
        sourceBuilder.fetchSource(new String[]{"name","studymodel","description","price"},new String[]{});
        //精确查询 multiMatchQuery

        //bool查询
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //设置过滤器
        boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").gte(0).lte(100));
        //排序  升序降序
        sourceBuilder.sort("studymodel", SortOrder.DESC);
        sourceBuilder.sort("price", SortOrder.ASC);

        //关键字查询 multiMatchQuery  boost提升字段权重
        sourceBuilder.query(boolQueryBuilder);
        //向搜索对象设置搜索源
        searchRequest.source(sourceBuilder);
        //执行搜索,es向Http发送请求
        SearchResponse search = client.search(searchRequest);
        //搜索结果
        SearchHits hits = search.getHits();
        //总记录数
        long totalHits = hits.getTotalHits();
        //得到最高匹配数的
        SearchHit[] searchHits = hits.getHits();
        //循环遍历获取
        for(SearchHit hit : searchHits){
            //获取id
            String id = hit.getId();
            //转换为map格式
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();

            //获取name
            String name= (String) sourceAsMap.get("name");
            //获取详细说明desc
            String studymodel = (String) sourceAsMap.get("studymodel");
            //课程编号
            String description = (String) sourceAsMap.get("description");
            //价格
            double price = (double) sourceAsMap.get("price");
            System.out.println(name);
            System.out.println(studymodel);
            System.out.println(description);
            System.out.println(price);
        }
    }


    //Highlight
    @Test
    public void testHighlight() throws IOException {
        //获取执行对象
        SearchRequest searchRequest = getSearchRequest();
        //构建搜索源
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        //设置过滤参数
        sourceBuilder.fetchSource(new String[]{"name","studymodel","description","price"},new String[]{});
        //精确查询 multiMatchQuery
        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery("spring 开发框架","name","description")
                //查询子的比列 3 *50 = 1.5 向上取整
                .minimumShouldMatch("50%");
        //设置权重
        multiMatchQueryBuilder.field("name",10);
        //关键字查询 multiMatchQuery  boost提升字段权重
        sourceBuilder.query(multiMatchQueryBuilder);
        //bool查询
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //包含的查询字段
        boolQueryBuilder.must(multiMatchQueryBuilder);
        //设置过滤器   价格的过滤按照0-100区间进行排序
        boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").gte(0).lte(100));
        //排序  升序排序
        sourceBuilder.sort("price",SortOrder.ASC);
        //高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        //前缀
        highlightBuilder.preTags("<tag>");
        //后缀
        highlightBuilder.postTags("</tag>");
        //高亮的字段
        highlightBuilder.fields().add(new HighlightBuilder.Field("name"));
        //设置到搜索源中
        sourceBuilder.highlighter(highlightBuilder);

        //向搜索对象设置搜索源
        searchRequest.source(sourceBuilder);
        //执行搜索,es向Http发送请求
        SearchResponse search = client.search(searchRequest);
        //搜索结果
        SearchHits hits = search.getHits();
        //总记录数
        long totalHits = hits.getTotalHits();
        //得到最高匹配数的
        SearchHit[] searchHits = hits.getHits();
        //循环遍历获取
        for(SearchHit hit : searchHits){
            //获取id
            String id = hit.getId();
            //转换为map格式
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            //获取name
            String name= (String) sourceAsMap.get("name");
            //获取高亮字段
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            //判空
            if(highlightFields != null){
                //获取搜索字段
                HighlightField nameHighlightField = highlightFields.get("name");
                //判断空
                if(nameHighlightField != null){
                    //获取全部的搜索字段
                    Text[] fragments = nameHighlightField.getFragments();
                    //字符
                    StringBuffer stringBuffer = new StringBuffer();
                    //遍历数组
                    for (Text text : fragments){
                        //追加到buffer后
                        stringBuffer.append(text.string());
                    }
                    //如果不等于空就重新赋值给name
                    name = stringBuffer.toString();
                }
            }
            //获取详细说明desc
            String studymodel = (String) sourceAsMap.get("studymodel");
            //课程编号
            String description = (String) sourceAsMap.get("description");
            //输出
            System.out.println(name);
            System.out.println(studymodel);
            System.out.println(description);
        }
    }

}
