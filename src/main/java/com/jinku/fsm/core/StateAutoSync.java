package com.jinku.fsm.core;

import java.util.ArrayList;
import java.util.List;

/**
 * 状态自动同步
 */
public class StateAutoSync {

    List<StateManager> managerList = new ArrayList<>();

    /**
     * 注册状态管理器
     *
     * @param stateManager
     */
    public synchronized void register(StateManager stateManager) {
        if (managerList.contains(stateManager)) {
            return;
        }
        managerList.add(stateManager);
    }

    public void syncState(String uuid, String managerKey) {
        List<StateManager> innerManagerList= new ArrayList<>(managerList);
        StateManager matchedStateManager = null;
        for (StateManager stateManager : innerManagerList) {
            if (stateManager.managerKey().equalsIgnoreCase(managerKey)) {
                matchedStateManager = stateManager;
            }
        }
        innerManagerList.clear();
        if (matchedStateManager == null) {
            return;
        }

        matchedStateManager.autoSyncState(uuid);
    }
}
