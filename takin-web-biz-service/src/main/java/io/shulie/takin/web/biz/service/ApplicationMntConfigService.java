package io.shulie.takin.web.biz.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.github.pagehelper.PageHelper;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.pamirs.takin.common.constant.ConfigConstants;
import com.pamirs.takin.common.constant.Constants;
import com.pamirs.takin.common.util.PageInfo;
import com.pamirs.takin.entity.domain.entity.TApplicationMntConfig;
import com.pamirs.takin.entity.domain.entity.TBaseConfig;
import com.pamirs.takin.entity.domain.vo.AgentApplicationConfigVo;
import com.pamirs.takin.entity.domain.vo.TApplicationMntConfigVo;
import io.shulie.takin.web.biz.common.CommonService;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import io.shulie.takin.web.ext.entity.UserExt;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;

@Service
public class ApplicationMntConfigService extends CommonService {


    /**
     * 分页查应用配置
     *
     * @return
     */
    public PageInfo<TApplicationMntConfigVo> queryApplicationConfigPage(Map<String, Object> paramMap) {
        PageHelper.startPage(PageInfo.getPageNum(paramMap), PageInfo.getPageSize(paramMap));
        List<TApplicationMntConfigVo> voList = tApplicationMntConfigDao.queryApplicationConfigPage(paramMap);
        return new PageInfo<>(CollectionUtils.isEmpty(voList) ? Lists.newArrayList() : voList);
    }

    /**
     * agent获取 所有配置
     *
     * @return
     */
    public AgentApplicationConfigVo queryConfig(String applicationName) {
        AgentApplicationConfigVo vo = new AgentApplicationConfigVo();
        vo.setCheatCheck(Constants.N);
        //如果应用名称不为空
        if (StringUtils.isNotEmpty(applicationName)) {
            UserExt user = WebPluginUtils.queryUserByKey();
            TApplicationMntConfig tApplicationMntConfig = tApplicationMntConfigDao.queryByApplicationNameAndUserId(
                applicationName, user == null ? null : user.getId());
            if (tApplicationMntConfig != null && Constants.INTEGER_USE.equals(tApplicationMntConfig.getCheatCheck())) {
                vo.setCheatCheck(Constants.Y);
            }
        }
        //查询全局配置
        TBaseConfig tBaseConfig = tbaseConfigDao.selectByPrimaryKey(ConfigConstants.SQL_CHECK);
        vo.setSqlCheck(Constants.N);
        //不为空  且值是数字  且 是 1
        if (tBaseConfig != null && NumberUtils.isDigits(tBaseConfig.getConfigValue()) && Constants.INTEGER_USE.equals(
            Integer.parseInt(tBaseConfig.getConfigValue()))) {
            vo.setSqlCheck(Constants.Y);
        }
        return vo;
    }

    /**
     * 批量更新
     */
    public void updateConfigBatch(TApplicationMntConfigVo configVo) {
        int cheatCheck = Integer.parseInt(configVo.getCheatCheck());
        List<String> applicationIdList = Splitter.on(",").omitEmptyStrings().trimResults().splitToList(
            configVo.getApplicationIds()).stream().distinct().collect(Collectors.toList());
        //先根据应用查出所有的
        List<TApplicationMntConfig> configList = tApplicationMntConfigDao.queryByApplicationIdList(applicationIdList);
        Date now = new Date();
        if (CollectionUtils.isEmpty(configList)) {
            configList = new ArrayList<>();
            for (String appId : applicationIdList) {
                TApplicationMntConfig mntConfig = new TApplicationMntConfig();
                mntConfig.setTamcId(snowflake.next());
                mntConfig.setApplicationId(Long.parseLong(appId));
                mntConfig.setCheatCheck(cheatCheck);
                mntConfig.setCreateTime(now);
                mntConfig.setUpdateTime(now);
                configList.add(mntConfig);
            }
            tApplicationMntConfigDao.insertList(configList);
        } else {
            batchInsertAndUpdate(cheatCheck, applicationIdList, configList, now);
        }
    }

    /**
     * 批量插入 与 更新
     */
    private void batchInsertAndUpdate(int cheatCheck, List<String> applicationIdList,
        List<TApplicationMntConfig> configList, Date now) {
        //先批量更新
        tApplicationMntConfigDao.updateCheatRuleByApplicationIdList(applicationIdList, cheatCheck);
        //如果数量不对需要插入
        if (configList.size() != applicationIdList.size()) {
            List<String> configAppIdList = new ArrayList<>();
            for (TApplicationMntConfig config : configList) {
                configAppIdList.add(config.getApplicationId().toString());
            }
            applicationIdList.removeAll(configAppIdList);
            configList = new ArrayList<>();
            for (String appId : applicationIdList) {
                TApplicationMntConfig mntConfig = new TApplicationMntConfig();
                mntConfig.setTamcId(snowflake.next());
                mntConfig.setApplicationId(Long.parseLong(appId));
                mntConfig.setCheatCheck(cheatCheck);
                mntConfig.setCreateTime(now);
                mntConfig.setUpdateTime(now);
                configList.add(mntConfig);
            }
            tApplicationMntConfigDao.insertList(configList);
        }
    }

}
