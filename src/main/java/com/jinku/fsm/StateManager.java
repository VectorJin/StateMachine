package com.jinku.fsm;

import java.util.ArrayList;
import java.util.List;

public abstract class StateManager {

    private List<StateListener> listeners = new ArrayList<>();
    private List<StateOperation> autoOperations = new ArrayList<>();

    /**
     * 状态管理器标识
     *
     * @return
     */
    abstract String managerKey();

    /**
     * 当前状态
     *
     * @return
     */
    abstract int currentState(String uuid);

    /**
     * 更新状态
     *
     * @param uuid
     * @param newState
     * @param expectState
     */
    abstract boolean setState(String uuid, int newState, int expectState);

    /**
     * 注册状态监听器
     *
     * @param listener
     */
    public synchronized void registerListener(StateListener listener) {
        if (listeners.contains(listener)) {
            return;
        }
        listeners.add(listener);
    }

    /**
     * 注册自动状态转换操作
     *
     * @param operation
     */
    public synchronized void registerAutoOp(StateOperation operation) {
        if (autoOperations.contains(operation)) {
            return;
        }
        autoOperations.add(operation);
    }

    /**
     * 执行状态转换操作
     *
     * @param uuid
     * @param stateOperation
     */
    public void doOperation(String uuid, StateOperation stateOperation) {
        int current = currentState(uuid);
        if (!stateOperation.preState().contains(current)) {
            return;
        }
        int newState = stateOperation.postState();
        stateOperation.operation(uuid);
        boolean success = setState(uuid, newState, current);

        if (success) {
            // 通知
            List<StateListener> innerListeners = new ArrayList<>(listeners);
            for (StateListener listener : innerListeners) {
                listener.stateChanged(uuid, current, newState);
            }
        }
    }

    /**
     * 自动同步状态
     *
     * @param uuid
     */
    public void autoSyncState(String uuid) {
        List<StateOperation> innerAutoOperations = new ArrayList<>(autoOperations);
        if (innerAutoOperations.size() == 0) {
            return;
        }

        int current = currentState(uuid);
        while (true) {
            StateOperation matchedOperation = null;
            for (StateOperation stateOperation : innerAutoOperations) {
                if (stateOperation.preState().contains(current)) {
                    matchedOperation = stateOperation;
                }
            }
            if (matchedOperation == null) {
                break;
            }
            matchedOperation.operation(uuid);
            int newState = matchedOperation.postState();
            boolean isSuccess = setState(uuid, newState, current);
            if (isSuccess) {
                current = newState;
            } else {
                current = currentState(uuid);
            }
        }
    }
}
