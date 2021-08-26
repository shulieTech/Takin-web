package io.shulie.takin.web.biz.utils.business.script;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import io.shulie.takin.web.amdb.enums.LinkResultCodeEnum;
import io.shulie.takin.web.biz.constant.ScriptDebugConstants;
import io.shulie.takin.web.biz.pojo.response.scriptmanage.ScriptDebugRequestListResponse;
import io.shulie.takin.web.common.enums.script.ScriptDebugStatusEnum;
import io.shulie.takin.web.common.vo.script.RequestAssertDetailVO;

/**
 * 脚本下的调试记录业务工具类
 *
 * @author liuchuan
 * @date 2021/5/12 3:21 下午
 */
public class ScriptDebugUtil {

    /**
     * 是否配置了漏数校验
     *
     * @param leakStatus 漏数状态
     * @return 是否
     */
    public static boolean noLeakConfig(Integer leakStatus) {
        return leakStatus != null && ScriptDebugConstants.NO_LEAK_CONFIG == leakStatus;
    }

    /**
     * 请求列表状态相关
     *
     * @param resultCode   响应状态, 0: 成功 1:失败 2:dubbo 错误 3:超时 4:未知 5:断言失败
     * @param assertResult 断言失败相关, 字符串 json 形式
     * @return 请求列表状态相关 对象
     */
    public static ScriptDebugRequestListResponse getRequestListStatusResponse(String resultCode, String assertResult) {
        ScriptDebugRequestListResponse response = new ScriptDebugRequestListResponse();

        // resultCode 判断, 赋值
        LinkResultCodeEnum linkResultCodeEnum = ScriptDebugUtil.getLinkResultCodeEnumByResultCode(resultCode);
        response.setResponseStatus(Integer.valueOf(linkResultCodeEnum.getCode()));
        response.setResponseStatusDesc(linkResultCodeEnum.getDesc());

        // 如果是失败, 或者断言失败
        // 断言结果反序列化, 赋值
        if (ScriptDebugUtil.isFailedOrFailedAssert(linkResultCodeEnum) &&
            JSONUtil.isJsonArray(assertResult)) {
            response.setAssertDetailList(JSONUtil.toList(assertResult, RequestAssertDetailVO.class));
        }

        return response;
    }

    /**
     * 失败, 或者 断言失败
     *
     * @param linkResultCodeEnum 流量明细状态
     * @return 是否是失败, 断言失败
     */
    public static boolean isFailedOrFailedAssert(LinkResultCodeEnum linkResultCodeEnum) {
        return LinkResultCodeEnum.FAILED.equals(linkResultCodeEnum) ||
            LinkResultCodeEnum.FAILED_ASSERT.equals(linkResultCodeEnum);
    }

    /**
     * 通过请求流量的 resultCode, 获得对应的枚举
     *
     * @param resultCode 记录结果
     * @return 调试流量明细记录状态枚举
     */
    public static LinkResultCodeEnum getLinkResultCodeEnumByResultCode(String resultCode) {
        LinkResultCodeEnum linkResultCodeEnum = null;
        if (StringUtils.isNotBlank(resultCode)) {
            linkResultCodeEnum = LinkResultCodeEnum.getByCode(resultCode);
        }

        if (linkResultCodeEnum == null) {
            linkResultCodeEnum = LinkResultCodeEnum.FAILED_UNKNOWN;
        }

        return linkResultCodeEnum;
    }

    /**
     * 是请求完成的状态
     *
     * @param status 状态
     * @return 是否
     */
    public static boolean isRequestEnd(Integer status) {
        return ScriptDebugStatusEnum.REQUEST_END.getCode().equals(status);
    }

    /**
     * 是调试失败的状态
     *
     * @param status 状态
     * @return 是否
     */
    public static boolean isFailed(Integer status) {
        return ScriptDebugStatusEnum.FAILED.getCode().equals(status);
    }

    /**
     * 是调试成功的状态
     *
     * @param status 状态
     * @return 是否
     */
    public static boolean isSuccess(Integer status) {
        return ScriptDebugStatusEnum.SUCCESS.getCode().equals(status);
    }

    /**
     * 是否是完成状态
     * 成功或失败
     *
     * @param status 状态
     * @return 是否
     */
    public static boolean isFinished(Integer status) {
        return isSuccess(status) || isFailed(status);
    }

}
