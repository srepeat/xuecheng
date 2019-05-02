package com.xuecheng.manage_course.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.CourseMarket;
import com.xuecheng.framework.domain.course.CoursePic;
import com.xuecheng.framework.domain.course.Teachplan;
import com.xuecheng.framework.domain.course.ext.CourseInfo;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.domain.course.response.AddCourseResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_course.dao.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 课程服务层
 * @author 鲜磊 on 2019/4/28
 **/
@Service
public class CourseService {

    @Autowired
    private TeachplanMapper teachplanMapper;

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private TeachplanRepository teachplanRepository;

    @Autowired
    private CourseBaseRepository courseBaseRepository;

    @Autowired
    private CourseMarketRepository CourseMarketRepository;

    @Autowired
    private CoursePicRepository coursePicRepository;


    //课程计划
    public TeachplanNode findTeachplanList(String courseId){
        return teachplanMapper.selectList(courseId);
    }

    //添加课程计划
    @Transactional //事务注解
    public ResponseResult addTeachplan(Teachplan teachplan){
        //校验课程Id和课程名称
        if(teachplan == null || StringUtils.isEmpty(teachplan.getCourseid()) || StringUtils.isEmpty(teachplan.getPname())){
            //抛出异常
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        //取出课程id
        String courseid = teachplan.getCourseid();
        //取出父节点Id
        String parentid = teachplan.getParentid();
        if(StringUtils.isEmpty(parentid)){
            //如果父结点为空则获取根结点
            parentid= this.getTeachplanRoot(courseid);
        }
        //取出父节点信息
        Optional<Teachplan> teachplanOptional = teachplanRepository.findById(parentid);
        if(!teachplanOptional.isPresent()){
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        //父节点
        Teachplan teachplanParent = teachplanOptional.get();
        //父节点级别
        String parentGrade = teachplanParent.getGrade();
        //设置父节点
        teachplan.setParentid(parentid);
        //未发布
        teachplan.setStatus("0");
        //子结点的级别，根据父结点来判断
        if(parentGrade.equals("1")){
            teachplan.setGrade("2");
        }else {
            teachplan.setGrade("3");
        }

        //设置课程id
        teachplan.setCourseid(teachplanParent.getCourseid());
        teachplanRepository.save(teachplan);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    //获取课程根结点，如果没有则添加根结点
    private String getTeachplanRoot(String courseId){
        //获取课程id
        Optional<CourseBase> optional = courseBaseRepository.findById(courseId);
        if(!optional.isPresent()){
            return null;
        }
        //获取课程
        CourseBase courseBase = optional.get();
        //获取课程根节点
        List<Teachplan> teachplanList = teachplanRepository.findByCourseidAndParentid(courseId, "0");
        if(teachplanList == null || teachplanList.size()<=0){
            //创建一个新的节点
            Teachplan teachplanRoot = new Teachplan();
            //课程id
            teachplanRoot.setCourseid(courseId);
            //课程id
            teachplanRoot.setPname(courseBase.getName());
            //根节点
            teachplanRoot.setParentid("0");
            //等级权限
            teachplanRoot.setGrade("1");//1级
            //发布状态
            teachplanRoot.setStatus("0");//未发布
            //保存
            teachplanRepository.save(teachplanRoot);
            //返回获取节点id
            return teachplanRoot.getId();
        }
        //获取集合中的id
        Teachplan teachplan = teachplanList.get(0);
        //返回节点id
        return teachplan.getId();

    }



    //课程分页查询
    public QueryResponseResult findCourseList(int page, int size, CourseListRequest courseListRequest) {
        if(courseListRequest == null){
            //创建一个新的
            courseListRequest = new CourseListRequest();
        }
        //判断参数
        if(page <=0){
            page = 0;
        }

        if(size <=0){
            size = 20;
        }

        //设置分页参数
        PageHelper.startPage(page,size);
        //分页查询
        List<CourseInfo> courseListPage = courseMapper.findCourseListPage(courseListRequest);
        PageInfo pageInfo = new PageInfo(courseListPage);
        //总记录数
        long total = pageInfo.getTotal();
        //封装结果集
        QueryResult<CourseInfo> queryResult = new QueryResult<>();
        queryResult.setList(pageInfo.getList());
        queryResult.setTotal(total);
        //返回结果集
        return new QueryResponseResult(CommonCode.SUCCESS, queryResult);
    }


    //添加课程基础信息
    @Transactional
    public AddCourseResult addCourseBase(CourseBase courseBase){
        //未发布
        courseBase.setStatus("202001");
        courseBaseRepository.save(courseBase);
        return new AddCourseResult(CommonCode.SUCCESS,courseBase.getId());
    }


    //根据Id查询
    public CourseBase getCoursebaseById(String courseId){
        Optional<CourseBase> optional = courseBaseRepository.findById(courseId);
        if(optional.isPresent()){
            return optional.get();
        }
        return null;
    }

    //先查询id然后更具id去修改当前页面信息
    @Transactional
    public ResponseResult updateCoursebase(String courseId,CourseBase courseBase){
        //查询课程id
        CourseBase coursebaseById = this.getCoursebaseById(courseId);
        if(coursebaseById == null){
            ExceptionCast.cast(CmsCode.CMS_UPDATE_NEOISNULL);
        }
        //修改信息
        coursebaseById.setName(courseBase.getName());
        coursebaseById.setMt(courseBase.getMt());
        coursebaseById.setSt(courseBase.getSt());
        coursebaseById.setGrade(courseBase.getGrade());
        coursebaseById.setStudymodel(courseBase.getStudymodel());
        coursebaseById.setUsers(courseBase.getUsers());
        coursebaseById.setDescription(courseBase.getDescription());
        //保存
        courseBaseRepository.save(coursebaseById);
        //返回结果集
        return new ResponseResult(CommonCode.SUCCESS);
    }

    //获取营销id
    public CourseMarket getCourseMarketById(String courseid){
        //查询营销id
        Optional<CourseMarket> optional = CourseMarketRepository.findById(courseid);
        if(optional.isPresent()){
           return optional.get();
        }
        return null;
    }

    //更新课程营销信息
    @Transactional
    public CourseMarket updateCourseMarket(String id, CourseMarket courseMarket) {
        CourseMarket courseMarketById = this.getCourseMarketById(id);
        //不等于空表示有信息，就进行修改
        if(courseMarket != null){
            courseMarket.setCharge(courseMarket.getCharge());
            courseMarket.setStartTime(courseMarket.getStartTime());//课程有效期，开始时间
            courseMarket.setEndTime(courseMarket.getEndTime());//课程有效期，结束时间
            courseMarket.setPrice(courseMarket.getPrice());
            courseMarket.setQq(courseMarket.getQq());
            courseMarket.setValid(courseMarket.getValid());
            //保存信息
            CourseMarketRepository.save(courseMarketById);
        }else {
            //如果没有就添加
            //添加营销信息
            courseMarketById = new CourseMarket();
            BeanUtils.copyProperties(courseMarket,courseMarketById);
            //设置课程id
            courseMarketById.setId(id);
            CourseMarketRepository.save(courseMarketById);
        }
        //返回对象所有信息
        return courseMarketById;

    }

    //保存图片到数据库中
    @Transactional
    public ResponseResult addCoursePic(String courseId, String pic) {
        CoursePic coursePic = null;
        Optional<CoursePic> optional = coursePicRepository.findById(courseId);
        //查询是否存在
        if(optional.isPresent()){
            coursePic = optional.get();
        }

        if(coursePic == null){
            coursePic = new CoursePic();
        }
        //保存数据库中字段
        coursePic.setCourseid(courseId);
        coursePic.setPic(pic);
        coursePicRepository.save(coursePic);
        //返回结果集
        return new ResponseResult(CommonCode.SUCCESS);

    }

    //查询图片
    public CoursePic findCoursePic(String courseId) {
        Optional<CoursePic> optional = coursePicRepository.findById(courseId);
        if(optional.isPresent()){
            CoursePic coursePic = optional.get();
            return coursePic;
        }
        return null;
    }

    //删除图片
    @Transactional
    public ResponseResult deleteCoursePic(String courseId) {
        long result = coursePicRepository.deleteByCourseid(courseId);
        //判断如果结果集返回大于0表示删除一条 1表示删除，0表示删除失败
        if(result >0 ){
            return new ResponseResult(CommonCode.SUCCESS);
        }
        return new ResponseResult(CommonCode.FAIL);
    }
}
