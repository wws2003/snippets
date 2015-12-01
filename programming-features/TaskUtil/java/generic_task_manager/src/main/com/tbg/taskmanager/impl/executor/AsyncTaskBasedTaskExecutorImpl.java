package com.tbg.taskmanager.impl.executor;

import android.os.AsyncTask;
import android.util.LongSparseArray;

import com.tbg.taskmanager.abstr.task.ITask;
import com.tbg.taskmanager.abstr.delegate.ITaskDelegate;
import com.tbg.taskmanager.abstr.executor.ITaskExecutor;
import com.tbg.taskmanager.common.Result;

/**
 * Created by wws2003 on 10/18/15.
 */

//Use this class for safe task manager (only one serial background thread + UI thread to manage)
public class AsyncTaskBasedTaskExecutorImpl implements ITaskExecutor {

    private LongSparseArray<InternalAsyncTask> mTaskMap;

    public AsyncTaskBasedTaskExecutorImpl() {
        this.mTaskMap = new LongSparseArray<InternalAsyncTask>();
    }

    @Override
    public <T> void executeTask(ITask<T> task, ITaskDelegate<T> taskDelegate) {
        InternalAsyncTask<T> asyncTask = new InternalAsyncTask<T>(mTaskMap, task, taskDelegate);
        asyncTask.execute();
    }

    public <T> void tryToCancelTask(long taskId, ITaskDelegate<T> taskDelegate) {
        InternalAsyncTask<T> asyncTask = mTaskMap.get(taskId);

        //If found async task in the map, i.e. task hasn't finished yet
        if(asyncTask != null) {
           if(asyncTask.cancel()) {
               taskDelegate.onTaskCancelled();
           }
        }
    }

    @Override
    public <T> Result<T> executeBackgroundTaskForResult(ITask<T> task) {
        InternalAsyncTask<T> asyncTask = new InternalAsyncTask<T>(mTaskMap, task, null);

        asyncTask.execute();
        try {
            Result<T> taskResult = asyncTask.get();
            return taskResult;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private class InternalAsyncTask<T> extends AsyncTask<Void, Integer, Result<T> > {

        private ITask<T> mTask;
        private ITaskDelegate<T> mTaskDelegate;
        private LongSparseArray<InternalAsyncTask> mTaskMap;

        public InternalAsyncTask(LongSparseArray<InternalAsyncTask> taskMap, ITask<T> task, ITaskDelegate<T> taskDelegate) {
            this.mTaskMap = taskMap;
            this.mTask = task;
            this.mTaskDelegate = taskDelegate;
        }

        //Overload.
        public boolean cancel() {
            if(super.cancel(true)) {
                mTask.setStatus(ITask.TaskStatus.CANCELLED);
                return true;
            }
            return false;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mTaskMap.append(mTask.getId(), this);
            if(mTaskDelegate != null) {
                mTaskDelegate.onTaskToBeExecuted();
            }
        }

        @Override
        protected Result<T> doInBackground(Void... params) {
            Result<T> result = mTask.execute();
            return result;
        }

        @Override
        protected void onPostExecute(Result<T> result) {
            super.onPostExecute(result);
            mTaskMap.remove(result.getTaskId());
            if(mTaskDelegate != null) {
                mTaskDelegate.onTaskExecuted(result);
            }
        }
    }
}
