package com.swmaestro.phonecontroller.wifi;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import com.swmaestro.phonecontroller.common.Util;

import android.os.Handler;
import android.util.Log;

public class IWifiUDP {
	private DatagramSocket socket = null;
	private Thread m_listenThread = null;
	private Handler mHandler = null;
	
	public void Initialize(Handler h) {
		try {
			socket = new DatagramSocket(Util.SERVER_PORT);
			mHandler = h;
			
			// start thread
			createListenThread();
			
			m_listenThread.start();
			
		} catch (SocketException e) {
			e.printStackTrace();
			Log.e("UDP", "Failed to create UDP Socket");
		}
	}
	
	public void createListenThread() {
		m_listenThread = new Thread () {
			public void run() {
				while (!this.isInterrupted()) {
					try {
						byte[] inbuf = new byte[Util.MAX_PACKET_SIZE];
						DatagramPacket packet = new DatagramPacket(inbuf, inbuf.length);
						socket.receive(packet);
						mHandler.obtainMessage(Util.EVENT_RECV, 0, 0, inbuf).sendToTarget();
					} catch (IOException e) {
						e.printStackTrace();
						Log.e("UDP", "Failed to Receive Packet");
						break;
					}
				}
			}
		};
	}
	
	public void SendData(final String s) {
		if (socket == null) return;
		
		Thread t = new Thread() {
			public void run() {
				try {
					DatagramPacket packet = new DatagramPacket(s.getBytes(), s.getBytes().length, InetAddress.getByName(Util.SERVER_IP), Util.SERVER_PORT);
					socket.send(packet);
				} catch (Exception e) {
					e.printStackTrace();
					Log.e("UDP", "Failed to Send Packet");
				}
			}
		};
		t.start();
	}
	
	public void close() {
		m_listenThread.interrupt();
		socket.close();
		socket=null;
	}	
}
