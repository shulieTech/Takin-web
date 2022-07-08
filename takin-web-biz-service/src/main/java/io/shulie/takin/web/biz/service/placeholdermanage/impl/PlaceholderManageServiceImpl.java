package io.shulie.takin.web.biz.service.placeholdermanage.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.utils.string.StringUtil;
import io.shulie.takin.web.biz.convert.placeholdermanage.PlaceholderManageConvert;
import io.shulie.takin.web.biz.pojo.request.placeholdermanage.PlaceholderManagePageRequest;
import io.shulie.takin.web.biz.pojo.request.placeholdermanage.PlaceholderManageRequest;
import io.shulie.takin.web.biz.pojo.response.placeholdermanage.PlaceholderManageResponse;
import io.shulie.takin.web.biz.service.placeholdermanage.PlaceholderManageService;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.exception.TakinWebExceptionEnum;
import io.shulie.takin.web.data.dao.placeholdermanage.PlaceholderManageDAO;
import io.shulie.takin.web.data.model.mysql.PlaceholderManageEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 占位符管理
 */
@Slf4j
@Component
public class PlaceholderManageServiceImpl implements PlaceholderManageService {

    @Resource
    private PlaceholderManageDAO placeholderManageDAO;

    private static final String placeholderKeyPre = "EV_";

    @Override
    public void createPlaceholder(PlaceholderManageRequest createRequest) {
        if (!createRequest.getPlaceholderKey().startsWith(placeholderKeyPre)) {
            throw new TakinWebException(TakinWebExceptionEnum.PLACEHOLDER_MANAGE_CREATE_ERROR, "占位符的标识必须以" + placeholderKeyPre + "开头！");
        }
        QueryWrapper<PlaceholderManageEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(PlaceholderManageEntity::getPlaceholderKey, createRequest.getPlaceholderKey());
        List<PlaceholderManageEntity> list = placeholderManageDAO.list(queryWrapper);
        if (CollectionUtils.isNotEmpty(list)) {
            throw new TakinWebException(TakinWebExceptionEnum.PLACEHOLDER_MANAGE_CREATE_ERROR, "当前占位符的key已经被使用，请修改后再进行创建！");
        }

        PlaceholderManageEntity placeholderManageEntity = new PlaceholderManageEntity();
        placeholderManageEntity.setPlaceholderKey(createRequest.getPlaceholderKey());
        placeholderManageEntity.setPlaceholderValue(createRequest.getPlaceholderValue());
        placeholderManageEntity.setRemark(createRequest.getRemark());
        placeholderManageDAO.save(placeholderManageEntity);
    }

    @Override
    public void updatePlaceholder(PlaceholderManageRequest request) {
        PlaceholderManageEntity byId = placeholderManageDAO.getById(request.getId());
        if (byId == null) {
            throw new TakinWebException(TakinWebExceptionEnum.PLACEHOLDER_MANAGE_UPDATE_ERROR, "修改的占位符不存在，请刷新页面后再试！");
        }
        if (request.getPlaceholderKey() != null && !byId.getPlaceholderKey().equals(request.getPlaceholderKey())) {
            QueryWrapper<PlaceholderManageEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(PlaceholderManageEntity::getPlaceholderKey, request.getPlaceholderKey());
            List<PlaceholderManageEntity> list = placeholderManageDAO.list(queryWrapper);
            if (CollectionUtils.isNotEmpty(list)) {
                throw new TakinWebException(TakinWebExceptionEnum.PLACEHOLDER_MANAGE_UPDATE_ERROR, "当前占位符的key已经被使用！");
            }
        }
        if (StringUtil.isNotEmpty(request.getPlaceholderKey())) {
            if (!request.getPlaceholderKey().startsWith(placeholderKeyPre)) {
                throw new TakinWebException(TakinWebExceptionEnum.PLACEHOLDER_MANAGE_UPDATE_ERROR, "占位符的标识必须以" + placeholderKeyPre + "开头！");
            }
            byId.setPlaceholderKey(request.getPlaceholderKey());
        }
        if (StringUtil.isNotEmpty(request.getPlaceholderValue())) {
            byId.setPlaceholderValue(request.getPlaceholderValue());
        }
        if (StringUtil.isNotEmpty(request.getRemark())) {
            byId.setRemark(request.getRemark());
        }
        byId.setUpdateTime(new Date());
        placeholderManageDAO.updateById(byId);
    }

    @Override
    public void deletePlaceholder(Long id) {
        PlaceholderManageEntity byId = placeholderManageDAO.getById(id);
        if (byId == null) {
            return;
        }
        log.info("删除占位符:{}", id);
        placeholderManageDAO.removeById(id);
    }

    @Override
    public PagingList<PlaceholderManageResponse> listPlaceholder(PlaceholderManagePageRequest request) {
        Page<PlaceholderManageEntity> page = new Page<>(request.getCurrent() + 1, request.getPageSize());
        QueryWrapper<PlaceholderManageEntity> wrapper = new QueryWrapper<>();
        if (StringUtil.isNotEmpty(request.getPlaceholderKey())) {
            wrapper.lambda().eq(PlaceholderManageEntity::getPlaceholderKey, request.getPlaceholderKey());
        }
        if (StringUtil.isNotEmpty(request.getPlaceholderValue())) {
            wrapper.lambda().like(PlaceholderManageEntity::getPlaceholderValue, request.getPlaceholderValue());
        }
        if (StringUtil.isNotEmpty(request.getRemark())) {
            wrapper.lambda().like(PlaceholderManageEntity::getRemark, request.getRemark());
        }
        Page<PlaceholderManageEntity> placeholderManageEntityPage = placeholderManageDAO.page(page, wrapper);
        if (placeholderManageEntityPage == null) {
            return PagingList.empty();
        }
        List<PlaceholderManageEntity> records = placeholderManageEntityPage.getRecords();
        List<PlaceholderManageResponse> placeholderManageResponses = PlaceholderManageConvert.INSTANCE.ofPlaceholderManageResponse(records);
        return PagingList.of(placeholderManageResponses, placeholderManageEntityPage.getTotal());
    }

    @Override
    public Map<String, String> getKvValue() {
        QueryWrapper<PlaceholderManageEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(PlaceholderManageEntity::getIsDeleted,0);
        List<PlaceholderManageEntity> manageEntities = placeholderManageDAO.list(queryWrapper);
        return manageEntities.stream().collect(Collectors.toMap(PlaceholderManageEntity::getPlaceholderKey, PlaceholderManageEntity::getPlaceholderValue));
    }
}
