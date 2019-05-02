package com.xuecheng.filesystem.dao;

import com.xuecheng.framework.domain.filesystem.FileSystem;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * 文件系统
 * @author 鲜磊 on 2019/5/2
 **/
public interface FileSystemRepository extends MongoRepository<FileSystem,String> {
}
