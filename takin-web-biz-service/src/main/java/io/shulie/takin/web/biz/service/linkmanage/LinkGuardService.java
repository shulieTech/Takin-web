package io.shulie.takin.web.biz.service.linkmanage;

import java.util.List;

import com.pamirs.takin.entity.domain.entity.LinkGuardEntity;
import com.pamirs.takin.entity.domain.query.LinkGuardQueryParam;
import com.pamirs.takin.entity.domain.vo.guardmanage.LinkGuardVo;
import io.shulie.takin.web.common.common.Response;

/**
 * @author 慕白
 * @date 2020-03-05 10:41
 */
public interface LinkGuardService {

    Response addGuard(LinkGuardVo guardVo);

    Response updateGuard(LinkGuardVo guardVo);

    Response deleteById(Long id);

    Response<List<LinkGuardVo>> selectByExample(LinkGuardQueryParam param);

    List<LinkGuardVo> agentSelect(String appName);

    Response<List<LinkGuardEntity>> selectAll();

    Response getById(Long id);

    /**
     * 挡板开启、关闭开关
     *
     * @param id     挡板主键
     * @param target 开关的期待状态
     * @return -
     */
    Response enableGuard(Long id, Boolean target);

    List<LinkGuardEntity> getAllEnabledGuard(String applicationId);
}
