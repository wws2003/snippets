package com.techburg.projectxclient.delegate.impl;

import java.util.List;

import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.techburg.projectxclient.app.ProjectXClientApp;
import com.techburg.projectxclient.delegate.abstr.IBuildInfoFetchDelegate;
import com.techburg.projectxclient.model.BuildInfo;

public class BuildInfoFetchDelegateImpl implements IBuildInfoFetchDelegate {

	private RestTemplate mRestTemplate;
	private SharedPreferences mSharedPreferences;
	private long mLastReceivedBuildId = 0;

	public BuildInfoFetchDelegateImpl() {
		mRestTemplate = new RestTemplate();
		mRestTemplate.getMessageConverters().add(new MappingJacksonHttpMessageConverter());
	}

	public void setSharedPreferences(SharedPreferences sharedPreference) {
		mSharedPreferences = sharedPreference;
	}

	@Override
	public synchronized long getLastReceivedBuildId() {
		long id = mSharedPreferences.getLong(ProjectXClientApp.PREFERENCE_LAST_RECEIVED_BUILD_ID, -1);
		if(id < 0) {
			id = 0;
		}
		mLastReceivedBuildId = id;
		return id;
	}

	@Override
	public synchronized void getNewBuildInfoListFromURL(String fetchUrl,
			List<BuildInfo> newBuildInfoList) {
		newBuildInfoList.clear();
		try {
			BuildInfo[] buildInfoArray = mRestTemplate.getForObject(fetchUrl, BuildInfo[].class);
			if(buildInfoArray != null) {
				for(BuildInfo buildInfo : buildInfoArray) {
					newBuildInfoList.add(buildInfo);
					long buildId = buildInfo.getId();
					if(mLastReceivedBuildId < buildId) {
						mLastReceivedBuildId = buildId;
					}
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public synchronized void updateLastReceivedId() {
		Editor editor = mSharedPreferences.edit();
		editor.putLong(ProjectXClientApp.PREFERENCE_LAST_RECEIVED_BUILD_ID, mLastReceivedBuildId);
		editor.commit();
	}

}
