package io.shulie.takin.web.data.mapper.mysql;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pamirs.takin.entity.domain.vo.application.NodeNumParam;
import io.shulie.takin.web.data.model.mysql.ApplicationAttentionListEntity;
import io.shulie.takin.web.data.model.mysql.ApplicationMntEntity;
import io.shulie.takin.web.data.param.application.ApplicationAttentionParam;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ApplicationAttentionListMapper extends BaseMapper<ApplicationAttentionListEntity> {


    List<ApplicationAttentionListEntity> getAttentionList(ApplicationAttentionParam param);

    void attendApplicationService(Map<String, String> param);
}
