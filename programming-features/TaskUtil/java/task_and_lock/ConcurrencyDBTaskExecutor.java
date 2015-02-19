package com.techburg.autospring.db.task.impl;

import java.util.concurrent.Callable;
import java.util.concurrent.locks.Lock;

import org.springframework.beans.factory.annotation.Autowired;

import com.techburg.autospring.db.task.abstr.AbstractDBTask;
import com.techburg.autospring.db.task.abstr.IDBTaskExecutor;
import com.techburg.autospring.factory.abstr.IDBTaskLockFactory;

public abstract class ConcurrencyDBTaskExecutor implements IDBTaskExecutor {

	private IDBTaskLockFactory mDBTaskLockFactory;
	
	@Autowired
	public void setDBTaskLockFactory(IDBTaskLockFactory factory) {
		mDBTaskLockFactory = factory;
	}

	@Override
	public void executeDBTask(AbstractDBTask dbTask) {
		Callable<Integer> callable = createTaskCallable(dbTask);
		handleCallableTask(callable, dbTask);
	}

	public abstract void handleCallableTask(Callable<Integer> callable, AbstractDBTask dbTask);
	
	private Callable<Integer> createTaskCallable(final AbstractDBTask dbTask) {
		Callable<Integer> callable = new Callable<Integer>() {

			@Override
			public Integer call() throws Exception {
				Lock currentLock = mDBTaskLockFactory.getReadWriteLockInstanceForDBTask(dbTask);
				currentLock.lock();
				try {
					dbTask.execute();
				} catch (Exception e) {
					e.printStackTrace();
				}
				finally {
					currentLock.unlock();
				}

				return 0;
			}
		};
		
		return callable;
	}
}
