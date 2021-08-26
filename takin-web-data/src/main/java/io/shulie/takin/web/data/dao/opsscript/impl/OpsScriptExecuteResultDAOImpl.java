package io.shulie.takin.web.data.dao.opsscript.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.shulie.takin.web.data.dao.opsscript.OpsScriptExecuteResultDAO;
import io.shulie.takin.web.data.model.mysql.OpsScriptExecuteResultEntity;
import io.shulie.takin.web.data.mapper.mysql.OpsScriptExecuteResultMapper;
import io.shulie.takin.web.data.util.MPUtil;
import org.springframework.stereotype.Service;

/**
 * 运维脚本执行结果(OpsScriptExecuteResult)表数据库 dao
 *
 * @author caijy
 * @since 2021-06-16 10:35:41
 */
@Service
public class OpsScriptExecuteResultDAOImpl
        extends ServiceImpl<OpsScriptExecuteResultMapper, OpsScriptExecuteResultEntity>
        implements OpsScriptExecuteResultDAO, MPUtil<OpsScriptExecuteResultEntity> {

}

