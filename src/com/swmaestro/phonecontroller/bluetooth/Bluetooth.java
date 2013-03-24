package com.swmaestro.phonecontroller.bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

// http://blog.naver.com/oh4zzang/40111958220
// UUID: http://blog.naver.com/oh4zzang/40111957130

public class Bluetooth {
	BluetoothAdapter mBtAdapter;
	BluetoothDevice mBtDevice;
	public BluetoothSocket mmSocket;
	
	InputStream mmInStream;
	OutputStream mmOutStream;

	public ArrayList<String> mDevicesName = new ArrayList<String>();
	public ArrayList<String> mDevicesAddr = new ArrayList<String>();
	
	public BroadcastReceiver mReceiver;
	
	public boolean DeviceSearchFinished = false;

	public void Initalize() {
		// get object
		mBtAdapter = BluetoothAdapter.getDefaultAdapter();	// if null, then Device wont support Bluetooth
		if (!mBtAdapter.isEnabled()) {
			Log.e("BLUETOOTH", "you have to turn on the Bluetooth Adapter");
			//Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			//startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		}

		mReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
				if (BluetoothDevice.ACTION_FOUND.equals(action)) {
					BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
					
					if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
						mDevicesName.add(device.getName());
						mDevicesAddr.add(device.getAddress());
					}
				} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
					DeviceSearchFinished = true;
					Log.d("BLUETOOTH", "Device Search Finished");
					CancelSearchDevice();
					
					for (int i=0; i<mDevicesName.size(); i++)
						Log.d("DEVICE", mDevicesName.get(i));
					
					// 첫번째 Device를 GUID로 연결
					String name="", mac="";
					int index = -1;
					for (int i=0; i<mDevicesName.size(); i++) {
						name = mDevicesName.get(i);
						mac = mDevicesAddr.get(i);
						if (name.indexOf("PC") > 0) {
							index = i;
						}
					}
					
					if (index < 0) {
						Log.e("BLUETOOTH", "No PC Bluetooth Device found");
					} else {
						Log.v("DEVICE NAME", name);
						Log.v("DEVICE ADDR", mac);
						
						CreateBluetoothDevice(mac);
						CreateSocket( "00001101-0000-1000-8000-00805F9B34FB" );
					}
					
					
					
					/*if (mDevicesName.size() == 0) {
						// device 0
					}*/
				}
			}
		};
		
	}
	
	public void SearchDevice() {
		DeviceSearchFinished = false;
		
		mBtDevice = GetPairedDevice();
		if (mBtDevice != null) {
			Log.e("BLUETOOTH", "Connect with Paired Device");
			CreateSocket( "00001101-0000-1000-8000-00805F9B34FB" );
			DeviceSearchFinished = true;
			return;
		}
		
		// search device
		mBtAdapter.startDiscovery();		
	}
	
	public void CancelSearchDevice() {
		// search cancel
		mBtAdapter.cancelDiscovery();
	}
	
	private BluetoothDevice GetPairedDevice() {
		Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();
		for (BluetoothDevice _d: pairedDevices) {
			if (_d.getName().indexOf("PC") > 0)
				return _d;
		}
		
		return null;
	}
	
	public boolean CreateBluetoothDevice(String MAC) {
		mBtDevice = null;
		mBtDevice = mBtAdapter.getRemoteDevice(MAC);
		if (mBtDevice == null) return false;
		else return true;
	}
	
	public boolean CreateSocket(String UUIDString) {
		mmSocket =  null;
		
		try {
			//final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
			Method m = mBtDevice.getClass().getMethod("createRfcommSocket", new Class[] {int.class});
			mmSocket = (BluetoothSocket) m.invoke(mBtDevice, 1);
			if (mmSocket == null)
				return false;
			
			// bonding
			mmSocket.connect();
			
			mmInStream = mmSocket.getInputStream();
			mmOutStream = mmSocket.getOutputStream();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}
	
	public boolean CloseSocket() {
		Log.i("BLUETOOTH", "Socket Closing");
		try {
			mmInStream.close();
			mmInStream = null;
			mmOutStream.close();
			mmOutStream = null;
			
			mmSocket.close();
			mmSocket = null;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	public boolean SendData(byte[] b) {
		if (mmOutStream == null) return false;

		try {
			mmOutStream.write(b);
			mmOutStream.flush();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	public boolean ReadData(byte[] b) {
		if (mmInStream == null) return false;
		//if (mmInStream.available() <= 0) return false;
		int bl = 0;
		
		try {
			bl = mmInStream.read(b);
			if (bl > 0) {
				
			}
		} catch (IOException e) {
			// connectionlost
			return false;
		}
		
		return true;
	}
}
