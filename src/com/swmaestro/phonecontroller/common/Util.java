package com.swmaestro.phonecontroller.common;

public class Util {
	// Connection Setting
	final public static int MAX_PACKET_SIZE = 1024;
	final public static int THREAD_DELAY = 50;
	
	// Server Connection
	final public static String SERVER_IP = "147.46.240.46";
	final public static int SERVER_PORT = 1236;
	
	// Network Event Consts
	final public static int EVENT_RECV = 100;
	final public static int EVENT_SENSOR = 101;

	final public static int EVENT_SENSOR_ACCL = 200;
	final public static int EVENT_SENSOR_ANGLE = 201;
	
	final public static int EVENT_BUTTON_DOWN = 300;
	final public static int EVENT_BUTTON_UP = 301;
	
	final public static int EVENT_CLOSE = 999;
	
	// Application Event Consts
	final public static int CONN_FAIL = 0x10;
	final public static int CONN_SUCCESS = 0x11;
	final public static int CONN_JOIN = 0x12;
	final public static int CONN_JOINALL = 0x13;
	final public static int CONN_QUIT = 0x14;
	final public static int CONN_CLOSE = 0x15;
	final public static int EVENT_ERROR = 0x20;
	final public static int EVENT_MODIFY = 0x50;
	final public static int EVENT_EDIT = 0x51;
	
	// Used by Application
	public static String CONN_ID = "";
}
