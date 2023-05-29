package io.shulie.takin.web.data.dao.opsscript;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.shulie.takin.web.data.model.mysql.OpsScriptManageEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import io.shulie.takin.web.data.param.opsscript.OpsScriptParam;

import java.util.List;

/**
 * 运维脚本主表(OpsScriptManage)表数据库 dao
 *
 * @author caijy
 * @since 2021-06-16 10:35:40
 */
public interface OpsScriptManageDAO extends IService<OpsScriptManageEntity> {

    IPage<OpsScriptManageEntity> findListPage(OpsScriptParam param, LambdaQueryWrapper<OpsScriptManageEntity> wrapper);

    List<OpsScriptManageEntity> findList(OpsScriptParam param);

    Boolean allocationUser(Long dataId, Long userId,Long deptId);
}

