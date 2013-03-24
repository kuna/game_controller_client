package com.swmaestro.phonecontroller.common;

public class Util {
	// handler event
	public static int EVENT_RECV = 100;
	public static int EVENT_SENSOR = 101;

	public static int EVENT_SENSOR_ACCL = 200;
	public static int EVENT_SENSOR_ANGLE = 201;
	
	public static int EVENT_BUTTON_DOWN = 300;
	public static int EVENT_BUTTON_UP = 301;
	
	public static int EVENT_CLOSE = 999;
	
	// consts
	public static int MAX_PACKET_SIZE = 1024;
	
	// temp
	public static String SERVER_IP = "192.168.2.2";
	public static int PORT = 1234;
	public static int THREAD_DELAY = 20;
}
