//package io.shulie.takin.web.diff.cloud.impl.middleware;
//
//import java.util.List;
//
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.google.common.collect.Lists;
//import io.shulie.takin.cloud.open.api.impl.CloudCommonApi;
//import io.shulie.takin.cloud.open.api.middleware.CloudMiddlewareApi;
//import io.shulie.takin.cloud.open.api.report.CloudReportApi;
//import io.shulie.takin.cloud.open.constant.CloudApiConstant;
//import io.shulie.takin.cloud.open.resp.middleware.MiddlewareLibraryResp;
//import io.shulie.takin.common.beans.response.ResponseResult;
//import io.shulie.takin.utils.http.HttpHelper;
//import io.shulie.takin.utils.http.takinResponseEntity;
//import io.shulie.takin.web.diff.api.middleware.MiddlewareApi;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.autoconfigure.takin.properties.takinCloudClientProperties;
//
///**
// * @author 无涯
// * @date 2021/2/24 4:03 下午
// */
//public class MiddlewareApiImpl implements MiddlewareApi {
//
//    @Autowired
//    private CloudMiddlewareApi cloudMiddlewareApi;
//    @Override
//    public List<MiddlewareLibraryResp> getList() {
//        ResponseResult<List<MiddlewareLibraryResp>> responseResult = cloudMiddlewareApi.getList();
//        if(responseResult.getSuccess()) {
//            return responseResult.getData();
//        }
//        return Lists.newArrayList();
//    }
//}
