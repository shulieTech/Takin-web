package io.shulie.takin.web.biz.init.fix;

import javax.annotation.Resource;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.shulie.takin.web.common.util.ActivityUtil;
import io.shulie.takin.web.data.mapper.mysql.BusinessLinkManageTableMapper;
import io.shulie.takin.web.data.model.mysql.BusinessLinkManageTableEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * @author zhaoyong
 */
@Component
@Slf4j
public class ActivityFixer {

    @Resource
    private BusinessLinkManageTableMapper businessLinkManageTableMapper;

    /**
     * 将历史数据业务活动字段entrance字段中的applicationName拆分出来
     */
    public void fix() {
        int current = 1;
        while (true){
            Page<BusinessLinkManageTableEntity> page = new Page<>();
            page.setCurrent(current);
            page.setSize(500);
            LambdaQueryWrapper<BusinessLinkManageTableEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(BusinessLinkManageTableEntity::getType,0);
            lambdaQueryWrapper.isNull(BusinessLinkManageTableEntity::getApplicationName);
            //查询所有普通类型,并且应用名为空的业务活动
            Page<BusinessLinkManageTableEntity> businessLinkManageTableEntityPage = businessLinkManageTableMapper.selectPage(page, lambdaQueryWrapper);
            if (businessLinkManageTableEntityPage != null && CollectionUtils.isNotEmpty(businessLinkManageTableEntityPage.getRecords())) {
                businessLinkManageTableEntityPage.getRecords().forEach(businessLinkManageTableEntity -> {
                    String entrace = businessLinkManageTableEntity.getEntrace();
                    String[] split = StringUtils.split(entrace, "\\|");
                    if (split.length != 4){
                        return;
                    }
                    businessLinkManageTableEntity.setApplicationName(split[0]);
                    businessLinkManageTableEntity.setEntrace(ActivityUtil.buildEntrance(split[0],split[1],split[2],split[3]));
                    businessLinkManageTableMapper.updateById(businessLinkManageTableEntity);
                });
            }

            //查询结果不够500条，结束循环
            if (businessLinkManageTableEntityPage == null || businessLinkManageTableEntityPage.getTotal() < 500){
                break;
            }
            current ++;
        }

    }
}
