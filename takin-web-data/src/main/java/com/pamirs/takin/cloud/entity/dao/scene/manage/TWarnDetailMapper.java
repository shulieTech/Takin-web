package com.pamirs.takin.cloud.entity.dao.scene.manage;

import java.util.List;

import com.pamirs.takin.cloud.entity.domain.bo.scenemanage.WarnBO;
import com.pamirs.takin.cloud.entity.domain.entity.scene.manage.WarnDetail;
import io.shulie.takin.adapter.api.model.request.WarnQueryParam;

/**
 * @author -
 */
public interface TWarnDetailMapper {

    /**
     * 依据主键删除
     *
     * @param id 数据主键
     * @return -
     */
    int deleteByPrimaryKey(Long id);

    /**
     * 插入
     *
     * @param record 入参
     * @return -
     */
    int insertSelective(WarnDetail record);

    /**
     * 依据主键查询
     *
     * @param id 数据主键
     * @return 数据内容
     */
    WarnDetail selectByPrimaryKey(Long id);

    /**
     * 依据主键更新
     *
     * @param record 数据内容(包括主键)
     * @return -
     */
    int updateByPrimaryKeySelective(WarnDetail record);

    /**
     * 警告汇总
     *
     * @param reportId 报告主键
     * @return -
     */
    List<WarnBO> summaryWarnByReportId(Long reportId);

    /**
     * 警告列表
     *
     * @param param -
     * @return -
     */
    List<WarnDetail> listWarn(WarnQueryParam param);

    /**
     * 统计报告总警告次数
     *
     * @param reportId 报告主键
     * @return -
     */
    Long countReportTotalWarn(Long reportId);

}
