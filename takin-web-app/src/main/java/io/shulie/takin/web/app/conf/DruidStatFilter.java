package io.shulie.takin.web.app.conf;

import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;

import com.pamirs.pradar.ext.druid.support.http.WebStatFilter;

@WebFilter(filterName = "druidStatFilter", urlPatterns = "/*",
    initParams = {
        @WebInitParam(name = "exclusions", value = "*.js,*.gif,*.jpg,*.bmp,*.png,*.css,*.ico,/druid/*")// 忽略资源
    })
public class DruidStatFilter extends WebStatFilter {

}
