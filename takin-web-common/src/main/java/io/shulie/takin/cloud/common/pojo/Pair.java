package io.shulie.takin.cloud.common.pojo;

import lombok.Data;

/**
 * @author liyuanba
 * @date 2021/10/14 10:06 上午
 */
@Data
public class Pair<K, V> extends AbstractEntry {
    private K key;
    private V value;

    public Pair() {
    }

    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }
}
