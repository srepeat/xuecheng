package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.CourseMarket;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * jpa增删改查
 * Created by Administrator.
 */
public interface CourseMarketRepository extends JpaRepository<CourseMarket,String> {


}
