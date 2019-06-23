package com.xuecheng.order.dao;

import com.xuecheng.framework.domain.task.XcTask;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;

/**
 * @author 鲜磊 on 2019/6/21
 **/
public interface XcTaskRepository extends JpaRepository<XcTask,String> {


    //查询N条记录
    public Page<XcTask> findByUpdateTimeBefore(Pageable pageable, Date pag);

    //更新修改时间
    @Modifying //表示更新
    @Query("update XcTask t set t.updateTime = :updateTime where t.id = :id")
    public int updateTaskTime(@Param(("id")) String id,@Param("updateTime") Date updateTime);


    //更新版本
    @Modifying //表示更新
    @Query("update XcTask t set t.version = :version+1 where t.id = :id and t.version = :version")
    public int updateTaskVersion(@Param("id") String id,@Param("version") int version);

}
