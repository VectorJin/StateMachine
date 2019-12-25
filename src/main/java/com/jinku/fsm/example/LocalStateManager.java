package com.jinku.fsm.example;

import com.jinku.fsm.event.AsyncDispatcher;
import com.jinku.fsm.event.Dispatcher;
import com.jinku.fsm.event.SyncDispatcher;
import com.jinku.fsm.state.StateListener;
import com.jinku.fsm.state.StateManager;
import com.jinku.fsm.state.StateTransition;

import java.util.ArrayList;
import java.util.List;

/**
 * 本地状态管理器
 */
public class LocalStateManager extends StateManager<LocalStateEvent> {

    private volatile LocalStateEnum currentState = LocalStateEnum.Init;

    public LocalStateManager(Dispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    protected void init() {
        // 注册自动状态转移操作
        registerAutoTransition(new StateTransition() {
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
    }

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

    @Override
    public void handle(LocalStateEvent event) {
        // 处理同步事件
        if (event.getType() == LocalStateEventType.Sync) {
            autoSyncState(event.getUuid());
        }

        if (event.getType() == LocalStateEventType.DoSomething) {
            doTransition(event.getUuid(), new StateTransition() {
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
        }
    }

    public enum LocalStateEnum {

        Init(0, "初始化"),
        Processing(1, "处理中"),
        Success(2, "成功"),
        Failed(3, "失败"),
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
//        Dispatcher dispatcher = new SyncDispatcher();
        Dispatcher dispatcher = new AsyncDispatcher();
        StateManager localStateManager = new LocalStateManager(dispatcher);
        /**
         * 注册状态变化监听器
         */
        localStateManager.registerListener(new StateListener() {
            @Override
            public void stateChanged(String uuid, int preState, int postState) {
                // do something
            }
        });

        dispatcher.register(LocalStateEventType.class, localStateManager);

        String uuid = "1223333";
        // 分发事件do something
        LocalStateEvent localStateEvent = new LocalStateEvent(uuid, LocalStateEventType.DoSomething);
        dispatcher.dispatch(localStateEvent);

        // 分发事件同步数据
        localStateEvent = new LocalStateEvent(uuid, LocalStateEventType.Sync);
        dispatcher.dispatch(localStateEvent);

        // 程序退出要关闭分发器
        dispatcher.shutdown();
    }
}
