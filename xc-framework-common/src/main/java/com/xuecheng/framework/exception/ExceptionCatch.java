package com.xuecheng.framework.exception;

import com.google.common.collect.ImmutableMap;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.model.response.ResultCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 捕获异常类
 * @author 鲜磊 on 2019/4/10
 **/
@ControllerAdvice //增强控制方法
public class ExceptionCatch {

    //打印日志
    public static final Logger LOGGER = LoggerFactory.getLogger(ExceptionCatch.class);


    //使用EXCEPTIONS存放异常类型和错误代码的映射，ImmutableMap的特点的一旦创建不可改变，并且线程安全
    private static ImmutableMap<Class<? extends Throwable> ,ResultCode> EXCEPTIONS;
    //使用builder来构建一个异常类型和错误代码的异常
    private static ImmutableMap.Builder<Class<? extends Throwable> , ResultCode> builder = ImmutableMap.builder();


    //捕获customerException
    @ExceptionHandler(CustomException.class)
    @ResponseBody  //转换为json格式
    public ResponseResult customException(CustomException customException){

        LOGGER.error("error exception{}:"+customException);
        ResultCode resultCode = customException.getResultCode();
        //返回状态码
        return new ResponseResult(resultCode);
    }


    //未知异常
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseResult exception(Exception exception){

        LOGGER.error("catch exception{}:"+exception);

        //判断EXCEPTIONS是否为null
        if(EXCEPTIONS == null)
            EXCEPTIONS = builder.build();
            final ResultCode resultCode = EXCEPTIONS.get(exception.getClass());
            final ResponseResult responseResult;
            if(resultCode != null){
                responseResult = new ResponseResult(resultCode);
            }else{
                responseResult = new ResponseResult(CommonCode.SERVER_ERROR);
            }
            return responseResult;

    }

    //讲异常存入Map中,指定异常直接获取
    static {
        //在这里加入一些基础的异常类型判断
        builder.put(HttpMessageNotReadableException.class, CommonCode.INVALID_PARAM);
    }

}
