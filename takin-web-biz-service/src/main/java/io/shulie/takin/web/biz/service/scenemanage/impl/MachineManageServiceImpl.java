package io.shulie.takin.web.biz.service.scenemanage.impl;

import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
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
import io.shulie.takin.web.biz.utils.SshInitUtil;
import io.shulie.takin.web.common.util.BeanCopyUtils;
import io.shulie.takin.web.data.dao.scenemanage.MachineManageDAO;
import io.shulie.takin.web.data.model.mysql.MachineManageEntity;
import io.shulie.takin.web.ext.api.user.WebUserExtApi;
import io.shulie.takin.web.ext.entity.UserExt;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

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

    @Value("${docker.cmd.download: cd /data && wget https://install-pkg.oss-cn-hangzhou.aliyuncs.com/alone-pkg/docker-compose_install.zip && unzip docker-compose_install.zip -x __MACOSX/* && mv docker-compose_install/* ./ }")
    private String dockerDownloadCmd;
    @Value("${docker.cmd.install: cd /data && sh docker-compose_install.sh}")
    private String dockerInstallCmd;
    @Value("${docker.cmd.pull: docker pull 192.168.10.11/library/BENCHMARK_SUITE_NAME:latest}")
    private String dockerPullCmd;
    @Value("${docker.cmd.run: docker run -itd --net=host --name BENCHMARK_SUITE_NAME 192.168.10.11/library/BENCHMARK_SUITE_NAME:latest}")
    private String dockerRunCmd;
    @Value("${docker.cmd.replaceAndRun: cd /data && wget https://shulie-daily.oss-cn-hangzhou.aliyuncs.com/yidongyun/pressure-engine.zip && unzip pressure-engine.zip}")
    private String dockerReplaceAndRunCmd;


    @Value("${harbor.machine.ip: 192.168.10.11}")
    private String harborMachineIp;
    @Value("${harbor.machine.username: root}")
    private String harborMachineUserName;
    @Value("${harbor.machine.password: shulie@2020}")
    private String harborMachinePassword;
    @Value("${docker.start.timeout: 15}")
    private Integer dockerStartTimeout;

    private SymmetricCrypto des;
    private final static ExecutorService THREAD_POOL = new ThreadPoolExecutor(4, 20,
            300L, TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(100), new ThreadFactoryBuilder()
            .setNameFormat("machine-manage-exec-%d").build(), new ThreadPoolExecutor.AbortPolicy());

    private static final ConcurrentHashMap<Long, String> deployStatusMap = new ConcurrentHashMap<>();

    private static final List<String> deployProgressList = new ArrayList<>();

    @Override
    public void afterPropertiesSet() throws Exception {
        //这样需要同时拿到数据库，代码，配置文件密码才会泄露
        des = new SymmetricCrypto(SymmetricAlgorithm.DES, (machinePasswordSaltPre + "fankfneioqn").getBytes());

        benchmarkMachineUrl = "http://" + benchmarkServerIp + ":" + benchmarkServerPort + "/api/machine/list?type=1";
        benchmarkSuiteListUrl = "http://" + benchmarkServerIp + ":" + benchmarkServerPort + "/api/benchmarksuite/getOrders";
        benchmarkUnInstallUrl = "http://" + benchmarkServerIp + ":" + benchmarkServerPort + "/api/engine/unInstall";

        //初始化进度列表
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

        QueryWrapper<MachineManageEntity> ipQueryWrapper = new QueryWrapper<>();
        ipQueryWrapper.lambda().eq(MachineManageEntity::getMachineIp, request.getMachineIp());
        List<MachineManageEntity> ipList = machineManageDAO.list(ipQueryWrapper);
        if (CollectionUtils.isNotEmpty(ipList)) {
            return "机器ip已存在";
        }

        MachineManageEntity machineManageEntity = new MachineManageEntity();
        machineManageEntity.setMachineName(request.getMachineName());
        machineManageEntity.setMachineIp(request.getMachineIp());
        machineManageEntity.setUserName(request.getUserName());
        machineManageEntity.setPassword(des.encryptHex(request.getPassword()));
        machineManageEntity.setStatus(0);
        machineManageEntity.setRemark(request.getRemark());
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
        if (CollectionUtils.isNotEmpty(pressureMachineResponses)) {
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
                    Map<String, List<PressureMachineDTO>> stringListMap = pressureMachineDTOS.stream().collect(Collectors.groupingBy(PressureMachineDTO::getIp));
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
        return PagingList.of(pressureMachineResponses, manageEntityPage.getTotal());
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
        if (MachineManageConstants.TYPE_TAKIN.equals(manageDAOById.getDeployType())) {
            //卸载已部署的节点
            MachineBaseReq baseReq = new MachineBaseReq();
            baseReq.setNodeName(manageDAOById.getMachineName());
            WebPluginUtils.fillCloudUserData(baseReq);
            cloudMachineApi.delete(baseReq);
        }
        if (MachineManageConstants.TYPE_BENCHMARK.equals(manageDAOById.getDeployType())) {
            SshInitUtil sshInitUtil = new SshInitUtil(manageDAOById.getMachineIp(), des.decryptStr(manageDAOById.getPassword()),
                    manageDAOById.getUserName());
            //停止容器服务
            String stopExec = sshInitUtil.execute("docker stop " + manageDAOById.getBenchmarkSuiteName());
            log.info("停止容器日志：" + stopExec);
            //删除容器
            String deleteExec = sshInitUtil.execute("docker rm -f " + manageDAOById.getBenchmarkSuiteName());
            log.info("删除容器日志：" + deleteExec);
            //删除镜像
            String dockerRmi = dockerPullCmd.replace("docker pull", "docker rmi -f").replace("BENCHMARK_SUITE_NAME", manageDAOById.getBenchmarkSuiteName());
            String deleteImageExec = sshInitUtil.execute(dockerRmi);
            log.info("删除镜像日志：" + deleteImageExec);
            Map<String, String> headerMap = getHeaderMap(httpRequest);
            unInstallBenchmark(headerMap, manageDAOById.getMachineIp(), manageDAOById.getBenchmarkSuiteName());
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
        log.info("开始调用获取{}用户的机器信息,用户名为:{}", url, externalName);
        Map<String, String> header = new HashMap<>();
        header.put("X-DEVOPS-UID", externalName);
        String result = HttpClientUtil.sendGet(url, header);
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
                SshInitUtil sshInitUtil = new SshInitUtil(manageDAOById.getMachineIp(), des.decryptStr(manageDAOById.getPassword()),
                        manageDAOById.getUserName());
//                //机器联通测试
//                String checkMachineExec = sshInitUtil.execute("echo machine_test");
//                if (checkMachineExec == null || !checkMachineExec.contains("machine_test")){
//
//                }

                //docker环境安装
                String checkDockerExec = sshInitUtil.execute("docker -v");
                if (checkDockerExec == null || !checkDockerExec.contains("version")) {
                    log.info("当前服务不存在docker环境,开始拉取docker环境安装包:" + checkDockerExec);
                    deployStatusMap.put(request.getId(), "拉docker环境安装包");
                    String dockerPullExec = sshInitUtil.execute(dockerDownloadCmd);
                    log.info("拉docker环境安装包日志：" + dockerPullExec);
                    log.info("安装docker环境");
                    deployStatusMap.put(request.getId(), "安装docker环境");
                    String dockerInstallExec = sshInitUtil.execute("source /etc/profile && " + dockerInstallCmd);
                    log.info("安装日志：" + dockerInstallExec);
                }
                //检测harbor配置
                String checkHarborExec = sshInitUtil.execute("cat /etc/docker/daemon.json");
                if (checkHarborExec == null || !checkHarborExec.contains(harborMachineIp)) {
                    log.info("开始修改目标机器的harbor配置");
                    String harborConf = "sed -i 's/\"quay.io\"/\"quay.io\",\"" + harborMachineIp + "\"/' /etc/docker/daemon.json";
                    String harborConfExec = sshInitUtil.execute(harborConf + " && systemctl daemon-reload && systemctl restart docker");
                    log.info("harbor配置修改日志：" + harborConfExec);
                }

                //设置harbor白名单
                deployStatusMap.put(request.getId(), "设置harbor白名单");
                log.info("开始设置harbor白名单");
                SshInitUtil harborShellUtil = this.getHarborShellInfo();
                String iptablesAccept = "iptables -I INPUT -s " + manageDAOById.getMachineIp() + " -p TCP --dport 80 -j ACCEPT";
                String harborShellExec = harborShellUtil.execute(iptablesAccept);
                log.info("设置harbor白名单日志：" + harborShellExec);

                //拉取镜像
                deployStatusMap.put(request.getId(), "拉取镜像");
                String dockerPull = dockerPullCmd.replace("BENCHMARK_SUITE_NAME", request.getBenchmarkSuiteName());
                log.info("开始拉取镜像，镜像命令为:{}", dockerPull);
                String dockerPullExec = sshInitUtil.execute(dockerPull);
                log.info("拉取镜像日志：" + dockerPullExec);

                //启动容器
                deployStatusMap.put(request.getId(), "启动容器");
                String dockerRun = dockerRunCmd.replaceAll("BENCHMARK_SUITE_NAME", request.getBenchmarkSuiteName());
                log.info("开始执行docker命令，运行命令为:{}", dockerRun);
                String dockerRunExec = sshInitUtil.execute(dockerRun);
                log.info("启动容器日志：" + dockerRunExec);

                //替换配置文件
                String dockerReplaceAndRun = dockerReplaceAndRunCmd + " && sed -i 's/LOCAL_PASSWORD/" + des.decryptStr(manageDAOById.getPassword()) + "/' ./pressure-engine/config/application-engine.yml && " +
                        "sed -i 's/TAKIN_LITE_IP/" + benchmarkServerIp + "/' ./pressure-engine/config/application-engine.yml && " +
                        "sed -i 's/TAKIN_LITE_PORT/" + benchmarkServerPort + "/' ./pressure-engine/config/application-engine.yml && " +
                        "sed -i 's/LOCALHOST_IP/" + manageDAOById.getMachineIp() + "/' ./pressure-engine/config/application-engine.yml && " +
                        "sed -i 's/USER_APPKEY/" + benchmarkUserAppKey + "/' ./pressure-engine/config/application-engine.yml && " +
                        "sed -i 's/SUITE_NAME/" + manageDAOById.getBenchmarkSuiteName() + "/' ./pressure-engine/config/application-engine.yml ";
                //替换并启动
                dockerReplaceAndRun = dockerReplaceAndRun + " && rm -f pressure-engine.zip && zip -r pressure-engine.zip ./pressure-engine && " +
                        "docker cp pressure-engine.zip " + manageDAOById.getBenchmarkSuiteName() + ":/data/pressure-engine.zip && rm -f pressure-engine.zip && rm -rf ./pressure-engine " +
                        "&& docker exec " + manageDAOById.getBenchmarkSuiteName() + " /bin/bash -c 'cd /data && mv pressure-engine pressure-engine_bak " +
                        "&& unzip pressure-engine.zip && cd pressure-engine && sh start.sh'";
                deployStatusMap.put(request.getId(), "启动服务");
                String replaceAndRunExec = sshInitUtil.execute(dockerReplaceAndRun);
                log.info("替换配置并启动服务日志：" + replaceAndRunExec);
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
                            long count = pressureMachineDTOS.stream().filter(o -> manageDAOById.getMachineIp().equals(o.getConfigIp())
                                    && manageDAOById.getBenchmarkSuiteName().trim().equals(o.getTypeMachine().trim())).count();
                            if (count > 0) {
                                deployStatusMap.put(request.getId(), "启动成功");
                                log.info("启动成功，结束监听");
                                break;
                            }
                        }
                    } catch (Exception e) {
                        log.error("查询已部署机器列表出现异常", e);
                    }
                }
            } catch (Exception e) {
                log.error("监听部署过程出现异常", e);
            } finally {
                //部署成功
                deployStatusMap.remove(request.getId());
                manageDAOById.setStatus(2);
                manageDAOById.setUpdateTime(new Date());
                machineManageDAO.updateById(manageDAOById);
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

    private Map<String, String> getHeaderMap(HttpServletRequest httpRequest) {
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("token", Objects.requireNonNull(httpRequest.getHeader("x-token")));
        headerMap.put("envCode", Objects.requireNonNull(httpRequest.getHeader("env-code")));
        headerMap.put("tenantCode", Objects.requireNonNull(httpRequest.getHeader("tenant-code")));
        return headerMap;
    }

    private void unInstallBenchmark(Map<String, String> headerMap, String ip, String suiteName) {
        HttpClientUtil.sendGet(benchmarkUnInstallUrl + "?configIp=" + ip + "&typeMachine="
                + suiteName + "&userAppKey=" + benchmarkUserAppKey, headerMap);
    }

    private List<PressureMachineDTO> getPressureMachineDTOList(Map<String, String> headerMap) {
        String sendGet = HttpClientUtil.sendGet(benchmarkMachineUrl, headerMap);
        try {
            JSONObject jsonObject = JSONObject.parseObject(sendGet);
            Object data = jsonObject.get("data");
            if (data != null) {
                List<PressureMachineDTO> pressureMachineDTOS = JSONObject.parseArray(data.toString(), PressureMachineDTO.class);
                //处理ip上面的特殊字符
                if (CollectionUtils.isNotEmpty(pressureMachineDTOS)) {
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
                    });
                }
                return pressureMachineDTOS;
            }
        } catch (Exception e) {
            log.error("解析benchmarkMachineUrl返回结果出现异常,返回值为:{}", sendGet, e);
        }
        return null;
    }

}
