package com.techburg.autospring.task.abstr;

import com.techburg.autospring.model.business.BuildInfo;
import com.techburg.autospring.model.business.Workspace;

public interface IBuildTask {
	public static class BuildTaskResult {
		public static final int SUCCESSFUL = 0;
		public static final int FAILED = 1;
		public static final int CANCELLED = 2;
	}
	
	void setWorkspace(Workspace workspace);
	Workspace getWorkspace();
	
	void cancel();
	int execute();
	
	void storeToBuildInfo(BuildInfo buildInfo, boolean toPersist);
	
	void setIdInQueue(long id);
	long getIdInQueue();
}
