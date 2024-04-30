package io.shulie.takin.web.data.dao.agent;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.shulie.takin.web.common.util.DataTransformUtil;
import io.shulie.takin.web.data.mapper.mysql.AgentMockDataMapper;
import io.shulie.takin.web.data.model.mysql.AgentMockDataEntity;
import io.shulie.takin.web.data.param.agent.AgentMockDataCreateParam;
import io.shulie.takin.web.data.result.agent.AgentMockDataResult;
import io.shulie.takin.web.data.util.MPUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AgentMockDataDAOImpl extends ServiceImpl<AgentMockDataMapper, AgentMockDataEntity>
        implements AgentMockDataDAO, MPUtil<AgentMockDataEntity> {

    @Override
    public void insert(AgentMockDataCreateParam param) {
        AgentMockDataEntity entity = new AgentMockDataEntity();
        BeanUtils.copyProperties(param, entity);
        this.baseMapper.insertData(entity);
    }

    @Override
    public List<AgentMockDataResult> getMockDataListByReportId(Long reportId) {
        List<AgentMockDataEntity> list = this.baseMapper.selectListByReportId(reportId);
        return DataTransformUtil.list2list(list, AgentMockDataResult.class);
    }
}
