package com.techburg.autospring.task.abstr;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Date;

import com.techburg.autospring.model.business.BuildInfo;
import com.techburg.autospring.util.FileUtil;

public abstract class AbstractBuildTask implements IBuildTask{
	protected long mId;
	private int mStatus;
	private Date mBeginBuildTime;
	private Date mEndBuildTime;
	private String mLogFileNamePrefix;
	private String mLogFileNameExtension;
	
	private static final String G_UNKNOWN_LOG_FILE_NAME = "---";
	private static final String LOG_DIR_NAME = "Logs";
	
	public AbstractBuildTask(String logFilePathPrefix, String logFileNameExtension, String logFileLocation) {
		mLogFileNamePrefix = logFilePathPrefix;
		mLogFileNameExtension  = logFileNameExtension;
	}

	@Override
	public void setIdInQueue(long id) {
		mId = id;
	}
	
	@Override
	public long getIdInQueue() {
		return mId;
	}

	@Override
	public void cancel() {
		mStatus = BuildTaskResult.CANCELLED;
	}
	
	@Override
	public int execute() {
		if(mStatus == BuildTaskResult.CANCELLED) {
			return BuildTaskResult.CANCELLED;
		}
		mBeginBuildTime = new Date();
		StringBuilder buildOutput = new StringBuilder();
		int ret = mainExecute(buildOutput);
		postExecuted(buildOutput.toString());
		mEndBuildTime = new Date();
		mStatus = ret;
		return ret;
	}

	@Override
	public void storeToBuildInfo(BuildInfo buildInfo, boolean toPersist) {
		buildInfo.setBeginTimeStamp(mBeginBuildTime);
		buildInfo.setEndTimeStamp(mEndBuildTime);
		buildInfo.setStatus(mStatus);
		buildInfo.setWorkspace(getWorkspace());
		if(!toPersist) {
			buildInfo.setId(mId);
		}
		String logFilePath = (mBeginBuildTime != null) ? getLogFileFullPath() : G_UNKNOWN_LOG_FILE_NAME;
		buildInfo.setLogFilePath(logFilePath);
	}

	private String getLogFileFullPath() {
		StringBuilder logFileNameBuilder = new StringBuilder();
		logFileNameBuilder.append(getWorkspace().getDirectoryPath())
						.append(File.separator)
						.append(LOG_DIR_NAME)
						.append(File.separator)
						.append(mLogFileNamePrefix)
						.append("_")
						.append(mBeginBuildTime.getTime())
						.append(".")
						.append(mLogFileNameExtension);
	
		return logFileNameBuilder.toString();
	}
	
	private void postExecuted(String buildResult) {
		StringBuilder logFolderNameBuilder = new StringBuilder();
		logFolderNameBuilder.append(getWorkspace().getDirectoryPath())
						.append(File.separator)
						.append(LOG_DIR_NAME);
		
		File logFolder = new File(logFolderNameBuilder.toString());
		if(!logFolder.exists()) {
			logFolder.mkdir();
		}
		
		try {
			writeOutputToLogFile(buildResult);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void writeOutputToLogFile(String output) throws Exception {
		FileUtil fileUtil = new FileUtil();
		OutputStream logFileOutputStream = null;
		try {
			logFileOutputStream = new BufferedOutputStream(new FileOutputStream(getLogFileFullPath()));
			fileUtil.writeStringToOutputStream(output, logFileOutputStream);
			logFileOutputStream.close();
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			logFileOutputStream.close();
		}
	}
	
	protected abstract int mainExecute(StringBuilder buildOutput);
}
