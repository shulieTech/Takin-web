package io.shulie.takin.web.data.dao;

import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.common.pojo.dto.PageBaseDTO;
import io.shulie.takin.web.data.param.probe.CreateProbeParam;
import io.shulie.takin.web.data.param.probe.UpdateProbeParam;
import io.shulie.takin.web.data.result.probe.ProbeDetailResult;
import io.shulie.takin.web.data.result.probe.ProbeListResult;

/**
 * 探针包表(Probe)表数据库 dao
 *
 * @author liuchuan
 * @since 2021-06-03 13:39:47
 */
public interface ProbeDAO {

    /**
     * 探针列表分页
     *
     *
     * @param pageDTO 分页参数
     * @return 分页列表
     */
    PagingList<ProbeListResult> pageProbe(PageBaseDTO pageDTO);

    /**
     * 通过 id 获得详情
     *
     * @param probeId 探针记录id
     * @return 详情
     */
    ProbeDetailResult getById(Long probeId);

    /**
     * 通过版本查看详情
     *
     * @param version 版本
     * @return 探针详情
     */
    ProbeDetailResult getByVersion(String version);

    /**
     * 通过实体的 id 更新
     *
     * @param updateProbeParam 更新所需参数
     * @return 是否成功
     */
    boolean updateById(UpdateProbeParam updateProbeParam);

    /**
     * 创建探针记录
     *
     * @param createProbeParam 创建所需参数
     * @return 是否成功
     */
    boolean save(CreateProbeParam createProbeParam);

}

