package com.swmaestro.phonecontroller.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;

import com.swmaestro.phonecontroller.ui.components.Controller;
import com.swmaestro.phonecontroller.ui.model.ControllerEvent;
import com.swmaestro.phonecontroller.common.Util;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.View;

public class UIManager {
	
	private static UIManager uiManager = new UIManager();
	private UIResourceManager uiResManager;
	private ControllerEvent controllerEvent;
	private String uiXmlFileName;
	private Handler mHandler;
	
	private UIManager() {
		uiResManager = new UIResourceManager();
		controllerEvent = new ControllerEvent();
		
		// handler
		h = new HandlerEvent();
		controllerEvent.setControllerEventListener(h);
		
		mHandler = null;
	}
	
	public static UIManager getInstance() {
		return uiManager;
	}
	
	public Intent showController(Context context, String gameName, String uiXmlFileName) {
		this.uiXmlFileName = uiXmlFileName;
		uiResManager.prepareUIResource(gameName);
		Intent intent = new Intent(context, Controller.class);
    	context.startActivity(intent);
    	return intent;
	}
	
	public View getLayout(Context context) {
        View layout = null;
        try{
        	StringReader reader = null;
    		UIXmlParser xmlParser = new UIXmlParserImpl();
    		ComponentBuilder componentBuilder = new ComponentBuilderImpl();
    		List<HashMap<String, String>> components = null;
    		String absolutePath = uiResManager.getGameUIResourceAbsolutePath();
    		if (absolutePath == null) {
    			throw new Exception("Unable to find Absolute Path");
    		}
    		String xmlFilePath = uiResManager.getGameUIResourceAbsolutePath() + File.separator + uiXmlFileName + ".xml";
    		Log.v("UIManager", "Loading " + xmlFilePath);
        	reader = getXmlData(xmlFilePath);
        	components = xmlParser.parse(reader);
        	layout = componentBuilder.buildLayout(context, components, uiResManager);
        } catch(Exception e) {
        	Log.d("Exception",e.toString());
        }
		return layout;
	}
	
	private StringReader getXmlData(String uiXmlPath) throws Exception {
		StringReader reader = null;
		File uiXmlFile = new File(uiXmlPath); 
		if(!uiXmlFile.exists())
			throw new Exception("uiXmlPath + does not exist!");
		FileInputStream fis = new FileInputStream(uiXmlFile);
		byte[] data = new byte[fis.available()];
		while (fis.read(data) != -1) {
			;
		}
		fis.close();
		String xmlData = new String(data);
		reader = new StringReader(xmlData);
		return reader;
	}
	
	public void setControllerEventListener(ControllerEventListener l) {
		controllerEvent.setControllerEventListener(l);
	}
	
	public void setHandler(Handler h) {
		mHandler = h;
	}
	
	private void sendHandlerMessage(int event, int button, String detail) {
		if (mHandler == null) return;
		mHandler.obtainMessage(event, button, 0, detail).sendToTarget();
	}

	// for event Handling
	class HandlerEvent implements ControllerEventListener {
		public boolean OnControllerEvent(ControllerEvent event) {
			if (event.getEvent() == ControllerEvent.TOUCH_DOWN) {
				sendHandlerMessage(Util.EVENT_BUTTON_DOWN, event.getComponentId(), event.getDetail());
			}
			if (event.getEvent() == ControllerEvent.TOUCH_UP) {
				sendHandlerMessage(Util.EVENT_BUTTON_UP, event.getComponentId(), event.getDetail());
			}
			return false;
		}
	}
	HandlerEvent h;
}
