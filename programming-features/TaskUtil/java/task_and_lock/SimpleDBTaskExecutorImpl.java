package com.techburg.autospring.db.task.impl;

import java.util.concurrent.Callable;

import com.techburg.autospring.db.task.abstr.AbstractDBTask;

public class SimpleDBTaskExecutorImpl extends ConcurrencyDBTaskExecutor {

	@Override
	public void handleCallableTask(final Callable<Integer> callable, AbstractDBTask dbTask) {
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				try {
					callable.call();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		
		Thread t = new Thread(runnable);
		t.start();
		if(dbTask.getScheduleMode() == AbstractDBTask.SCHEDULE_SYNC_MODE) {
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void waitAllFinish() {
		
	}

}
