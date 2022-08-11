package io.shulie.takin.web.biz.service.scenemanage.impl;

import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import cn.hutool.http.HttpRequest;
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
import io.shulie.takin.web.biz.pojo.dto.ShellInfo;
import io.shulie.takin.web.biz.pojo.dto.machinemanage.PressureMachineDTO;
import io.shulie.takin.web.biz.pojo.request.scene.*;
import io.shulie.takin.web.biz.pojo.response.scene.BenchmarkSuiteResponse;
import io.shulie.takin.web.biz.service.scenemanage.MachineManageService;
import io.shulie.takin.web.biz.utils.ShellClient;
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
    @Value("${benchmark.machine.list.url: http://192.168.1.220/api/machine/list?type=1}")
    private String benchmarkMachineUrl;
    @Value("${benchmark.suite.list.url: http://192.168.1.220/api/benchmarksuite/getOrders}")
    private String benchmarkSuiteListUrl;
    @Value("${docker.cmd.download: cd /data && wget https://install-pkg.oss-cn-hangzhou.aliyuncs.com/alone-pkg/docker-compose_install.zip && unzip docker-compose_install.zip -x __MACOSX/* && mv docker-compose_install/* ./ }")
    private String dockerDownloadCmd;
    @Value("${docker.cmd.install: cd /data && sh docker-compose_install.sh}")
    private String dockerInstallCmd;
    @Value("${docker.cmd.pull: docker pull 192.168.10.11/library/BENCHMARK_SUITE_NAME:latest}")
    private String dockerPullCmd;
    @Value("${docker.cmd.run: docker run -itd --net=host --name BENCHMARK_SUITE_NAME BENCHMARK_SUITE_NAME:latest}")
    private String dockerRunCmd;

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

    @Override
    public void afterPropertiesSet() throws Exception {
        //这样需要同时拿到数据库，代码，配置文件密码才会泄露
        des = new SymmetricCrypto(SymmetricAlgorithm.DES, (machinePasswordSaltPre + "fankfneioqn").getBytes());
    }

    @Resource
    private MachineManageDAO machineManageDAO;
    @Resource
    private CloudMachineApi cloudMachineApi;
    @Resource
    private PluginManager pluginManager;
    @Resource
    private ShellClient shellClient;

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
    public PagingList<PressureMachineResponse> list(PressureMachineQueryRequest request, HttpRequest httpRequest) {
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
                            pressureMachineResponse.setMemory(nodeMetricsResp.getMemory() == null ? "" : nodeMetricsResp.getMemory().toString());
                            pressureMachineResponse.setEngineStatus(nodeMetricsResp.getStatus());
                        }
                    });
                }
            }
            long benchmarkCount = pressureMachineResponses.stream().filter(o -> MachineManageConstants.TYPE_BENCHMARK.equals(o.getDeployType())).count();
            if (benchmarkCount > 0) {
                List<PressureMachineDTO> pressureMachineDTOS = this.getPressureMachineDTOList(httpRequest);
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
    public void delete(PressureMachineBaseRequest request) {
        MachineManageEntity manageDAOById = machineManageDAO.getById(request.getId());
        if (manageDAOById == null) {
            return;
        }
        //部署中的内容不能删除
        if (manageDAOById.getStatus() == 1) {
            return;
        }
        //已部署的节点需要先进行卸载
        if (manageDAOById.getStatus() == 2) {
            //卸载已部署的节点
            PressureMachineBaseRequest disableRequest = new PressureMachineBaseRequest();
            disableRequest.setId(request.getId());
            this.disable(disableRequest);
        }
        machineManageDAO.removeById(request.getId());
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
    public String disable(PressureMachineBaseRequest request) {
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
            //停止容器服务
            List<String> stopExec = shellClient.exec(getShellInfo(manageDAOById, "docker stop " + manageDAOById.getBenchmarkSuiteName()));
            log.info("停止容器日志：" + stopExec);
            //删除容器
            List<String> deleteExec = shellClient.exec(getShellInfo(manageDAOById, "docker rm -f " + manageDAOById.getBenchmarkSuiteName()));
            log.info("删除容器日志：" + deleteExec);
            //删除镜像
            List<String> deleteImageExec = shellClient.exec(getShellInfo(manageDAOById, "docker rmi -f " + manageDAOById.getBenchmarkSuiteName()));
            log.info("删除镜像日志：" + deleteImageExec);
            manageDAOById.setBenchmarkSuiteName(null);
        }
        manageDAOById.setDeployType(null);
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
    public String benchmarkEnable(PressureMachineBaseRequest request, HttpRequest httpRequest) {
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
                List<String> checkDockerExec = shellClient.exec(getShellInfo(manageDAOById, "docker -v"));
                if (CollectionUtils.isEmpty(checkDockerExec) || !checkDockerExec.toString().contains("version")) {
                    log.info("当前服务不存在docker环境,开始拉取docker环境安装包");
                    deployStatusMap.put(request.getId(), "拉docker环境安装包");
                    List<String> dockerPullExec = shellClient.exec(getShellInfo(manageDAOById, dockerDownloadCmd));
                    log.info("安装docker环境");
                    deployStatusMap.put(request.getId(), "安装docker环境");
                    List<String> dockerInstallExec = shellClient.exec(getShellInfo(manageDAOById, dockerInstallCmd));
                }
                //设置harbor白名单
                deployStatusMap.put(request.getId(), "设置harbor白名单");
                log.info("开始设置harbor白名单");
                shellClient.exec(this.getHarborShellInfo(manageDAOById.getMachineIp()));

                //拉取镜像
                deployStatusMap.put(request.getId(), "拉取镜像");
                String dockerPull = dockerPullCmd.replace("BENCHMARK_SUITE_NAME", request.getBenchmarkSuiteName());
                log.info("开始拉取镜像，镜像命令为:{}", dockerPull);
                List<String> dockerPullExec = shellClient.exec(getShellInfo(manageDAOById, dockerPull));

                //启动容器
                deployStatusMap.put(request.getId(), "启动容器");
                String dockerRun = dockerRunCmd.replaceAll("BENCHMARK_SUITE_NAME", request.getBenchmarkSuiteName());
                log.info("开始执行docker命令，运行命令为:{}", dockerRun);
                List<String> dockerRunExec = shellClient.exec(getShellInfo(manageDAOById, dockerRun));

                //监听启动成功
                long startTimeMillis = System.currentTimeMillis();
                while (true) {
                    try {
                        if (System.currentTimeMillis() - startTimeMillis > dockerStartTimeout * 1000 * 60) {
                            log.error("docker启动超过指定时间，设置为成功，机器状态还是不会成功，让客户从页面卸载");
                            break;
                        }
                        Thread.sleep(3000);
                        List<PressureMachineDTO> pressureMachineDTOS = this.getPressureMachineDTOList(httpRequest);
                        if (CollectionUtils.isNotEmpty(pressureMachineDTOS)) {
                            List<String> stringList = pressureMachineDTOS.stream().map(PressureMachineDTO::getIp).collect(Collectors.toList());
                            if (stringList.contains(manageDAOById.getMachineIp())) {
                                deployStatusMap.put(request.getId(), "启动成功");
                                log.info("启动成功，结束监听");
                                break;
                            }
                        }
                    } catch (Exception e) {
                        log.error("查询已部署机器列表出现异常");
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

    private ShellInfo getShellInfo(MachineManageEntity entity, String cmd) {
        ShellInfo shellInfo = new ShellInfo();
        shellInfo.setSsh(true);
        shellInfo.setNoPass(false);
        shellInfo.setCmd(cmd);
        shellInfo.setIp(entity.getMachineIp());
        shellInfo.setName(entity.getUserName());
        shellInfo.setPassword(des.decryptStr(entity.getPassword()));
        shellInfo.setSpawn(false);
        return shellInfo;
    }

    private ShellInfo getHarborShellInfo(String machineIp) {
        String iptablesAccept = "iptables -I INPUT -s " + machineIp + " -p TCP --dport 80 -j ACCEPT";
        ShellInfo shellInfo = new ShellInfo();
        shellInfo.setSsh(true);
        shellInfo.setNoPass(false);
        shellInfo.setCmd(iptablesAccept);
        shellInfo.setIp(harborMachineIp);
        shellInfo.setName(harborMachineUserName);
        shellInfo.setPassword(harborMachinePassword);
        shellInfo.setSpawn(false);
        return shellInfo;
    }

    @Override
    public PagingList<BenchmarkSuiteResponse> benchmarkSuiteList(BenchmarkSuitePageRequest request, HttpRequest httpRequest) {
        String sendGet = HttpClientUtil.sendGet(benchmarkSuiteListUrl, getHeaderMap(httpRequest));
        try {
            JSONObject jsonObject = JSONObject.parseObject(sendGet);
            Object data = jsonObject.get("data");
            int total = jsonObject.getIntValue("total");
            if (data != null) {
                JSONObject dataJson = JSONObject.parseObject(data.toString());
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
    public ResponseResult<String> deployProgress(Long id) {
        String progress = deployStatusMap.get(id);
        if (progress == null) {
            return ResponseResult.fail("该机器已不在部署中", "请刷新页面");
        }
        return ResponseResult.success(progress);
    }

    private Map<String, String> getHeaderMap(HttpRequest httpRequest) {
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("token", httpRequest.header("token"));
        headerMap.put("envCode", httpRequest.header("envCode"));
        headerMap.put("tenantCode", httpRequest.header("tenantCode"));
        return headerMap;
    }

    private List<PressureMachineDTO> getPressureMachineDTOList(HttpRequest httpRequest) {
        String sendGet = HttpClientUtil.sendGet(benchmarkMachineUrl, getHeaderMap(httpRequest));
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
