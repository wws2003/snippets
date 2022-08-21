package com.tbg.taskmanager.abstr.delegate;

import com.tbg.taskmanager.common.Result;

/**
 * Created by wws2003 on 10/18/15.
 */
public interface ITaskDelegate<T> {
    void onTaskToBeExecuted();
    void onTaskExecuted(Result<T> taskResult);
    void onTaskCancelled();
}
