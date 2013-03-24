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
	public int event;
	public int param;
	public String detail;
	
	public byte[] GetByte() {
		String msg = String.format("%d#%d#%s\n", event,param,detail);
		return msg.getBytes();
	}
	
	public String GetString() {
		return String.format("%d#%d#%s\n", event,param,detail);
	}
}
