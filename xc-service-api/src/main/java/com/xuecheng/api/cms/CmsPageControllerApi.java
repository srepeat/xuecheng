package com.xuecheng.api.cms;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.model.response.QueryResponseResult;

/**
 * @author 鲜磊 on 2019/3/31
 **/
public interface CmsPageControllerApi {
    //分页查询
    public QueryResponseResult findList(int page, int size, QueryPageRequest queryPageRequest);

    //新增页面
    public CmsPageResult add(CmsPage cmsPage);


}
