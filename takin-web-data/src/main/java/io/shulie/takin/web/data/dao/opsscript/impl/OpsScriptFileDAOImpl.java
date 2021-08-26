package io.shulie.takin.web.data.dao.opsscript.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.shulie.takin.web.data.dao.opsscript.OpsScriptFileDAO;
import io.shulie.takin.web.data.model.mysql.OpsScriptFileEntity;
import io.shulie.takin.web.data.mapper.mysql.OpsScriptFileMapper;
import io.shulie.takin.web.data.util.MPUtil;
import org.springframework.stereotype.Service;

/**
 * 运维脚本文件(OpsScriptFile)表数据库 dao
 *
 * @author caijy
 * @since 2021-06-16 10:35:39
 */
@Service
public class OpsScriptFileDAOImpl
        extends ServiceImpl<OpsScriptFileMapper, OpsScriptFileEntity>
        implements OpsScriptFileDAO, MPUtil<OpsScriptFileEntity> {

}

