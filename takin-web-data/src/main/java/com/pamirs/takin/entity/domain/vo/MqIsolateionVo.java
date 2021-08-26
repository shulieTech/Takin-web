package com.pamirs.takin.entity.domain.vo;

/**
 * @author xingchen
 * @ClassName: MqIsolateionVo
 * @package: com.pamirs.takin.entity.domain.vo
 * @date 2019/5/21下午3:46
 */
public class MqIsolateionVo {
    /**
     * 可隔离的broker地址,写入gen.conf 的文件
     */
    private String isoBrokerAddr;

    /**
     * geo文件名
     */
    private String geoFileName;
    /**
     * 待切换nameserver文件拼接名称
     */
    private String clusterName;
    /**
     * 写入文件内容
     */
    private String fileContent;

    public String getFileContent() {
        return fileContent;
    }

    public void setFileContent(String fileContent) {
        this.fileContent = fileContent;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getIsoBrokerAddr() {
        return isoBrokerAddr;
    }

    public void setIsoBrokerAddr(String isoBrokerAddr) {
        this.isoBrokerAddr = isoBrokerAddr;
    }

    public String getGeoFileName() {
        return geoFileName;
    }

    public void setGeoFileName(String geoFileName) {
        this.geoFileName = geoFileName;
    }
}
