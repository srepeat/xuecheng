package com.xuecheng.framework.domain.cms.response;

import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.model.response.ResultCode;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author 鲜磊 on 2019/5/9
 **/
@Data
@NoArgsConstructor
@ToString
public class CmsPostPageResult extends ResponseResult {

    String pageUrl;

    public CmsPostPageResult(ResultCode resultCode,String pageUrl){
        super(resultCode);
        this.pageUrl = pageUrl;
    }
}
