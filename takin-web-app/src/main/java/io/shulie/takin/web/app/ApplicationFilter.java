package io.shulie.takin.web.app;

import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.lang.NonNull;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author 张天赐
 */
public class ApplicationFilter implements TypeFilter, Filter {
    /**
     * Determine whether this filter matches for the class described by
     * the given metadata.
     *
     * @param metadataReader        the metadata reader for the target class
     * @param metadataReaderFactory a factory for obtaining metadata readers
     *                              for other classes (such as superclasses and interfaces)
     * @return whether this filter matches
     */
    @Override
    public boolean match(@NonNull MetadataReader metadataReader, @NonNull MetadataReaderFactory metadataReaderFactory) {
        // 调试用 - 忽略启动任务
        if (1 > 1) {
            return metadataReader.getClassMetadata()
                .getClassName().startsWith("io.shulie.takin.web.biz.job");
        }
        // 正常启动.
        else {return false;}
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

    }

    @Override
    public void destroy() {

    }
}
