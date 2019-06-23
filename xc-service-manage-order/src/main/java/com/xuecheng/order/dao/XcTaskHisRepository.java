package com.xuecheng.order.dao;

import com.xuecheng.framework.domain.task.XcTaskHis;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 历史任务
 * @author 鲜磊 on 2019/6/23
 **/
public interface XcTaskHisRepository extends JpaRepository<XcTaskHis,String> {
}
