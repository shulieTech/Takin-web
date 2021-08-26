package io.shulie.takin.web.data.dao.exception;

import java.util.List;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.shulie.takin.web.data.mapper.custom.exception.CustomExceptionInfoMapper;
import io.shulie.takin.web.data.model.mysql.ExceptionInfoEntity;
import io.shulie.takin.web.data.param.exception.ExceptionParam;
import io.shulie.takin.web.data.result.exception.ExceptionResult;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * @author 无涯
 * @date 2021/1/4 7:38 下午
 */
@Repository
public class ExceptionDaoImpl implements io.shulie.takin.web.data.dao.exception.ExceptionDao {

    @Autowired
    private CustomExceptionInfoMapper customExceptionInfoMapper;

    @Override
    public void insert(ExceptionParam param) {
        ExceptionInfoEntity entity = new ExceptionInfoEntity();
        BeanUtils.copyProperties(param, entity);
        customExceptionInfoMapper.saveOrUpdate(entity);
    }

    @Override
    public List<ExceptionResult> getList() {
        List<ExceptionInfoEntity> entities = customExceptionInfoMapper.list();
        List<ExceptionResult> results = entities.stream().map(exceptionInfoEntity -> {
            ExceptionResult result = new ExceptionResult();
            BeanUtils.copyProperties(exceptionInfoEntity, result);
            return result;
        }).collect(Collectors.toList());
        return results;
    }

    @Override
    public ExceptionResult getByAgentCode(String agentCode) {
        LambdaQueryWrapper<ExceptionInfoEntity> wrapper = new LambdaQueryWrapper();
        wrapper.eq(ExceptionInfoEntity::getAgentCode, agentCode);
        wrapper.orderByDesc(ExceptionInfoEntity::getGmtCreate);
        List<ExceptionInfoEntity> entities = customExceptionInfoMapper.list(wrapper);
        if (entities == null || entities.size() == 0) {
            return null;
        }
        ExceptionResult result = new ExceptionResult();
        BeanUtils.copyProperties(entities.get(0), result);
        return result;
    }
}
