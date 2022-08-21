package com.tbg.taskmanager.abstr.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.tbg.taskmanager.abstr.locator.ILocator;
import com.tbg.taskmanager.abstr.task.ITask;
import com.tbg.taskmanager.common.Result;

/**
 * Created by wws2003 on 10/29/15.
 */
public abstract class AbstractTaskService extends IntentService {

    public static final String TASK_IDENTIFIER_KEY = "abstract_task_service_task_identifier";
    public static final String TASK_RESULT_IDENTIFIER_KEY = "abstract_task_service_task_result_identifier";
    public static final String TASK_RESULT_NOTIFY_INTENT_ACTION_NAME = "abstract_task_service_task_notify_action";
    public static final String TASK_RESULT_NOTIFY_INTENT_ACTION_CATEGORY = "abstract_task_service_task_notify";

    private ILocator<ITask> mTaskLocator;
    private ILocator<Result> mTaskResultLocator;

    public AbstractTaskService() {
        super("com.tbg.taskmanager.abstr.service.AbstractTaskService");
    }

    public AbstractTaskService(String name) {
        super(name);
        mTaskLocator = getTaskLocator();
        mTaskResultLocator = getTaskResultLocator();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        long taskId = intent.getExtras().getLong(TASK_IDENTIFIER_KEY);
        ITask task = mTaskLocator.getItem(taskId);
        if(task != null) {
            //Execute task and retrieve result
            Result result = task.execute();

            //Clear task in locator
            try {
                mTaskLocator.removeItem(taskId);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }

            //Push task result to locator
            try {
                mTaskResultLocator.pushItem(result, taskId);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }

            //Notify task result
            notifyTaskResult(result, intent);
        }
    }

    protected abstract ILocator<ITask> getTaskLocator();

    protected abstract ILocator<Result> getTaskResultLocator();

    private void notifyTaskResult(Result result, Intent intent) {
        Intent notifyIntent = new Intent(TASK_RESULT_NOTIFY_INTENT_ACTION_NAME);
        notifyIntent.putExtra(TASK_RESULT_IDENTIFIER_KEY, result.getTaskId());
        notifyIntent.addCategory(TASK_RESULT_NOTIFY_INTENT_ACTION_CATEGORY);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(notifyIntent);
     }
}
