package io.shulie.takin.eventcenter;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.collect.Maps;
import io.shulie.takin.eventcenter.annotation.IntrestFor;
import org.springframework.stereotype.Component;

/**
 * @author vincent
 */
@Component
public class ListenerContainer {
    private final HashMap<String, Map<String, Listener>> LISTENERS = new HashMap<>();

    /**
     * 获取监听器列表
     *
     * @return -
     */
    HashMap<String, Map<String, Listener>> getListeners() {
        return LISTENERS;
    }

    /**
     * @param event  -
     * @param method -
     */
    protected void addListener(IntrestFor event, Object obj, Method method) {

        if (!LISTENERS.containsKey(event.event())) {
            Map<String, Listener> map = Maps.newHashMap();
            LISTENERS.put(event.event(), map);
        }
        Map<String, Listener> map = LISTENERS.get(event.event());
        Class<?>[] parameters = method.getParameterTypes();
        String parameter = Arrays.stream(parameters).map(Class::getName).collect(Collectors.joining(","));
        map.put(method.getDeclaringClass() + "-" + method.getName() + "-" + parameter, new Listener(event, obj, method));
    }

    /**
     *
     */
    protected static class Listener {
        private final IntrestFor intrestFor;
        private final Object object;
        private final Method method;

        public Listener(IntrestFor intrestFor, Object object, Method method) {
            this.intrestFor = intrestFor;
            this.object = object;
            this.method = method;
        }

        public Method getMethod() {
            return method;
        }

        public IntrestFor getIntrestFor() {
            return intrestFor;
        }

        public Object getObject() {
            return object;
        }
    }
}
