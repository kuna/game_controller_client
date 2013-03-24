package com.swmaestro.phonecontroller.wifi;

import java.io.BufferedReader;

import com.swmaestro.phonecontroller.common.Util;

import android.os.Handler;
import android.util.Log;

public class IWifiThread extends Thread {
	Handler mHandler;
	BufferedReader m_br;
	
	public void Initalize(Handler h, BufferedReader b) {
		mHandler = h;
		m_br = b;
	}
	
	public void run() {
		Log.i("WIFITHREAD", "Start Thread");
		
		while (!this.isInterrupted()) {
			try {
				String line = m_br.readLine();
				Log.i("WIFITHREAD", line);
				mHandler.obtainMessage(Util.EVENT_RECV, 0, 0, line).sendToTarget();
			} catch (Exception e) {
				Log.e("WIFITHREAD", "Error during Socket Connection. stop Thread.");
				this.interrupt();
				break;
			}
		}
	}

}
