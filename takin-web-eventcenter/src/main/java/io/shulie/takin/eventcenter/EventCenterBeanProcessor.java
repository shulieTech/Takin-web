package io.shulie.takin.eventcenter;

import java.lang.reflect.Method;

import io.shulie.takin.eventcenter.annotation.IntrestFor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

/**
 * @author vincent
 */
@Component
public class EventCenterBeanProcessor implements BeanPostProcessor {

    @Autowired
    private ListenerContainer listenerContainer;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Method[] methods = ReflectionUtils.getAllDeclaredMethods(bean.getClass());
        if (methods != null) {
            for (Method method : methods) {
                IntrestFor intrests = AnnotationUtils.findAnnotation(method, IntrestFor.class);
                if (intrests == null) {
                    continue;
                }
                listenerContainer.addListener(intrests, bean, method);
            }
        }
        return bean;
    }
}
