package com.techburg.autospring.task.impl;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.techburg.autospring.model.business.Workspace;
import com.techburg.autospring.task.abstr.AbstractBuildTask;
import com.techburg.autospring.util.FileUtil;

public class BuildTaskUnixScriptImpl extends AbstractBuildTask {

	private String mDefaultScriptFileLocation;
	private String mDefaultScriptFileName;
	private Workspace mWorkspace;

	private static final String gCommand = "/bin/bash";

	public BuildTaskUnixScriptImpl(String logFilePathPrefix, String logFileExtension, String logFileLocation, String scriptFileLocation, String scriptFileName) {
		super(logFilePathPrefix, logFileExtension, logFileLocation);
		this.mDefaultScriptFileLocation = scriptFileLocation;
		this.mDefaultScriptFileName = scriptFileName;
	}

	@Override
	public void setWorkspace(Workspace workspace) {
		mWorkspace = workspace;
	}
	
	@Override
	public Workspace getWorkspace() {
		return mWorkspace;
	}

	@Override
	protected int mainExecute(StringBuilder outputBuilder) {
		List<String> commandsAndArguments = new ArrayList<String>();
		String scriptFilePath = (mWorkspace != null) ? mWorkspace.getScriptFilePath() : mDefaultScriptFileLocation + File.separator + mDefaultScriptFileName;
		commandsAndArguments.add(gCommand);
		commandsAndArguments.add(scriptFilePath);
		ProcessBuilder processBuilder = new ProcessBuilder(commandsAndArguments);

		int result = BuildTaskResult.SUCCESSFUL;
		try {
			result = startProcessAndGetOutputString(processBuilder, outputBuilder);
		} 
		catch (Exception e) {
			e.printStackTrace();
			return BuildTaskResult.FAILED;
		}
		
		return result;
	}

	private int startProcessAndGetOutputString(ProcessBuilder processBuilder, StringBuilder outputBuilder) throws Exception {
		FileUtil fileUtil = new FileUtil();
		InputStream processBufferedOutputStream = null;
		try {
			processBuilder.redirectErrorStream(true);
			Process process = processBuilder.start();

			processBufferedOutputStream = new BufferedInputStream(process.getInputStream());
			fileUtil.getStringFromInputStream(processBufferedOutputStream, outputBuilder);

			process.waitFor();
			int processExitValue = process.exitValue();
			System.out.println("Process exit value: " + processExitValue);

			return processExitValue;
		} 
		catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
		finally {
			processBufferedOutputStream.close();
		}
	}

}
