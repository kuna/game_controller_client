package com.swmaestro.phonecontroller.common;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

/*
 * Data structure should be like this : 
 * 1. (int) event - defined with const int
 * 2. (int) param - detail information
 * 3. (int) datalen
 * 4. (byte) data
 */

public class DataStructure {
	public String id;
	public int event;
	public String detail;
	
	public DataStructure() {
		id = Util.CONN_ID;
	}
	
	public byte[] GetByte() {
		return GetString().getBytes();
	}
	
	public String GetString() {
		id = Util.CONN_ID;
		return String.format("EVENT %s %d %s\n", id, event, detail);
	}
	
	public String GetString(String attr) {
		id = Util.CONN_ID;
		return String.format("EVENT %s %s %s\n", id, attr, detail);
	}
}
