package com.swmaestro.phonecontroller;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
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
    // 1. oncreate ¿Ã»ƒ Data ∞Ëº” »Æ¿Œ«œ¥¬ æ≤∑πµÂ µπ∏Æ±‚
    // 2. ∏ﬁº≠µÂ ¡∂±› ¥ı √º∞Ë»≠ Ω√ƒ—∫∏±‚... ¿œ¥‹ ∫Ì∑Á≈ıΩ∫ ±‚±‚ √£¥¬µ•±Ó¡ˆ ∞Ì∑¡
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
        /*
        UIManager uiManager = UIManager.getInstance();
		uiManager.setHandler(mHandler);
		uiManager.showController(c, "racing", "ui");
		*/
		setupButtonEvent();
		
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("Server IP 설정");
		alert.setMessage("IP Address");

		// Set an EditText view to get user input
		final EditText input = new EditText(this);
		alert.setView(input);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				Util.SERVER_IP = input.getText().toString();
				// Do something with value!
			}
		});


		alert.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// Canceled.
					}
				});
		
		alert.show();
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
    	//close();
    }
    
    public void close() {
    	mSensor.stopSensor();
		mWifi.close();
		finish();
    }
    
    Handler mHandler;
    public void setupButtonEvent()
    {
    	Button b1 = (Button)findViewById(R.id.btnGogun);
    	b1.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {      
				UIManager uiManager = UIManager.getInstance();
				uiManager.setHandler(mHandler);
				uiManager.showController(c, "gogun", "ui");
			}
        });
        
    	Button b2 = (Button)findViewById(R.id.btnRacing);
    	b2.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				UIManager uiManager = UIManager.getInstance();
				uiManager.setHandler(mHandler);
				uiManager.showController(c, "racing", "ui");
			}
        });
    }
}
