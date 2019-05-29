package com.xuecheng.manage_media.service;

import com.xuecheng.framework.domain.media.MediaFile;
import com.xuecheng.framework.domain.media.request.QueryMediaFileRequest;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.manage_media.dao.MediaFileRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 鲜磊 on 2019/5/29
 **/
@Service
public class MediaFileService {

    @Autowired
    private MediaFileRepository mediaFileRepository;

    //查询文件列表
    public QueryResponseResult findList(int page, int size, QueryMediaFileRequest queryMediaFileRequest) {

        //判断自定义条件是否为null
        if(queryMediaFileRequest == null){
            queryMediaFileRequest = new QueryMediaFileRequest();
        }

        //查询条件
        MediaFile mediaFile = new MediaFile();
        //标签
        if(StringUtils.isNotEmpty(queryMediaFileRequest.getTag())){
            mediaFile.setTag(queryMediaFileRequest.getTag());
        }
        //原始名称
        if(StringUtils.isNotEmpty(queryMediaFileRequest.getFileOriginalName())){
            mediaFile.setFileOriginalName(queryMediaFileRequest.getFileOriginalName());
        }
        //状态
        if(StringUtils.isNotEmpty(queryMediaFileRequest.getProcessStatus())){
            mediaFile.setProcessStatus(queryMediaFileRequest.getProcessStatus());
        }

        //条件匹配器
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
        //条件模糊查询
        .withMatcher("tag", ExampleMatcher.GenericPropertyMatchers.contains())
        .withMatcher("fileOriginalName", ExampleMatcher.GenericPropertyMatchers.contains());
        //精确匹配
        //.withMatcher("processStatus", ExampleMatcher.GenericPropertyMatchers.exact());

        //定义example实例
        Example<MediaFile> mediaFileExample = Example.of(mediaFile,exampleMatcher);

        //分页查询
        if(page<0){
            page=1;
        }
        page = page-1;

        if(size<0){
            size=5;
        }

        Pageable pageable = new PageRequest(page,size);
        //数据库查询
        Page<MediaFile> filePage = mediaFileRepository.findAll(mediaFileExample, pageable);
        long totalElements = filePage.getTotalElements();
        List<MediaFile> content = filePage.getContent();

        QueryResult<MediaFile> queryResult = new QueryResult<>();
        queryResult.setTotal(totalElements);
        queryResult.setList(content);
        //返回结果集
        return new QueryResponseResult(CommonCode.SUCCESS,queryResult);
    }
}
