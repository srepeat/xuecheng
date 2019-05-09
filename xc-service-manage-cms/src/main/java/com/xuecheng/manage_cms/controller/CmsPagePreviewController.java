package com.xuecheng.manage_cms.controller;

import com.xuecheng.framework.web.BaseController;
import com.xuecheng.manage_cms.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletOutputStream;

/**
 * 预览页面静态化
 * @author 鲜磊 on 2019/4/19
 **/
@Controller
public class CmsPagePreviewController extends BaseController {

    @Autowired
    private PageService pageService;

    //接收页面Id
    @RequestMapping(value="/cms/preview/{pageId}",method = RequestMethod.GET)
    public void preview(@PathVariable("pageId") String pageId){
        String pageHtml = pageService.getPageHtml(pageId);
        try {
            //使用servlet输入流将输出到页面响应写到浏览器
            ServletOutputStream servletOutputStream = response.getOutputStream();
            //设置请求头--ssi解析时自动解析为HTML
            response.setHeader("Content-type","text/html;charset=utf‐8");
            //写到浏览器
            servletOutputStream.write(pageHtml.getBytes("utf-8"));

        }catch (Exception e){
            e.printStackTrace();
        }

    }

}
