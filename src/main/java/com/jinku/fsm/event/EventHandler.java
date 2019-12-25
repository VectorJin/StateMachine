package com.jinku.fsm.event;

/**
 * 事件处理器
 *
 * @param <T>
 */
public interface EventHandler<T extends AbstractEvent> {

    /**
     * 处理T类型的事件
     *
     * @param event
     */
    void handle(T event);
}
