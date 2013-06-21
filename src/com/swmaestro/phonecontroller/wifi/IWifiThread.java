package com.swmaestro.phonecontroller.wifi;

import java.io.BufferedReader;
import java.net.SocketException;
import java.util.ArrayList;

import com.swmaestro.phonecontroller.common.Util;

import android.os.Handler;
import android.util.Log;

public class IWifiThread extends Thread {
	ArrayList<Handler> mHandler;
	BufferedReader m_br;
	
	public void Initalize(ArrayList<Handler> h, BufferedReader b) {
		mHandler = h;
		m_br = b;
	}
	
	public void run() {
		Log.i("WIFITHREAD", "Start Thread");
		
		while (!this.isInterrupted()) {
			try {
				String line = m_br.readLine();
				if (line == null) {
					Log.e("WIFITHREAD", "Disconnected");
					for (int i=0; i<mHandler.size(); i++)
						mHandler.get(i).obtainMessage(Util.CONN_CLOSE, 0, 0, null).sendToTarget();
					this.interrupt();
					break;
				}
				Log.i("WIFITHREAD", line);
				for (int i=0; i<mHandler.size(); i++)
					mHandler.get(i).obtainMessage(Util.EVENT_RECV, 0, 0, line).sendToTarget();
			} catch (SocketException e) {
				Log.e("WIFITHREAD", "Socket closed");
				for (int i=0; i<mHandler.size(); i++)
					mHandler.get(i).obtainMessage(Util.CONN_CLOSE, 0, 0, null).sendToTarget();
				this.interrupt();
				break;
			} catch (Exception e) {
				e.printStackTrace();
				Log.e("WIFITHREAD", "Error during Socket Connection. stop Thread.");

				for (int i=0; i<mHandler.size(); i++)
					mHandler.get(i).obtainMessage(Util.CONN_CLOSE, 0, 0, null).sendToTarget();
				this.interrupt();
				break;
			}
		}
	}

}
