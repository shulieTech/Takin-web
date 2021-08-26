package com.pamirs.takin.entity.dao.scenemanage;

import java.util.List;

import com.pamirs.takin.entity.domain.bo.scenemanage.WarnBO;
import com.pamirs.takin.entity.domain.entity.scenemanage.WarnDetail;
import com.pamirs.takin.entity.domain.vo.sla.WarnQueryParam;

public interface TWarnDetailMapper {

    int deleteByPrimaryKey(Long id);

    int insertSelective(WarnDetail record);

    WarnDetail selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(WarnDetail record);

    /**
     * 警告汇总
     *
     * @param reportId
     * @return
     */
    List<WarnBO> summaryWarnByReportId(Long reportId);

    /**
     * 警告列表
     *
     * @param param
     * @return
     */
    List<WarnDetail> listWarn(WarnQueryParam param);

    /**
     * 统计报告总警告次数
     *
     * @param reportId
     * @return
     */
    Long countReportTotalWarn(Long reportId);

}
