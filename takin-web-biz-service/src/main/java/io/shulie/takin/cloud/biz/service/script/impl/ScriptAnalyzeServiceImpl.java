package io.shulie.takin.cloud.biz.service.script.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import io.shulie.takin.adapter.api.entrypoint.script.ScriptFileApi;
import io.shulie.takin.adapter.api.model.request.script.ScriptVerifyRequest;
import io.shulie.takin.cloud.biz.service.script.ScriptAnalyzeService;
import io.shulie.takin.cloud.common.exception.TakinCloudException;
import io.shulie.takin.cloud.common.exception.TakinCloudExceptionEnum;
import io.shulie.takin.cloud.common.script.util.SaxUtil;
import io.shulie.takin.cloud.common.utils.JmxUtil;
import io.shulie.takin.cloud.common.utils.UrlUtil;
import io.shulie.takin.cloud.ext.content.enums.NodeTypeEnum;
import io.shulie.takin.cloud.ext.content.script.ScriptNode;
import io.shulie.takin.cloud.ext.content.script.ScriptParseExt;
import io.shulie.takin.cloud.ext.content.script.ScriptUrlExt;
import io.shulie.takin.cloud.ext.content.script.ScriptVerityExt;
import io.shulie.takin.cloud.ext.content.script.ScriptVerityRespExt;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ScriptAnalyzeServiceImpl implements ScriptAnalyzeService {

    @Resource
    private ScriptFileApi scriptFileApi;

    @Override
    public ScriptVerityRespExt verityScript(ScriptVerityExt scriptVerityExt) {
        ScriptVerityRespExt scriptVerityRespExt = new ScriptVerityRespExt();
        List<String> errorMsgList = new ArrayList<>();
        scriptVerityRespExt.setErrorMsg(errorMsgList);

        if (CollectionUtils.isEmpty(scriptVerityExt.getRequest())) {
            throw new TakinCloudException(TakinCloudExceptionEnum.SCRIPT_VERITY_ERROR, "脚本校验业务活动不能为空");
        }
        List<String> requestList;
        try {
            requestList = getRequestUrls(scriptVerityExt.getScriptPath());
        } catch (Exception e) {
            errorMsgList.add("脚本解析失败:" + e.getMessage());
            return scriptVerityRespExt;
        }

        if (CollectionUtils.isEmpty(requestList)) {
            errorMsgList.add("脚本中没有获取到请求链接！");
            return scriptVerityRespExt;
        }
        // 混合压测前的业务逻辑
        if (Integer.valueOf(0).equals(scriptVerityExt.getVersion())) {
            List<String> oldErrorMsgList = checkScriptFileContainsRequestUrl(scriptVerityExt.getScriptPath(),
                scriptVerityExt.getRequest());
            if (oldErrorMsgList.size() > 0) {errorMsgList.addAll(oldErrorMsgList);}
        }
        // 增加新版压测校验
        if (scriptVerityExt.isUseNewVerify()) {
            try {
                ScriptVerifyRequest request = new ScriptVerifyRequest();
                request.setScriptPath(scriptVerityExt.getScriptPaths());
                request.setCsvPaths(scriptVerityExt.getCsvPaths());
                request.setAttachments(scriptVerityExt.getAttachments());
                request.setPluginPaths(scriptVerityExt.getPluginPaths());
                scriptFileApi.verify(request);
            } catch (Exception e) {
                log.error("jmx校验异常", e);
                errorMsgList.add(e.getMessage());
            }
        }
        {return scriptVerityRespExt;}
    }

    @Override
    public void updateScriptContent(String uploadPath) {
        SaxUtil.updateJmx(uploadPath);
    }

    @Override
    public ScriptParseExt parseScriptFile(String uploadPath) {
        return SaxUtil.parseJmx(uploadPath);
    }

    @Override
    public List<ScriptNode> buildNodeTree(String scriptFile) {
        return JmxUtil.buildNodeTree(scriptFile);
    }

    private List<String> getRequestUrls(String filePath) {
        List<ScriptNode> scriptNodes = JmxUtil.buildNodeTree(filePath);
        List<ScriptNode> samplerScriptNodes = JmxUtil.getScriptNodeByType(NodeTypeEnum.SAMPLER, scriptNodes);
        if (CollectionUtils.isNotEmpty(samplerScriptNodes)) {
            return samplerScriptNodes.stream().map(ScriptNode::getRequestPath).filter(Objects::nonNull).collect(
                Collectors.toList());
        }
        return null;
    }

    /**
     * 检查脚本文件中包含请求路径的逻辑
     * <p>不包含则抛出异常</p>
     *
     * @param scriptFilePath 脚本文件路径
     * @param requestUrlList 请求路径集合
     * @return 错误描述集合
     */
    private List<String> checkScriptFileContainsRequestUrl(String scriptFilePath, List<String> requestUrlList) {
        List<String> errorMsgList = new LinkedList<>();
        try {
            ScriptParseExt scriptParseExt = SaxUtil.parseJmx(scriptFilePath);
            List<ScriptUrlExt> requestUrl = scriptParseExt.getRequestUrl();
            Set<String> errorSet = new HashSet<>();
            int unbindCount = 0;
            Map<String, Integer> urlMap = new HashMap<>(requestUrl.size());
            for (String request : requestUrlList) {
                Set<String> tempErrorSet = new HashSet<>();
                for (ScriptUrlExt url : requestUrl) {
                    if (UrlUtil.checkEqual(request, url.getPath()) && url.getEnable()) {
                        unbindCount = unbindCount + 1;
                        tempErrorSet.clear();
                        if (!urlMap.containsKey(url.getName())) {
                            urlMap.put(url.getName(), 1);
                        } else {
                            urlMap.put(url.getName(), urlMap.get(url.getName()) + 1);
                        }
                        break;
                    } else {
                        tempErrorSet.add(request);
                    }
                }
                errorSet.addAll(tempErrorSet);
            }

            Set<String> urlErrorSet = new HashSet<>();
            urlMap.forEach((k, v) -> {
                if (v > 1) {
                    urlErrorSet.add("脚本中[" + k + "]重复" + v + "次");
                }
            });
            if (urlErrorSet.size() > 0) {
                errorMsgList.add("脚本文件配置不正确:" + urlErrorSet);
            }
            //存在业务活动都关联不上脚本中的请求连接
            if (requestUrl.size() > unbindCount) {
                errorMsgList.add("业务活动与脚本文件不匹配:" + errorSet);
            }
        } catch (Exception e) {
            errorMsgList.add(e.getMessage());
        }
        return errorMsgList;
    }
}
