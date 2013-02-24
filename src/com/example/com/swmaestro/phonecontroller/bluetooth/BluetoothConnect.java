package com.example.com.swmaestro.phonecontroller.bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

public class BluetoothConnect extends Thread {
	public static int MESSAGE_READ = 100;
	
	private final BluetoothSocket mmSocket;
	private final InputStream mmInStream;
	private final OutputStream mmOutStream;
	private final Handler mHandler;
	
	public BluetoothConnect(BluetoothSocket socket, Handler h) {
		mmSocket = socket;
		InputStream ti = null;
		OutputStream to = null;
		
		try {
			ti = socket.getInputStream();
			to = socket.getOutputStream(); 
		} catch (IOException e) {
			Log.e("BLUETOOTHCON", "Cannot get Bluetooth Stream");
		}
		
		mmInStream = ti;
		mmOutStream = to;
		mHandler = h;
	}
	
	public void run() {
		byte[] buffer = new byte[1024];
		int bytes;
		
		while (true) {
			try {
				bytes = mmInStream.read(buffer);
				mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer).sendToTarget();
			} catch (IOException e) {
				Log.e("BLUETOOTHCON", "Cannot read Bluetooth Stream");
				break;
			}
		}
	}
	
	public void write(byte[] bytes) {
		try {
			mmOutStream.write(bytes);
			mmOutStream.flush();
		} catch (IOException e) {
			Log.e("BLUETOOTHCON", "Cannot write Bluetooth Stream");
		}
	}
	
	public void cancel() {
		try {
			mmSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
