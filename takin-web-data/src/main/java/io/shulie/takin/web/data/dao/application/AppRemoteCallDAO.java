package io.shulie.takin.web.data.dao.application;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.data.model.mysql.AppRemoteCallEntity;
import io.shulie.takin.web.data.param.application.AppRemoteCallCreateParam;
import io.shulie.takin.web.data.param.application.AppRemoteCallQueryParam;
import io.shulie.takin.web.data.param.application.AppRemoteCallUpdateParam;
import io.shulie.takin.web.data.result.application.AppRemoteCallResult;

/**
 * @author 无涯
 * @date 2021/5/29 12:14 上午
 */
public interface AppRemoteCallDAO extends IService<AppRemoteCallEntity> {

    /**
     * 新增
     */
    void insert(AppRemoteCallCreateParam param);

    /**
     * 新增
     */
    void batchInsert(List<AppRemoteCallCreateParam> param);

    /**
     * 批量更新
     */
    void batchSaveOrUpdate(List<AppRemoteCallUpdateParam> param);

    /**
     * 更新
     */
    void update(AppRemoteCallUpdateParam param);

    /**
     * 根据id获取
     *
     * @return
     */
    AppRemoteCallResult getResultById(Long id);

    /**
     * 根据id删除
     */
    void deleteById(Long id);

    /**
     * 根据id删除
     */
    void deleteByApplicationIds(List<Long> applicationIds);

    /**
     * 查询数据
     *
     * @return
     */
    List<AppRemoteCallResult> getList(AppRemoteCallQueryParam param);

    /**
     * 查询数据
     *
     * @return
     */
    PagingList<AppRemoteCallResult> pagingList(AppRemoteCallQueryParam param);

    /**
     * 根据应用名和租户查询
     *
     * @return
     */
    List<AppRemoteCallResult> selectByAppNameUnderCurrentUser(String appName);

    /**
     * 更新应用名
     */
    void updateAppName(Long applicationId, String appName);

    /**
     * 查询总记录数
     * @param param
     * @return
     */
    Long getRecordCount(AppRemoteCallQueryParam param);

    /**
     * 数据查询分片
     * @param param
     * @param start
     * @param size
     * @return
     */
    List<AppRemoteCallResult> getPartRecord(AppRemoteCallQueryParam param,long start,int size);


    /**
     * 根据id 批量逻辑删除
     * @param ids
     */
    void batchLogicDelByIds(List<Long> ids);

    /**
     * 批量保存
     * @param list
     */
    void batchSave(List<AppRemoteCallResult> list);

    /**
     * 查询全部有效的记录
     * @return
     */
    List<AppRemoteCallResult> getAllRecord();
}
