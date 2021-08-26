package io.shulie.takin.web.data.dao.application;

import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.shulie.takin.web.data.mapper.mysql.WhiteListMapper;
import io.shulie.takin.web.data.model.mysql.WhiteListEntity;
import io.shulie.takin.web.data.param.application.ApplicationWhiteListCreateParam;
import io.shulie.takin.web.data.param.application.ApplicationWhiteListUpdateParam;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author fanxx
 * @date 2020/11/9 9:19 下午
 */
@Component
public class ApplicationWhiteListDAOImpl implements ApplicationWhiteListDAO {

    @Autowired
    private WhiteListMapper whiteListMapper;

    @Override
    public int insert(ApplicationWhiteListCreateParam param) {
        WhiteListEntity entity = new WhiteListEntity();
        BeanUtils.copyProperties(param, entity);
        return whiteListMapper.insert(entity);
    }

    @Override
    public int insertBatch(List<ApplicationWhiteListCreateParam> paramList) {
        int count = 0;
        if (CollectionUtils.isNotEmpty(paramList)) {
            for (ApplicationWhiteListCreateParam param : paramList) {
                WhiteListEntity entity = new WhiteListEntity();
                entity.setInterfaceName(param.getInterfaceName());
                entity.setType(param.getType());
                entity.setDictType(param.getDictType());
                entity.setUseYn(Integer.parseInt(param.getUseYn()));
                entity.setApplicationId(Long.parseLong(param.getApplicationId()));
                entity.setCustomerId(param.getCustomerId());
                entity.setUserId(param.getUserId());
                entity.setIsHandwork(param.getIsHandwork());
                entity.setIsGlobal(param.getIsGlobal());
                count = count + whiteListMapper.insert(entity);
            }
        }
        return count;
    }

    @Override
    public int allocationUser(ApplicationWhiteListUpdateParam param) {
        if (StringUtils.isBlank(param.getApplicationId())) {
            return 0;
        }
        LambdaQueryWrapper<WhiteListEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WhiteListEntity::getApplicationId, param.getApplicationId());
        List<WhiteListEntity> whiteListEntityList = whiteListMapper.selectList(queryWrapper);
        if (CollectionUtils.isNotEmpty(whiteListEntityList)) {
            for (WhiteListEntity entity : whiteListEntityList) {
                entity.setUserId(param.getUserId());
                whiteListMapper.updateById(entity);
            }
        }
        return 0;
    }
}
