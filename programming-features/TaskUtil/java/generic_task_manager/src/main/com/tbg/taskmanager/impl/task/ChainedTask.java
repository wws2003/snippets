package com.tbg.taskmanager.impl.task;

import com.tbg.taskmanager.abstr.task.AbstractTask;
import com.tbg.taskmanager.abstr.task.ITask;
import com.tbg.taskmanager.abstr.task.ITaskResultProcessor;
import com.tbg.taskmanager.common.Result;

/**
 * Created by wws2003 on 10/29/15.
 */
public class ChainedTask<T> extends AbstractTask<T> {

    private boolean mResultCascaded;
    private ITask<T> mInitialTask;
    private ITaskResultProcessor<T> mTaskResultProcessor;

    public ChainedTask(ITask<T> initialTask, ITaskResultProcessor<T> taskResultProcessor, boolean resultCascaded) {
        this.mResultCascaded = resultCascaded;
        this.mInitialTask = initialTask;
        this.mTaskResultProcessor = taskResultProcessor;
    }

    @Override
    public Result<T> doExecute() {
        Result<T> initialResult = mInitialTask.execute();
        if(mTaskResultProcessor == null) {
            return getOwnResult(initialResult);
        }
        Result<T> derivedResult = mTaskResultProcessor.processResult(initialResult);
        return mResultCascaded ? getOwnResult(derivedResult) : getOwnResult(initialResult);
    }

    private Result<T> getOwnResult(Result<T> retrievedResult) {
        return new Result<T>(retrievedResult.getElement(), getId(), retrievedResult.getResultCode());
    }
}
