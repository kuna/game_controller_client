package com.swmaestro.phonecontroller;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.Toast;

import com.swmaestro.phonecontroller.bluetooth.BluetoothConnect;
import com.swmaestro.phonecontroller.common.DataStructure;
import com.swmaestro.phonecontroller.common.Util;
import com.swmaestro.phonecontroller.sensor.ISensor;
import com.swmaestro.phonecontroller.ui.UIManager;
import com.swmaestro.phonecontroller.wifi.IWifiUDP;

public class Intro extends Activity {
	Context c;
	//Bluetooth mBt;
	//BluetoothConnect mBtc;
	
	//IWifi mWifi; <- TCP
	IWifiUDP mWifi;
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
        			mWifi.SendData(mDs.GetString());
        		}
        		if (m.what == Util.EVENT_BUTTON_DOWN) {
        			Toast.makeText(c, "Down", Toast.LENGTH_SHORT).show();
        			
        			DataStructure mDs = new DataStructure();
        			mDs.event = m.what;
        			mDs.param = 0;
        			mDs.detail = (String) m.obj;
        			mWifi.SendData(mDs.GetString());
        		}
        		if (m.what == Util.EVENT_BUTTON_UP) {
        			Toast.makeText(c, "Up", Toast.LENGTH_SHORT).show();
        			
        			DataStructure mDs = new DataStructure();
        			mDs.event = m.what;
        			mDs.param = 0;
        			mDs.detail = (String) m.obj;
        			mWifi.SendData(mDs.GetString());
        		}
        		if (m.what == Util.EVENT_CLOSE) {
        			close();
        		}
        	}
        };
        
        mWifi = new IWifiUDP();
        mWifi.Initialize(mHandler);
        mSensor = new ISensor();
        mSensor.Initalize(mHandler, this);
        c = this;
        
        // auto cali, auto connect, auto ui
        mSensor.calibrateOri();
		UIManager uiManager = UIManager.getInstance();
		uiManager.setHandler(mHandler);
		uiManager.showController(c, "racing", "ui");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_intro, menu);
        return true;
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) { 
        	close();
        } 
        return true;
    }
    
    @Override
    public void onBackPressed() {
    	close();
    }
    
    public void close() {
		mSensor.stopSensor();
		mWifi.close();
		finish();
    }
    
    Handler mHandler;
    public void setupButtonEvent()
    {

        // auto cali, auto connection, auto controller setup
        Button b1 = null;//(Button)findViewById(R.id.btnConnect);
        b1.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				UIManager uiManager = UIManager.getInstance();
				uiManager.setHandler(mHandler);
				uiManager.showController(c, "test1", "ui");
				//Toast.makeText(c, "Waiting for Connection", Toast.LENGTH_SHORT).show();
				//mWifi.Connect("192.168.0.33", 1234);				
				
				Toast.makeText(c, "UDP Dont need to be Connected", Toast.LENGTH_SHORT).show();
				
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
        
        Button b2 = null;//(Button)findViewById(R.id.btnSend);
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

        Button b3 = null;//(Button)findViewById(R.id.btnClose);
        b3.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				mSensor.stopSensor();
				mWifi.close();
				finish();
				
				//mBt.CloseSocket();
			}
        });
        
        Button b4 = null;//(Button)findViewById(R.id.btnCali);
        b4.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				mSensor.calibrateOri();
			}
        });
        
        Button b5 = null;//(Button)findViewById(R.id.btnUp)  ;
        b5.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View arg0, MotionEvent arg1) {
				if (arg1.getAction() == MotionEvent.ACTION_DOWN) {
					Log.v("TEST", "DOWN");
        			DataStructure mDs = new DataStructure();
        			mDs.event = Util.EVENT_BUTTON_DOWN;
        			mDs.param = 0;
        			mDs.detail = "test";
        			mWifi.SendData(mDs.GetString());
					
				}
				if (arg1.getAction() == MotionEvent.ACTION_UP) {
					Log.v("TEST", "UP");
        			DataStructure mDs = new DataStructure();
        			mDs.event = Util.EVENT_BUTTON_UP;
        			mDs.param = 0;
        			mDs.detail = "test";
        			mWifi.SendData(mDs.GetString());
				}
				return false;
			}
		});
    }
}
