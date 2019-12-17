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
     * 执行的操作
     */
    void operation(String uuid);

    /**
     * 执行操作后的状态
     *
     * @return
     */
    int postState();
}
