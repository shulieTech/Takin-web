package io.shulie.takin.web.biz.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.client.naming.utils.CollectionUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import com.jayway.jsonpath.JsonPath;
import io.shulie.takin.web.biz.pojo.request.scriptmanage.ScriptCsvDataTemplateRequest;
import io.shulie.takin.web.biz.pojo.response.scriptmanage.ScriptCsvDataTemplateResponse;
import io.shulie.takin.web.ext.entity.traffic.TrafficRecorderExtResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class CsvTemplateUtils {


    /**
     * 1. URL中的数据
     * 1. 获取当前应用当前时间段的数据，sql中不使用接口只使用应用进行匹配
     * 2. 如果URL能匹配上，取出其中需要的参数
     *
     * @param request
     * @return
     */
    public static ScriptCsvDataTemplateResponse.TemplateDTO parseRestfulUrl(ScriptCsvDataTemplateRequest request) {
        ScriptCsvDataTemplateResponse.TemplateDTO requestStrDTO = new ScriptCsvDataTemplateResponse.TemplateDTO();
        // restful 从url获取
        requestStrDTO.setKey("Url参数");
        requestStrDTO.setTitle("Url参数");
        Integer count = ApiMatcher.isRestful(request.getServiceName());
        if (count > 0) {
            // 识别到时restful 格式 解析出
            List<ScriptCsvDataTemplateResponse.TemplateDTO> templateDTOList = Lists.newArrayList();
            for (int i = 0; i < count; i++) {
                ScriptCsvDataTemplateResponse.TemplateDTO templateDTO = new ScriptCsvDataTemplateResponse.TemplateDTO();
                templateDTO.setKey("url#value-" + i);
                templateDTO.setTitle("value-" + i);
                templateDTOList.add(templateDTO);
            }
            requestStrDTO.setChildren(templateDTOList);
        }
        return requestStrDTO;
    }

    /**
     *
     * @param serviceName
     * @return
     */
    public static Object readRestfulValue(String serviceName,String jsonPath) {
        if (StringUtils.isBlank(serviceName)) {
            return "";
        }
        List<Object> restfulValue = ApiMatcher.getRestfulValue(serviceName);
        try {
            String[] split = jsonPath.split("-");
            return restfulValue.size() > Integer.parseInt(split[1]) ? restfulValue.get(Integer.parseInt(split[1])) :"";

        } catch (Exception e) {
            log.error("restful解析失败", e);
            return "";
        }
    }

    /**
     * 2. requestBody
     * 1. 获取当前时间段的数据
     * 2. 通过jsonPath获取需要的数据（如果选中的是数组中的对象，只取第一个）
     *
     * @param responseList
     * @return
     */
    public static ScriptCsvDataTemplateResponse.TemplateDTO parseRequestBody(List<TrafficRecorderExtResponse> responseList) {
        ScriptCsvDataTemplateResponse.TemplateDTO requestStrDTO = new ScriptCsvDataTemplateResponse.TemplateDTO();
        requestStrDTO.setKey("requestBody参数");
        requestStrDTO.setTitle("requestBody参数");
        List<String> responseBodyList = responseList.stream().map(TrafficRecorderExtResponse::getRequestBody).filter(StringUtils::isNotBlank).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(responseBodyList)) {
            return requestStrDTO;
        }
        requestStrDTO.setChildren(mergeJson(responseBodyList, "requestBody"));
        return requestStrDTO;
    }


    /**
     * 3. requestParam的数据和header中的数据
     * 1. 根据时间段，URL和应用获取数据
     * 2. 如果当前csv其他变量从上面两个里面取数了，需要对查出来的数据根据traceId进行筛选
     * 3. 根据？和&符号拆分获取变量，再进行匹配，获取需要的参数
     *
     * @param responseList
     * @return
     */
    public static ScriptCsvDataTemplateResponse.TemplateDTO parseHeader(List<TrafficRecorderExtResponse> responseList) {
        List<String> headerList = responseList.stream().map(TrafficRecorderExtResponse::getRequestHeader).filter(StringUtils::isNotBlank).collect(Collectors.toList());
        ScriptCsvDataTemplateResponse.TemplateDTO requestStrDTO = new ScriptCsvDataTemplateResponse.TemplateDTO();
        requestStrDTO.setKey("header参数");
        requestStrDTO.setTitle("header参数");
        if (CollectionUtils.isEmpty(headerList)) {
            return requestStrDTO;
        }
        List<ScriptCsvDataTemplateResponse.TemplateDTO> requestBody = mergeJson(headerList, "header");
        requestStrDTO.setChildren(requestBody.stream().map(t -> {
            ScriptCsvDataTemplateResponse.TemplateDTO children = new ScriptCsvDataTemplateResponse.TemplateDTO();
            children.setTitle(t.getTitle());
            children.setKey(t.getKey());
            return children;
        }).collect(Collectors.toList()));
        return requestStrDTO;
    }

//    /**
//     * 3. RequestParam的数据和header中的数据
//     * 1. 根据时间段，URL和应用获取数据
//     * 2. 如果当前csv其他变量从上面两个里面取数了，需要对查出来的数据根据traceId进行筛选
//     * 3. 根据？和&符号拆分获取变量，再进行匹配，获取需要的参数
//     *
//     * @param responseList
//     * @return
//     */
//    public static List<ScriptCsvDataTemplateResponse.RequestStrDTO> parseRequestParam(List<TrafficRecorderExtResponse> responseList) {
////        List<String> paramList = responseList.stream().map(TrafficRecorderExtResponse::getRequestParam).filter(StringUtils::isNotBlank).collect(Collectors.toList());
////        if(CollectionUtils.isEmpty(paramList)) {
////            return Lists.newArrayList();
////        }
////        List<ScriptCsvDataTemplateResponse.RequestJsonDTO> requestBody = mergeJson(headerList);
////        return requestBody.stream().map(t -> {
////            ScriptCsvDataTemplateResponse.RequestStrDTO requestStrDTO = new ScriptCsvDataTemplateResponse.RequestStrDTO();
////            requestStrDTO.setField(t.getField());
////            requestStrDTO.setPath(t.getPath());
////            return requestStrDTO;
////        }).collect(Collectors.toList());
//        //todo
//        return Lists.newArrayList();
//    }

    /**
     * 读取json的value
     *
     * @param responseBody
     * @param jsonPath
     * @return
     */
    public static Object readJsonValue(String responseBody, String jsonPath) {
        if (StringUtils.isBlank(responseBody)) {
            return "";
        }
        try {
            return JsonPath.read(responseBody, jsonPath);
        } catch (Exception e) {
            log.error("读取失败", e);
            return "";
        }
    }

    private static void buildJsonPath(Object obj, String path, List<ScriptCsvDataTemplateResponse.TemplateDTO> requestBody, String prefix) {
        if (obj instanceof JSONObject) {
            JSONObject jsonObject = (JSONObject) obj;
            jsonObject.keySet().forEach(key -> {
                ScriptCsvDataTemplateResponse.TemplateDTO requestJsonDTO = new ScriptCsvDataTemplateResponse.TemplateDTO();
                List<ScriptCsvDataTemplateResponse.TemplateDTO> childrenRequestBody = new ArrayList<>();
                String newPath = path + "." + key;
                //System.out.println(newPath);
                requestJsonDTO.setKey(prefix + "#" + newPath);
                requestJsonDTO.setTitle(key);
                requestJsonDTO.setChildren(childrenRequestBody);
                requestBody.add(requestJsonDTO);
                buildJsonPath(jsonObject.get(key), newPath, childrenRequestBody, prefix);
            });
        } else if (obj instanceof JSONArray) {
            JSONArray jsonArray = (JSONArray) obj;
            for (int i = 0; i < jsonArray.size(); i++) {
                String newPath = path + "[" + i + "]";
                //System.out.println(newPath);
                buildJsonPath(jsonArray.get(i), newPath, requestBody, prefix);
            }
        }
    }

    private static JsonNode mergeJsonNodes(JsonNode node1, JsonNode node2) {
        ObjectMapper mapper = new ObjectMapper();
        // 如果其中一个节点为空，则返回另一个节点
        if (node1 == null || node1.isMissingNode()) {
            return node2;
        }
        if (node2 == null || node2.isMissingNode()) {
            return node1;
        }
        //如果都为数组，将数据中的所有对象合并为一个对象
        if (node1.isArray() && node2.isArray()) {
            List<JsonNode> all = new ArrayList<>();
            for (Iterator<JsonNode> it = node1.elements(); it.hasNext(); ) {
                JsonNode jsonNode1 = it.next();
                all.add(jsonNode1);
            }
            for (Iterator<JsonNode> it = node2.elements(); it.hasNext(); ) {
                JsonNode jsonNode2 = it.next();
                all.add(jsonNode2);
            }
            if (CollectionUtils.isEmpty(all)) {
                return node1;
            }
            JsonNode firstNode = all.get(0);
            for (JsonNode jsonNode : all) {
                firstNode = mergeJsonNodes(firstNode, jsonNode);
            }
            ArrayNode newArrayNode = mapper.createArrayNode();
            newArrayNode.add(firstNode);
            return newArrayNode;
        }

        // 如果两个节点都是对象节点，则递归合并
        if (node1.isObject() && node2.isObject()) {
            ObjectNode mergedNode = mapper.createObjectNode();
            mergedNode.setAll((ObjectNode) node1);
            for (Iterator<String> it = node2.fieldNames(); it.hasNext(); ) {
                String field = it.next();
                JsonNode jsonNode1 = node1.get(field);
                JsonNode jsonNode2 = node2.get(field);
                if (jsonNode1 != null && jsonNode2 != null) {
                    //同个key，数组和对象不能合并
                    if (jsonNode1.isArray() && !jsonNode2.isArray()) {
                        throw new RuntimeException(field + "属性中既有对象，又有数组，请更换数据选取时间");
                    }
                    if (jsonNode2.isArray() && !jsonNode1.isArray()) {
                        throw new RuntimeException(field + "属性中既有对象，又有数组，请更换数据选取时间");
                    }
                }
                mergedNode.set(field, mergeJsonNodes(jsonNode1, jsonNode2));
            }
            return mergedNode;
        }
        if (!node1.isObject() && !node2.isObject()) {
            return node2;
        }
        // 一个节点为具体值，一个是个对象
        throw new RuntimeException(node1 + "属性中既有对象，又有具体值，请更换数据选取时间");
    }


    private static List<ScriptCsvDataTemplateResponse.TemplateDTO> mergeJson(List<String> jsonList, String prefix) {
        JsonNode mergedNode = null;
        ObjectMapper mapper = new ObjectMapper();
        for (int i = 0; i < jsonList.size(); i++) {
            try {
                JsonNode node = mapper.readTree(jsonList.get(i));
                // 创建一个新的JsonNode对象，并将两个JsonNode对象合并
                mergedNode = mergeJsonNodes(mergedNode, node);
            } catch (Exception e) {
                log.error("合并解析失败:{}", jsonList.get(i));
            }
        }
        List<ScriptCsvDataTemplateResponse.TemplateDTO> requestBody = Lists.newArrayList();
        try {
            JSONObject jsonObject = JSONObject.parseObject(mapper.writeValueAsString(mergedNode));
            buildJsonPath(jsonObject, "$", requestBody, prefix);
        } catch (JsonProcessingException e) {
            log.error("解析出RequestJsonDTO:{}", e.getMessage());
        }
        return requestBody;
    }


//    private static class RestfulApiMatcher {
//
//        private static final Pattern placeholderPattern = Pattern.compile("\\{([^}]+)}");
//
//        // private final int placeholderCount;
//
//        private String[] slashSegments;
//
//        private boolean[] braceStart;
//        private boolean[] braceEnd;
//        private int slashSegmentsLength;
//        private boolean braceMatch;
//        private boolean mixExpressionMatch;
//        private String subApi;
//        private int subApiLength;
//        private String suffixApi;
//        private boolean isBlankSubApi;
//        private boolean isBlankSuffixApi;
//        /**
//         * 是否是全匹配
//         */
//        private boolean matchesAll;
//
//        public static Integer isRestful(String url) {
//            java.util.regex.Matcher matcher = placeholderPattern.matcher(url);
//            int count = 0;
//            while (matcher.find()) {
//                count++;
//            }
//            return count;
//        }
//
//        //public static
//
////        private RestfulApiMatcher(String pattern) {
////            java.util.regex.Matcher matcher = placeholderPattern.matcher(pattern);
////            int count = 0;
////            while (matcher.find()) {
////                count++;
////            }
////            this.placeholderCount = count;
////            if (count > 0) {
////                pattern = matcher.replaceAll("{value}");
////            }
////            this.pattern = pattern;
////            final int endIndex = pattern.indexOf("/{");
////            if (endIndex < 0) {
////                matchesAll = true;
////            } else {
////                this.slashSegments = StringUtils.split(pattern, '/');
////                this.slashSegmentsLength = this.slashSegments.length;
////                String[] braceSegments = StringUtils.split(pattern, '{');
////                this.braceMatch = braceSegments.length >= 3 || (braceSegments.length == 2 && "/".equals(
////                        braceSegments[0]) && braceSegments[1].contains("}"));
////                this.mixExpressionMatch = pattern.contains("/{") && pattern.contains("}");
////                this.subApi = pattern.substring(0, endIndex);
////                this.subApiLength = subApi.length();
////                this.isBlankSubApi = StringUtils.isBlank(subApi);
////                this.suffixApi = pattern.substring(pattern.indexOf("}") + 1);
////                this.isBlankSuffixApi = StringUtils.isBlank(suffixApi);
////                this.braceStart = new boolean[this.slashSegmentsLength];
////                this.braceEnd = new boolean[this.slashSegmentsLength];
////                for (int i = 0; i < slashSegmentsLength; i++) {
////                    String word = slashSegments[i];
////                    this.braceStart[i] = word.charAt(0) == '{';
////                    this.braceEnd[i] = word.charAt(word.length() - 1) == '}';
////                }
////            }
////        }
////
////        private String format(String url) {
////            /*
////              确保首位是/
////             */
////            if (url.charAt(0) != '/') {
////                url = '/' + url;
////            }
////
////            /*
////              确保长度大于1的末尾不是/
////             */
////            if (url.length() > 1 && url.charAt(url.length() - 1) == '/') {
////                url = url.substring(0, url.length() - 1);
////            }
////            return url;
////        }
////
////        public boolean match(String url) {
////            if (StringUtils.isBlank(url)) {
////                return false;
////            }
////            url = format(url.trim());
////            if (url.equals(pattern)) {
////                return true;
////            }
////            /*
////              如果全匹配则匹配失败,因为上面已经执行了 equals
////             */
////            if (matchesAll) {
////                return false;
////            }
////            if (slashSegmentsLength != StringUtils.split(url, '/').length) {
////                return false;
////            }
////            try {
////                String[] sourceSplit = StringUtils.split(url, '/');
////                String temp = "";
////
////                //规则中含有多个参数或者只有一个参数的规则
////                if (braceMatch) {
////                    //如果规则长度和源字符不相等,进入下一条规则匹配
////                    if (slashSegmentsLength != sourceSplit.length) {
////                        return false;
////                    }
////                    //如果长度相等
////                    //忽略第一个空值,从第二位开始匹配
////                    int paramCount = 0;
////                    int wordCount = 0;
////                    for (int i = 0, len = slashSegmentsLength; i < len; i++) {
////                        String word = slashSegments[i];
////                        //如果是变量,跳过
////                        if (braceStart[i] && braceEnd[i]) {
////                            paramCount++;
////                            continue;
////                        }
////                        //如果两者不相等,直接进入下一个规则匹配
////                        if (!word.equals(sourceSplit[i])) {
////                            return false;
////                        }
////                        wordCount++;
////                    }
////                    //如果等值匹配上,则返回规则
////                    //如果是全参数匹配,需要继续向下检索,是否存在等值匹配
////                    //保存临时结果集
////                    return paramCount == slashSegmentsLength - wordCount;
////                }
////                // /app/add/{name}
////                // /app/add/1
////
////                // /app/{name}/add
////                // /app/1/add
////                if (mixExpressionMatch) {
////                    //如果包含前缀
////                    if (!isBlankSubApi && url.startsWith(subApi)) {
////                        //获取后缀
////                        String tempStr = null;
////                        if (!isBlankSuffixApi) {
////                            tempStr = url.substring(subApiLength + 1);
////                        } else {
////                            //如果后缀为空,需要继续比较位数
////                            if (slashSegmentsLength != sourceSplit.length) {
////                                return false;
////                            }
////                        }
////
////                        // add rules:如果属于格式2 同时匹配后缀api
////                        return tempStr == null || (tempStr.contains("/") && tempStr.substring(tempStr.indexOf("/"))
////                                .equals(suffixApi));
////                    }
////                }
////                if (!StringUtils.isBlank(temp)) {
////                    return true;
////                }
////            } catch (Throwable ignore) {
////            }
////            return false;
////        }
////
////        @Override
////        public boolean equals(Object o) {
////            if (this == o) {return true;}
////            if (!(o instanceof RestfulApiMatcher)) {return false;}
////
////            RestfulApiMatcher matcher = (RestfulApiMatcher)o;
////
////            return pattern != null ? pattern.equals(matcher.pattern) : matcher.pattern == null;
////        }
////
////        @Override
////        public int hashCode() {
////            return pattern != null ? pattern.hashCode() : 0;
////        }
//    }


}
