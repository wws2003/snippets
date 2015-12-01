package com.tbg.taskmanager.abstr.task;

import com.tbg.taskmanager.common.Result;

/**
 * Created by wws2003 on 10/18/15.
 */
public interface ITask<T> {
    class TaskStatus {
         public static final int WAITING = 0;
         public static final int RUNNING = 1;
         public static final int CANCELLED = 2;
         public static final int FINISHED = 3;
    }
    Result<T> execute();

    int getStatus();
    void setStatus(int status);

    long getId();
    void setId(long id);
}
