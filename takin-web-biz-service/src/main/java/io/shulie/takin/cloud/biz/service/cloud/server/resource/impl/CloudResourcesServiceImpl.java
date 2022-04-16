package io.shulie.takin.cloud.biz.service.cloud.server.resource.impl;

import com.google.common.collect.Lists;
import io.shulie.takin.adapter.api.model.response.cloud.resources.CloudResource;
import io.shulie.takin.adapter.api.model.response.cloud.resources.Resource;
import io.shulie.takin.cloud.biz.service.cloud.server.resource.CloudResourcesService;
import io.shulie.takin.web.data.dao.cloud.resouces.CloudResourcesDao;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CloudResourcesServiceImpl implements CloudResourcesService {
    @javax.annotation.Resource
    private CloudResourcesDao cloudResourcesDao;
    private static final Map<Integer, Integer> cache = new HashMap<>(3);

    static {
        cache.put(3, 3);
        cache.put(1, 2);
        cache.put(2, 1);
        cache.put(0,0);
    }

    @Override
    public Resource getDetail(List<CloudResource> resources, Integer taskId, String resourceId, String sortField, String sortType, Integer currentPage, Integer pageSize) {
        Resource resource = new Resource();
        resource.setTaskId(taskId);
        resource.setResourceId(resourceId);
        resource.setCurrentPage(currentPage);
        resource.setPageSize(pageSize);
        //1.查询压力机明细
        if (CollectionUtils.isNotEmpty(resources)) {
            resource.setResources(doTurnPage(resources, sortField, sortType, currentPage, pageSize));
            int size = resources.size();
            resource.setResourcesAmount(size);
            Map<Integer, Integer> collect = resources.stream().collect(Collectors.toMap(CloudResource::getStatus, n -> 1, (n1, n2) -> n1 + 1));
            collect.entrySet().forEach(entry -> {
                switch (entry.getKey()) {
                    case 0:
                        resource.setInitializedAmount(entry.getValue());
                        //默认全部压测中完成
                        break;
                    case 1:
                        resource.setAliveAmount(entry.getValue());
                        break;
                    case 3:
                        resource.setUnusualAmount(entry.getValue());
                        break;
                    case 2:
                        resource.setInactiveAmount(entry.getValue());
                }
            });
        } else {
            resource.setResources(Lists.newArrayList());
        }
        //2.查询状态
        cloudResourcesDao.getResourceStatus(resource);
        return resource;
    }

    private List doTurnPage(List<CloudResource> resources, String sortField, String sortType, Integer currentPage, Integer pageSize) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return resources.stream().sorted((r1, r2) -> {
            switch (sortField) {
                case "startTime":
                    try {
                        Date s1 = sdf.parse(r1.getStartTime());
                        Date s2 = sdf.parse(r2.getStartTime());
                        if (StringUtils.equals("asc", sortType)) {
                            return s1.before(s2) ? -1 : 1;
                        } else {
                            return s1.before(s2) ? 1 : -1;
                        }
                    } catch (ParseException e) {
                        //Ignore
                        return 0;
                    }
                case "stopTime":
                    try {
                        Date s1 = sdf.parse(r1.getStopTime());
                        Date s2 = sdf.parse(r2.getStopTime());
                        if (StringUtils.equals("asc", sortType)) {
                            return s1.before(s2) ? -1 : 1;
                        } else {
                            return s1.before(s2) ? 1 : -1;
                        }
                    } catch (ParseException e) {
                        //Ignore
                        return 0;
                    }
                case "hostIp":
                    String h1 = r1.getHostIp();
                    String h2 = r2.getHostIp();
                    if (StringUtils.isBlank(h1)) {
                        h1 = "";
                    }
                    if (StringUtils.isBlank(h2)) {
                        h2 = "";
                    }
                    if (StringUtils.equals("asc", sortType)) {
                        return h1.compareTo(h2);
                    } else {
                        return h1.compareTo(h2) == 1 ? -1 : 1;
                    }
                case "status":
                    Integer s1 = cache.get(r1.getStatus());
                    Integer s2 = cache.get(r2.getStatus());
                    String host1 = r1.getHostIp();
                    String host2 = r2.getHostIp();
                    if (StringUtils.isBlank(host1)) {
                        host1 = "";
                    }
                    if (StringUtils.isBlank(host2)) {
                        host2 = "";
                    }
                    if (StringUtils.equals("desc", sortType)) {
                        if (s1 > s2) {
                            return -1;
                        } else if (s1 < s2) {
                            return 1;
                        } else {
                            return host1.compareTo(host2) == 1 ? -1 : 1;
                        }
                    } else {
                        if (s1 > s2) {
                            return 1;
                        } else if (s1 < s2) {
                            return -1;
                        } else {
                            return host1.compareTo(host2);
                        }
                    }
                default:
                    return 0;
            }
        }).skip(currentPage * pageSize).limit(pageSize).collect(Collectors.toList());
    }
}
