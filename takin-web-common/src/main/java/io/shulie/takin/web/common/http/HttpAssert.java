package io.shulie.takin.web.common.http;

import com.alibaba.fastjson.JSON;

import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.web.common.domain.ErrorInfo;
import io.shulie.takin.web.common.domain.WebResponse;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.exception.TakinWebExceptionEnum;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * http 返回值断言
 */
@Slf4j
public class HttpAssert {

    /**
     * @param webResponse 请求返回的response
     * @param param       请求入参
     * @param eventName   事件(此次请求做什么)，比如 调用xxx查询xx
     */
    public static void isOk(WebResponse webResponse, Object param, String eventName){
        if (!Objects.isNull(webResponse)) {
            if (webResponse.getSuccess()) {
                return;
            }
            ErrorInfo error = webResponse.getError();
            String errorMsg = Objects.isNull(error) ? "" : error.getMsg();
            log.error(eventName + "返回异常,入参={},错误信息={}", JSON.toJSONString(param), JSON.toJSONString(error));
            throw new TakinWebException(TakinWebExceptionEnum.SCENE_REPORT_THIRD_PARTY_ERROR, eventName + "返回异常！原因为" + errorMsg);
        } else {
            log.error(eventName + "返回为空,入参={}", JSON.toJSONString(param));
            throw new TakinWebException(TakinWebExceptionEnum.SCENE_REPORT_THIRD_PARTY_ERROR, eventName + "返回为空！");
        }
    }

    /**
     * @param webResponse 请求返回的response
     * @param param       请求入参
     * @param eventName   事件(此次请求做什么)，比如 调用xxx查询xx
     */
    public static void isOk(ResponseResult webResponse, Object param, String eventName){
        if (!Objects.isNull(webResponse)) {
            if (webResponse.getSuccess()) {
                return;
            }
            ResponseResult.ErrorInfo error = webResponse.getError();
            String errorMsg = Objects.isNull(error) ? "" : error.getMsg();
            log.error(eventName + "返回异常,入参={},错误信息={}", JSON.toJSONString(param), JSON.toJSONString(error));
            throw new TakinWebException(TakinWebExceptionEnum.SCENE_REPORT_THIRD_PARTY_ERROR, eventName + "返回异常！原因为" + errorMsg);
        } else {
            log.error(eventName + "返回为空,入参={}", JSON.toJSONString(param));
            throw new TakinWebException(TakinWebExceptionEnum.SCENE_REPORT_THIRD_PARTY_ERROR, eventName + "返回为空！");
        }
    }
}
