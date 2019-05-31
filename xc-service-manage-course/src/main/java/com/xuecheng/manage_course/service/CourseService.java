package com.xuecheng.manage_course.service;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.cms.response.CmsPostPageResult;
import com.xuecheng.framework.domain.course.*;
import com.xuecheng.framework.domain.course.ext.CourseInfo;
import com.xuecheng.framework.domain.course.ext.CoursePublishResult;
import com.xuecheng.framework.domain.course.ext.CourseView;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.domain.course.response.AddCourseResult;
import com.xuecheng.framework.domain.course.response.CourseCode;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_course.client.CmsPageClient;
import com.xuecheng.manage_course.dao.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Id;
import java.text.SimpleDateFormat;
import java.util.Date;
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
    private CourseMarketRepository courseMarketRepository;

    @Autowired
    private CoursePicRepository coursePicRepository;

    @Autowired
    private CmsPageClient cmsPageClient;

    @Autowired
    private CoursePubRepository coursePubRepository;

    @Autowired
    private TeachplanMediaRepository teachplanMediaRepository;

    //配置文件取属性
    @Value("${course-publish.dataUrlPre}")
    private String publish_dataUrlPre;
    @Value("${course-publish.pagePhysicalPath}")
    private String publish_page_physicalpath;
    @Value("${course-publish.pageWebPath}")
    private String publish_page_webpath;
    @Value("${course-publish.siteId}")
    private String publish_siteId;
    @Value("${course-publish.templateId}")
    private String publish_templateId;
    @Value("${course-publish.previewUrl}")
    private String previewUrl;



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
        Optional<CourseMarket> optional = courseMarketRepository.findById(courseid);
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
            courseMarketRepository.save(courseMarketById);
        }else {
            //如果没有就添加
            //添加营销信息
            courseMarketById = new CourseMarket();
            BeanUtils.copyProperties(courseMarket,courseMarketById);
            //设置课程id
            courseMarketById.setId(id);
            courseMarketRepository.save(courseMarketById);
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

    //查询课程视图包括基本信息、课程营销、图片、课程计划
    public CourseView getCoruseView(String id) {

        //创建一个视图对象
        CourseView courseView = new CourseView();

        //查询基本课程
        Optional<CourseBase> baseOptional = courseBaseRepository.findById(id);
        if(baseOptional.isPresent()){
            CourseBase courseBase = baseOptional.get();
            courseView.setCourseBase(courseBase);
        }

        //课程图片
        Optional<CoursePic> picOptional = coursePicRepository.findById(id);
        if(picOptional.isPresent()){
            CoursePic coursePic = picOptional.get();
            courseView.setCoursePic(coursePic);
        }

        //营销课程
        Optional<CourseMarket> marketOptional = courseMarketRepository.findById(id);
        if(marketOptional.isPresent()){
            CourseMarket courseMarket = marketOptional.get();
            courseView.setCourseMarket(courseMarket);
        }


        //课程计划
        TeachplanNode teachplanNode = teachplanMapper.selectList(id);
        courseView.setTeachplanNode(teachplanNode);

        return courseView;
    }

    //课程预览
    public CoursePublishResult preview(String courseId) {
        //查询课程
        CourseBase courseBase = this.findCourseBaseById(courseId);
        //创建CMSPage
        CmsPage cmsPage = new CmsPage();
        //设置属性
        cmsPage.setSiteId(publish_siteId);//siteId
        //模板
        cmsPage.setTemplateId(publish_templateId);
        //页面名称
        cmsPage.setPageName(courseId+".html");
        //页面别名
        cmsPage.setPageAliase(courseBase.getName());
        //页面访问路径
        cmsPage.setPageWebPath(publish_page_webpath);
        //页面存储路径
        cmsPage.setPagePhysicalPath(publish_page_physicalpath);
        //数据Url
        cmsPage.setDataUrl(publish_dataUrlPre+courseId);

        //远程调用CMS保存信息
        CmsPageResult cmsPageResult = cmsPageClient.saveCmsPage(cmsPage);
        if(!cmsPageResult.isSuccess()){
            //提示错误信息
            return new CoursePublishResult(CommonCode.FAIL,null);
        }

        //页面id
        String pageId = cmsPageResult.getCmsPage().getPageId();
        //拼装dataUrl信息
        String url = previewUrl + pageId;
        //返回信息
        return new CoursePublishResult(CommonCode.SUCCESS,url);
    }

    //查询课程基本信息
    public CourseBase findCourseBaseById(String courseId){
        Optional<CourseBase> baseOptional = courseBaseRepository.findById(courseId);
        if(baseOptional.isPresent()){
            CourseBase courseBase = baseOptional.get();
            return courseBase;
        }
        //没有就抛出异常
        ExceptionCast.cast(CourseCode.COURSE_GET_NOTEXISTS);
        return null;
    }

    //课程发布  课程发布存在问题
    @Transactional
    public CoursePublishResult publish(String id) {
        //查询课程
        CourseBase courseBaseById = this.findCourseBaseById(id);

        //准备页面信息
        CmsPage cmsPage = new CmsPage();
        cmsPage.setSiteId(publish_siteId);//站点id
        cmsPage.setDataUrl(publish_dataUrlPre+id);//数据模型url
        cmsPage.setPageName(id+".html");//页面名称
        cmsPage.setPageAliase(courseBaseById.getName());//页面别名，就是课程名称
        cmsPage.setPagePhysicalPath(publish_page_physicalpath);//页面物理路径
        cmsPage.setPageWebPath(publish_page_webpath);//页面webpath
        cmsPage.setTemplateId(publish_templateId);//页面模板id
        //调用cms一键发布接口将课程详情页面发布到服务器
        CmsPostPageResult cmsPostPageResult = cmsPageClient.postPageQuick(cmsPage);
        if(!cmsPostPageResult.isSuccess()){
            return new CoursePublishResult(CommonCode.FAIL,null);
        }

        //保存课程的发布状态为“已发布”
        CourseBase courseBase = this.saveCoursePubState(id);
        if(courseBase == null){
            return new CoursePublishResult(CommonCode.FAIL,null);
        }

        //保存课程索引信息？？？？
        //先创建一个coursePub对象
        CoursePub coursePub = createCoursePub(id);
        //将coursePub对象保存到数据库
        CoursePub coursePubNew = saveCoursePub(id, coursePub);
        if(coursePubNew == null){
            //创建课程索引信息失败
            ExceptionCast.cast(CourseCode.COURSE_PUBLISH_CREATE_INDEX_ERROR);
        }
        //缓存课程的信息
        //...
        //得到页面的url
        String pageUrl = cmsPostPageResult.getPageUrl();
        return new CoursePublishResult(CommonCode.SUCCESS,pageUrl);
    }

    //将coursePub对象保存到数据库
    private CoursePub saveCoursePub(String id,CoursePub coursePub){

        CoursePub coursePubNew = null;
        //根据课程id查询coursePub
        Optional<CoursePub> coursePubOptional = coursePubRepository.findById(id);
        if(coursePubOptional.isPresent()){
            coursePubNew = coursePubOptional.get();
        }else{
            coursePubNew = new CoursePub();
        }

        //将coursePub对象中的信息保存到coursePubNew中
        BeanUtils.copyProperties(coursePub,coursePubNew);
        coursePubNew.setId(id);
        //时间戳,给logstach使用
        coursePub.setTimestamp(new Date());
        //发布时间
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
        String date = simpleDateFormat.format(new Date());
        coursePub.setPubTime(date);
        coursePubRepository.save(coursePub);
        return coursePub;
    }
    //创建coursePub对象
    private CoursePub createCoursePub(String id){
        CoursePub coursePub = new CoursePub();
        //coursePub.setId(id);
        //根据课程id查询course_base
        Optional<CourseBase> baseOptional = courseBaseRepository.findById(id);
        if(baseOptional.isPresent()){
            CourseBase courseBase = baseOptional.get();
            //将courseBase属性拷贝到CoursePub中
            BeanUtils.copyProperties(courseBase,coursePub);
        }

        //查询课程图片
        Optional<CoursePic> picOptional = coursePicRepository.findById(id);
        if(picOptional.isPresent()){
            CoursePic coursePic = picOptional.get();
            BeanUtils.copyProperties(coursePic, coursePub);
        }

        //课程营销信息
        Optional<CourseMarket> marketOptional = courseMarketRepository.findById(id);
        if(marketOptional.isPresent()){
            CourseMarket courseMarket = marketOptional.get();
            BeanUtils.copyProperties(courseMarket, coursePub);
        }

        //课程计划信息
        TeachplanNode teachplanNode = teachplanMapper.selectList(id);
        String jsonString = JSON.toJSONString(teachplanNode);
        //将课程计划信息json串保存到 course_pub中
        coursePub.setTeachplan(jsonString);
        return coursePub;
    }



    //更新课程状态为已发布 202002
    private CourseBase  saveCoursePubState(String courseId){
        CourseBase courseBaseById = this.findCourseBaseById(courseId);
        courseBaseById.setStatus("202002");
        courseBaseRepository.save(courseBaseById);
        return courseBaseById;
    }

    //保存课程管理
    public ResponseResult savemedia(TeachplanMedia teachplanMedia) {

        //检验数据
        if(teachplanMedia == null || StringUtils.isEmpty(teachplanMedia.getTeachplanId())){
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        //课程计划是否为3级
        String teachplanId = teachplanMedia.getTeachplanId();
        //查询课程计划
        Optional<Teachplan> optional = teachplanRepository.findById(teachplanId);
        if(!optional.isPresent()){
            ExceptionCast.cast(CourseCode.COURSE_MEDIA_TEACHPLAN_ISNULL);
        }
        //获取课程列表信息
        Teachplan teachplan = optional.get();

        //获取课程节点
        String grade = teachplan.getGrade();

        //判断是否为3级计划
        if(StringUtils.isEmpty(grade) || !grade.equals("3")){
            ExceptionCast.cast(CourseCode.COURSE_MEDIA_TEACHPLAN_GRADEERROR);
        }

        //创建对象
        TeachplanMedia one = null;
        //查询课程计划信息
        Optional<TeachplanMedia> mediaOptional = teachplanMediaRepository.findById(teachplanId);
        if(mediaOptional.isPresent()){
            one = mediaOptional.get();
        }else {
            one = new TeachplanMedia();
        }

        //保存媒资信息与课程计划信息
        one.setTeachplanId(teachplanId);
        one.setCourseId(teachplanMedia.getCourseId());
        one.setMediaFileOriginalName(teachplanMedia.getMediaFileOriginalName());
        one.setMediaId(teachplanMedia.getMediaId());
        one.setMediaUrl(teachplanMedia.getMediaUrl());
        //保存信息
        teachplanMediaRepository.save(one);
        //返回成功
        return new ResponseResult(CommonCode.SUCCESS);
    }
}
