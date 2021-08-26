package io.shulie.takin.web.data.dao.impl;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.common.pojo.dto.PageBaseDTO;
import io.shulie.takin.web.common.util.CommonUtil;
import io.shulie.takin.web.data.dao.ProbeDAO;
import io.shulie.takin.web.data.mapper.mysql.ProbeMapper;
import io.shulie.takin.web.data.model.mysql.ProbeEntity;
import io.shulie.takin.web.data.param.probe.CreateProbeParam;
import io.shulie.takin.web.data.param.probe.UpdateProbeParam;
import io.shulie.takin.web.data.result.probe.ProbeDetailResult;
import io.shulie.takin.web.data.result.probe.ProbeListResult;
import io.shulie.takin.web.data.util.MPUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 探针包表(Probe)表数据库 dao
 *
 * @author liuchuan
 * @since 2021-06-03 13:39:47
 */
@Service
public class ProbeDAOImpl implements ProbeDAO, MPUtil<ProbeEntity> {

    @Autowired
    private ProbeMapper probeMapper;

    @Override
    public PagingList<ProbeListResult> pageProbe(PageBaseDTO pageDTO) {
        Page<ProbeEntity> probeEntityPage = probeMapper.selectPage(this.setPage(pageDTO),
            this.getCustomerLambdaQueryWrapper().select(ProbeEntity::getId, ProbeEntity::getVersion)
                .orderByDesc(ProbeEntity::getGmtUpdate));

        List<ProbeEntity> records = probeEntityPage.getRecords();
        if (records.isEmpty()) {
            return PagingList.empty();
        }

        return PagingList.of(CommonUtil.list2list(records, ProbeListResult.class), probeEntityPage.getTotal());
    }

    @Override
    public ProbeDetailResult getById(Long probeId) {
        ProbeEntity probeEntity = probeMapper.selectOne(this.getLimitOneLambdaQueryWrapper()
            .select(ProbeEntity::getId,ProbeEntity::getPath).eq(ProbeEntity::getId, probeId));
        if (probeEntity == null) {
            return null;
        }

        ProbeDetailResult probeDetailResult = new ProbeDetailResult();
        BeanUtils.copyProperties(probeEntity, probeDetailResult);
        return probeDetailResult;
    }

    @Override
    public ProbeDetailResult getByVersion(String version) {
        ProbeEntity probeEntity = probeMapper.selectOne(this.getCustomerLambdaQueryWrapper().select(ProbeEntity::getId,
            ProbeEntity::getVersion, ProbeEntity::getPath).eq(ProbeEntity::getVersion, version));
        if (probeEntity == null) {
            return null;
        }

        ProbeDetailResult probeDetailResult = new ProbeDetailResult();
        BeanUtils.copyProperties(probeEntity, probeDetailResult);
        return probeDetailResult;
    }

    @Override
    public boolean updateById(UpdateProbeParam updateProbeParam) {
        ProbeEntity probeEntity = new ProbeEntity();
        BeanUtils.copyProperties(updateProbeParam, probeEntity);
        return SqlHelper.retBool(probeMapper.updateById(probeEntity));
    }

    @Override
    public boolean save(CreateProbeParam createProbeParam) {
        ProbeEntity probeEntity = new ProbeEntity();
        BeanUtils.copyProperties(createProbeParam, probeEntity);
        int affectedRows = probeMapper.insert(probeEntity);
        createProbeParam.setId(probeEntity.getId());
        return SqlHelper.retBool(affectedRows);
    }

}

