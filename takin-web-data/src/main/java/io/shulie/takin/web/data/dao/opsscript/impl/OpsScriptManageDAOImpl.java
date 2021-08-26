package io.shulie.takin.web.data.dao.opsscript.impl;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.shulie.takin.utils.string.StringUtil;
import io.shulie.takin.web.data.dao.opsscript.OpsScriptManageDAO;
import io.shulie.takin.web.data.mapper.mysql.OpsScriptManageMapper;
import io.shulie.takin.web.data.model.mysql.OpsScriptManageEntity;
import io.shulie.takin.web.data.param.opsscript.OpsScriptParam;
import io.shulie.takin.web.data.util.MPUtil;
import org.springframework.stereotype.Service;

/**
 * 运维脚本主表(OpsScriptManage)表数据库 dao
 *
 * @author caijy
 * @since 2021-06-16 10:35:40
 */
@Service
public class OpsScriptManageDAOImpl
        extends ServiceImpl<OpsScriptManageMapper, OpsScriptManageEntity>
        implements OpsScriptManageDAO, MPUtil<OpsScriptManageEntity> {


    @Override
    public IPage<OpsScriptManageEntity> findListPage(OpsScriptParam param, LambdaQueryWrapper<OpsScriptManageEntity> wrapper) {
        if (wrapper == null) {
            wrapper = getLambdaQueryWrapper();
        }
        wrapper.like(StringUtil.isNotBlank(param.getName()), OpsScriptManageEntity::getName, param.getName());
        if (Objects.nonNull(param.getScriptType())) {
            wrapper.eq(OpsScriptManageEntity::getScriptType, param.getScriptType());
        }

        return this.page(setPage(param.getCurrent() + 1, param.getPageSize()),
                wrapper);
    }

    @Override
    public List<OpsScriptManageEntity> findList(OpsScriptParam param) {
        LambdaQueryWrapper<OpsScriptManageEntity> wrapper = getLambdaQueryWrapper();
        wrapper.eq(OpsScriptManageEntity::getIsDeleted, 0);
        if (StringUtil.isNotBlank(param.getName())) {
            wrapper.like(OpsScriptManageEntity::getName, param.getName());
        }
        if (Objects.nonNull(param.getScriptType())) {
            wrapper.eq(OpsScriptManageEntity::getScriptType, param.getScriptType());
        }
        if (param.getCustomerId() != null) {
            wrapper.eq(OpsScriptManageEntity::getCustomerId, param.getCustomerId());
        }
        return this.list(wrapper);
    }

    @Override
    public Boolean allocationUser(Long dataId, Long userId) {
        OpsScriptManageEntity one = this.lambdaQuery().eq(OpsScriptManageEntity::getId, dataId).one();
        if (one != null) {
            return this.lambdaUpdate()
                    .set(OpsScriptManageEntity::getUserId, userId)
                    .set(OpsScriptManageEntity::getGmtUpdate, new Date())
                    .eq(OpsScriptManageEntity::getId, dataId).update();
        }
        return false;
    }
}

