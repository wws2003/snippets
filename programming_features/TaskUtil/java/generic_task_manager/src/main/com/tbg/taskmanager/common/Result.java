package com.tbg.taskmanager.common;

/**
 * Created by wws2003 on 10/18/15.
 */
public class Result<T> {
    private T mElement;
    private long mTaskId;
    private int mResultCode;

    public Result(T element, long taskId, int resultCode) {
        this.mElement = element;
        this.mTaskId = taskId;

        this.mResultCode = resultCode;
    }

    public T getElement() {
        return mElement;
    }

    public int getResultCode() {
        return mResultCode;
    }

    public long getTaskId() {
        return mTaskId;
    }

}
