package com.xuecheng.framework.domain.learning.response;

import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.model.response.ResultCode;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author 鲜磊 on 2019/6/2
 **/
@Data
@ToString
@NoArgsConstructor
public class GetMediaResult extends ResponseResult {

    //文件路径
    private String fileUrl;

    public GetMediaResult(ResultCode resultCode, String fileUrl){
        super(resultCode);
        this.fileUrl = fileUrl;
    }

}
