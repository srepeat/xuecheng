package com.xuecheng.api.filesystem;

import com.xuecheng.framework.domain.filesystem.response.UploadFileResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传
 * @author 鲜磊 on 2019/5/2
 **/
@Api(value = "文件上传接口",description = "提供文件上传功能")
public interface FileSystemControllerApi {

    /**
     *
     * @param multipartFile 文件
     * @param filetag 文件标签
     * @param businesskey 业务Key
     * @param metadata 源信息，json格式
     * @return
     */
    @ApiOperation("文件上传")
    public UploadFileResult upload(MultipartFile multipartFile,String filetag,String businesskey,String metadata);

}
