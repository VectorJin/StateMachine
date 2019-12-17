package com.jinku.fsm;

public interface StateListener {

    /**
     * preState --> postState 状态变化时触发的操作
     */
    void stateChanged(String uuid, int preState, int postState);
}
