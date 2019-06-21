package com.xuecheng.order.dao;

import com.xuecheng.framework.domain.task.XcTask;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

/**
 * @author 鲜磊 on 2019/6/21
 **/
public interface XcTaskRepository extends JpaRepository<XcTask,String> {


    //查询N条记录
    public Page<XcTask> findByUpdateTimeBefore(Pageable pageable, Date pag);

    //更新修改时间
    @Query("update XcTask t set t.updateTime = :updateTime where t.id = :id")
    public int updateTaskTime(@Param(("id")) int id,@Param("updateTime") Date updateTime);
}
