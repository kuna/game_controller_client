package com.swmaestro.phonecontroller.ui.model;

import java.util.ArrayList;
import java.util.List;

import com.swmaestro.phonecontroller.ui.ControllerEventListener;

public class ControllerEvent {
	public static final int TOUCH_DOWN = 0;
	public static final int TOUCH_UP = 1;
	
	private static List<ControllerEventListener> eventListenerList = new ArrayList<ControllerEventListener>();
	private int componentId;
	private int event;
	private String detail;
	
	public void setControllerEventListener(ControllerEventListener l) {
		eventListenerList.add(l);
	}
	
	public void generateEvent(int componentId, int event) {
		this.componentId = componentId;
		this.event = event;
		notifyToListeners();
	}
	
	public void generateEvent(int componentId, int event, String detail) {
		this.componentId = componentId;
		this.event = event;
		this.detail = detail;
		notifyToListeners();
	}
	
	public int getComponentId() {
		return componentId;
	}
	
	public int getEvent() {
		return event;
	}
	
	public String getDetail() {
		return detail;
	}
	
	private void notifyToListeners() {
		for (ControllerEventListener l : eventListenerList) {
			l.OnControllerEvent(this);
		}
	}
	
}
