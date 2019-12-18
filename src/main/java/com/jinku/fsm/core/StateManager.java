package com.jinku.fsm.core;

import com.jinku.fsm.example.LocalStateManager;

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
    public abstract String managerKey();

    /**
     * 当前状态
     *
     * @return
     */
    public abstract int currentState(String uuid);

    /**
     * 更新状态
     *
     * @param uuid
     * @param newState
     * @param expectState
     */
    public abstract boolean setState(String uuid, int newState, int expectState);

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

        System.out.println(managerKey() + " manual do: currentState=" + current);
        System.out.println(managerKey() + " manual do: do something");
        int newState = stateOperation.operation(uuid);
        System.out.println(managerKey()  + " manual do: newState=" + newState);
        boolean isSuccess = setState(uuid, newState, current);
        System.out.println(managerKey() + " manual do: setState result=" + isSuccess);
        if (isSuccess) {
            try {
                // 通知
                List<StateListener> innerListeners = new ArrayList<>(listeners);
                for (StateListener listener : innerListeners) {
                    System.out.println(managerKey() + " autoSync: notify listener;oldState=" + current + ";newState=" + newState);
                    listener.stateChanged(uuid, current, newState);
                }
            } catch (Exception e) {
                System.err.print(managerKey() + " manual do: notify listener exception");
                e.printStackTrace();
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
            System.out.println(managerKey() + " autoSync: currentState=" + current);
            System.out.println(managerKey() + " autoSync: do something");
            int newState = matchedOperation.operation(uuid);
            System.out.println(managerKey() + " autoSync: newState=" + newState);
            boolean isSuccess = setState(uuid, newState, current);
            System.out.println(managerKey() + " autoSync: setState result=" + isSuccess);

            try {
                // 通知
                List<StateListener> innerListeners = new ArrayList<>(listeners);
                for (StateListener listener : innerListeners) {
                    System.out.println(managerKey() + " autoSync: notify listener;oldState=" + current + ";newState=" + newState);
                    listener.stateChanged(uuid, current, newState);
                }
            } catch (Exception e) {
                System.err.print(managerKey() + " autoSync: notify listener exception");
                e.printStackTrace();
            }

            if (isSuccess) {
                current = newState;
            } else {
                current = currentState(uuid);
            }
        }
    }
}
