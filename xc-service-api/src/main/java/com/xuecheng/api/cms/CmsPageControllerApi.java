package com.xuecheng.api.cms;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @author 鲜磊 on 2019/3/31
 **/

@Api(value = "Cms管理接口",description = "Cms页面管理接口，提供增、删、改、查")
public interface CmsPageControllerApi {
    //分页查询
    @ApiOperation("分页查询列表")
    public QueryResponseResult findList(int page, int size, QueryPageRequest queryPageRequest);

    //新增页面
    @ApiOperation("新增页面")
    public CmsPageResult add(CmsPage cmsPage);

    //根据id查询
    @ApiOperation("通过ID查询页面")
    public CmsPage findById(String id);


    //修改页面
    @ApiOperation("修改页面")
    public CmsPageResult edit(String id,CmsPage cmsPage);

    //删除页面
    @ApiOperation("删除页面")
    public ResponseResult delete(String id);

}