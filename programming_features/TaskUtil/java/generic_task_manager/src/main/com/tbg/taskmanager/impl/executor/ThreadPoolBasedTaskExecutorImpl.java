package com.tbg.taskmanager.impl.executor;

import android.os.Handler;

import com.tbg.taskmanager.abstr.delegate.ITaskDelegate;
import com.tbg.taskmanager.abstr.executor.ITaskExecutor;
import com.tbg.taskmanager.abstr.task.ITask;
import com.tbg.taskmanager.common.Result;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

/**
 * Created by wws2003 on 10/20/15.
 */
public class ThreadPoolBasedTaskExecutorImpl implements ITaskExecutor {

    private ExecutorService mExecutorService = null;

    public ThreadPoolBasedTaskExecutorImpl() {
        this.mExecutorService = Executors.newCachedThreadPool();
    }

    @Override
    public <T> void executeTask(ITask<T> task, ITaskDelegate<T> taskDelegate) {
        Handler taskDelegateHandler = new Handler();
        if(taskDelegate != null) {
            taskDelegate.onTaskToBeExecuted();
        }

        FutureTask<Result<T> > futureTask = new FutureTask<>(new InternalFutureTask<T>(task, taskDelegate, taskDelegateHandler));
        mExecutorService.submit(futureTask);
    }

    @Override
    public <T> void tryToCancelTask(long taskId, ITaskDelegate<T> taskDelegate) {
        //TODO Implement
    }

    @Override
    public <T> Result<T> executeBackgroundTaskForResult(final ITask<T> task) {
        InternalFutureTask<T> futureTask = new InternalFutureTask<T>(task, null, null);
        Future<Result<T>> future = mExecutorService.submit(futureTask);
        try {
            return future.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private class InternalFutureTask<T> implements Callable<Result<T> > {
        private ITask<T> mTask;
        private ITaskDelegate<T> mTaskDelegate;
        private Handler mTaskDelegateHandler;

        public InternalFutureTask(ITask<T> task, ITaskDelegate<T> taskDelegate, Handler taskDelegateHandler) {
            this.mTask = task;
            this.mTaskDelegate = taskDelegate;
            this.mTaskDelegateHandler = taskDelegateHandler;
        }

        @Override
        public Result<T> call() throws Exception {
            final Result<T> taskResult = mTask.execute();
            if (mTaskDelegateHandler != null) {
                mTaskDelegateHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(mTaskDelegate != null) {
                            mTaskDelegate.onTaskToBeExecuted();
                        }
                    }
                });
            }
            return taskResult;
        }
    }
}
