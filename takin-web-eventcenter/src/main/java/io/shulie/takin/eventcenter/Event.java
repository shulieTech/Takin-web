package io.shulie.takin.eventcenter;

import lombok.Data;

/**
 * 事件
 *
 * @author -
 */
@Data
public class Event {
    private Object ext;
    private String eventName;
}
