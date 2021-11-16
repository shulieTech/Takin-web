package io.shulie.takin.web.common.agent;

import java.util.List;

import io.shulie.takin.web.common.enums.agentupgradeonline.PluginTypeEnum;
import io.shulie.takin.web.common.pojo.bo.agent.PluginCreateBO;

/**
 * @Description agent包解析器
 * @Author ocean_wll
 * @Date 2021/11/10 8:01 下午
 */
public interface IAgentZipResolver {

    /**
     * 获取插件枚举值
     *
     * @return PluginTypeEnum
     */
    PluginTypeEnum getPluginType();

    /**
     * 解析上传的agent包是否正确
     *
     * @param filePath 文件路径
     * @return 错误信息
     */
    List<String> checkFile(String filePath);

    /**
     * 获取zip包根文件夹名称
     *
     * @return 根文件夹名称
     */
    String getZipBaseDirName();

    /**
     * 处理agent包，解压缩，解析配置文件，获取配置信息
     *
     * @param agentPkgPath agent包地址
     * @return PluginCreateBO集合
     */
    List<PluginCreateBO> processFile(String agentPkgPath);
}
