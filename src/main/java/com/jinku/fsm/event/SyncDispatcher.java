package com.jinku.fsm.event;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 同步事件分发器
 */
public class SyncDispatcher implements Dispatcher {

    Map<Class<? extends Enum>, EventHandler> eventHandlerMap;

    public SyncDispatcher() {
        eventHandlerMap = new ConcurrentHashMap<>();
    }

    @Override
    public void register(Class<? extends Enum> eventType, EventHandler handler) {
        eventHandlerMap.put(eventType, handler);
    }

    public void dispatch(final AbstractEvent event) {
        final EventHandler eventHandler = eventHandlerMap.get(event.getType().getClass());
        if (eventHandler == null) {
            throw new RuntimeException("no matched event handler;type=" + event.getType());
        }

        try {
            System.out.println("SyncDispatcher handle event start;event=" + event);
            eventHandler.handle(event);
            System.out.println("SyncDispatcher handle event end;event=" + event);
        } catch (Exception e) {
            System.out.println("SyncDispatcher handle event exception;event=" + event);
            e.printStackTrace();
        }
    }

    @Override
    public void shutdown() {

    }
}
