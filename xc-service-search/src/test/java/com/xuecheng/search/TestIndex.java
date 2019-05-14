package com.xuecheng.search;

import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.IndicesClient;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.get.GetResult;
import org.elasticsearch.rest.RestStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 鲜磊 on 2019/5/14
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestIndex {

    /**
     * 注入客户端
     */
    @Autowired
    RestHighLevelClient client;

    @Autowired
    RestClient restClient;

    private static final String id = "dR9XtmoBEAYpzZ-lIMsN";

    //删除索引库
    @Test
    public void testDeleteIndex() throws IOException {

        //删除索引请求对象
        DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest("xc_course");
        //获取客户端执行平台
        IndicesClient indices = client.indices();
        //删除索引
        DeleteIndexResponse delete = indices.delete(deleteIndexRequest);
        //返回响应结果
        boolean acknowledged = delete.isAcknowledged();
        System.out.println(acknowledged);
    }

    //创建索引库
    @Test
    public void testCreatIndex() throws IOException {
        //创建索引库名称
        CreateIndexRequest createIndexRequest = new CreateIndexRequest("xc_course");
        //设置参数
        createIndexRequest.settings(Settings.builder().put("number_of_shards","1").put("number_of_replicas","0"));
        //添加映射信息
        createIndexRequest.mapping("doc"," {\n" +
                        " \t\"properties\": {\n" +
                        " \"name\": {\n" +
                        " \"type\": \"text\",\n" +
                        " \"analyzer\":\"ik_max_word\",\n" +
                        " \"search_analyzer\":\"ik_smart\"\n" +
                        " },\n" +
                        " \"description\": {\n" +
                        " \"type\": \"text\",\n" +
                        " \"analyzer\":\"ik_max_word\",\n" +
                        " \"search_analyzer\":\"ik_smart\"\n" +
                        " },\n" +
                        " \"studymodel\": {\n" +
                        " \"type\": \"keyword\"\n" +
                        " },\n" +
                        " \"price\": {\n" +
                        " \"type\": \"float\"\n" +
                        " }\n" +
                        " }\n" +
                        "}", XContentType.JSON);

        //创建索引操作客户端
        IndicesClient indices = client.indices();
        //创建索引库
        CreateIndexResponse createIndexResponse = indices.create(createIndexRequest);
        //返回结果
        boolean shardsAcknowledged = createIndexResponse.isAcknowledged();
        System.out.println(shardsAcknowledged);
    }


    //添加文档
    @Test
    public void testAddDoc() throws IOException {
        //准备JSON数据
        Map<String,Object> jsonMap = new HashMap<>();

        jsonMap.put("name","spring cloud实战");
        jsonMap.put("description","本课程主要从四个章节进行讲解： 1.微服务架构入门 2.spring cloud" +
                "基础入门 3.实战Spring Boot 4.注册中心eureka。");
        jsonMap.put("studymodel","201001");
        //日期格式化
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        jsonMap.put("timestamp",simpleDateFormat.format(new Date()));
        jsonMap.put("price",5.6f);
        //索引库,索引请求对象
        IndexRequest indexRequest = new IndexRequest("xc_course","doc");
        //指定索引文章内容格式
        indexRequest.source(jsonMap);
        //索引响应对象
        IndexResponse indexResponse = client.index(indexRequest);
        //获取响应结果
        DocWriteResponse.Result result = indexResponse.getResult();
        System.out.println(result);

    }


    //通过ID获取索引库
    @Test
    public void testGetIndex() throws IOException {
        //查询id
        //GET查询
        GetRequest getRequest = new GetRequest("xc_course","doc",id);
        //client执行平台
        GetResponse documentFields = client.get(getRequest);
        //判断是否存在
        boolean exists = documentFields.isExists();
        //转换为Map格式
        Map<String, Object> sourceAsMap = documentFields.getSourceAsMap();

        System.out.println(sourceAsMap);
    }

    //修改字段  修改单个字段数据
    @Test
    public void testUpdataDoc() throws IOException {
        //查询id
        //请求修改
        UpdateRequest updateRequest = new UpdateRequest("xc_course","doc",id);
        //数据模型
        Map<String,Object> map = new HashMap<>();
        map.put("name","spring 课程");
        updateRequest.doc(map);
        //修改客户端执行平台
        UpdateResponse update = client.update(updateRequest);
        //返回状态
        RestStatus status = update.status();
        System.out.println(status);

    }

    //删除文档
    @Test
    public void testDeleteDoc() throws IOException {
        //删除请求
        DeleteRequest deleteRequest = new DeleteRequest("xc_course","doc",id);
        //删除平台
        DeleteResponse delete = client.delete(deleteRequest);
        //删除响应结果集
        DocWriteResponse.Result result = delete.getResult();
        System.out.println(result);
    }






}
