package io.shulie.takin.web.app.conf;

import io.shulie.takin.web.biz.task.TaskUtils;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.stereotype.Component;

/**
 * @author by: hezhongqi
 * @Package io.shulie.takin.app.conf
 * @ClassName: AutowireStaticSmartInitializingSingleton
 * @Description: TODO
 * @Date: 2021/8/16 11:54
 */
@Component
public class AutowireStaticSmartInitializingSingleton implements SmartInitializingSingleton {
    @Autowired
    private AutowireCapableBeanFactory beanFactory;

    /**
     * 当所有的单例Bena初始化完成后，对static静态成员进行赋值
     * 手动管理这种case的依赖注入，更可控。而非交给Spring容器去自动处理
     * 工具类本身并不需要加入到Spring容器内，这对于有大量这种case的话，是可以节约开销的
     *
     */
    @Override
    public void afterSingletonsInstantiated() {
        // 因为是给static静态属性赋值，因此这里new一个实例做注入是可行的
        beanFactory.autowireBean(new WebPluginUtils());
        beanFactory.autowireBean(new TaskUtils());
    }
}
