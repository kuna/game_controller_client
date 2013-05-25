package com.swmaestro.phonecontroller.ui;

import java.io.File;

import android.os.Environment;
import android.util.Log;

public class UIResourceManager {
	private String extStorageAbsPath;
	private final String uiResourceDir = "gameControllerRes";
	private String gameName;
	private File []fileList;
	
	public UIResourceManager() {
		extStorageAbsPath = getExternalStorageAbsolutePath();
	}
	private String getExternalStorageAbsolutePath() {
		String ext = Environment.getExternalStorageState();
		if (!ext.equals(Environment.MEDIA_MOUNTED))
			return null;
		
		return Environment.getExternalStorageDirectory().getAbsolutePath();
	}
	
	public String getUIResourceAbsolutePath() {
		if (extStorageAbsPath == null)
			return null;

		Log.i("UIResPath", extStorageAbsPath + File.separator + uiResourceDir);
		File resPath = new File(extStorageAbsPath + File.separator + uiResourceDir);
		if (!resPath.exists())
			return null;
		
		return extStorageAbsPath + File.separator + uiResourceDir;
	}

	public boolean prepareUIResource(String gameName) {
		if (getUIResourceAbsolutePath() == null)
			return false;
		
		File resDir = new File(getUIResourceAbsolutePath() + File.separator + gameName);
		fileList = resDir.listFiles();
		this.gameName = gameName; 
		return true;
	}
	
	public String getGameUIResourceAbsolutePath() {
		String uiResAbsPath = null;
		if ((uiResAbsPath = getUIResourceAbsolutePath()) == null)
			return null;

		Log.i("GameUIResPath", uiResAbsPath + File.separator + gameName);
		File resPath = new File(uiResAbsPath + File.separator + gameName);
		if (!resPath.exists())
			return null;
		
		return uiResAbsPath + File.separator + gameName;
	}
	
	public String getResourceAbsolutePath(String resourceFile) {
		for (File resFile : fileList) {
			if (resFile.getName().equals(resourceFile)) {
				return resFile.getAbsolutePath();
			}
		}
		return null;
	}
}
