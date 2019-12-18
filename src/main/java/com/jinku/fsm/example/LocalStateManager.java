package com.jinku.fsm.example;

import com.jinku.fsm.core.StateAutoSync;
import com.jinku.fsm.core.StateListener;
import com.jinku.fsm.core.StateManager;
import com.jinku.fsm.core.StateOperation;

import java.util.ArrayList;
import java.util.List;

/**
 * 本地状态管理器
 */
public class LocalStateManager extends StateManager {

    private volatile LocalStateEnum currentState = LocalStateEnum.Init;

    @Override
    public String managerKey() {
        return "local_state_manager";
    }

    @Override
    public int currentState(String uuid) {
        return currentState.getState();
    }

    @Override
    public boolean setState(String uuid, int newState, int expectState) {

        if (expectState != currentState.state) {
            return false;
        }

        synchronized (this) {
            if (expectState != currentState.state) {
                return false;
            }
            currentState = LocalStateEnum.fromState(newState);
        }
        return true;
    }

    public enum LocalStateEnum {

        Init(0, "初始化"),
        Processing(1, "处理中"),
        Success(2,"成功"),
        Failed(3,"失败"),
        ;

        int state;
        String remark;

        LocalStateEnum(int state, String remark) {
            this.state = state;
            this.remark = remark;
        }

        public static LocalStateEnum fromState(Integer state) {
            if (state == null) {
                return null;
            }
            for (LocalStateEnum stateEnum : LocalStateEnum.values()) {
                if (stateEnum.state == state) {
                    return stateEnum;
                }
            }
            return null;
        }

        public int getState() {
            return state;
        }

        public String getRemark() {
            return remark;
        }
    }

    /**
     * 本地状态管理器 调用示例
     *
     * @param args
     */
    public static void main(String[] args) {
        LocalStateManager localStateManager = new LocalStateManager();
        final String localManagerKey = localStateManager.managerKey();

        /**
         * 注册状态变化监听器
         */
        localStateManager.registerListener(new StateListener() {
            @Override
            public void stateChanged(String uuid, int preState, int postState) {
                // do something
            }
        });

        String uuid = "123";
        //
        localStateManager.doOperation(uuid, new StateOperation() {
            @Override
            public List<Integer> preState() {
                List<Integer> list = new ArrayList<>();
                list.add(LocalStateEnum.Init.state);
                return list;
            }

            @Override
            public int operation(String uuid) {
                // do something than change state
                return LocalStateEnum.Processing.state;
            }
        });

        StateAutoSync stateAutoSync = new StateAutoSync();
        stateAutoSync.register(localStateManager);

        // 注册自动状态转移操作
        localStateManager.registerAutoOp(new StateOperation() {
            @Override
            public List<Integer> preState() {
                List<Integer> list = new ArrayList<>();
                list.add(LocalStateEnum.Processing.state);
                return list;
            }

            @Override
            public int operation(String uuid) {
                // do something than change state
                return LocalStateEnum.Success.getState();
            }
        });

        // 遍历所有的处理中的uuid，调用 stateAutoSync 自动同步状态
        stateAutoSync.syncState(uuid, localManagerKey);
    }
}
