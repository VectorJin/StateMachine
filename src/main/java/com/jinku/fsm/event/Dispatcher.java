package com.jinku.fsm.event;

/**
 * 事件分发器，负责维护事件与事件处理器和分发事件
 */
public interface Dispatcher {

    void register(Class<? extends Enum> eventType, EventHandler handler);

    void dispatch(final AbstractEvent event);

    void shutdown();
}
