package io.shulie.takin.web.data.dao.application;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import io.shulie.takin.web.data.model.mysql.LinkGuardEntity;
import io.shulie.takin.web.data.param.application.LinkGuardCreateParam;
import io.shulie.takin.web.data.param.application.LinkGuardUpdateUserParam;
import io.shulie.takin.web.data.result.linkguard.LinkGuardResult;

/**
 * @author fanxx
 * @date 2020/11/4 8:44 下午
 */
public interface LinkGuardDAO extends IService<LinkGuardEntity> {

    /**
     * 新建挡板, 起名为2, 为了防止与
     * mp 冲突
     *
     * @param param 创建所需参数
     * @return 操作行数
     */
    int insert2(LinkGuardCreateParam param);

    List<LinkGuardResult> selectByAppNameUnderCurrentUser(String appName);

    int allocationUser(LinkGuardUpdateUserParam param);

    /**
     * 通过应用id, 获得挡板列表
     * 来自导出请求, 需要的字段不多
     *
     * @param applicationId 应用id
     * @return 挡板列表
     */
    List<LinkGuardEntity> listFromExportByApplicationId(Long applicationId);

    /**
     * 更新应用名
     */
    void updateAppName(Long applicationId, String appName);

    List<LinkGuardResult> selectByAppNameUnderCurrentUser(Long appId);

}
