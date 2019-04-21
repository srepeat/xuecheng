package com.xuecheng.test.freemarker;

import com.xuecheng.test.freemarker.model.Student;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.*;

/**
 * @author 鲜磊 on 2019/4/17
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class FreemarkerTest001 {


    /***
     * freemarker两种设置模板的方式，
     *  1、获取固定模板方式，拿到模板的路径，实现对数据模板的实现
     *         获取路径的方式设置模板
     *  2、使用自定义字符串模板，也就是html的三大结果，在body体中获取数据模型中的参数
     *          自定义模板要使用stringTemplateLoader字符串模板加载器
     *  相对比较第一种相对固定，不便于灵活更改模板，第二张相对灵活，可以自定义数据模型
     * @throws Exception
     */

    @Test
    public void testGenerateHtml() throws Exception{
        //创建配置类
        Configuration configuration = new Configuration(Configuration.getVersion());
        //得到class path路径
        String classpath = this.getClass().getResource("/").getPath();
        //设置模板路径
        configuration.setDirectoryForTemplateLoading(new File(classpath + "/templates/"));
        //设置字符集
        //configuration.setDefaultEncoding("utf-8");
        //获取模板内容
        //加载模板
        Template template = configuration.getTemplate("test1.ftl");
        //数据模型
        Map map = getMap();
        //静态化
        String content = FreeMarkerTemplateUtils.processTemplateIntoString(template, map);
        //静态化内容
        System.out.println(content);
        //输入流
        InputStream inputStream = IOUtils.toInputStream(content);
        //输出
        FileOutputStream fileOutputStream = new FileOutputStream(new File("d:/test.html"));
        //Copy输入->输出
        IOUtils.copy(inputStream,fileOutputStream);
        //关流
        inputStream.close();
        fileOutputStream.close();
    }



    //通过自定义模板字符串实现页面生成
    @Test
    public void testGenerateHtml01ByString() throws Exception{
        //获取配置类
        Configuration configuration = new Configuration(Configuration.getVersion());
        //设置字符串模板
        String templateString = ""+"<html>\n"+"<head></head>\n"+"<body>\n"+"名称:${name}\n"+"</body>\n"+"</html>";
        //字符串模板加载器
        StringTemplateLoader stringTemplateLoader = new StringTemplateLoader();
        //params1->name自定义名称 params2->自定义字符串模板
        stringTemplateLoader.putTemplate("template",templateString);
        //配置模板加载器
        configuration.setTemplateLoader(stringTemplateLoader);
        //获得模板
        Template template = configuration.getTemplate("template", "utf-8");
        //数据模型
        Map map = getMap();
        //静态化
        String content = FreeMarkerTemplateUtils.processTemplateIntoString(template, map);
        //打印
        System.out.println(content);
        //IO指定输入位置
        //输入流
        InputStream inputStream = IOUtils.toInputStream(content);
        //输出流
        FileOutputStream outputStream = new FileOutputStream(new File("d:/test02.html"));
        IOUtils.copy(inputStream,outputStream);

        //关流
        inputStream.close();
        outputStream.close();
    }


    //数据模型
    private Map getMap(){
        Map<String, Object> map = new HashMap<>();
        //向数据模型放数据
        map.put("name","黑马程序员");
        Student stu1 = new Student();
        stu1.setName("小明");
        stu1.setAge(18);
        stu1.setMoney(1000.86f);
        stu1.setBirthday(new Date());
        Student stu2 = new Student();
        stu2.setName("小红");
        stu2.setMoney(200.1f);
        stu2.setAge(19);
//        stu2.setBirthday(new Date());
        List<Student> friends = new ArrayList<>();
        friends.add(stu1);
        stu2.setFriends(friends);
        stu2.setBestFriend(stu1);
        List<Student> stus = new ArrayList<>();
        stus.add(stu1);
        stus.add(stu2);
        //向数据模型放数据
        map.put("stus",stus);
        //准备map数据
        HashMap<String,Student> stuMap = new HashMap<>();
        stuMap.put("stu1",stu1);
        stuMap.put("stu2",stu2);
        //向数据模型放数据
        map.put("stu1",stu1);
        //向数据模型放数据
        map.put("stuMap",stuMap);
        return map;
    }


}
