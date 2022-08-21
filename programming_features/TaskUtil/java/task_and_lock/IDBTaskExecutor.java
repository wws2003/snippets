package com.techburg.autospring.db.task.abstr;

public interface IDBTaskExecutor {
	void executeDBTask(AbstractDBTask dbTask);
	void waitAllFinish();
}
