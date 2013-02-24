package com.example.com.swmaestro.phonecontroller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import com.example.com.swmaestro.phonecontroller.bluetooth.Bluetooth;
import com.example.com.swmaestro.phonecontroller.bluetooth.BluetoothConnect;
import com.example.com.swmaestro.phonecontroller.common.*;
import com.example.com.swmaestro.phonecontroller.sensor.ISensor;
import com.example.com.swmaestro.phonecontroller.wifi.IWifi;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.IntentFilter;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class Intro extends Activity {
	Context c;
	Bluetooth mBt;
	BluetoothConnect mBtc;
	
	IWifi mWifi;
	ISensor mSensor;

    @Override
    // 1. oncreate 이후 Data 계속 확인하는 쓰레드 돌리기
    // 2. 메서드 조금 더 체계화 시켜보기... 일단 블루투스 기기 찾는데까지 고려
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        mHandler = new Handler () {
        	public void handleMessage(Message m) {
        		if (m.what == BluetoothConnect.MESSAGE_READ) {
        			//Log.i("BLUETOOTHMSG", m.obj.toString());
        		}
        		if (m.what == Util.EVENT_RECV) {
        			if ("Shake".equals(m.obj)) {
        				// Vibration
        				Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        				v.vibrate(500);
        			}
        		}
        		if (m.what == Util.EVENT_SENSOR_ACCL) {
        			DataStructure mDs = new DataStructure();
        			mDs.event = m.what;
        			mDs.param = 0;
        			mDs.detail = (String) m.obj;
        			mWifi.SendData(mDs.GetString());
        		}
        		if (m.what == Util.EVENT_SENSOR_ANGLE) {
        			DataStructure mDs = new DataStructure();
        			mDs.event = m.what;
        			mDs.param = 0;
        			mDs.detail = (String) m.obj;
        			Log.i("test", mDs.GetString());
        			mWifi.SendData(mDs.GetString());
        		}
        	}
        };
        
        mWifi = new IWifi();
        mWifi.Initalize(mHandler);
        mBt = new Bluetooth();
        mBt.Initalize();
        mSensor = new ISensor();
        mSensor.Initalize(mHandler, this);
        c = this;

        Button b1 = (Button)findViewById(R.id.btnConnect);
        b1.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				Toast.makeText(c, "Waiting for Connection", Toast.LENGTH_SHORT).show();
				mWifi.Connect("192.168.0.108", 1234);				
				
				/*
				 * BLUETOOTH
				 * 
				Toast.makeText(c, "Wait for search Device", Toast.LENGTH_SHORT).show();
				
				// add receiver
				IntentFilter filter;
				filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
				c.registerReceiver(mBt.mReceiver, filter);
				filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
				c.registerReceiver(mBt.mReceiver, filter);
				
				// search Device .. automatically connect
				mBt.SearchDevice();
				*/
			}
        });
        
        Button b2 = (Button)findViewById(R.id.btnSend);
        b2.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Toast.makeText(c, "test", Toast.LENGTH_SHORT).show();
				
				mWifi.SendData("test\n");
				/*
				 * BLUETOOTH
				 * 
				 * DataStructure mDs = new DataStructure();
				mDs.event = 0;
				mDs.param = 0;
				mDs.detail = "";
				
				mBt.SendData(mDs.GetByte());*/
				//mBtc.write(mDs.GetByte());
			}
	    });

        Button b3 = (Button)findViewById(R.id.btnClose);
        b3.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				mSensor.stopSensor();
				mWifi.Disconnect();
				
				//mBt.CloseSocket();
			}
        });
        
        Button b4 = (Button)findViewById(R.id.btnCali);
        b4.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				mSensor.calibrateOri();
			}
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_intro, menu);
        return true;
    }
    
    Handler mHandler;
}
