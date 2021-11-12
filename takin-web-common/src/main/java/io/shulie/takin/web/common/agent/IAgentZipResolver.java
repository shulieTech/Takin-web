package io.shulie.takin.web.common.agent;

import java.util.List;

/**
 * @Description agent包解析器
 * @Author ocean_wll
 * @Date 2021/11/10 8:01 下午
 */
public interface IAgentZipResolver {

    /**
     * 解析上传的agent包是否正确
     *
     * @param filePath 文件路径
     * @return 错误信息
     */
    List<String> checkFile(String filePath);

    /**
     * 读取module.properties文件内容
     *
     * @param filePath 文件路径
     * @return module.properties文件内容
     */
    String readModuleInfo(String filePath);
}
