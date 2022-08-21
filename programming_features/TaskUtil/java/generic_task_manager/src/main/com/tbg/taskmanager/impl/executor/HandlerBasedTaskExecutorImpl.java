package com.tbg.taskmanager.impl.executor;

import android.os.Handler;

import com.tbg.taskmanager.abstr.task.ITask;
import com.tbg.taskmanager.abstr.delegate.ITaskDelegate;
import com.tbg.taskmanager.abstr.executor.ITaskExecutor;
import com.tbg.taskmanager.common.Result;

/**
 * Created by wws2003 on 10/20/15.
 */
public class HandlerBasedTaskExecutorImpl implements ITaskExecutor {

    private Handler mTaskExecutingHandler;
    private Handler mTaskDelegateHandler;

    public HandlerBasedTaskExecutorImpl(Handler taskExecutingHandler, Handler taskDelegateHandler) {
        this.mTaskExecutingHandler = taskExecutingHandler;
        this.mTaskDelegateHandler = taskDelegateHandler;
    }

    @Override
    public <T> void executeTask(ITask<T> task, ITaskDelegate<T> taskDelegate) {

        preExecute(taskDelegate);

        //What for?
        waitBeforeExecuteTask();

        startExecuteTask(task, taskDelegate);
    }

    @Override
    public <T> void tryToCancelTask(long taskId, ITaskDelegate<T> taskDelegate) {
        //TODO Implement or at least throw some kind of run time exception to notice
    }

    @Override
    public <T> Result<T> executeBackgroundTaskForResult(final ITask<T> task) {
        ResultStoreRunnable<T> resultStore = new ResultStoreRunnable<>(task);
        mTaskExecutingHandler.post(new ResultStoreRunnable<>(task));

        try {
            mTaskExecutingHandler.wait();
            return resultStore.getTaskResult();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    private <T> void preExecute(final ITaskDelegate<T> taskDelegate) {
        mTaskDelegateHandler.post(new Runnable() {
            @Override
            public void run() {
                if(taskDelegate != null) {
                    taskDelegate.onTaskToBeExecuted();
                }
            }
        });
    }

    private void waitBeforeExecuteTask() {
        //FIXME: This is apparently not the correct solution
        try {
            mTaskDelegateHandler.wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private <T> void startExecuteTask(final ITask<T> task, final ITaskDelegate<T> taskDelegate) {
        mTaskExecutingHandler.post(new Runnable() {
            @Override
            public void run() {
                final Result<T> taskResult = task.execute();
                mTaskDelegateHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(taskDelegate != null) {
                            taskDelegate.onTaskToBeExecuted();
                        }
                    }
                });
            }
        });
    }

    private class ResultStoreRunnable<T> implements Runnable {

        private Result<T> mTaskResult;
        private ITask<T> mTask;

        public ResultStoreRunnable(ITask<T> task) {
            mTask = task;
        }

        @Override
        public void run() {
            mTaskResult = mTask.execute();
        }

        public Result<T> getTaskResult() {
            return mTaskResult;
        }
    }

}
