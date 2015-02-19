package com.techburg.projectxclient.delegate.abstr;

import java.util.List;

import com.techburg.projectxclient.model.BuildInfo;

public interface IBuildInfoFetchDelegate {
	long getLastReceivedBuildId();
	void getNewBuildInfoListFromURL(String fetchUrl, List<BuildInfo> newBuildInfoList);
	void updateLastReceivedId();
}
