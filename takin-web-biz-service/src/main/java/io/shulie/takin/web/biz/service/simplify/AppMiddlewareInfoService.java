package io.shulie.takin.web.biz.service.simplify;

import java.util.List;

import javax.annotation.Resource;

import com.github.pagehelper.PageInfo;
import com.github.pagehelper.PageHelper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.pamirs.takin.entity.dao.simplify.TAppMiddlewareInfoMapper;
import com.pamirs.takin.entity.domain.query.agent.AppMiddlewareQuery;
import com.pamirs.takin.entity.domain.entity.simplify.TAppMiddlewareInfo;

import io.shulie.takin.web.common.common.Response;
import io.shulie.takin.web.data.dao.application.ApplicationDAO;
import io.shulie.takin.web.data.result.application.ApplicationDetailResult;

/**
 * @author <a href="mailto:tangyuhan@shulie.io">yuhan.tang</a>
 * @date 2020-03-25 17:43
 */
@Slf4j
@Service
public class AppMiddlewareInfoService {

    @Resource
    private TAppMiddlewareInfoMapper tAppMiddlewareInfoMapper;

    @Resource
    private ApplicationDAO applicationDAO;

    public Response queryPage(AppMiddlewareQuery query) {

        //WebPluginUtils.fillMiddlewareUserData(query);
        ApplicationDetailResult tApplicationMnt = applicationDAO.getApplicationById(query.getApplicationId());

        if (null == tApplicationMnt) {
            return Response.fail("未查询到应用相关数据");
        }
        PageHelper.startPage(query.getPageNum(), query.getPageSize());
        List<TAppMiddlewareInfo> list = tAppMiddlewareInfoMapper.selectList(query);
        PageInfo<TAppMiddlewareInfo> pageInfo = new PageInfo<>(list);
        return Response.success(pageInfo.getList(), pageInfo.getTotal());
    }
}
