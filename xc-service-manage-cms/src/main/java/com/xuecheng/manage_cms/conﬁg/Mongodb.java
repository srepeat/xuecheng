package com.xuecheng.manage_cms.conﬁg;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 鲜磊 on 2019/4/18
 **/
@Configuration //配置类
public class Mongodb {


    @Value("${spring.data.mongodb.database}")
    private String db;


    //配置一个GridFs读取GridFs存入的数据
    @Bean
    public GridFSBucket gridFSBucket(MongoClient mongoClient){
        MongoDatabase database = mongoClient.getDatabase(db);
        GridFSBucket gridFSBucket = GridFSBuckets.create(database);
        return  gridFSBucket;
    }
}
