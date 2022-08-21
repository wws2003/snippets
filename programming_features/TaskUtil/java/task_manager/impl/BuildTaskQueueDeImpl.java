package com.techburg.autospring.task.impl;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.NoSuchElementException;

import com.techburg.autospring.model.business.BuildInfo;
import com.techburg.autospring.task.abstr.IBuildTask;
import com.techburg.autospring.task.abstr.IBuildTaskQueue;

public class BuildTaskQueueDeImpl implements IBuildTaskQueue {
	
	private Deque<IBuildTask> mWaitingTasks;
	private IBuildTask mBuildingTask;
	private Deque<Long> mCancelledBuildTaskIdQueue;
	private long mNextQueueId = 0;
	
	public BuildTaskQueueDeImpl() {
		mWaitingTasks = new ArrayDeque<IBuildTask>();
		mBuildingTask = null;
		mCancelledBuildTaskIdQueue = new ArrayDeque<Long>();
	}

	public synchronized void addBuildTaskToQueue(IBuildTask buildTask) {
		mWaitingTasks.add(buildTask);
	}

	@Override
	public synchronized long genNextQueueId() {
		return mNextQueueId++;
	}
	
	@Override
	public synchronized void markBuildTaskToBeCancelled(long taskId) {
		mCancelledBuildTaskIdQueue.add(taskId);
	}
	
	@Override
	public synchronized long getCancelledBuildTaskId() {
		return mCancelledBuildTaskIdQueue.isEmpty() ? -1 : mCancelledBuildTaskIdQueue.getFirst();
	}

	@Override
	public synchronized void cancelBuildTask(long taskId) {
		for(IBuildTask waitingBuildTask : mWaitingTasks) {
			if(taskId == waitingBuildTask.getIdInQueue()) {
				waitingBuildTask.cancel();
			}
		}
		mCancelledBuildTaskIdQueue.pop();
	}
	
	public synchronized IBuildTask popBuildTaskFromQueue() {
		try {
			return mWaitingTasks.pop();
		}
		catch (NoSuchElementException e) {
			return null;
		}
	}

	public synchronized void getBuildInfoListFromWaitingTaskList(List<BuildInfo> buildInfoList) {
		for(IBuildTask waitingBuildTask : mWaitingTasks) {
			BuildInfo waitingBuildInfo = new BuildInfo();
			waitingBuildTask.storeToBuildInfo(waitingBuildInfo, false);
			buildInfoList.add(waitingBuildInfo);
		}
	}

	@Override
	public synchronized void getBuildInfoListFromBuildingTask(List<BuildInfo> buildInfoList) {
		BuildInfo buildingBuildInfo = new BuildInfo();
		if(mBuildingTask != null) {
			mBuildingTask.storeToBuildInfo(buildingBuildInfo, false);
			buildInfoList.add(buildingBuildInfo);
		}
	}

	@Override
	public synchronized void setBuildingTask(IBuildTask buildingTask) {
		mBuildingTask = buildingTask;
	}

	@Override
	public synchronized IBuildTask getBuildingTask() {
		return mBuildingTask;
	}
}
