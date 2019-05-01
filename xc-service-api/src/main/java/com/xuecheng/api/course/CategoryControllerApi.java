package com.xuecheng.api.course;

import com.xuecheng.framework.domain.course.ext.CategoryNode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.List;

/**
 * 课程分类接口
 * @author 鲜磊 on 2019/4/30
 **/
@Api(value = "课程分类",description = "课程分类管理")
public interface CategoryControllerApi {

    @ApiOperation("分类查询")
    public CategoryNode findList();
}
