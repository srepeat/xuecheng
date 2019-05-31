package com.xuecheng.search.service;

import com.xuecheng.framework.domain.course.CoursePub;
import com.xuecheng.framework.domain.search.CourseSearchParam;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 鲜磊 on 2019/5/18
 **/
@Service
public class EsCourseService {


    @Value("${xuecheng.course.index}")
    private String index;

    @Value("${xuecheng.course.type}")
    private String type;

    @Value("${xuecheng.course.source_field}")
    private String source_field;

    @Autowired
    private RestHighLevelClient client;

    public QueryResponseResult<CoursePub> list(int page, int size, CourseSearchParam courseSearchParam) {
        //以防空指针
        if(courseSearchParam == null){
            courseSearchParam = new CourseSearchParam();
        }
        //构建索引源
        SearchRequest searchRequest = new SearchRequest(index);
        //设置类型
        searchRequest.types(type);
        //构建搜索源
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //bool查询
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //设置过滤字段
        String[] split_source_field = source_field.split(",");
        searchSourceBuilder.fetchSource(split_source_field,new String[]{});

        //关键字
        if(StringUtils.isNotEmpty(courseSearchParam.getKeyword())){
            MultiMatchQueryBuilder matchQueryBuilder = QueryBuilders.multiMatchQuery(courseSearchParam.getKeyword(), "name", "teachplan", "description")
                    .minimumShouldMatch("70%") //设置匹配占比
                    .field("name", 10);//提升另个字段的Boost值
            //搜索包含那些字段
            boolQueryBuilder.must(matchQueryBuilder);
        }

        //使用过滤器方式实现
        if(StringUtils.isNotEmpty(courseSearchParam.getMt())){
            //一级分类
            boolQueryBuilder.filter(QueryBuilders.termQuery("mt",courseSearchParam.getMt()));
        }

        if(StringUtils.isNotEmpty(courseSearchParam.getSt())){
            //二级分类
            boolQueryBuilder.filter(QueryBuilders.termQuery("st",courseSearchParam.getSt()));
        }

        if(StringUtils.isNotEmpty(courseSearchParam.getGrade())){
            //难度等级分类
            boolQueryBuilder.filter(QueryBuilders.termQuery("grade",courseSearchParam.getGrade()));
        }

        //分页
        if(page <= 0){
            page = 1;
        }

        if(size <= 0){
            size = 12;
        }

        int start = (page-1)*size;
        //页码
        searchSourceBuilder.from(start);
        //大小
        searchSourceBuilder.size(size);

        //bool查询
        searchSourceBuilder.query(boolQueryBuilder);

        //高亮显示
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.preTags("<font class='eslight'>");
        highlightBuilder.postTags("</font>");
        //设置高亮字段
        highlightBuilder.fields().add(new HighlightBuilder.Field("name"));
        searchSourceBuilder.highlighter(highlightBuilder);
        //请求搜索
        searchRequest.source(searchSourceBuilder);

        //分页数据
        QueryResult<CoursePub> queryResult = new QueryResult<>();
        //数据列表
        List<CoursePub> list = new ArrayList<>();
        //搜索
        try {
            SearchResponse searchResponse = client.search(searchRequest);
            //获取全部数据
            SearchHits searchHits = searchResponse.getHits();
            //总记录数
            long totalHits = searchHits.getTotalHits();
            queryResult.setTotal(totalHits);
            SearchHit[] hits = searchHits.getHits();
            for(SearchHit hit : hits){
                //创建对象
                CoursePub coursePub = new CoursePub();
                //原字段列表
                Map<String, Object> sourceAsMap = hit.getSourceAsMap();

                //id
                String id = (String) sourceAsMap.get("id");
                coursePub.setId(id);

                //课程名称
                String name = (String) sourceAsMap.get("name");
                //高亮设置
                Map<String, HighlightField> highlightFields = hit.getHighlightFields();
                if(highlightFields != null){
                    //获取高亮字段
                    HighlightField nameFields = highlightFields.get("name");
                    if(nameFields != null){
                        //获取数据
                        Text[] fragments = nameFields.getFragments();
                        //操作字符串
                        StringBuffer stringBuffer = new StringBuffer();
                        for(Text text : fragments){
                            //追加到字符串后面
                            stringBuffer.append(text);
                        }
                        name = stringBuffer.toString();
                    }
                }
                coursePub.setName(name);

                //图片
                String pic = (String) sourceAsMap.get("pic");
                coursePub.setPic(pic);

                //价格
                Double price = null;
                try {
                    if(sourceAsMap.get("price") != null){
                        price = (Double) sourceAsMap.get("price");
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                //设置参数
                coursePub.setPrice(price);

                //
                Double price_old = null;
                try {
                    if(sourceAsMap.get("price_old") != null){
                        price_old = (Double) sourceAsMap.get("price_old");
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                //设置参数
                coursePub.setPrice_old(price_old);
                //讲coursePub对象放置数据列表中
                list.add(coursePub);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        queryResult.setList(list);

        QueryResponseResult<CoursePub> responseResult = new QueryResponseResult<>(CommonCode.SUCCESS,queryResult);
        return responseResult;
    }

    //查询课程信息
    public Map<String, CoursePub> getall(String id) {

        //设置索引库
        SearchRequest searchRequest = new SearchRequest(index);
        //设置类型
        searchRequest.types(type);

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //查询条件
        searchSourceBuilder.query(QueryBuilders.termsQuery("id",id));


        searchRequest.source(searchSourceBuilder);

        SearchResponse search = null;
        //创建容器
        Map<String,CoursePub> map = new HashMap<>();
        try {
            search = client.search(searchRequest);

            //获取搜索列表
            SearchHits hits = search.getHits();
            SearchHit[] searchHits = hits.getHits();

            for(SearchHit hit : searchHits){

                CoursePub coursePub = new CoursePub();
                //String courseId = hit.getId();
                Map<String, Object> sourceAsMap = hit.getSourceAsMap();

                //获取参数绑定参数
                String courseId = (String) sourceAsMap.get("id");
                String name = (String) sourceAsMap.get("name");
                String grade = (String) sourceAsMap.get("grade");
                String charge = (String) sourceAsMap.get("charge");
                String pic = (String) sourceAsMap.get("pic");
                String description = (String) sourceAsMap.get("description");
                String teachplan = (String) sourceAsMap.get("teachplan");
                coursePub.setId(courseId);
                coursePub.setName(name);
                coursePub.setPic(pic);
                coursePub.setGrade(grade);
                coursePub.setCharge(charge);
                coursePub.setTeachplan(teachplan);
                coursePub.setDescription(description);
                map.put(courseId,coursePub);

            }

        } catch (IOException e) {
            e.printStackTrace();
        }


        return map;

    }
}
