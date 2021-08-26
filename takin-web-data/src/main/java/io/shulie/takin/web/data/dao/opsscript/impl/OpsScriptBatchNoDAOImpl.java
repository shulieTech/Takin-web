package io.shulie.takin.web.data.dao.opsscript.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.shulie.takin.web.data.dao.opsscript.OpsScriptBatchNoDAO;
import io.shulie.takin.web.data.model.mysql.OpsScriptBatchNoEntity;
import io.shulie.takin.web.data.mapper.mysql.OpsScriptBatchNoMapper;
import io.shulie.takin.web.data.util.MPUtil;
import org.springframework.stereotype.Service;

/**
 * 运维脚本批次号表(OpsScriptBatchNo)表数据库 dao
 *
 * @author caijy
 * @since 2021-06-16 10:35:41
 */
@Service
public class OpsScriptBatchNoDAOImpl
        extends ServiceImpl<OpsScriptBatchNoMapper, OpsScriptBatchNoEntity>
        implements OpsScriptBatchNoDAO, MPUtil<OpsScriptBatchNoEntity> {

}

