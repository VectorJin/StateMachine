package com.jinku.fsm.event;

/**
 * 事件定义（包含事件枚举 和 事件数据）
 *
 * @param <T>
 */
public abstract class AbstractEvent<T extends Enum<T>> {

    private final T type;

    public AbstractEvent(T type) {
        this.type = type;
    }

    public T getType() {
        return type;
    }

    @Override
    public String toString() {
        return "Event{" + "type=" + type +  '}';
    }
}
