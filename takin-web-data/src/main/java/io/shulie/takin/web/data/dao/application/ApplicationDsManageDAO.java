package io.shulie.takin.web.data.dao.application;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import io.shulie.takin.web.data.model.mysql.ApplicationDsManageEntity;

/**
 * 应用下 影子库/表 dao,
 * 数据库的交互, 简单语句的封装
 *
 * @author liuchuan
 */
public interface ApplicationDsManageDAO extends IService<ApplicationDsManageEntity> {

    /**
     * 通过应用id, 获取 dsManage 列表 // 注意 ：导出使用，不能用于其他
     *
     * @param applicationId 应用id
     * @return dsManage 列表
     */
    List<ApplicationDsManageEntity> listByApplicationId(Long applicationId);

    /**
     * 根据应用id, url, 获取 影子库/表 记录
     *
     * @param applicationId 应用id
     * @param url 数据库url
     * @return dsManage 实例
     */
    ApplicationDsManageEntity getByApplicationIdAndUrl(Long applicationId, String url);

    /**
     * 通过 应用id, 方案类型 获得 ds 列表
     *
     * @param applicationId 应用id
     * @param dsType 方案类型
     * @return ds 列表
     */
    List<ApplicationDsManageEntity> listByApplicationIdAndDsType(Long applicationId, Integer dsType);

    /**
     * 更新应用名
     * @param applicationId
     * @param appName
     */
    void updateAppName(Long applicationId,String appName);

}
