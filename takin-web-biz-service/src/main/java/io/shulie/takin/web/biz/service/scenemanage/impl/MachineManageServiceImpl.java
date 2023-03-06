package io.shulie.takin.web.biz.service.scenemanage.impl;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.shulie.takin.cloud.entrypoint.machine.CloudMachineApi;
import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import io.shulie.takin.cloud.sdk.model.request.machine.MachineAddReq;
import io.shulie.takin.cloud.sdk.model.request.machine.MachineBaseReq;
import io.shulie.takin.cloud.sdk.model.response.machine.NodeMetricsResp;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.plugin.framework.core.PluginManager;
import io.shulie.takin.utils.string.StringUtil;
import io.shulie.takin.web.amdb.util.HttpClientUtil;
import io.shulie.takin.web.biz.constant.MachineManageConstants;
import io.shulie.takin.web.biz.pojo.dto.machinemanage.PressureMachineDTO;
import io.shulie.takin.web.biz.pojo.request.scene.*;
import io.shulie.takin.web.biz.pojo.response.scene.BenchmarkSuiteResponse;
import io.shulie.takin.web.biz.service.scenemanage.MachineManageService;
import io.shulie.takin.web.biz.utils.FileUtils;
import io.shulie.takin.web.biz.utils.SshInitUtil;
import io.shulie.takin.web.common.util.BeanCopyUtils;
import io.shulie.takin.web.data.dao.scenemanage.MachineManageDAO;
import io.shulie.takin.web.data.mapper.mysql.MachineManageMapper;
import io.shulie.takin.web.data.model.mysql.MachineManageEntity;
import io.shulie.takin.web.ext.api.user.WebUserExtApi;
import io.shulie.takin.web.ext.entity.UserExt;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Component
public class MachineManageServiceImpl implements MachineManageService, InitializingBean {

    @Value("${machine.password.sale.pre: machinePasswordSaltPre}")
    private String machinePasswordSaltPre;
    @Value("${yidongyun.user.machine.url: http://devops.testcloud.com/ms/testcloudplatform/api/service/user/host}")
    private String url;
    @Value("${benchmark.server.ip:192.168.1.220}")
    private String benchmarkServerIp;
    @Value("${benchmark.server.port:80}")
    private String benchmarkServerPort;
    @Value("${benchmark.user.appKey: f524efbb720797aedc4d3339cbf9dda0}")
    private String benchmarkUserAppKey;
    private String benchmarkMachineUrl;
    private String benchmarkSuiteListUrl;
    private String benchmarkUnInstallUrl;

    @Value("${docker.cmd.download: cd / && mkdir data && cd /data && wget https://install-pkg.oss-cn-hangzhou.aliyuncs.com/alone-pkg/docker-compose_install.zip && unzip docker-compose_install.zip -x __MACOSX/* && mv docker-compose_install/* ./ }")
    private String dockerDownloadCmd;
    @Value("${docker.cmd.install: cd /data && sh docker-compose_install.sh}")
    private String dockerInstallCmd;
    @Value("${docker.cmd.pull: docker pull 192.168.10.11/library/BENCHMARK_SUITE_NAME:latest}")
    private String dockerPullCmd;
    @Value("${docker.cmd.run: docker run -itd --net=host --name BENCHMARK_SUITE_NAME 192.168.10.11/library/BENCHMARK_SUITE_NAME:latest}")
    private String dockerRunCmd;
    @Value("${docker.pressure.url: https://shulie-daily.oss-cn-hangzhou.aliyuncs.com/yidongyun/pressure-engine.zip}")
    private String dockerPressureUrl;


    @Value("${harbor.machine.ip:192.168.10.11}")
    private String harborMachineIp;
    @Value("${harbor.machine.username: root}")
    private String harborMachineUserName;
    @Value("${harbor.machine.password: shulie@2020}")
    private String harborMachinePassword;
    @Value("${docker.start.timeout: 15}")
    private Integer dockerStartTimeout;
    @Value("${ssh.exec.timeout: 100000}")
    private Integer sshExecTime;

    @Resource
    private MachineManageMapper machineManageMapper;

    private SymmetricCrypto des;
    private final static ExecutorService THREAD_POOL = new ThreadPoolExecutor(20, 40, 300L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(100), new ThreadFactoryBuilder().setNameFormat("machine-manage-exec-%d").build(), new ThreadPoolExecutor.AbortPolicy());

    private static final ConcurrentHashMap<Long, String> deployStatusMap = new ConcurrentHashMap<>();

    private static final List<String> deployProgressList = new ArrayList<>();

    @Autowired
    @Qualifier("redisTemplate")
    private RedisTemplate redisTemplate;

    @Override
    public void afterPropertiesSet() throws Exception {
        //这样需要同时拿到数据库，代码，配置文件密码才会泄露
        des = new SymmetricCrypto(SymmetricAlgorithm.DES, (machinePasswordSaltPre + "fankfneioqn").getBytes());

        benchmarkMachineUrl = "http://" + benchmarkServerIp + ":" + benchmarkServerPort + "/api/machine/list?type=1";
        benchmarkSuiteListUrl = "http://" + benchmarkServerIp + ":" + benchmarkServerPort + "/api/benchmarksuite/getOrders";
        benchmarkUnInstallUrl = "http://" + benchmarkServerIp + ":" + benchmarkServerPort + "/api/engine/unInstall";

        //初始化进度列表
        deployProgressList.add("验证机器连通性");
        deployProgressList.add("检测harbor连通性");
        deployProgressList.add("检查docker环境");
        deployProgressList.add("拉docker环境安装包");
        deployProgressList.add("安装docker环境");
        deployProgressList.add("设置harbor白名单");
        deployProgressList.add("拉取镜像");
        deployProgressList.add("启动容器");
        deployProgressList.add("启动服务");
        deployProgressList.add("启动成功");
    }

    @Resource
    private MachineManageDAO machineManageDAO;
    @Resource
    private CloudMachineApi cloudMachineApi;
    @Resource
    private PluginManager pluginManager;

    @Override
    public String create(PressureMachineCreateRequest request) {
        QueryWrapper<MachineManageEntity> nameQueryWrapper = new QueryWrapper<>();
        nameQueryWrapper.lambda().eq(MachineManageEntity::getMachineName, request.getMachineName());
        List<MachineManageEntity> nameList = machineManageDAO.list(nameQueryWrapper);
        if (CollectionUtils.isNotEmpty(nameList)) {
            return "机器名称已存在";
        }
        // 机器ip 可运行多个 by 2023.02.01
        // QueryWrapper<MachineManageEntity> ipQueryWrapper = new QueryWrapper<>();
        // ipQueryWrapper.lambda().eq(MachineManageEntity::getMachineIp, request.getMachineIp());
        // List<MachineManageEntity> ipList = machineManageDAO.list(ipQueryWrapper);
        // if (CollectionUtils.isNotEmpty(ipList)) {
        //     return "机器ip已存在";
        // }

        MachineManageEntity machineManageEntity = new MachineManageEntity();
        machineManageEntity.setMachineName(request.getMachineName());
        machineManageEntity.setMachineIp(request.getMachineIp());
        machineManageEntity.setUserName(request.getUserName());
        machineManageEntity.setPassword(des.encryptHex(request.getPassword()));
        machineManageEntity.setStatus(0);
        machineManageEntity.setRemark(request.getRemark());
        machineManageEntity.setTag(request.getTag());
        //查询这个机器是否已经是节点机器
        ContextExt req = new ContextExt();
        WebPluginUtils.fillCloudUserData(req);
        ResponseResult<List<NodeMetricsResp>> list = cloudMachineApi.list(req);
        if (list != null && CollectionUtils.isNotEmpty(list.getData())) {
            List<NodeMetricsResp> nodeMetrics = list.getData().stream().filter(o -> o.getNodeIp().equals(request.getMachineIp())).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(nodeMetrics)) {
                machineManageEntity.setStatus(2);
                machineManageEntity.setMachineName(nodeMetrics.get(0).getName());
            }
        }
        machineManageDAO.save(machineManageEntity);
        return null;
    }

    @Override
    public PagingList<PressureMachineResponse> list(PressureMachineQueryRequest request, HttpServletRequest httpRequest) {
        Page<MachineManageEntity> page = new Page<>(request.getCurrent() + 1, request.getPageSize());
        QueryWrapper<MachineManageEntity> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(request.getName())) {
            queryWrapper.like("machine_name", request.getName());
        }
        Page<MachineManageEntity> manageEntityPage = machineManageDAO.page(page, queryWrapper);
        List<PressureMachineResponse> pressureMachineResponses = BeanCopyUtils.copyList(manageEntityPage.getRecords(), PressureMachineResponse.class);
        //查询引擎数据
        if (CollectionUtils.isEmpty(pressureMachineResponses)) {
            return PagingList.of(pressureMachineResponses, manageEntityPage.getTotal());
        }
        fillPressureMachineData(httpRequest, pressureMachineResponses);
        return PagingList.of(pressureMachineResponses, manageEntityPage.getTotal());
    }

    private void fillPressureMachineData(HttpServletRequest httpRequest, List<PressureMachineResponse> pressureMachineResponses) {
        long takinCount = pressureMachineResponses.stream().filter(o -> MachineManageConstants.TYPE_TAKIN.equals(o.getDeployType())).count();
        if (takinCount > 0) {
            ContextExt req = new ContextExt();
            WebPluginUtils.fillCloudUserData(req);
            ResponseResult<List<NodeMetricsResp>> list = cloudMachineApi.list(req);
            if (list != null && CollectionUtils.isNotEmpty(list.getData())) {
                Map<String, List<NodeMetricsResp>> stringListMap = list.getData().stream().collect(Collectors.groupingBy(NodeMetricsResp::getNodeIp));
                pressureMachineResponses.forEach(pressureMachineResponse -> {
                    List<NodeMetricsResp> nodeMetrics = stringListMap.get(pressureMachineResponse.getMachineIp());
                    if (CollectionUtils.isNotEmpty(nodeMetrics)) {
                        NodeMetricsResp nodeMetricsResp = nodeMetrics.get(0);
                        pressureMachineResponse.setCpu(nodeMetricsResp.getCpu() == null ? "" : nodeMetricsResp.getCpu().toString());
                        pressureMachineResponse.setMemory(nodeMetricsResp.getMemory() == null ? "" : nodeMetricsResp.getMemory().divide(new BigDecimal(1024 * 1024), 2, RoundingMode.HALF_UP) + "G");
                        pressureMachineResponse.setEngineStatus(nodeMetricsResp.getStatus());
                    }
                });
            }
        }
        long benchmarkCount = pressureMachineResponses.stream().filter(o -> MachineManageConstants.TYPE_BENCHMARK.equals(o.getDeployType())).count();
        if (benchmarkCount > 0) {
            List<PressureMachineDTO> pressureMachineDTOS = this.getPressureMachineDTOList(getHeaderMap(httpRequest));
            if (CollectionUtils.isNotEmpty(pressureMachineDTOS)) {
                Map<String, List<PressureMachineDTO>> stringListMap = pressureMachineDTOS.stream().collect(Collectors.groupingBy(PressureMachineDTO::getConfigIp));
                pressureMachineResponses.forEach(pressureMachineResponse -> {
                    List<PressureMachineDTO> machineDTOS = stringListMap.get(pressureMachineResponse.getMachineIp());
                    if (CollectionUtils.isNotEmpty(machineDTOS)) {
                        PressureMachineDTO pressureMachineDTO = machineDTOS.get(0);
                        pressureMachineResponse.setCpu(pressureMachineDTO.getCpuNum() + "");
                        pressureMachineResponse.setMemory(pressureMachineDTO.getMemory());
                        pressureMachineResponse.setEngineStatus("ready");
                    }
                });
            }

        }
        //如果机器为已部署，然后没有上报信息，状态为不可用
        pressureMachineResponses.forEach(pressureMachineResponse -> {
            if (deployStatusMap.containsKey(pressureMachineResponse.getId())) {
                pressureMachineResponse.setDeployProgressList(deployProgressList);
                String progress = deployStatusMap.get(pressureMachineResponse.getId());
                pressureMachineResponse.setCurrentProgressIndex(deployProgressList.indexOf(progress));
            }
            if (pressureMachineResponse.getStatus() == 2 && pressureMachineResponse.getEngineStatus() == null) {
                pressureMachineResponse.setEngineStatus("not ready");
            }
        });
    }


    @Override
    public void update(PressureMachineUpdateRequest request) {
        MachineManageEntity manageDAOById = machineManageDAO.getById(request.getId());
        if (manageDAOById == null) {
            return;
        }
        if (StringUtil.isNotEmpty(request.getPassword())) {
            manageDAOById.setPassword(des.encryptHex(request.getPassword()));
        }
        if (StringUtil.isNotEmpty(request.getUserName())) {
            manageDAOById.setUserName(request.getUserName());
        }
        if (StringUtil.isNotEmpty(request.getRemark())) {
            manageDAOById.setRemark(request.getRemark());
        }
        if (StringUtils.isNotBlank(request.getTag())) {
            manageDAOById.setTag(request.getTag());
        }
        manageDAOById.setUpdateTime(new Date());
        machineManageDAO.updateById(manageDAOById);
    }

    @Override
    public String delete(PressureMachineBaseRequest request, HttpServletRequest httpRequest) {
        MachineManageEntity manageDAOById = machineManageDAO.getById(request.getId());
        if (manageDAOById == null) {
            return "删除的数据已经不存在，请刷新页面再试";
        }
        //部署中的内容不能删除
        if (manageDAOById.getStatus() == 1) {
            return "部署中的内容不能删除";
        }
        //已部署的节点需要先进行卸载
        if (manageDAOById.getStatus() == 2) {
            //卸载已部署的节点
            PressureMachineBaseRequest disableRequest = new PressureMachineBaseRequest();
            disableRequest.setId(request.getId());
            this.disable(disableRequest, httpRequest);
        }
        machineManageDAO.removeById(request.getId());
        return null;
    }


    /**
     * 返回失败原因
     *
     * @param request
     * @return
     */
    @Override
    public String enable(PressureMachineBaseRequest request) {
        MachineManageEntity manageDAOById = machineManageDAO.getById(request.getId());
        if (manageDAOById == null) {
            return "没有找到对应机器数据，请刷新页面再试";
        }
        if (manageDAOById.getUserName() == null) {
            return "当前机器没有用户名，请先补充用户名";
        }
        if (manageDAOById.getPassword() == null) {
            return "当前机器没有密码，请先补充密码";
        }
        SshInitUtil sshInitUtil = new SshInitUtil(manageDAOById.getMachineIp(), des.decryptStr(manageDAOById.getPassword()), manageDAOById.getUserName());
        //机器联通测试
        String checkMachineExec = sshInitUtil.execute("echo machine_test", sshExecTime);
        if (checkMachineExec == null || !checkMachineExec.contains("machine_test")) {
            return "机器连通性验证未通过，请确认用户名和密码是否正确";
        }
        MachineAddReq machineAddReq = new MachineAddReq();
        machineAddReq.setNodeIp(manageDAOById.getMachineIp());
        machineAddReq.setPassword(des.decryptStr(manageDAOById.getPassword()));
        machineAddReq.setUsername(manageDAOById.getUserName());
        machineAddReq.setName(manageDAOById.getMachineName());
        WebPluginUtils.fillCloudUserData(machineAddReq);
        cloudMachineApi.add(machineAddReq);

        manageDAOById.setUpdateTime(new Date());
        manageDAOById.setStatus(2);
        manageDAOById.setDeployType(MachineManageConstants.TYPE_TAKIN);
        machineManageDAO.updateById(manageDAOById);
        return null;
    }

    @Override
    public String disable(PressureMachineBaseRequest request, HttpServletRequest httpRequest) {
        MachineManageEntity manageDAOById = machineManageDAO.getById(request.getId());
        if (manageDAOById == null) {
            return "没有找到对应机器数据，请刷新页面再试";
        }
        SshInitUtil sshInitUtil = new SshInitUtil(manageDAOById.getMachineIp(), des.decryptStr(manageDAOById.getPassword()), manageDAOById.getUserName());
        //机器联通测试
        String checkMachineExec = sshInitUtil.execute("echo machine_test", sshExecTime);
        if (checkMachineExec == null || !checkMachineExec.contains("machine_test")) {
            return "机器连通性验证未通过，请确认用户名和密码是否正确";
        }
        if (MachineManageConstants.TYPE_TAKIN.equals(manageDAOById.getDeployType())) {
            //卸载已部署的节点
            MachineBaseReq baseReq = new MachineBaseReq();
            baseReq.setNodeName(manageDAOById.getMachineName());
            WebPluginUtils.fillCloudUserData(baseReq);
            cloudMachineApi.delete(baseReq);
        }
        if (MachineManageConstants.TYPE_BENCHMARK.equals(manageDAOById.getDeployType())) {
            //停止容器服务
            String stopExec = sshInitUtil.execute("docker stop " + manageDAOById.getBenchmarkSuiteName(), sshExecTime);
            log.info("停止容器日志：" + stopExec);
            //删除容器
            String deleteExec = sshInitUtil.execute("docker rm -f " + manageDAOById.getBenchmarkSuiteName(), sshExecTime);
            log.info("删除容器日志：" + deleteExec);
            //删除镜像
            String dockerRmi = dockerPullCmd.replace("docker pull", "docker rmi -f").replace("BENCHMARK_SUITE_NAME", manageDAOById.getBenchmarkSuiteName());
            String deleteImageExec = sshInitUtil.execute(dockerRmi, sshExecTime);
            log.info("删除镜像日志：" + deleteImageExec);
            Map<String, String> headerMap = getHeaderMap(httpRequest);
            this.unInstallBenchmark(headerMap, manageDAOById.getMachineIp(), manageDAOById.getBenchmarkSuiteName());
        }
        manageDAOById.setBenchmarkSuiteName("");
        manageDAOById.setDeployType("");
        manageDAOById.setUpdateTime(new Date());
        manageDAOById.setStatus(0);
        machineManageDAO.updateById(manageDAOById);
        return null;
    }

    @Override
    public void syncMachine() {
        WebUserExtApi userExtApi = pluginManager.getExtension(WebUserExtApi.class);
        UserExt userExt = userExtApi.traceUser();
        String externalName = userExt.getExternalName();
        String sendUrl = url;
        sendUrl += ("?projectId=" + WebPluginUtils.traceEnvCode());
        log.info("开始调用获取{}用户的机器信息,用户名为:{}", sendUrl, externalName);
        Map<String, String> header = new HashMap<>();
        header.put("X-DEVOPS-UID", externalName);
        String result = HttpClientUtil.sendGet(sendUrl, header);
        log.info("获取到结果为:{}", result);
        if (StringUtil.isNotEmpty(result)) {
            List<MachineManageEntity> manageEntities = new ArrayList<>();
            try {
                JSONObject jsonObject = JSONObject.parseObject(result);
                Object data = jsonObject.get("data");
                if (data == null) {
                    return;
                }
                JSONArray jsonArray = JSONObject.parseArray(data.toString());
                if (CollectionUtils.isNotEmpty(jsonArray)) {
                    for (Object o : jsonArray) {
                        JSONObject json = JSONObject.parseObject(o.toString());
                        MachineManageEntity machineManageEntity = new MachineManageEntity();
                        machineManageEntity.setMachineIp(json.get("ip").toString());
                        machineManageEntity.setMachineName(json.get("name").toString());
                        machineManageEntity.setStatus(0);
                        manageEntities.add(machineManageEntity);
                    }
                }
            } catch (Exception e) {
                log.error("结果解析出现异常", e);
            }
            if (CollectionUtils.isNotEmpty(manageEntities)) {
                List<String> ipList = manageEntities.stream().map(MachineManageEntity::getMachineIp).collect(Collectors.toList());
                QueryWrapper<MachineManageEntity> ipListQuery = new QueryWrapper<>();
                ipListQuery.lambda().in(MachineManageEntity::getMachineIp, ipList);
                List<MachineManageEntity> machineManageEntities = machineManageDAO.list(ipListQuery);
                if (CollectionUtils.isNotEmpty(machineManageEntities)) {
                    List<String> ipStringList = machineManageEntities.stream().map(MachineManageEntity::getMachineIp).collect(Collectors.toList());
                    log.info("同步过来的机器出现ip相同的机器，过滤掉这部分数据" + ipStringList);
                    manageEntities = manageEntities.stream().filter(o -> !ipStringList.contains(o.getMachineIp())).collect(Collectors.toList());
                }

                if (CollectionUtils.isNotEmpty(manageEntities)) {
                    List<String> nameList = manageEntities.stream().map(MachineManageEntity::getMachineName).collect(Collectors.toList());
                    QueryWrapper<MachineManageEntity> nameListQuery = new QueryWrapper<>();
                    nameListQuery.lambda().in(MachineManageEntity::getMachineName, nameList);
                    List<MachineManageEntity> nameMachineManageEntities = machineManageDAO.list(nameListQuery);
                    if (CollectionUtils.isNotEmpty(nameMachineManageEntities)) {
                        List<String> nameStringList = nameMachineManageEntities.stream().map(MachineManageEntity::getMachineName).collect(Collectors.toList());
                        log.info("同步过来的机器出现名称相同的机器，过滤掉这部分数据" + nameStringList);
                        manageEntities = manageEntities.stream().filter(o -> !nameStringList.contains(o.getMachineName())).collect(Collectors.toList());
                    }
                }

                if (CollectionUtils.isNotEmpty(manageEntities)) {
                    machineManageDAO.saveBatch(manageEntities);
                }
            }
        }
    }

    @Override
    public String benchmarkEnable(PressureMachineBaseRequest request, HttpServletRequest httpRequest) {
        if (request.getBenchmarkSuiteName() == null) {
            return "benchmark部署需要上传部署组件名称";
        }
        MachineManageEntity manageDAOById = machineManageDAO.getById(request.getId());
        if (manageDAOById == null) {
            return "没有找到对应机器数据，请刷新页面再试";
        }
        if (manageDAOById.getUserName() == null) {
            return "当前机器没有用户名，请先补充用户名";
        }
        if (manageDAOById.getPassword() == null) {
            return "当前机器没有密码，请先补充密码";
        }
        SshInitUtil sshInitUtil = new SshInitUtil(manageDAOById.getMachineIp(), des.decryptStr(manageDAOById.getPassword()), manageDAOById.getUserName());
        //机器联通测试
        String checkMachineExec = sshInitUtil.execute("echo machine_test", sshExecTime);
        deployStatusMap.put(request.getId(), "验证机器连通性");
        if (checkMachineExec == null || !checkMachineExec.contains("machine_test")) {
            manageDAOById.setStatus(3);
            return "机器" + manageDAOById.getMachineIp() + "连通性验证未通过，请确认用户名和密码是否正确";
        }

        //检测harbor连通性
        StringBuffer checkHarborBuffer = new StringBuffer().append("curl --connect-timeout 10 ").append(harborMachineIp);
        String checkHarborLinkExec = sshInitUtil.execute(checkHarborBuffer.toString(), sshExecTime);
        deployStatusMap.put(request.getId(), "检测harbor连通性");
        if (StringUtils.isBlank(checkHarborLinkExec.trim()) || checkHarborLinkExec.contains("Connection timed out")) {
            log.info("检测harbor连通性:{}", checkHarborLinkExec);
            manageDAOById.setStatus(3);
            return "机器" + manageDAOById.getMachineIp() + "检测harbor连通性验证未通过，请确认与harbor机器" + harborMachineIp + "连通性";
        }

        Map<String, String> headerMap = getHeaderMap(httpRequest);
        //首先更新状态为进行中
        manageDAOById.setBenchmarkSuiteName(request.getBenchmarkSuiteName());
        manageDAOById.setDeployType(MachineManageConstants.TYPE_BENCHMARK);
        manageDAOById.setUpdateTime(new Date());
        manageDAOById.setStatus(1);
        machineManageDAO.updateById(manageDAOById);

        //开始部署
        deployStatusMap.put(request.getId(), "检查docker环境");
        THREAD_POOL.execute(() -> {
            try {
                //docker环境安装
                String checkDockerExec = sshInitUtil.execute("docker -v", sshExecTime);
                if (checkDockerExec == null || !checkDockerExec.contains("version")) {
                    log.info("当前服务不存在docker环境,开始拉取docker环境安装包:" + checkDockerExec);
                    deployStatusMap.put(request.getId(), "拉docker环境安装包");
                    String dockerPullExec = sshInitUtil.execute(dockerDownloadCmd, sshExecTime);
                    log.info("拉docker环境安装包日志：" + dockerPullExec);
                    log.info("安装docker环境");
                    deployStatusMap.put(request.getId(), "安装docker环境");
                    String dockerInstallExec = sshInitUtil.execute("source /etc/profile && " + dockerInstallCmd, sshExecTime);
                    log.info("安装日志：" + dockerInstallExec);
                }
                //安装完成后再次检测docker环境是否安装成功，如果不成功就退出
                String checkDockerExecTwice = sshInitUtil.execute("docker -v", sshExecTime);
                if (checkDockerExecTwice == null || !checkDockerExecTwice.contains("version")) {
                    manageDAOById.setStatus(3);
                    return;
                }

                //检测harbor配置
                String checkHarborExec = sshInitUtil.execute("cat /etc/docker/daemon.json", sshExecTime);
                if (checkHarborExec == null || !checkHarborExec.contains(harborMachineIp)) {
                    log.info("开始修改目标机器的harbor配置");
                    String harborConf = "sed -i 's/\"quay.io\"/\"quay.io\",\"" + harborMachineIp + "\"/' /etc/docker/daemon.json";
                    String harborConfExec = sshInitUtil.execute(harborConf + " && systemctl daemon-reload && systemctl restart docker", sshExecTime);
                    log.info("harbor配置修改日志：" + harborConfExec);
                }

                //设置harbor白名单
                deployStatusMap.put(request.getId(), "设置harbor白名单");
                log.info("开始设置harbor白名单");
                SshInitUtil harborShellUtil = this.getHarborShellInfo();
                StringBuffer whiteListBuffer = new StringBuffer()
                        .append("iptables -I INPUT -s ").append(manageDAOById.getMachineIp()).append(" -p TCP --dport 80 -j ACCEPT");
                String harborShellExec = harborShellUtil.execute(whiteListBuffer.toString(), sshExecTime);
                log.info("设置harbor白名单日志：" + harborShellExec);

                //拉取镜像
                deployStatusMap.put(request.getId(), "拉取镜像");
                String dockerPull = dockerPullCmd.replace("BENCHMARK_SUITE_NAME", request.getBenchmarkSuiteName());
                log.info("开始拉取镜像，镜像命令为:{}", dockerPull);
                String dockerPullExec = sshInitUtil.execute(dockerPull, sshExecTime);
                log.info("拉取镜像日志：" + dockerPullExec);

                StringBuffer dockerImagesCheckBuffer = new StringBuffer().append("docker images | grep ").append(manageDAOById.getBenchmarkSuiteName()).append("| grep -v grep | awk '{print $1}'");
                String dockerImagesExec = sshInitUtil.execute(dockerImagesCheckBuffer.toString(), sshExecTime);
                if (null == dockerImagesExec || !dockerImagesExec.contains(manageDAOById.getBenchmarkSuiteName())) {
                    manageDAOById.setStatus(3);
                    return;
                }

                //启动容器
                deployStatusMap.put(request.getId(), "启动容器");
                String dockerRun = dockerRunCmd.replaceAll("BENCHMARK_SUITE_NAME", request.getBenchmarkSuiteName());
                log.info("开始执行docker命令，运行命令为:{}", dockerRun);
                String dockerRunExec = sshInitUtil.execute(dockerRun, sshExecTime);
                log.info("启动容器日志：" + dockerRunExec);

//              //替换配置文件
                StringBuffer dockerPressureEnvConfBuffer = new StringBuffer().append("docker exec ")
                        .append(manageDAOById.getBenchmarkSuiteName()).append(" /bin/bash -c ").append("'cd /data")
                        .append(" && rm -rf /data/*.swp")
                        .append(" && sed -i \"s/192.168.1.205/").append(benchmarkServerIp).append("/g\" /data/pressure.engine.env.conf")
                        .append(" && sed -i \"s/192.168.1.222/").append(manageDAOById.getMachineIp()).append("/g\" /data/pressure.engine.env.conf")
                        .append(" && sed -i \"s/test@shulie2021/").append(des.decryptStr(manageDAOById.getPassword())).append("/g\" /data/pressure.engine.env.conf")
                        .append(" && rm -rf pressure-engine")
                        .append(" && rm -rf pressure-engine.zip*")
                        .append(" && wget ").append(dockerPressureUrl)
                        .append(" && unzip -o pressure-engine.zip")
                        .append(" && rm -rf pressure-engine.zip*")
                        .append(" && rm -rf /data/pressure-engine/config/*.swp")
                        .append(" && sed -i 's/LOCAL_PASSWORD/").append(des.decryptStr(manageDAOById.getPassword())).append("/g' /data/pressure-engine/config/application-test.yml")
                        .append(" && sed -i 's/TAKIN_LITE_IP/").append(benchmarkServerIp).append("/g' /data/pressure-engine/config/application-test.yml")
                        .append(" && sed -i 's/TAKIN_LITE_PORT/").append(benchmarkServerPort).append("/g' /data/pressure-engine/config/application-test.yml")
                        .append(" && sed -i 's/LOCAL_HOST_IP/").append(manageDAOById.getMachineIp()).append("/g' /data/pressure-engine/config/application-test.yml")
                        .append(" && sed -i 's/USER_APPKEY/").append(benchmarkUserAppKey).append("/g' /data/pressure-engine/config/application-test.yml")
                        .append(" && sed -i 's/TENANT_ID/").append(WebPluginUtils.traceTenantId()).append("/g' /data/pressure-engine/config/application-test.yml")
                        .append(" && sed -i 's/ENV_CODE/").append(WebPluginUtils.traceEnvCode()).append("/g' /data/pressure-engine/config/application-test.yml")
                        .append(" && sed -i 's/PORT/").append(10000 + request.getId() + "").append("/g' /data/pressure-engine/config/application-test.yml")
                        .append(" && sed -i 's/BENCHMARK_SUITE_NAME/").append(request.getBenchmarkSuiteName()).append("/g' /data/pressure-engine/config/application-test.yml")
                        .append(" && sed -i 's/BENCHMARK_MACHINE_ID/").append(manageDAOById.getId()).append("/g' /data/pressure-engine/config/application-test.yml")
                        .append(" && cd /data/pressure-engine")
                        .append(" && sh start.sh -e test -t 1'");

                //启动进程
                deployStatusMap.put(request.getId(), "启动服务");
                log.info("开始执行docker命令，运行命令为:{}", dockerPressureEnvConfBuffer);
                String dockerAppRunExec = sshInitUtil.execute(dockerPressureEnvConfBuffer.toString(), sshExecTime);
                log.info("启动服务日志：" + dockerAppRunExec);
                //监听启动成功
                long startTimeMillis = System.currentTimeMillis();
                while (true) {
                    try {
                        if (System.currentTimeMillis() - startTimeMillis > dockerStartTimeout * 1000 * 60) {
                            log.error("docker启动超过指定时间，设置为成功，机器状态还是不会成功，让客户从页面卸载");
                            break;
                        }
                        Thread.sleep(3000);
                        List<PressureMachineDTO> pressureMachineDTOS = this.getPressureMachineDTOList(headerMap);
                        if (CollectionUtils.isNotEmpty(pressureMachineDTOS)) {
                            long count = pressureMachineDTOS.stream().filter(o -> manageDAOById.getMachineIp().equals(o.getConfigIp()) && manageDAOById.getBenchmarkSuiteName().trim().equals(o.getTypeMachine().trim())).count();
                            if (count > 0) {
                                deployStatusMap.put(request.getId(), "启动成功");
                                log.info("启动成功，结束监听");
                                manageDAOById.setStatus(2);
                                break;
                            }
                        }
                    } catch (Exception e) {
                        log.error("查询已部署机器列表出现异常", e);
                    }
                }
            } catch (Exception e) {
                log.error("监听部署过程出现异常", e);
                manageDAOById.setStatus(3);
            } finally {
                //部署成功
                deployStatusMap.remove(request.getId());
                manageDAOById.setUpdateTime(new Date());
                log.info("部署完成更新状态，" + manageDAOById.getId());
                machineManageDAO.updateById(manageDAOById);
                log.info("部署完成更新状态完成" + manageDAOById.getId());
            }
        });
        return null;
    }

    private SshInitUtil getHarborShellInfo() {
        return new SshInitUtil(harborMachineIp, harborMachinePassword, harborMachineUserName);
    }

    @Override
    public PagingList<BenchmarkSuiteResponse> benchmarkSuiteList(BenchmarkSuitePageRequest request, HttpServletRequest httpRequest) {
        String reqUrl = benchmarkSuiteListUrl + "?current=" + request.getCurrent() + "&pageSize=" + request.getPageSize();
        if (request.getSuite() != null) {
            reqUrl = reqUrl + "&suite=" + request.getSuite();
        }
        if (request.getPid() != null) {
            reqUrl = reqUrl + "&pid=" + request.getPid();
        }
        String sendGet = HttpClientUtil.sendGet(reqUrl, getHeaderMap(httpRequest));
        try {
            JSONObject jsonObject = JSONObject.parseObject(sendGet);
            Object data = jsonObject.get("data");
            if (data != null) {
                JSONObject dataJson = JSONObject.parseObject(data.toString());
                int total = dataJson.getIntValue("total");
                Object records = dataJson.get("records");
                if (records != null) {
                    List<BenchmarkSuiteResponse> benchmarkSuiteResponses = JSONObject.parseArray(records.toString(), BenchmarkSuiteResponse.class);
                    return PagingList.of(benchmarkSuiteResponses, total);
                }
            }
        } catch (Exception e) {
            log.error("解析benchmarkSuiteListUrl返回结果出现异常,返回值为:{}", sendGet, e);
        }
        return PagingList.empty();
    }

    @Override
    public ResponseResult<String> readExcelBachtCreate(MultipartFile file) {
        if (file == null) {
            return ResponseResult.fail("上传文件为空,请重新上传文件", "请重新上传文件");
        }
        try {
            InputStream stream = new BufferedInputStream(file.getInputStream());
            ExcelReader reader = ExcelUtil.getReader(stream);
            List<List<Object>> readList = reader.read();
            List<MachineManageEntity> machineManageEntities = new ArrayList<>();
            for (int i = 0; i < readList.size(); i++) {
                if (i == 0) {
                    continue;
                }
                List<Object> sheets = readList.get(i);
                MachineManageEntity machineManageEntity = new MachineManageEntity();
                machineManageEntity.setMachineName(Objects.nonNull(sheets.get(0)) ? sheets.get(0).toString() : "");
                machineManageEntity.setMachineIp(Objects.nonNull(sheets.get(1)) ? sheets.get(1).toString() : "");
                machineManageEntity.setUserName(Objects.nonNull(sheets.get(2)) ? sheets.get(2).toString() : "");
                machineManageEntity.setPassword(Objects.nonNull(sheets.get(3)) ? des.encryptHex(sheets.get(3).toString()) : null);
                machineManageEntity.setTag(Objects.nonNull(sheets.get(4)) ? sheets.get(4).toString() : "");
                machineManageEntity.setStatus(0);
                machineManageEntities.add(machineManageEntity);
            }

            String machineNameStr = getMachineName(machineManageEntities);
            if (StringUtils.isNotBlank(machineNameStr)) {
                return ResponseResult.fail(machineNameStr, machineNameStr);
            }

//            String machineIpStr = getMachineIp(machineManageEntities);
//            if (StringUtils.isNotBlank(machineIpStr)) {
//                return ResponseResult.fail(machineIpStr, machineIpStr);
//            }

            getNodeMsg(machineManageEntities);
            List<MachineManageEntity> errorMachineList = machineManageEntities.stream().filter(a -> {
                if (StringUtils.isBlank(a.getPassword()) || StringUtils.isBlank(a.getUserName()) || StringUtils.isBlank(a.getMachineIp())) {
                    return true;
                }
                return false;
            }).collect(Collectors.toList());

            if (CollectionUtils.isEmpty(machineManageEntities)) {
                return ResponseResult.fail("上传文件内容为空,请重新上传文件", "请重新上传文件");
            }
            if (CollectionUtils.isNotEmpty(errorMachineList)) {
                String errorMsg = errorMachineList.stream().map(a -> {
                    if (StringUtils.isNotBlank(a.getMachineIp())) {
                        return a.getMachineIp();
                    } else if (StringUtils.isNotBlank(a.getUserName())) {
                        return a.getUserName();
                    }
                    return "本条记录为空";
                }).collect(Collectors.joining(","));
                return ResponseResult.fail(JSON.toJSONString(errorMsg) + ",机器信息缺失,请重新上传", "机器信息缺失,请重新上传");
            }
            machineManageDAO.saveBatch(machineManageEntities);
            return ResponseResult.success();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 根据tag批量部署机器
     *
     * @param request
     * @return
     */
    @Override
    public ResponseResult<String> benchmarkEnableByTag(HttpServletRequest httpRequest, BenchmarkMachineDeployRequest request) {
        List<String> msgList = new ArrayList<>();
        ExecutorService cachePool = Executors.newFixedThreadPool(15);
        List<MachineManageEntity> list = getAllMachineByTag(request.getTag());
        if (CollectionUtils.isEmpty(list)) {
            return ResponseResult.fail("机器tag：" + request.getTag() + "下没有机器列表,请重试", "请重试");
        }

        List<MachineManageEntity> deployEdMachineList = new ArrayList<>();

        for (MachineManageEntity machineManageEntity : list) {
            try {
                //判断机器状态，如果状态是1或者2就跳过不部署
                if (machineManageEntity.getStatus() == 1 || machineManageEntity.getStatus() == 2) {
                    deployEdMachineList.add(machineManageEntity);
                    continue;
                }
                PressureMachineBaseRequest pressureMachineBaseRequest = new PressureMachineBaseRequest();
                pressureMachineBaseRequest.setId(machineManageEntity.getId());
                pressureMachineBaseRequest.setBenchmarkSuiteName(request.getBenchmarkSuiteName());
                FutureTask<String> future = new FutureTask<>(() -> benchmarkEnable(pressureMachineBaseRequest, httpRequest));
                cachePool.execute(future);
                msgList.add(future.get());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        if (CollectionUtils.isNotEmpty(deployEdMachineList)) {
            String errorMsg = deployEdMachineList.stream().map(MachineManageEntity::getMachineIp).collect(Collectors.joining(","));
            return ResponseResult.fail("机器" + errorMsg + "已部署或部署中", "请重新部署");
        }
        if (CollectionUtils.isNotEmpty(msgList)) {
            String msg = msgList.stream().collect(Collectors.joining(","));
            return ResponseResult.success(msg);
        }
        return ResponseResult.success("部署成功");
    }

    private List<MachineManageEntity> getAllMachineByTag(String tag) {
        LambdaQueryWrapper<MachineManageEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(MachineManageEntity::getTag, Arrays.asList(tag));
        List<MachineManageEntity> list = machineManageDAO.list(queryWrapper);
        return list;
    }

    /**
     * 获取所有tag
     *
     * @return
     */
    @Override
    public List<String> getAllTag() {
        QueryWrapper<MachineManageEntity> ipQueryWrapper = new QueryWrapper<>();
        ipQueryWrapper.select("distinct tag").isNotNull("tag");
        List<MachineManageEntity> machineManageEntities = machineManageDAO.list(ipQueryWrapper);
        List<String> tags = machineManageEntities.stream().map(MachineManageEntity::getTag).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(tags)) {
            return Collections.EMPTY_LIST;
        }
        return tags;
    }

    /**
     * 根据tag获取机器列表
     *
     * @param request
     * @return
     */
    @Override
    public List<PressureMachineResponse> listMachinesByTag(HttpServletRequest httpRequest, PressureMachineQueryByTagRequest request) {
        LambdaQueryWrapper<MachineManageEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(MachineManageEntity::getTag, request.getTags());
        List<MachineManageEntity> machineManageEntityPage = machineManageDAO.list(lambdaQueryWrapper);
        List<PressureMachineResponse> pressureMachineResponses = BeanCopyUtils.copyList(machineManageEntityPage, PressureMachineResponse.class);
        return pressureMachineResponses;
    }

    /**
     * 根据机器id列表获取机器信息
     *
     * @param request
     * @param httpRequest
     * @return
     */
    @Override
    public ResponseResult<List<PressureMachineResponse>> listMachinesByIds(PressureMachineQueryByTagRequest request, HttpServletRequest httpRequest) {
        LambdaQueryWrapper<MachineManageEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(MachineManageEntity::getId, request.getMachineIds());
        lambdaQueryWrapper.eq(MachineManageEntity::getIsDeleted, 0);
        List<MachineManageEntity> machineManageEntityPage = this.machineManageMapper.selectList(lambdaQueryWrapper);
        List<PressureMachineResponse> pressureMachineResponses = BeanCopyUtils.copyList(machineManageEntityPage, PressureMachineResponse.class);
        return ResponseResult.success(pressureMachineResponses);
    }

    /**
     * 获取机器信息
     *
     * @param request
     * @param httpRequest
     * @return
     */
    @Override
    public ResponseResult<List<PressureMachineResponse>> listMachines(PressureMachineQueryByTagRequest request, HttpServletRequest httpRequest) {
        LambdaQueryWrapper<MachineManageEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(MachineManageEntity::getMachineIp, request.getMachineIp());
        lambdaQueryWrapper.eq(MachineManageEntity::getBenchmarkSuiteName, request.getSuiteName());
        lambdaQueryWrapper.eq(MachineManageEntity::getIsDeleted, 0);
        List<MachineManageEntity> machineManageEntityPage = this.machineManageMapper.selectList(lambdaQueryWrapper);
        List<PressureMachineResponse> pressureMachineResponses = BeanCopyUtils.copyList(machineManageEntityPage, PressureMachineResponse.class);
        return ResponseResult.success(pressureMachineResponses);
    }

    /**
     * 外网安装，只选择一台机器拉取tar包，然后自动分发到所有机器，最后倒入镜像部署
     *
     * @param request
     * @param httpRequest
     * @return
     */
    @Override
    public String benchmarkEnableExternal(PressureMachineLoadRequest request, HttpServletRequest httpRequest) {
        if (request.getBenchmarkSuiteName() == null) {
            return "benchmark部署需要上传部署组件名称";
        }
        MachineManageEntity manageDAOById = machineManageDAO.getById(request.getId());
        if (manageDAOById == null) {
            return "没有找到对应机器数据，请刷新页面再试";
        }
        if (manageDAOById.getUserName() == null) {
            return "当前机器没有用户名，请先补充用户名";
        }
        if (manageDAOById.getPassword() == null) {
            return "当前机器没有密码，请先补充密码";
        }
        SshInitUtil sshInitUtil = new SshInitUtil(manageDAOById.getMachineIp(), des.decryptStr(manageDAOById.getPassword()), manageDAOById.getUserName());

        //检测harbor连通性
        StringBuffer checkHarborBuffer = new StringBuffer().append("curl --connect-timeout 10 ").append(harborMachineIp);
        String checkHarborLinkExec = sshInitUtil.execute(checkHarborBuffer.toString(), sshExecTime);
        deployStatusMap.put(request.getId(), "检测harbor连通性");
        if (StringUtils.isBlank(checkHarborLinkExec.trim()) || checkHarborLinkExec.contains("Connection timed out")) {
            log.info("检测harbor连通性:{}", checkHarborLinkExec);
            manageDAOById.setStatus(3);
            return "机器" + manageDAOById.getMachineIp() + "检测harbor连通性验证未通过，请确认与harbor机器" + harborMachineIp + "连通性";
        }

        Map<String, String> headerMap = getHeaderMap(httpRequest);
        //首先更新状态为进行中
        manageDAOById.setBenchmarkSuiteName(request.getBenchmarkSuiteName());
        manageDAOById.setDeployType(MachineManageConstants.TYPE_BENCHMARK);
        manageDAOById.setUpdateTime(new Date());
        manageDAOById.setStatus(1);
        machineManageDAO.updateById(manageDAOById);

        //开始部署
        deployStatusMap.put(request.getId(), "检查docker环境");
        THREAD_POOL.execute(() -> {
            try {
                //docker环境安装
                String checkDockerExec = sshInitUtil.execute("docker -v", sshExecTime);
                if (checkDockerExec == null || !checkDockerExec.contains("version")) {
                    log.info("当前服务不存在docker环境,开始拉取docker环境安装包:" + checkDockerExec);
                    deployStatusMap.put(request.getId(), "拉docker环境安装包");
                    String dockerPullExec = sshInitUtil.execute(dockerDownloadCmd, sshExecTime);
                    log.info("拉docker环境安装包日志：" + dockerPullExec);
                    log.info("安装docker环境");
                    deployStatusMap.put(request.getId(), "安装docker环境");
                    String dockerInstallExec = sshInitUtil.execute("source /etc/profile && " + dockerInstallCmd, sshExecTime);
                    log.info("安装日志：" + dockerInstallExec);
                }
                //安装完成后再次检测docker环境是否安装成功，如果不成功就退出
                String checkDockerExecTwice = sshInitUtil.execute("docker -v", sshExecTime);
                if (checkDockerExecTwice == null || !checkDockerExecTwice.contains("version")) {
                    manageDAOById.setStatus(3);
                    return;
                }

                //检测harbor配置
                String checkHarborExec = sshInitUtil.execute("cat /etc/docker/daemon.json", sshExecTime);
                if (checkHarborExec == null || !checkHarborExec.contains(harborMachineIp)) {
                    log.info("开始修改目标机器的harbor配置");
                    String harborConf = "sed -i 's/\"quay.io\"/\"quay.io\",\"" + harborMachineIp + "\"/' /etc/docker/daemon.json";
                    String harborConfExec = sshInitUtil.execute(harborConf + " && systemctl daemon-reload && systemctl restart docker", sshExecTime);
                    log.info("harbor配置修改日志：" + harborConfExec);
                }

                //设置harbor白名单
                deployStatusMap.put(request.getId(), "设置harbor白名单");
                log.info("开始设置harbor白名单");
                SshInitUtil harborShellUtil = this.getHarborShellInfo();
                StringBuffer whiteListBuffer = new StringBuffer()
                        .append("iptables -I INPUT -s ").append(manageDAOById.getMachineIp()).append(" -p TCP --dport 80 -j ACCEPT");
                String harborShellExec = harborShellUtil.execute(whiteListBuffer.toString(), sshExecTime);
                log.info("设置harbor白名单日志：" + harborShellExec);

                //拉取镜像
                StringBuffer loadStr = new StringBuffer().append("cd /opt && docker load -i ").append(request.getBenchmarkSuiteName()).append(".tar");
                sshInitUtil.execute(loadStr.toString(), sshExecTime);
                deployStatusMap.put(request.getId(), "拉取镜像");
                log.info("开始拉取镜像，镜像命令为:{}", loadStr.toString());
                String dockerPullExec = sshInitUtil.execute(loadStr.toString(), sshExecTime);
                log.info("拉取镜像日志：" + dockerPullExec);

                StringBuffer dockerImagesCheckBuffer = new StringBuffer().append("docker images | grep ").append(manageDAOById.getBenchmarkSuiteName()).append("| grep -v grep | awk '{print $1}'");
                String dockerImagesExec = sshInitUtil.execute(dockerImagesCheckBuffer.toString(), sshExecTime);
                if (null == dockerImagesExec || !dockerImagesExec.contains(manageDAOById.getBenchmarkSuiteName())) {
                    manageDAOById.setStatus(3);
                    return;
                }

                //启动容器
                deployStatusMap.put(request.getId(), "启动容器");
                String dockerRun = dockerRunCmd.replaceAll("BENCHMARK_SUITE_NAME", request.getBenchmarkSuiteName());
                log.info("开始执行docker命令，运行命令为:{}", dockerRun);
                String dockerRunExec = sshInitUtil.execute(dockerRun, sshExecTime);
                log.info("启动容器日志：" + dockerRunExec);

                //替换配置文件
                StringBuffer dockerPressureEnvConfBuffer = new StringBuffer().append("docker exec ")
                        .append(manageDAOById.getBenchmarkSuiteName()).append(" /bin/bash -c ").append("'cd /data")
                        .append(" && rm -rf /data/*.swp")
                        .append(" && sed -i \"s/192.168.1.205/").append(benchmarkServerIp).append("/g\" /data/pressure.engine.env.conf")
                        .append(" && sed -i \"s/192.168.1.222/").append(manageDAOById.getMachineIp()).append("/g\" /data/pressure.engine.env.conf")
                        .append(" && sed -i \"s/test@shulie2021/").append(des.decryptStr(manageDAOById.getPassword())).append("/g\" /data/pressure.engine.env.conf")
                        .append(" && rm -rf pressure-engine")
                        .append(" && rm -rf pressure-engine.zip*")
                        .append(" && wget ").append(dockerPressureUrl)
                        .append(" && unzip -o pressure-engine.zip")
                        .append(" && rm -rf pressure-engine.zip*")
                        .append(" && rm -rf /data/pressure-engine/config/*.swp")
                        .append(" && sed -i 's/LOCAL_PASSWORD/").append(des.decryptStr(manageDAOById.getPassword())).append("/g' /data/pressure-engine/config/application-test.yml")
                        .append(" && sed -i 's/TAKIN_LITE_IP/").append(benchmarkServerIp).append("/g' /data/pressure-engine/config/application-test.yml")
                        .append(" && sed -i 's/TAKIN_LITE_PORT/").append(benchmarkServerPort).append("/g' /data/pressure-engine/config/application-test.yml")
                        .append(" && sed -i 's/LOCAL_HOST_IP/").append(manageDAOById.getMachineIp()).append("/g' /data/pressure-engine/config/application-test.yml")
                        .append(" && sed -i 's/USER_APPKEY/").append(benchmarkUserAppKey).append("/g' /data/pressure-engine/config/application-test.yml")
                        .append(" && sed -i 's/TENANT_ID/").append(WebPluginUtils.traceTenantId()).append("/g' /data/pressure-engine/config/application-test.yml")
                        .append(" && sed -i 's/ENV_CODE/").append(WebPluginUtils.traceEnvCode()).append("/g' /data/pressure-engine/config/application-test.yml")
                        .append(" && sed -i 's/PORT/").append(10000 + request.getId() + "").append("/g' /data/pressure-engine/config/application-test.yml")
                        .append(" && sed -i 's/BENCHMARK_SUITE_NAME/").append(request.getBenchmarkSuiteName()).append("/g' /data/pressure-engine/config/application-test.yml")
                        .append(" && sed -i 's/BENCHMARK_MACHINE_ID/").append(manageDAOById.getId()).append("/g' /data/pressure-engine/config/application-test.yml")
                        .append(" && cd /data/pressure-engine")
                        .append(" && sh start.sh -e test -t 1'");

                //启动进程
                deployStatusMap.put(request.getId(), "启动服务");
                log.info("开始执行docker命令，运行命令为:{}", dockerPressureEnvConfBuffer);
                String dockerAppRunExec = sshInitUtil.execute(dockerPressureEnvConfBuffer.toString(), sshExecTime);
                log.info("启动服务日志：" + dockerAppRunExec);
                //监听启动成功
                long startTimeMillis = System.currentTimeMillis();
                while (true) {
                    try {
                        if (System.currentTimeMillis() - startTimeMillis > dockerStartTimeout * 1000 * 60) {
                            log.error("docker启动超过指定时间，设置为成功，机器状态还是不会成功，让客户从页面卸载");
                            break;
                        }
                        Thread.sleep(3000);
                        List<PressureMachineDTO> pressureMachineDTOS = this.getPressureMachineDTOList(headerMap);
                        if (CollectionUtils.isNotEmpty(pressureMachineDTOS)) {
                            long count = pressureMachineDTOS.stream().filter(o -> manageDAOById.getMachineIp().equals(o.getConfigIp()) && manageDAOById.getBenchmarkSuiteName().trim().equals(o.getTypeMachine().trim())).count();
                            if (count > 0) {
                                deployStatusMap.put(request.getId(), "启动成功");
                                log.info("启动成功，结束监听");
                                manageDAOById.setStatus(2);
                                break;
                            }
                        }
                    } catch (Exception e) {
                        log.error("查询已部署机器列表出现异常", e);
                    }
                }
            } catch (Exception e) {
                log.error("监听部署过程出现异常", e);
                manageDAOById.setStatus(3);
            } finally {
                //部署成功
                deployStatusMap.remove(request.getId());
                manageDAOById.setUpdateTime(new Date());
                log.info("部署完成更新状态，" + manageDAOById.getId());
                machineManageDAO.updateById(manageDAOById);
                log.info("部署完成更新状态完成" + manageDAOById.getId());
            }
        });
        return null;
    }

    private String loadImage(PressureMachineLoadRequest request, HttpServletRequest httpServletRequest) {
        MachineManageEntity manage = request.getMachineManageEntity();
        SshInitUtil sshInitUtil =  new SshInitUtil(manage.getMachineIp(), des.decryptStr(manage.getPassword()), manage.getUserName());

        //机器联通测试
        String checkMachineExec = sshInitUtil.execute("echo machine_test", sshExecTime);
        deployStatusMap.put(request.getId(), "验证机器连通性");
        if (checkMachineExec == null || !checkMachineExec.contains("machine_test")) {
            manage.setStatus(3);
            return "机器" + manage.getMachineIp() + "连通性验证未通过，请确认用户名和密码是否正确";
        }
        //安装scp一键登录脚本
        StringBuffer sshpassStr = new StringBuffer()
                .append("yum install -y gcc && cd /opt")
                .append(" && wget https://shulie-daily.oss-cn-hangzhou.aliyuncs.com/yidongyun-hy/sshpass-1.10.tar.gz ")
                .append(" && tar xvzf sshpass-1.10.tar.gz && cd sshpass-1.10")
                .append(" && ./configure && make && make install");
        sshInitUtil.execute(sshpassStr.toString(), sshExecTime);

        StringBuffer imageStr = new StringBuffer()
                .append("sshpass -p ").append(des.decryptStr(request.getBashPass()))
                .append(" scp ").append(request.getBaseUserName()).append("@").append(request.getBaseIp()).append(":/opt/").append(request.getBenchmarkSuiteName()).append(".tar ")
                .append(" /opt");
        sshInitUtil.execute(imageStr.toString(), sshExecTime);

        redisTemplate.opsForValue().set("machine:load:info" + request.getTag(), JSON.toJSONString(manage));
        benchmarkEnableExternal(request, httpServletRequest);
        return "部署成功";
    }

    public void tt(PressureMachineLoadRequest request, HttpServletRequest httpServletRequest) {
        LambdaQueryWrapper<MachineManageEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(MachineManageEntity::getTag, request.getTag());
        lambdaQueryWrapper.eq(MachineManageEntity::getIsDeleted, 0);
        List<MachineManageEntity> manageList = this.machineManageMapper.selectList(lambdaQueryWrapper);
        Object installMachine = redisTemplate.opsForValue().get("machine:load:info" + request.getTag());
        MachineManageEntity manage;
        if (Objects.nonNull(installMachine)) {
            String machineStr = installMachine.toString();
            manage = JSON.parseObject(machineStr,MachineManageEntity.class);
            request.setBaseIp(manage.getMachineIp());
            request.setBaseUserName(manage.getUserName());
            request.setBaseUserName(manage.getPassword());
        } else {
            //如果redis没有，就去从harbor scp过去
            request.setBaseIp(harborMachineIp);
            request.setBashPass(harborMachinePassword);
            request.setBaseUserName(harborMachineUserName);
        }
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        for (MachineManageEntity machineManageEntity : manageList) {
            request.setMachineManageEntity(machineManageEntity);
            executorService.submit(() -> loadImage(request, httpServletRequest));
        }
    }


    /**
     * 查询这个机器是否已经是节点机器
     *
     * @param machineManageEntities
     */
    private void getNodeMsg(List<MachineManageEntity> machineManageEntities) {
        List<MachineManageEntity> manageEntityArrayList = new ArrayList<>();
        ContextExt req = new ContextExt();
        WebPluginUtils.fillCloudUserData(req);
        ResponseResult<List<NodeMetricsResp>> list = cloudMachineApi.list(req);
        if (list != null && CollectionUtils.isNotEmpty(list.getData())) {
            List<String> nodeIpList = list.getData().stream().map(NodeMetricsResp::getNodeIp).collect(Collectors.toList());
            machineManageEntities.forEach(a -> {
                if (nodeIpList.contains(a.getMachineIp())) {
                    a.setStatus(2);
                    a.setMachineName("");
                }
            });
        }
    }

    private String getMachineIp(List<MachineManageEntity> machineManageEntities) {
        StringBuffer errorString = new StringBuffer();
        List<String> machineIpList = machineManageEntities.stream().map(MachineManageEntity::getMachineIp).filter(a -> StringUtils.isNotBlank(a)).collect(Collectors.toList());
        QueryWrapper<MachineManageEntity> ipQueryWrapper = new QueryWrapper<>();
        ipQueryWrapper.lambda().in(MachineManageEntity::getMachineIp, machineIpList);
        List<MachineManageEntity> ipList = machineManageDAO.list(ipQueryWrapper);
        if (CollectionUtils.isNotEmpty(ipList)) {
            String machineIp = ipList.stream().map(MachineManageEntity::getMachineIp).collect(Collectors.joining(","));
            errorString.append(machineIp).append(",").append("机器ip已存在");
            return errorString.toString();
        }
        return null;
    }

    private String getMachineName(List<MachineManageEntity> machineManageEntities) {
        StringBuffer errorString = new StringBuffer();
        List<String> machineNameList = machineManageEntities.stream().map(MachineManageEntity::getMachineName).filter(a -> StringUtils.isNotBlank(a)).collect(Collectors.toList());
        QueryWrapper<MachineManageEntity> nameQueryWrapper = new QueryWrapper<>();
        nameQueryWrapper.lambda().in(MachineManageEntity::getMachineName, machineNameList);
        List<MachineManageEntity> nameList = machineManageDAO.list(nameQueryWrapper);
        if (CollectionUtils.isNotEmpty(nameList)) {
            String machineName = nameList.stream().map(MachineManageEntity::getMachineName).collect(Collectors.joining(","));
            errorString.append(machineName).append(",").append("机器名称已存在");
            return errorString.toString();
        }
        return null;
    }

    private Map<String, String> getHeaderMap(HttpServletRequest httpRequest) {
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("token", Objects.requireNonNull(httpRequest.getHeader("x-token")));
        headerMap.put("envCode", Objects.requireNonNull(httpRequest.getHeader("env-code")));
        headerMap.put("tenantCode", Objects.requireNonNull(httpRequest.getHeader("tenant-code")));
        return headerMap;
    }

    private void unInstallBenchmark(Map<String, String> headerMap, String ip, String suiteName) {
        HttpClientUtil.sendGet(benchmarkUnInstallUrl + "?configIp=" + ip + "&typeMachine=" + suiteName + "&userAppKey=" + benchmarkUserAppKey, headerMap);
    }

    private List<PressureMachineDTO> getPressureMachineDTOList(Map<String, String> headerMap) {
        String sendGet = HttpClientUtil.sendGet(benchmarkMachineUrl, headerMap);
        try {
            JSONObject jsonObject = JSONObject.parseObject(sendGet);
            Object data = jsonObject.get("data");
            if (Objects.isNull(data)) {
                return null;
            }
            List<PressureMachineDTO> pressureMachineDTOS = JSONObject.parseArray(data.toString(), PressureMachineDTO.class);
            //处理ip上面的特殊字符
            if (CollectionUtils.isEmpty(pressureMachineDTOS)) {
                return pressureMachineDTOS;
            }
            pressureMachineDTOS.forEach(o -> {
                if (o.getIp() != null) {
                    String ip = o.getIp();
                    if (ip.startsWith("[")) {
                        ip = ip.substring(1);
                    }
                    if (ip.endsWith("]")) {
                        ip = ip.substring(0, ip.length() - 1);
                    }
                    o.setIp(ip);
                }
                if (o.getConfigIp() == null) {
                    o.setConfigIp(o.getIp());
                }
            });
            return pressureMachineDTOS;

        } catch (Exception e) {
            log.error("解析benchmarkMachineUrl返回结果出现异常,返回值为:{}", sendGet, e);
        }
        return null;
    }

}
