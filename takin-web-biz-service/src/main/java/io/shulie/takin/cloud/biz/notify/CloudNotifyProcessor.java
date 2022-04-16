package io.shulie.takin.cloud.biz.notify;

import io.shulie.takin.cloud.constant.enums.CallbackType;

public interface CloudNotifyProcessor<T extends CloudNotifyParam> {

    String process(T param);

    CallbackType type();
}
