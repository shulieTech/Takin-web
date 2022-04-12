package io.shulie.takin.web.data.dao.application.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import io.shulie.takin.common.beans.component.SelectVO;
import io.shulie.takin.web.data.dao.application.MiddlewareTypeDAO;
import io.shulie.takin.web.data.mapper.mysql.MiddlewareTypeMapper;
import io.shulie.takin.web.data.model.mysql.InheritedSelectVO;
import io.shulie.takin.web.data.model.mysql.MiddlewareTypeEntity;
import io.shulie.takin.web.data.util.MPUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Repository;

@Repository
public class MiddlewareTypeDAOImpl extends ServiceImpl<MiddlewareTypeMapper, MiddlewareTypeEntity>
    implements MiddlewareTypeDAO, MPUtil<MiddlewareTypeEntity> {

    @Override
    public List<InheritedSelectVO> selectList() {
        LambdaQueryWrapper<MiddlewareTypeEntity> wrapper = this.getLambdaQueryWrapper()
            .eq(MiddlewareTypeEntity::getIsDeleted, 0);
        List<MiddlewareTypeEntity> typeEntities = list(wrapper);
        if (CollectionUtils.isNotEmpty(typeEntities)) {

            Map<String, Set<MiddlewareTypeEntity>> groupByMap = typeEntities.stream().collect(
                Collectors.groupingBy(MiddlewareTypeEntity::getType,
                    Collectors.mapping(Function.identity(), Collectors.toSet())));

            List<InheritedSelectVO> sortedVo = new ArrayList<>();
            for (Entry<String, Set<MiddlewareTypeEntity>> entry : groupByMap.entrySet()) {
                String key = entry.getKey();
                List<MiddlewareTypeEntity> value = new ArrayList<>(entry.getValue());
                value.sort(Comparator.comparing(MiddlewareTypeEntity::getValueOrder));
                if (value.size() > 1) {
                    int minValueOrder = Integer.MAX_VALUE;
                    List<SelectVO> children = new ArrayList<>(value.size());
                    for (MiddlewareTypeEntity entity : value) {
                        minValueOrder = Math.min(minValueOrder, entity.getValueOrder());
                        children.add(new SelectVO(entity.getName(), entity.getEngName()));
                    }
                    sortedVo.add(new InheritedSelectVO(key, key, children, minValueOrder));
                } else {
                    MiddlewareTypeEntity typeEntity = value.iterator().next();
                    String type = typeEntity.getType();
                    String engName = typeEntity.getEngName();
                    Integer valueOrder = typeEntity.getValueOrder();
                    if (type.equals(engName)) {
                        sortedVo.add(new InheritedSelectVO(type, engName, null, valueOrder));
                    } else {
                        List<SelectVO> children = new ArrayList<>(1);
                        children.add(new SelectVO(typeEntity.getName(), engName));
                        sortedVo.add(new InheritedSelectVO(type, type, children, valueOrder));
                    }
                }
            }
            sortedVo.sort(Comparator.comparing(InheritedSelectVO::getOrder));
            return sortedVo;
        }
        return Lists.newArrayList();
    }
}
