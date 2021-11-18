package io.shulie.takin.web.data.dao.pradar;

import java.util.List;
import java.util.Objects;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.common.pojo.dto.PageBaseDTO;
import io.shulie.takin.web.common.util.CommonUtil;
import io.shulie.takin.web.data.mapper.mysql.PradarZkConfigMapper;
import io.shulie.takin.web.data.model.mysql.PradarZkConfigEntity;
import io.shulie.takin.web.data.param.pradarconfig.PagePradarZkConfigParam;
import io.shulie.takin.web.data.param.pradarconfig.PradarConfigCreateParam;
import io.shulie.takin.web.data.result.pradarzkconfig.PradarZkConfigResult;
import io.shulie.takin.web.data.util.MPUtil;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author junshao
 * @date 2021/07/08 3:20 下午
 */
@Component
public class PradarZkConfigDAOImpl implements PradarZkConfigDAO, MPUtil<PradarZkConfigEntity> {

    @Autowired
    private PradarZkConfigMapper pradarZkConfigMapper;

    @Override
    public List<PradarZkConfigResult> list() {
        List<PradarZkConfigEntity> result = pradarZkConfigMapper.selectList(this.getLambdaQueryWrapper()
            .select(PradarZkConfigEntity::getZkPath, PradarZkConfigEntity::getValue));
        return CommonUtil.list2list(result, PradarZkConfigResult.class);
    }

    @Override
    public boolean insert(PradarConfigCreateParam createParam) {
        return SqlHelper.retBool(pradarZkConfigMapper.insert(createParam));
    }

    @Override
    public boolean update(PradarConfigCreateParam updateParam) {
        return SqlHelper.retBool(pradarZkConfigMapper.updateById(updateParam));
    }

    @Override
    public int delete(PradarConfigCreateParam deleteParam) {
        if (!Objects.isNull(deleteParam.getId())) {
            return pradarZkConfigMapper.deleteById(deleteParam.getId());
        }
        return 0;
    }

    @Override
    public PradarZkConfigResult getById(Long id) {
        return CommonUtil.copyBeanPropertiesWithNull(pradarZkConfigMapper.selectById(id), PradarZkConfigResult.class);
    }

    @Override
    public PradarZkConfigResult getByZkPath(String zkPath) {
        List<PradarZkConfigResult> configList = pradarZkConfigMapper.selectListByZkPath(zkPath,
            WebPluginUtils.traceTenantId(), WebPluginUtils.traceEnvCode());
        return configList.isEmpty() ? null : configList.get(0);
    }

    @Override
    public IPage<PradarZkConfigResult> page(PagePradarZkConfigParam param, PageBaseDTO pageBaseDTO) {
        return pradarZkConfigMapper.selectPageByTenantIdAndEnvCode(this.setPage(pageBaseDTO), param);
    }

    @Override
    public PagingList<PradarZkConfigResult> page(Long tenantId, String envCode,
        PageBaseDTO pageBaseDTO) {
        IPage<PradarZkConfigEntity> page = pradarZkConfigMapper.selectPage(this.setPage(pageBaseDTO),
            this.getLambdaQueryWrapper().select(PradarZkConfigEntity::getId, PradarZkConfigEntity::getZkPath,
                PradarZkConfigEntity::getValue, PradarZkConfigEntity::getType, PradarZkConfigEntity::getRemark,
                PradarZkConfigEntity::getModifyTime)
                .orderByDesc(PradarZkConfigEntity::getId));
        if (page.getTotal() == 0) {
            return PagingList.empty();
        }
        return PagingList.of(CommonUtil.list2list(page.getRecords(), PradarZkConfigResult.class), page.getTotal());
    }

    @Override
    public boolean deleteById(Long id) {
        return SqlHelper.retBool(pradarZkConfigMapper.deleteById(id));
    }

}
