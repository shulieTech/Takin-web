package io.shulie.takin.cloud.common.constants;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * @author zhaoyong
 * 没有长度的队列，判断的时候永远返回false
 * 让线程池不使用队列，如果核心线程数满，直接创建线程到最大线程数；
 */
public class NoLengthBlockingQueue<E> extends ArrayBlockingQueue<E> {

    public NoLengthBlockingQueue() {
        super(1);
    }

    @Override
    public boolean offer(E e) {
        return false;
    }
}
