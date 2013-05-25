package com.swmaestro.phonecontroller.wifi;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

import com.swmaestro.phonecontroller.common.Util;

import android.os.Handler;
import android.util.Log;

/*
 * NEXT Milestone :
 * 1. ���⿡���� �޽����� �ް� �����غ���
 * 2. Ư���޽��� ������ �������� �۵���Ű�� (���������� ����)
 * 3. �ٸ� �������� ���谡 �غ�Ǿ����� �̸� �������� �������� �����
 * 4. ���������� �̸� ó���ϵ��� �����ϱ�
 * 5. ���� �����
 */

public class IWifi {
	private String s_ip;
	private int s_port;	
	
	private Socket socket;
	private BufferedReader m_nReader;
	private BufferedWriter m_nWriter;
	private IWifiThread m_WThread;
	
	private static ArrayList<Handler> mHandler = new ArrayList<Handler>();

	public IWifi() {
	}
	
	public static void AddHandler(Handler h) {
		mHandler.add(h);
	}
	
	public void Connect(String ip, int port) {
		s_ip = ip;
		s_port = port;
		
		Thread _t = new Thread() {
			public void run() {
				try
				{
					Log.v("WIFI", "Func Executed");
					
					socket = new Socket(s_ip, s_port);
					socket.setTcpNoDelay(true);		
					m_nWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
					m_nReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

					Log.i("WIFI", "Connection Established");
					m_WThread = new IWifiThread();
					m_WThread.Initalize(mHandler, m_nReader);
					m_WThread.start();
					
					// send event
					for (int i=0; i<mHandler.size(); i++)
						mHandler.get(i).obtainMessage(Util.CONN_SUCCESS, 0, 0, null).sendToTarget();
				} catch (Exception e) {
					e.printStackTrace();
					Log.e("WIFI", "Connection Initalize Error");
					
					// send event
					for (int i=0; i<mHandler.size(); i++)
						mHandler.get(i).obtainMessage(Util.CONN_FAIL, 0, 0, null).sendToTarget();
				}
			}
		};
		_t.start();
	}
	
	public boolean SendData(String str) {
		if (m_nWriter == null) return false;
		PrintWriter out = new PrintWriter(m_nWriter, true);
		out.write(str);
		out.flush();
		return true;
	}
	
	public boolean close() {
		return Disconnect();
	}
	public boolean Disconnect() {
		try {
			m_WThread.interrupt();
			m_nWriter = null;
			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("WIFI", "Disconnection Error");
			return false;
		}
		return true;
	}
}
