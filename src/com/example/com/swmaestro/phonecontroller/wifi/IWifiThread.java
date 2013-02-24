package com.example.com.swmaestro.phonecontroller.wifi;

import java.io.BufferedReader;

import com.example.com.swmaestro.phonecontroller.common.Util;

import android.os.Handler;
import android.util.Log;

public class IWifiThread extends Thread {
	Handler mHandler;
	BufferedReader m_br;
	public boolean bThread = false;
	
	public void Initalize(Handler h, BufferedReader b) {
		mHandler = h;
		m_br = b;
	}
	
	public void stopThread() {
		bThread = false;
	}
	
	public void run() {
		bThread = true;
		Log.i("WIFITHREAD", "Start Thread");
		
		while (bThread) {
			try {
				//char[] buf = new char[256];
				//m_br.read(buf);
				//String line = buf.toString();
				String line = m_br.readLine();
				Log.i("WIFITHREAD", line);
				mHandler.obtainMessage(Util.EVENT_RECV, 0, 0, line).sendToTarget();
			} catch (Exception e) {
				e.printStackTrace();
				Log.e("WIFITHREAD", "Error during Socket Connection");
			}
		}
	}

}
