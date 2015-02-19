package com.techburg.autospring.task.abstr;

import java.util.List;

import com.techburg.autospring.model.business.BuildInfo;

public interface IBuildTaskProcessor {
	public static class BuildTaskProcessorResult {
		public static final int ADD_TASK_FAILED = -1;
		public static final int ADD_TASK_SUCCESSFUL= 0;
		public static final int CANCEL_TASK_FAILED = -1;
		public static final int CANCEL_TASK_SUCCESSFUL= 0;
		public static final int START_FAILED = -1;
		public static final int START_SUCCESSFUL= 0;
		public static final int ALREADY_STARTED = 1;
		public static final int STOP_FAILED = -1;
		public static final int STOP_SUCCESSFUL= 0;
		public static final int ALREADY_STOPPED = 1;
	}
	
	int start();
	int addBuildTask(IBuildTask buildTask);
	int cancelTask(long taskId);
	int cancelAllBuildTask();
	int stop();
	
	void getBuildingBuildInfoList(List<BuildInfo> buildInfoList);
	void getWaitingBuildInfoList(List<BuildInfo> buildInfoList);
}
