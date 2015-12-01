package com.tbg.taskmanager.abstr.delegate;

/**
 * Created by wws2003 on 11/3/15.
 */
public abstract  class AbstractTaskResultListener<T> implements ITaskDelegate<T> {

    @Override
    public void onTaskCancelled() {
        //Do nothing
    }

    @Override
    public void onTaskToBeExecuted() {
        //Do nothing
    }
}
