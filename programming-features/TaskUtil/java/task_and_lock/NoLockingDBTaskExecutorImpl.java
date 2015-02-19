package com.techburg.autospring.db.task.impl;

import com.techburg.autospring.db.task.abstr.AbstractDBTask;
import com.techburg.autospring.db.task.abstr.IDBTaskExecutor;

public class NoLockingDBTaskExecutorImpl implements IDBTaskExecutor {

	@Override
	public void executeDBTask(AbstractDBTask dbTask) {
		dbTask.execute();
	}

	@Override
	public void waitAllFinish() {
		
	}

}
