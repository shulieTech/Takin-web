package io.shulie.takin.web.biz.service.scenemanage.impl;

import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.shulie.takin.cloud.entrypoint.machine.CloudMachineApi;
import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import io.shulie.takin.cloud.sdk.model.request.machine.MachineAddReq;
import io.shulie.takin.cloud.sdk.model.request.machine.MachineBaseReq;
import io.shulie.takin.cloud.sdk.model.response.machine.NodeMetricsResp;
import io.shulie.takin.common.beans.page.PagingDevice;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.utils.string.StringUtil;
import io.shulie.takin.web.biz.pojo.request.scene.PressureMachineBaseRequest;
import io.shulie.takin.web.biz.pojo.request.scene.PressureMachineCreateRequest;
import io.shulie.takin.web.biz.pojo.request.scene.PressureMachineResponse;
import io.shulie.takin.web.biz.pojo.request.scene.PressureMachineUpdateRequest;
import io.shulie.takin.web.biz.service.scenemanage.MachineManageService;
import io.shulie.takin.web.common.util.BeanCopyUtils;
import io.shulie.takin.web.data.dao.scenemanage.MachineManageDAO;
import io.shulie.takin.web.data.model.mysql.MachineManageEntity;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class MachineManageServiceImpl implements MachineManageService, InitializingBean {

    @Value("${machine.password.sale.pre: machinePasswordSaltPre}")
    private String machinePasswordSaltPre;

    private SymmetricCrypto des;

    @Override
    public void afterPropertiesSet() throws Exception {
        //这样需要同时拿到数据库，代码，配置文件密码才会泄露
        des = new SymmetricCrypto(SymmetricAlgorithm.DES, (machinePasswordSaltPre + "fankfneioqn").getBytes());
    }

    @Resource
    private MachineManageDAO machineManageDAO;

    @Resource
    private CloudMachineApi cloudMachineApi;

    @Override
    public String create(PressureMachineCreateRequest request) {
        QueryWrapper<MachineManageEntity> nameQueryWrapper = new QueryWrapper<>();
        nameQueryWrapper.lambda().eq(MachineManageEntity::getMachineName,request.getMachineName());
        List<MachineManageEntity> nameList = machineManageDAO.list(nameQueryWrapper);
        if (CollectionUtils.isNotEmpty(nameList)){
            return "机器名称已存在";
        }

        QueryWrapper<MachineManageEntity> ipQueryWrapper = new QueryWrapper<>();
        ipQueryWrapper.lambda().eq(MachineManageEntity::getMachineIp,request.getMachineIp());
        List<MachineManageEntity> ipList = machineManageDAO.list(ipQueryWrapper);
        if (CollectionUtils.isNotEmpty(ipList)){
            return "机器ip已存在";
        }

        MachineManageEntity machineManageEntity = new MachineManageEntity();
        machineManageEntity.setMachineName(request.getMachineName());
        machineManageEntity.setMachineIp(request.getMachineIp());
        machineManageEntity.setUserName(request.getUserName());
        machineManageEntity.setPassword(des.encryptHex(request.getPassword()));
        machineManageEntity.setStatus(0);
        machineManageDAO.save(machineManageEntity);
        return null;
    }

    @Override
    public PagingList<PressureMachineResponse> list(PagingDevice request) {
        Page<MachineManageEntity> page = new Page<>(request.getCurrent() + 1, request.getPageSize());
        Page<MachineManageEntity> manageEntityPage = machineManageDAO.page(page);
        List<PressureMachineResponse> pressureMachineResponses = BeanCopyUtils.copyList(manageEntityPage.getRecords(), PressureMachineResponse.class);
        //查询引擎数据
        if (CollectionUtils.isNotEmpty(pressureMachineResponses)) {
            ContextExt req = new ContextExt();
            WebPluginUtils.fillCloudUserData(req);
            ResponseResult<List<NodeMetricsResp>> list = cloudMachineApi.list(req);
            if (list != null && CollectionUtils.isNotEmpty(list.getData())) {
                Map<String, List<NodeMetricsResp>> stringListMap = list.getData().stream().collect(Collectors.groupingBy(NodeMetricsResp::getNodeIp));
                pressureMachineResponses.forEach(pressureMachineResponse -> {
                    List<NodeMetricsResp> nodeMetrics = stringListMap.get(pressureMachineResponse.getMachineIp());
                    if (CollectionUtils.isNotEmpty(nodeMetrics)) {
                        NodeMetricsResp nodeMetricsResp = nodeMetrics.get(0);
                        pressureMachineResponse.setCpu(nodeMetricsResp.getCpu());
                        pressureMachineResponse.setMemory(nodeMetricsResp.getMemory());
                        pressureMachineResponse.setEngineStatus(nodeMetricsResp.getStatus());
                    }
                });
            }
        }
        return PagingList.of(pressureMachineResponses, manageEntityPage.getTotal());
    }

    @Override
    public void update(PressureMachineUpdateRequest request) {
        MachineManageEntity manageDAOById = machineManageDAO.getById(request.getId());
        if (manageDAOById == null){
            return;
        }
        if (StringUtil.isNotEmpty(request.getPassword())){
            manageDAOById.setPassword(des.encryptHex(request.getPassword()));
        }
        if (StringUtil.isNotEmpty(request.getUserName())){
            manageDAOById.setUserName(request.getUserName());
        }
        manageDAOById.setUpdateTime(new Date());
        machineManageDAO.updateById(manageDAOById);
    }

    @Override
    public void delete(PressureMachineBaseRequest request) {
        MachineManageEntity manageDAOById = machineManageDAO.getById(request.getId());
        if (manageDAOById == null){
            return;
        }
        //部署中的内容不能删除
        if (manageDAOById.getStatus() == 1){
            return;
        }
        //已部署的节点需要先进行卸载
        if (manageDAOById.getStatus() == 2){
            //卸载已部署的节点
            MachineBaseReq baseReq = new MachineBaseReq();
            baseReq.setNodeName(manageDAOById.getMachineName());
            WebPluginUtils.fillCloudUserData(baseReq);
            cloudMachineApi.delete(baseReq);
        }
        machineManageDAO.removeById(request.getId());
    }

    /**
     * 返回失败原因
     * @param request
     * @return
     */
    @Override
    public String enable(PressureMachineBaseRequest request) {
        MachineManageEntity manageDAOById = machineManageDAO.getById(request.getId());
        if (manageDAOById == null){
            return "没有找到对应机器数据，请刷新页面再试";
        }
        MachineAddReq machineAddReq = new MachineAddReq();
        machineAddReq.setNodeIp(manageDAOById.getMachineIp());
        machineAddReq.setPassword(des.decryptStr(manageDAOById.getPassword()));
        machineAddReq.setUserName(manageDAOById.getUserName());
        machineAddReq.setName(manageDAOById.getMachineName());
        WebPluginUtils.fillCloudUserData(machineAddReq);
        cloudMachineApi.add(machineAddReq);

        manageDAOById.setUpdateTime(new Date());
        manageDAOById.setStatus(2);
        machineManageDAO.updateById(manageDAOById);
        return null;
    }

    @Override
    public String disable(PressureMachineBaseRequest request) {
        MachineManageEntity manageDAOById = machineManageDAO.getById(request.getId());
        if (manageDAOById == null){
            return "没有找到对应机器数据，请刷新页面再试";
        }
        //卸载已部署的节点
        MachineBaseReq baseReq = new MachineBaseReq();
        baseReq.setNodeName(manageDAOById.getMachineName());
        WebPluginUtils.fillCloudUserData(baseReq);
        cloudMachineApi.delete(baseReq);
        manageDAOById.setUpdateTime(new Date());
        manageDAOById.setStatus(0);
        machineManageDAO.updateById(manageDAOById);
        return null;
    }

    @Override
    public void syncMachine() {
        //todo 同步机器逻辑，先找到客户机器，新增到当前机器列表
    }


}
