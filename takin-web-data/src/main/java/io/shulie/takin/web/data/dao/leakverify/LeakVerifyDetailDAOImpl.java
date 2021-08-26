package io.shulie.takin.web.data.dao.leakverify;

import java.util.List;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Lists;
import io.shulie.takin.web.data.mapper.mysql.LeakVerifyDetailMapper;
import io.shulie.takin.web.data.model.mysql.LeakVerifyDetailEntity;
import io.shulie.takin.web.data.param.leakverify.LeakVerifyDetailCreateParam;
import io.shulie.takin.web.data.param.leakverify.LeakVerifyDetailQueryParam;
import io.shulie.takin.web.data.result.leakverify.LeakVerifyDetailResult;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author fanxx
 * @date 2021/1/5 8:24 下午
 */
@Component
public class LeakVerifyDetailDAOImpl implements LeakVerifyDetailDAO {

    @Autowired
    private LeakVerifyDetailMapper detailMapper;

    @Override
    public int insert(LeakVerifyDetailCreateParam createParam) {
        return 0;
    }

    @Override
    public int insertBatch(List<LeakVerifyDetailCreateParam> createParamList) {
        if (CollectionUtils.isNotEmpty(createParamList)) {
            List<LeakVerifyDetailEntity> entityList = createParamList.stream().map(createParam -> {
                LeakVerifyDetailEntity entity = new LeakVerifyDetailEntity();
                BeanUtils.copyProperties(createParam, entity);
                return entity;
            }).collect(Collectors.toList());
            entityList.forEach(entity -> detailMapper.insert(entity));
        }
        return 0;
    }

    @Override
    public List<LeakVerifyDetailResult> selectList(LeakVerifyDetailQueryParam queryParam) {
        List<LeakVerifyDetailResult> resultList = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(queryParam.getResultIdList())) {
            LambdaQueryWrapper<LeakVerifyDetailEntity> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.in(LeakVerifyDetailEntity::getResultId, queryParam.getResultIdList());
            queryWrapper.eq(LeakVerifyDetailEntity::getIsDeleted, 0);
            queryWrapper.select(LeakVerifyDetailEntity::getResultId,
                    LeakVerifyDetailEntity::getLeakSql,
                    LeakVerifyDetailEntity::getStatus);
            List<LeakVerifyDetailEntity> entityList = detailMapper.selectList(queryWrapper);
            if (CollectionUtils.isNotEmpty(entityList)) {
                resultList = entityList.stream().map(entity -> {
                    LeakVerifyDetailResult result = new LeakVerifyDetailResult();
                    result.setResultId(entity.getResultId());
                    result.setLeakSql(entity.getLeakSql());
                    result.setStatus(entity.getStatus());
                    return result;
                }).collect(Collectors.toList());
            }
        }
        return resultList;
    }
}
