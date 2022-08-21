package com.techburg.autospring.db.task.impl;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.techburg.autospring.db.task.abstr.AbstractDBTask;

public class ThreadPoolDBTaskExecutorImpl extends ConcurrencyDBTaskExecutor {
	private ExecutorService mExecutorService;
	
	public ThreadPoolDBTaskExecutorImpl() {
		mExecutorService = Executors.newCachedThreadPool();
	}
	
	@Override
	public void handleCallableTask(Callable<Integer> callable,
			AbstractDBTask dbTask) {
		Future<Integer> future = mExecutorService.submit(callable);

		if(dbTask.getScheduleMode() == AbstractDBTask.SCHEDULE_SYNC_MODE) {
			try {
				future.get();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	public void waitAllFinish() {
		ExecutorService oldExecutorService = mExecutorService;
		mExecutorService = Executors.newCachedThreadPool();
		oldExecutorService.shutdown();
		try {
			oldExecutorService.awaitTermination(1, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
