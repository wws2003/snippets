package com.tbg.taskmanager.abstr.task;

import com.tbg.taskmanager.common.Result;

/**
 * Created by wws2003 on 10/29/15.
 */
public interface ITaskResultProcessor<T> {
    Result<T> processResult(Result<T> primitiveResult);
}
