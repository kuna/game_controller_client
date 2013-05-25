package com.swmaestro.phonecontroller;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.swmaestro.phonecontroller.bluetooth.BluetoothConnect;
import com.swmaestro.phonecontroller.common.DataStructure;
import com.swmaestro.phonecontroller.common.Util;
import com.swmaestro.phonecontroller.sensor.ISensor;
import com.swmaestro.phonecontroller.ui.ComponentModifier;
import com.swmaestro.phonecontroller.ui.UIManager;
import com.swmaestro.phonecontroller.ui.dlg_Loading;
import com.swmaestro.phonecontroller.ui.components.Controller;
import com.swmaestro.phonecontroller.wifi.IWifi;
import com.swmaestro.phonecontroller.wifi.IWifiUDP;

public class Intro extends Activity {
	Context c;
	//Bluetooth mBt;
	//BluetoothConnect mBtc;
	
	IWifi mWifi; //<- TCP
	//IWifiUDP mWifi;
	ISensor mSensor;
    Handler mHandler;
    UIManager uiManager = null;
    
    Intent _intent = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        c = this;

        mHandler = new Handler() {
        	public void handleMessage(Message m) {
                String args[];
                DataStructure mDs;
                
        		switch (m.what) {
        		case Util.EVENT_RECV:
        			args = m.obj.toString().split(" ");
        			if ("Shake".equals(m.obj)) {
        				// Vibration
        				Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        				v.vibrate(500);
        			}
        			if (args[0].equals("ERROR")) {
        				this.obtainMessage(Util.EVENT_ERROR, 0, 0, null).sendToTarget();
        			}
        			if (args[0].equals("OK")) {
        				if (args[1].equals("JOIN")) {
        					if (args[2].equals("ALL")) {
        						this.obtainMessage(Util.CONN_JOINALL, 0, 0, null).sendToTarget();
        					} else {
        						this.obtainMessage(Util.CONN_JOIN, 0, 0, args[2]).sendToTarget();
        					}
        				}
        			}
        			if (args[0].equals("MODIFY")) {
        				this.obtainMessage(Util.EVENT_MODIFY, 0, 0, args).sendToTarget();
        			}
        			if (args[0].equals("EDIT")) {
        				this.obtainMessage(Util.EVENT_EDIT, 0, 0, args).sendToTarget();
        			}
        			if (args[0].equals("QUIT")) {
        				this.obtainMessage(Util.CONN_QUIT, 0, 0, args).sendToTarget();
        			}
        			break;
        		case Util.EVENT_SENSOR_ACCL:
        			mDs = new DataStructure();
        			mDs.event = m.what;
        			mDs.detail = (String) m.obj;
        			mWifi.SendData(mDs.GetString());
        			break;
        		case Util.EVENT_SENSOR_ANGLE:
        			mDs = new DataStructure();
        			mDs.event = m.what;
        			mDs.detail = (String) m.obj;
        			mWifi.SendData(mDs.GetString());
        			break;
        		case Util.EVENT_BUTTON_DOWN:	// m.obj: Key
        			mDs = new DataStructure();
        			mDs.event = m.what;
        			mDs.detail = Integer.toString(m.arg1);
        			mWifi.SendData(mDs.GetString());
        			break;
        		case Util.EVENT_BUTTON_UP:
        			mDs = new DataStructure();
        			mDs.event = m.what;
        			mDs.detail = Integer.toString(m.arg1);
        			mWifi.SendData(mDs.GetString());
        			break;
        		case Util.CONN_QUIT:
        			/*
        			 * Close Controller
        			 */
        			if (Controller.ConActivity != null) {
        				Controller.ConActivity.finish();
        				Controller.ConActivity = null;
        			}
        			
        			/*
        			 * return to Initalized status
        			 */
        			Toast.makeText(c, "Closed by server", Toast.LENGTH_SHORT).show();
        			
        			if (mSensor != null) mSensor.stopSensor();
        			
        			setContentView(R.layout.dlg_connect);
        			setupButtonEvent();
        			break;
        		case Util.CONN_CLOSE:
        			/*
        			 * close program
        			 */
        			AlertDialog.Builder alert = new AlertDialog.Builder(c);

    				alert.setTitle("Server Disconnected");
    				alert.setMessage("Connection disconnected by Command QUIT.");

    				alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
    					public void onClick(DialogInterface dialog, int whichButton) {
    						finish();
    					}
    				});

    				alert.show();
        			break;
        		case Util.CONN_FAIL:
        			dlg_Loading.hideLoading();
        			Toast.makeText(c, "Failed to connect server", Toast.LENGTH_SHORT);
        			close();
        			break;
        		case Util.CONN_SUCCESS:
        			dlg_Loading.hideLoading();
        			
        			/*
        			 * send CONNCON to register controller
        			 */
        			mWifi.SendData("CONNCON");
        			
        			/*
        			 * Change form to Connection form
        			 */
        			setContentView(R.layout.dlg_connect);
        			setupButtonEvent();
        			break;
        		case Util.CONN_JOIN:
        			Util.CONN_ID = (String) m.obj;
        			Toast.makeText(c, "Successfully Joined. waiting for other players...", Toast.LENGTH_SHORT)
        			.show();
        			break;
        		case Util.CONN_JOINALL:
        			/*
        			 * Connection Established, close loading form and show controller main screen
        			 */
        			dlg_Loading.hideLoading();
        			setContentView(R.layout.dlg_connected);
        			
        			/*
        			 * Enable Sensor
        			 *//*
        	        mSensor = new ISensor();
        	        mSensor.Initalize(this, c);
        	        mSensor.calibrateOri();*/
        	        break;
        		case Util.EVENT_MODIFY:
        			args = (String[]) m.obj;
        			if (Controller.ConActivity != null) {
        				Controller.ConActivity.finish();
        				Controller.ConActivity = null;
        			}
        			
        			if (args[2].length() > 0) {
        				// if len is 0, then just close controller
	        	        uiManager = UIManager.getInstance();
	        			uiManager.setHandler(mHandler);
	        			_intent = uiManager.showController(c, args[2], args[3]);	// gamename, xmlfilename
        			}
        			
        			/*
        			 * CALLBACK for UI Updating done.
        			 */
        			
        			break;
        		case Util.EVENT_EDIT:
        			args = (String[]) m.obj;
        			Log.i("EDIT_ID", args[2]);
        			Log.i("EDIT_TYPE", args[3]);	// BUTTON, LABEL, 
        			Log.i("EDIT_ATTR", args[4]);	// VISIBLE, X, Y, WIDTH, HEIGHT, OPACITY, ENABLED, TEXT, IMAGE
        			Log.i("EDIT_VAL", args[5]);
        			
        			// when UI is alive
        			if (Controller.ConActivity != null) {
        				try {
            				int _id = Integer.parseInt(args[2]);
        					Object _obj = Controller.ConActivity.findViewById(_id);
        					if (_obj == null) {
        						Log.e("EDIT", "ERROR: No object exists matching the required ID");
        					} else {
        						ComponentModifier.ModifyComponent(_obj, args[3], args[4], args[5]);
        					}
        				} catch (Exception e) {
        					e.printStackTrace();
        				}
        			}
        		}
        	}
        };

        /*
         * Loading form
         */
        dlg_Loading.showLoading(c);

		/*
		 * Connection to Server
		 */
        mWifi = new IWifi();
        IWifi.AddHandler(mHandler);
        mWifi.Connect(Util.SERVER_IP, Util.SERVER_PORT);
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
    	if(mSensor!=null) mSensor.stopSensor();
		if(mWifi!=null) mWifi.close();
		finish();
    }
    
  public void setupButtonEvent()
  {
    	Button btn_Connect = (Button)findViewById(R.id.btn_connect);
    	btn_Connect.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
						TextView t = (TextView)findViewById(R.id.edit_connect);
				String str = String.format("JOIN %s", t.getText());
				
				dlg_Loading.showLoading(c);
				mWifi.SendData(str);
				}
			});
    	
    	/*
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
        });*/
	}
}
