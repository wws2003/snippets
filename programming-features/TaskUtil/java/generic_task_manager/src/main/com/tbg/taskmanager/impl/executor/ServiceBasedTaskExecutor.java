package com.tbg.taskmanager.impl.executor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import com.tbg.taskmanager.abstr.delegate.ITaskDelegate;
import com.tbg.taskmanager.abstr.executor.ITaskExecutor;
import com.tbg.taskmanager.abstr.locator.ILocator;
import com.tbg.taskmanager.abstr.service.AbstractTaskService;
import com.tbg.taskmanager.abstr.task.ITask;
import com.tbg.taskmanager.common.Result;

/**
 * Created by wws2003 on 10/29/15.
 */
public class ServiceBasedTaskExecutor implements ITaskExecutor {

    private Context mContext;
    private ILocator<ITask> mTaskLocator;
    private ILocator<Result> mTaskResultLocator;
    private Class<? extends AbstractTaskService> mServiceClass;

    public ServiceBasedTaskExecutor(Context context,
                                    ILocator<ITask> taskLocator,
                                    ILocator<Result> taskResultLocator,
                                    Class<? extends AbstractTaskService> serviceClass) {

        this.mContext = context;
        this.mTaskLocator = taskLocator;
        this.mTaskResultLocator = taskResultLocator;
        this.mServiceClass = serviceClass;
    }

    @Override
    public <T> void executeTask(ITask<T> task, ITaskDelegate<T> taskDelegate) {
        if(taskDelegate != null) {
            taskDelegate.onTaskToBeExecuted();
        }

        registerTaskListener(task, taskDelegate);

        //Push task to locator to allow service to retrieve
        try {
            pushTaskToLocator(task);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        //Start intent service
        Intent intentForService = new Intent(mContext, mServiceClass);
        intentForService.putExtra(AbstractTaskService.TASK_IDENTIFIER_KEY, task.getId());
        mContext.startService(intentForService);
    }

    @Override
    public <T> void tryToCancelTask(long taskId, ITaskDelegate<T> taskDelegate) {
        //TODO Implement
    }

    @Override
    public <T> Result<T> executeBackgroundTaskForResult(ITask<T> task) {
        //TODO Implement
        return null;
    }

    private <T> void registerTaskListener(ITask<T> task, ITaskDelegate<T> taskDelegate) {
        TaskServiceListener taskServiceListener = new TaskServiceListener(mTaskResultLocator);
        taskServiceListener.resetTask(task, taskDelegate);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(AbstractTaskService.TASK_RESULT_NOTIFY_INTENT_ACTION_NAME);
        intentFilter.addCategory(AbstractTaskService.TASK_RESULT_NOTIFY_INTENT_ACTION_CATEGORY);
        LocalBroadcastManager.getInstance(mContext).registerReceiver(taskServiceListener, intentFilter);
    }

    private <T> void pushTaskToLocator(ITask<T> task) throws Exception{
        mTaskLocator.pushItem(task, task.getId());
    }

    public class TaskServiceListener extends BroadcastReceiver {

        private ITaskDelegate mTaskDelegate;
        private ILocator<Result> mTaskResultLocator;
        private long mTaskId;

        public TaskServiceListener(ILocator<Result> taskResultLocator) {
            mTaskResultLocator = taskResultLocator;
        }

        public void resetTask(ITask task, ITaskDelegate taskDelegate) {
            mTaskDelegate = taskDelegate;
            mTaskId = task.getId();
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            long taskId = intent.getExtras().getLong(AbstractTaskService.TASK_RESULT_IDENTIFIER_KEY);
            if(taskId == mTaskId) {
                Result taskResult = mTaskResultLocator.getItem(taskId);
                if(mTaskDelegate != null) {
                    mTaskDelegate.onTaskExecuted(taskResult);
                }
                try {
                    mTaskResultLocator.removeItem(taskId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
