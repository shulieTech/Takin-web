package io.shulie.takin.web.data.dao.application.impl;

import cn.hutool.core.convert.Convert;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.shulie.takin.web.data.dao.application.ApplicationPluginDownloadPathDAO;
import io.shulie.takin.web.data.mapper.mysql.ApplicationPluginDownloadPathMapper;
import io.shulie.takin.web.data.model.mysql.ApplicationPluginDownloadPathEntity;
import io.shulie.takin.web.data.param.application.CreateApplicationPluginDownloadPathParam;
import io.shulie.takin.web.data.param.application.UpdateApplicationPluginDownloadPathParam;
import io.shulie.takin.web.data.result.application.ApplicationPluginDownloadPathDetailResult;
import io.shulie.takin.web.data.util.MPUtil;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * 探针根目录(ApplicationPluginDownloadPath)表数据库 dao 层实现
 *
 * @author 南风
 * @date 2021-11-10 16:12:07
 */
@Service
public class ApplicationPluginDownloadPathDAOImpl extends ServiceImpl<ApplicationPluginDownloadPathMapper, ApplicationPluginDownloadPathEntity>
        implements ApplicationPluginDownloadPathDAO, MPUtil<ApplicationPluginDownloadPathEntity> {

    private LambdaQueryWrapper<ApplicationPluginDownloadPathEntity> buildQuery(LambdaQueryWrapper<ApplicationPluginDownloadPathEntity> LambdaQueryWrapper){
        if(Objects.isNull(LambdaQueryWrapper)){
            return this.getLambdaQueryWrapper().eq(ApplicationPluginDownloadPathEntity::getIsDeleted,0);
        }else{
           return LambdaQueryWrapper.eq(ApplicationPluginDownloadPathEntity::getIsDeleted,0);
        }
    }

    private ApplicationPluginDownloadPathDetailResult convertResult(ApplicationPluginDownloadPathEntity entity){
        return Convert.convert(ApplicationPluginDownloadPathDetailResult.class,entity);
    }

    private void createOrUpdate(ApplicationPluginDownloadPathEntity entity){
        this.saveOrUpdate(entity);
    }


    @Override
    public ApplicationPluginDownloadPathDetailResult queryDetailByCustomerId() {
        LambdaQueryWrapper<ApplicationPluginDownloadPathEntity> queryWrapper = this.buildQuery(this.getCustomerQueryWrapper().lambda());
        return this.convertResult(this.getOne(queryWrapper));
    }


    @Override
    public void createConfig(CreateApplicationPluginDownloadPathParam createParam) {
        this.createOrUpdate(createParam);
    }


    @Override
    public void updateConfig(UpdateApplicationPluginDownloadPathParam updateParam) {
        this.saveOrUpdate(updateParam);
    }
}

