package io.shulie.takin.web.common.agent;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.lang.StringUtils;

/**
 * @Description 抽象类agent解析器
 * @Author ocean_wll
 * @Date 2021/11/10 8:04 下午
 */
public abstract class AgentZipResolverSupport implements IAgentZipResolver {

    /**
     * 抽象方法，不同实现类去检查不同的文件
     *
     * @param filePath 文件路径
     * @return list集合
     */
    public abstract List<String> checkFile0(String filePath);

    /**
     * 获取zip包根文件夹名称
     *
     * @return 根文件夹名称
     */
    public abstract String getZipBaseDirName();

    @Override
    public List<String> checkFile(String filePath) {
        List<String> errorMessages = new ArrayList<>();
        // 检查 module.properties 是否存在
        if (!checkPropertiesExists(filePath)) {
            errorMessages.add("缺少module.properties");
        } else {
            errorMessages.addAll(checkFile0(filePath));
        }
        return errorMessages;
    }

    /**
     * 检查根目录下是否存在 module.properties 文件
     *
     * @return true 存在，false 不存在
     */
    protected Boolean checkPropertiesExists(String filePath) {
        boolean result = false;
        if (StringUtils.isBlank(filePath) || !filePath.endsWith(".zip")) {
            return false;
        }

        try (ZipFile zip = new ZipFile(new File(filePath));) {
            ZipEntry zipEntry = zip.getEntry(getZipBaseDirName() + File.separator + "module.properties");
            if (zipEntry != null) {
                result = true;
            }
        } catch (IOException e) {
            // ignore
        }

        return result;
    }
}
