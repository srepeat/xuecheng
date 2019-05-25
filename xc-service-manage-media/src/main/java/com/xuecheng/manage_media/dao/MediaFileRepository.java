package com.xuecheng.manage_media.dao;

import com.xuecheng.framework.domain.media.MediaFile;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * 插入媒资数据层
 */
public interface MediaFileRepository extends MongoRepository<MediaFile,String> {
}
