package com.xuecheng.framework.exception;

import com.xuecheng.framework.model.response.ResultCode;

/**
 * 自定义异常类
 * @author 鲜磊 on 2019/4/10
 **/
public class CustomException extends RuntimeException {

    private ResultCode resultCode;

    //异常信息
    public CustomException(ResultCode resultCode){
        this.resultCode = resultCode;
    }

    //获取信息
    public ResultCode getResultCode(){
        return resultCode;
    }

}
