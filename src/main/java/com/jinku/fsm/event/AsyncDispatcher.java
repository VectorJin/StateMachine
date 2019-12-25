package com.jinku.fsm.event;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 异步事件分发器，内部使用线程池来处理事件
 */
public class AsyncDispatcher implements Dispatcher {

    Map<Class<? extends Enum>, EventHandler> eventHandlerMap;
    ExecutorService executor;

    public AsyncDispatcher() {
        eventHandlerMap = new ConcurrentHashMap<>();
        this.executor = Executors.newSingleThreadExecutor();
    }

    public AsyncDispatcher(int threadSize) {
        eventHandlerMap = new ConcurrentHashMap<>();
        this.executor = Executors.newFixedThreadPool(threadSize);
    }

    public AsyncDispatcher(ExecutorService executor) {
        eventHandlerMap = new ConcurrentHashMap<>();
        this.executor = executor;
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

        executor.execute(() -> {
            try {
                System.out.println("dispatch handle event start;event=" + event);
                eventHandler.handle(event);
                System.out.println("dispatch handle event end;event=" + event);
            } catch (Exception e) {
                System.out.println("dispatch handle event exception;event=" + event);
                e.printStackTrace();
            }
        });
    }

    @Override
    public void shutdown() {
        executor.shutdown();
        System.out.println("dispatcher shutdown");
    }
}
