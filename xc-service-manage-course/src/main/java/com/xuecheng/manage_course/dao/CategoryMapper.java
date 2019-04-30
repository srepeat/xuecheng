package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.ext.CategoryNode;
import org.apache.ibatis.annotations.Mapper;

/**
 * 分类dao接口
 * @author 鲜磊 on 2019/4/30
 **/
@Mapper
public interface CategoryMapper {

    //分类查询
    public CategoryNode selectList();
}
