package io.shulie.takin.web.amdb.api;

/**
 * 链路相关
 *
 * @author shiyajian
 * create: 2020-12-14
 */
public interface LinkClient {

    /**
     * 根据入口名称梳理出整条链路下面的信息
     * @param entryName
     * @return
     */
    Object getFullLinkByEntryName(String entryName);
}
