package io.shulie.takin.web.biz.service.blacklist;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.google.common.collect.Lists;
import com.pamirs.takin.entity.dao.confcenter.TApplicationMntDao;
import com.pamirs.takin.entity.domain.entity.TApplicationMnt;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.biz.cache.AgentConfigCacheManager;
import io.shulie.takin.web.biz.constant.BizOpConstants.Vars;
import io.shulie.takin.web.biz.init.sync.ConfigSyncService;
import io.shulie.takin.web.biz.pojo.input.blacklist.BlacklistCreateInput;
import io.shulie.takin.web.biz.pojo.input.blacklist.BlacklistSearchInput;
import io.shulie.takin.web.biz.pojo.input.blacklist.BlacklistUpdateInput;
import io.shulie.takin.web.biz.pojo.output.blacklist.BlacklistOutput;
import io.shulie.takin.web.biz.service.linkManage.impl.WhiteListFileService;
import io.shulie.takin.web.common.context.OperationLogContextHolder;
import io.shulie.takin.web.common.enums.blacklist.BlacklistTypeEnum;
import io.shulie.takin.web.common.exception.ExceptionCode;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import io.shulie.takin.web.common.vo.blacklist.BlacklistVO;
import io.shulie.takin.web.data.dao.blacklist.BlackListDAO;
import io.shulie.takin.web.data.param.blacklist.BlacklistCreateNewParam;
import io.shulie.takin.web.data.param.blacklist.BlacklistSearchParam;
import io.shulie.takin.web.data.param.blacklist.BlacklistUpdateParam;
import io.shulie.takin.web.data.result.blacklist.BlacklistResult;
import io.shulie.takin.web.ext.entity.UserExt;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author 无涯
 * @date 2021/4/6 2:15 下午
 */
@Service
public class BlacklistServiceImpl implements BlacklistService {
    @Autowired
    private BlackListDAO blackListDAO;
    @Autowired
    private ConfigSyncService configSyncService;
    @Autowired
    private WhiteListFileService whiteListFileService;
    @Resource
    private TApplicationMntDao applicationMntDao;
    @Autowired
    private AgentConfigCacheManager agentConfigCacheManager;

    @Override
    public void insert(BlacklistCreateInput input) {
        BlacklistSearchParam searchParam = new BlacklistSearchParam();
        searchParam.setApplicationId(input.getApplicationId());
        searchParam.setRedisKey(input.getRedisKey());
        List<BlacklistResult> results = blackListDAO.selectList(searchParam);
        if (CollectionUtils.isNotEmpty(results)) {
            throw new TakinWebException(ExceptionCode.BLACKLIST_ADD_ERROR, "不允许重复黑名单");
        }
        BlacklistCreateNewParam param = new BlacklistCreateNewParam();
        BeanUtils.copyProperties(input, param);
        WebPluginUtils.fillUserData(param);
        // 目前就redis
        param.setType(BlacklistTypeEnum.REDIS.getType());
        param.setUseYn(1);
        blackListDAO.newInsert(param);
        // 刷新agent数据
        updateAgentData(input.getApplicationId());
    }

    @Override
    public void update(BlacklistUpdateInput input) {
        BlacklistResult result = blackListDAO.selectById(input.getBlistId());
        if (result == null) {
            throw new TakinWebException(ExceptionCode.BLACKLIST_UPDATE_ERROR, "未查到该id");
        }
        BlacklistSearchParam searchParam = new BlacklistSearchParam();
        searchParam.setApplicationId(result.getApplicationId());
        searchParam.setRedisKey(input.getRedisKey());
        List<BlacklistResult> results = blackListDAO.selectList(searchParam);
        if (!result.getRedisKey().equals(input.getRedisKey()) && CollectionUtils.isNotEmpty(results)) {
            throw new TakinWebException(ExceptionCode.BLACKLIST_UPDATE_ERROR, "不允许重复黑名单");
        }

        BlacklistUpdateParam param = new BlacklistUpdateParam();
        BeanUtils.copyProperties(input, param);
        blackListDAO.update(param);
        updateAgentData(result.getApplicationId());
    }

    @Override
    public void enable(BlacklistUpdateInput input) {
        BlacklistResult result = blackListDAO.selectById(input.getBlistId());
        if (result == null) {
            throw new TakinWebException(ExceptionCode.BLACKLIST_UPDATE_ERROR, "未查到该id");
        }
        BlacklistUpdateParam param = new BlacklistUpdateParam();
        BeanUtils.copyProperties(input, param);
        blackListDAO.update(param);
        updateAgentData(result.getApplicationId());
    }

    private void updateAgentData(Long applicationId) {
        // 刷新agent数据
        TApplicationMnt tApplicationMnt = applicationMntDao.queryApplicationinfoById(applicationId);
        whiteListFileService.writeWhiteListFile();
        configSyncService.syncAllowList(WebPluginUtils.getUserAppKey(tApplicationMnt.getUserId()), applicationId, tApplicationMnt.getApplicationName());
        agentConfigCacheManager.evictRecallCalls(tApplicationMnt.getApplicationName());
    }

    @Override
    public void batchEnable(List<Long> ids, Integer useYn) {
        List<BlacklistResult> results = blackListDAO.selectByIds(ids);
        if (CollectionUtils.isEmpty(results)) {
            throw new TakinWebException(ExceptionCode.BLACKLIST_DELETE_ERROR, "批量更新失败");
        }
        OperationLogContextHolder.addVars(Vars.BLACKLIST_VALUE, results.stream()
            .map(BlacklistResult::getRedisKey).collect(Collectors.joining(",")));
        ids.forEach(id -> {
            BlacklistUpdateParam param = new BlacklistUpdateParam();
            param.setBlistId(id);
            param.setUseYn(useYn);
            blackListDAO.update(param);
        });
        updateAgentData(results.get(0).getApplicationId());
    }

    @Override
    public void delete(Long id) {
        BlacklistResult result = blackListDAO.selectById(id);
        if (result == null) {
            throw new TakinWebException(ExceptionCode.BLACKLIST_DELETE_ERROR, "未查到该id");
        }
        OperationLogContextHolder.addVars(Vars.BLACKLIST_VALUE, result.getRedisKey());
        OperationLogContextHolder.addVars(Vars.BLACKLIST_TYPE, BlacklistTypeEnum.getDescByType(result.getType()));
        blackListDAO.delete(id);
        updateAgentData(result.getApplicationId());
    }

    @Override
    public void batchDelete(List<Long> ids) {
        List<BlacklistResult> results = blackListDAO.selectByIds(ids);
        if (CollectionUtils.isEmpty(results)) {
            throw new TakinWebException(ExceptionCode.BLACKLIST_DELETE_ERROR, "批量删除失败");
        }
        OperationLogContextHolder.addVars(Vars.BLACKLIST_VALUE, results.stream()
            .map(BlacklistResult::getRedisKey).collect(Collectors.joining(",")));
        blackListDAO.batchDelete(ids);
        updateAgentData(results.get(0).getApplicationId());
    }

    @Override
    public PagingList<BlacklistVO> pageList(BlacklistSearchInput input) {
        BlacklistSearchParam param = new BlacklistSearchParam();
        BeanUtils.copyProperties(input, param);
        PagingList<BlacklistVO> pagingList = blackListDAO.pageList(param);
        if (pagingList.isEmpty()) {
            return PagingList.of(Lists.newArrayList(),pagingList.getTotal());
        }
        for (BlacklistVO vo : pagingList.getList()) {
            WebPluginUtils.fillQueryResponse(vo);
        }
        return pagingList;
    }

    @Override
    public List<BlacklistOutput> selectList(BlacklistSearchInput input) {
        BlacklistSearchParam param = new BlacklistSearchParam();
        BeanUtils.copyProperties(input, param);
        UserExt user = WebPluginUtils.getUser();
        if (user != null) {
            param.setCustomerId(WebPluginUtils.getCustomerId());
            param.setUserId(user.getId());
        }
        List<BlacklistResult> results = blackListDAO.selectList(param);
        if (CollectionUtils.isEmpty(results)) {
            return Lists.newArrayList();
        }
        return results.stream().map(result -> {
            BlacklistOutput output = new BlacklistOutput();
            BeanUtils.copyProperties(result, output);
            return output;
        }).collect(Collectors.toList());
    }

    @Override
    public BlacklistOutput selectById(Long id) {
        BlacklistResult result = blackListDAO.selectById(id);
        if (result == null) {
            return null;
        }
        BlacklistOutput output = new BlacklistOutput();
        BeanUtils.copyProperties(result, output);
        return output;
    }
}
