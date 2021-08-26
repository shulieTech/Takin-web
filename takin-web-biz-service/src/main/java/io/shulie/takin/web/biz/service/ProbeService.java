package io.shulie.takin.web.biz.service;

import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.biz.pojo.output.probe.CreateProbeOutput;
import io.shulie.takin.web.biz.pojo.output.probe.ProbeListOutput;
import io.shulie.takin.web.common.pojo.dto.PageBaseDTO;

/**
 * 探针包表(Probe)表服务接口
 *
 * @author liuchuan
 * @since 2021-06-03 13:40:57
 */
public interface ProbeService {

    /**
     * 探针列表分页
     *
     * @param pageDTO 分页参数
     * @return 分页列表
     */
    PagingList<ProbeListOutput> pageProbe(PageBaseDTO pageDTO);

    /**
     * 创建探针记录
     *
     * @param probePath 探针路径
     * @return 探针记录id
     */
    CreateProbeOutput create(String probePath);

}
