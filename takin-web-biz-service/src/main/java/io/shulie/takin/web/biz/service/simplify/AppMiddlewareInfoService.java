package io.shulie.takin.web.biz.service.simplify;

import java.util.List;

import javax.annotation.Resource;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pamirs.takin.entity.dao.confcenter.TApplicationMntDao;
import com.pamirs.takin.entity.dao.simplify.TAppMiddlewareInfoMapper;
import com.pamirs.takin.entity.domain.entity.TApplicationMnt;
import com.pamirs.takin.entity.domain.entity.simplify.TAppMiddlewareInfo;
import com.pamirs.takin.entity.domain.query.agent.AppMiddlewareQuery;
import io.shulie.takin.web.common.common.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author <a href="tangyuhan@shulie.io">yuhan.tang</a>
 * @package: com.pamirs.takin.web.api.service.simplify
 * @date 2020-03-25 17:43
 */
@Slf4j
@Service
public class AppMiddlewareInfoService {

    @Resource
    private TAppMiddlewareInfoMapper tAppMiddlewareInfoMapper;

    @Resource
    private TApplicationMntDao tApplicationMntDao;


    public Response queryPage(AppMiddlewareQuery query) {

        //WebPluginUtils.fillMiddlewareUserData(query);
        TApplicationMnt tApplicationMnt = tApplicationMntDao.queryApplicationinfoById(query.getApplicationId());
        if (null == tApplicationMnt) {
            return Response.fail("未查询到应用相关数据");
        }
        PageHelper.startPage(query.getPageNum(), query.getPageSize());
        List<TAppMiddlewareInfo> list = tAppMiddlewareInfoMapper.selectList(query);
        PageInfo<TAppMiddlewareInfo> pageInfo = new PageInfo<>(list);
        return Response.success(pageInfo.getList(), pageInfo.getTotal());
    }
}
