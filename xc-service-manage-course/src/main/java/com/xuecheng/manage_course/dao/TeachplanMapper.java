package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import org.apache.ibatis.annotations.Mapper;

/**
 *查询页面tree树节点接口
 * @author 鲜磊 on 2019/4/28
 **/
@Mapper
public interface TeachplanMapper {

    //通过id查询树节点
    public TeachplanNode selectList(String courseId);

}
