package io.shulie.takin.web.data.dao.sys;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.shulie.takin.web.data.mapper.mysql.VersionMapper;
import io.shulie.takin.web.data.model.mysql.VersionEntity;
import io.shulie.takin.web.data.util.MPUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
public class VersionDAOImpl extends ServiceImpl<VersionMapper, VersionEntity>
    implements VersionDAO, MPUtil<VersionEntity> {

    @Override
    public VersionEntity selectLeast() {
        LambdaQueryWrapper<VersionEntity> wrapper = this.getLimitOneLambdaQueryWrapper()
            .orderByDesc(VersionEntity::getCreateTime);
        return getOne(wrapper);
    }

    /**
     * 根据condition查找指定版本号的最新版本
     *
     * @param eq      是否等于， true-等于
     * @param version 指定版本号
     * @return 非指定版本号的最新版本信息
     */
    @Override
    public VersionEntity selectLeastOnVersionCondition(boolean eq, String version) {
        LambdaQueryWrapper<VersionEntity> wrapper = this.getLimitOneLambdaQueryWrapper();
        if (eq) {
            wrapper.eq(VersionEntity::getVersion, version);
        } else {
            wrapper.ne(VersionEntity::getVersion, version);
        }
        wrapper.orderByDesc(VersionEntity::getCreateTime);
        return this.getOne(wrapper);
    }

    @Override
    public boolean exists(String version) {
        if (StringUtils.isBlank(version)) {
            return false;
        }
        LambdaQueryWrapper<VersionEntity> wrapper = this.getLimitOneLambdaQueryWrapper()
            .eq(VersionEntity::getVersion, version);
        return this.getOne(wrapper) != null;
    }

    @Override
    public boolean publish(VersionEntity entity) {
        String version;
        if (entity == null || StringUtils.isBlank(version = entity.getVersion()) || exists(version)) {
            log.info("发布版本号为空或已存在，新增版本失败");
            return false;
        }
        return this.save(entity);
    }
}
