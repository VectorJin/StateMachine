package com.jinku.fsm.state;

import java.util.List;

public interface StateTransition {

    /**
     * 前置状态
     *
     * @return
     */
    List<Integer> preState();

    /**
     * 执行的操作，返回操作后的状态
     */
    int operation(String uuid);
}
