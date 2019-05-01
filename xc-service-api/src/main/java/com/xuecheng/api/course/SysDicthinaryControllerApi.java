package com.xuecheng.api.course;

import com.xuecheng.framework.domain.system.SysDictionary;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 数据字段
 * @author 鲜磊 on 2019/5/1
 **/
@Api(value = "数据字典接口",description = "提供数据字典接口的管理、查询功能")
public interface SysDicthinaryControllerApi {

    @ApiOperation("数据字典查询接口")
    public SysDictionary findByType(String type);
}
