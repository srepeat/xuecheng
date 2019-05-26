package com.xuecheng.api.media;

import com.xuecheng.framework.domain.media.response.CheckChunkResult;
import com.xuecheng.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author 鲜磊 on 2019/5/26
 **/
@Api(value = "煤资管理接口",description = "媒资管理接口，提供文件上传，文件处理等接口")
public interface MediaUploadControllerApi {

    @ApiOperation("文件上传注册")
    public ResponseResult register(String fileMd5,String fileName,
                                   Long fileSize,String mimetype,String fileExt);

    @ApiOperation("文件分块检查")
    public CheckChunkResult checkchunk(String fileMd5,Integer chunk,Integer chunkSize);

    @ApiOperation("分块上传")
    public ResponseResult uploadchunk(MultipartFile file, String fileMd5, Integer chunk);

    @ApiOperation("合并文件")
    public ResponseResult mergechunks(String fileMd5,String fileName,
                                      Long fileSize,String mimetype,String fileExt);
}
