package com.tbg.taskmanager.abstr.task;

import com.tbg.taskmanager.common.Result;

/**
 * Created by wws2003 on 10/20/15.
 */
public abstract class AbstractTask<T> implements ITask<T> {
    private long mId;
    private int mStatus = TaskStatus.WAITING;

    public AbstractTask() {

    }

    public AbstractTask(long id) {
        mId = id;
    }

    @Override
    public synchronized int getStatus() {
        return mStatus;
    }

    @Override
    public synchronized void setStatus(int status) {
        mStatus = status;
    }

    @Override
    public synchronized long getId() {
        return mId;
    }

    @Override
    public synchronized void setId(long id) {
        mId = id;
    }

    @Override
    public Result<T> execute() {
        setStatus(TaskStatus.RUNNING);
        Result<T> result = doExecute();
        setStatus(TaskStatus.FINISHED);
        return result;
    }

    public Result<T> generateResult(T element, int resultCode) {
        return new Result<>(element, getId(), resultCode);
    }

    public abstract Result<T> doExecute();
}
