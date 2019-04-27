package com.xuecheng.manage_cms_client.config;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 鲜磊 on 2019/4/25
 **/
@Configuration
public class MongoConfig {


    @Value("${spring.data.mongodb.database}")
    private String db;


    /**
     * 获取下载流模板数据
     * @param mongoClient
     * @return
     */
    @Bean
    public GridFSBucket gridFSBucket(MongoClient mongoClient){
        //获取连接数据库
        MongoDatabase database = mongoClient.getDatabase(db);
        return  GridFSBuckets.create(database);
    }

}
