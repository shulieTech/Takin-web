package io.shulie.takin.web.biz.pojo.response.application;

import java.util.List;
import java.util.regex.Pattern;

import lombok.Data;

/**
 * @author fanxx
 * @date 2020/10/13 7:29 下午
 */
@Data
public class PluginLibSupportResponse {

    private String libName;
    private List<Pattern> regexpList;

}
