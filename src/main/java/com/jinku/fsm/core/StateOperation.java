package com.jinku.fsm.core;

import java.util.List;

public interface StateOperation {

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
