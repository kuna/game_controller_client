package com.swmaestro.phonecontroller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import kr.soma.classifier.HMM;
import kr.soma.controller.GestureController;
import kr.soma.events.AccelerationEvent;
import kr.soma.events.AccelerationListener;
import kr.soma.events.RecognitionEvent;
import kr.soma.events.RecognitionListener;
import kr.soma.filter.Direction;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.DisplayMetrics;
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
import com.swmaestro.phonecontroller.ui.ComponentBuilderImpl;
import com.swmaestro.phonecontroller.ui.ComponentModifier;
import com.swmaestro.phonecontroller.ui.UIManager;
import com.swmaestro.phonecontroller.ui.dlg_Loading;
import com.swmaestro.phonecontroller.ui.components.Controller;
import com.swmaestro.phonecontroller.ui.components.GTextView;
import com.swmaestro.phonecontroller.wifi.IWifi;
import com.swmaestro.phonecontroller.wifi.IWifiUDP;

import be.ac.ulg.montefiore.run.jahmm.Hmm;
import be.ac.ulg.montefiore.run.jahmm.ObservationDiscrete;


public class Intro extends Activity {
	Context c;
	
	IWifi mWifi; // TCP
	ISensor mSensor;
    Handler mHandler;
    UIManager uiManager = null;
    
    Intent _intent = null;
    private boolean running = true;
    
    // accel sensor detector
    private static GestureController gc;
    private SensorManager sm;
    private Sensor accSensor;
    private boolean recognitionMode;
    private boolean sensordatasend = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        c = this;
        
        setupHandlerEvent();
        setupSensor();
        
        // set screen ratio
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        Util.SCREEN_RATIO = (double)dm.widthPixels / 480.0;
        
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
        /*
         * Test case start
         * 
         *
        
        Thread t = new Thread() {
        	public void run() {
        		try {
        			mHandler.obtainMessage(Util.EVENT_MODIFY, 0, 0, new String[]{"", "", "Flag", "game"}).sendToTarget();
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
        	}
        };
        t.start();
         * 
         * Test case end
         */
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
    /*
    @Override
    protected void onPause() {
    	super.onPause();
    	if (sm != null)
    		sm.unregisterListener(gc);    // unregister acceleration listener
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	if (sm != null)
    		sm.registerListener(gc, accSensor, SensorManager.SENSOR_DELAY_FASTEST);
    }
    */
    @Override
    public void onBackPressed() {
    	close();
    }
    
    public void close() {
    	if(mSensor!=null) mSensor.stopSensor();
		if(mWifi!=null) mWifi.close();
		running = false;
		finish();
    }
    
	public void setupButtonEvent()
	{
    	Button btn_Connect = (Button)findViewById(R.id.btn_connect);
    	btn_Connect.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
						TextView t = (TextView)findViewById(R.id.edit_connect);
				if ( t.getText().equals("")) {
					Toast.makeText(c, "Enter PIN Number", Toast.LENGTH_SHORT).show();
					return;
				}
				
				String str = String.format("JOIN %s", t.getText());
				
				dlg_Loading.showLoading(c);
				mWifi.SendData(str);
				}
			});
	}
	
	public void setupSensor() {
		sm = (SensorManager)getSystemService(android.content.Context.SENSOR_SERVICE);
		accSensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		
		gc = new GestureController();  // 제스쳐 인식 객체 생성 
		gc.enableRecognitionMode(true); // 제스쳐 인식 모드 on
		sm.registerListener(gc, accSensor, SensorManager.SENSOR_DELAY_FASTEST);
		
		setSensorModel(null);
		
		gc.addRecognitionListener(new RecognitionListener() {
			public void doRecognitionProcess(RecognitionEvent event) {
				switch(event.getMsg()) {
				case GestureController.START_RECOGNITION:
					Log.i("Sensor", "Motion Recognization start");
				break;
				case GestureController.END_RECOGNITION:
					Log.i("Sensor", "Motion Recognization end");
					if (event.getObj1() == null) {
						Log.i("Sensor", "Unable to found current Gesture");
					}
					else {
						Log.i("Sensor", ((HMM)event.getObj1()).getName());
						mHandler.obtainMessage(Util.EVENT_GESTURE, 0, 0, ((HMM)event.getObj1()).getName()).sendToTarget();
					}
					break;
				case GestureController.NOT_ENOUGH_DATA:
					break;
				}
			}
		});
		
		//
		mSensor = new ISensor();
		mSensor.Initalize(mHandler, c);
	}
	
	public static void setSensorModel(ArrayList<String> name) {
		Map<String, Hmm<ObservationDiscrete<Direction>>> hmms = gc.readModelsFromFiles(name); // GestureController/HMM 에 저장된 모든 모델 파일을 불러옴
		gc.setModels(hmms); // 모델 객체들을  등록 
	}
	
	public void setupHandlerEvent() {
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
        				this.obtainMessage(Util.EVENT_ERROR, 0, 0, args[1]).sendToTarget();
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
        			if (args[0].equals("SENDDATA")) {
        				// start to send custom data
        				// - after calibrate it
        				mSensor.calibrateOri();
        				sensordatasend = true;
        			}
        			break;
        		case Util.EVENT_ERROR:
        			dlg_Loading.hideLoading();
        			Toast.makeText(c, "An Error occured.", Toast.LENGTH_SHORT).show();
        			break;
        		case Util.EVENT_SENSOR_ACCL:
        			/*mDs = new DataStructure();
        			mDs.event = m.what;
        			mDs.detail = (String) m.obj;
        			if (sensordatasend)
        				mWifi.SendData(mDs.GetString());*/
        			break;
        		case Util.EVENT_SENSOR_ANGLE:
        			mDs = new DataStructure();
        			mDs.event = m.what;
        			mDs.detail = (String) m.obj;
        			if (sensordatasend)
        				mWifi.SendData(mDs.GetString());
        			break;
        		case Util.EVENT_BUTTON_DOWN:	// m.obj: Key
        			mDs = new DataStructure();
        			mDs.event = m.what;
        			mDs.detail = Integer.toString(m.arg1);
        			mWifi.SendData(mDs.GetString());
        			
        			// check Trigger variables
        			for (HashMap<String, String> data : ComponentBuilderImpl.triggers) {
        				if (data.get("id").equals( mDs.detail )) {
							String[] objs = ((String) data.get("targetid")).split(" ");
							for (String objName: objs) {
								View obj = (View) Controller.ConActivity.findViewById( Integer.parseInt(objName) );
								
	        					// check option
	        					if (data.get("attr").equals("display")) {
	        						if (data.get("val").equals("true")) {
	        							obj.setVisibility(View.VISIBLE);
	        						} else if (data.get("val").equals("false")) {
	        							obj.setVisibility(View.INVISIBLE);
	        						}
	        					}
							}
        				}
        			}
        			
        			// gesture recognization start
        			gc.startRecognition();
        			
        			break;
        		case Util.EVENT_BUTTON_UP:
        			mDs = new DataStructure();
        			mDs.event = m.what;
        			mDs.detail = Integer.toString(m.arg1);
        			mWifi.SendData(mDs.GetString());
        			
        			// gesture recognization end
        			gc.finishRecognition();
        			
        			break;
        		case Util.EVENT_MOVE_DOWN:
        			Log.i("MOVE", "DOWN");
        			mDs = new DataStructure();
        			mDs.event = m.what;
        			mDs.detail = "";
        			//mWifi.SendData(mDs.GetString());
        			break;
        		case Util.EVENT_MOVE_UP:
        			Log.i("MOVE", "UP");
        			mDs = new DataStructure();
        			mDs.event = m.what;
        			mDs.detail = "";
        			//mWifi.SendData(mDs.GetString());
        			break;
        		case Util.EVENT_GESTURE:
        			mDs = new DataStructure();
        			mDs.event = m.what;
        			mDs.detail = (String) m.obj;
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
        			Toast.makeText(c, "QUIT from game", Toast.LENGTH_SHORT).show();
        			
        			if (mSensor != null) {
        				mSensor.stopSensor();
        				mSensor = null;
        			}
        			
        			setContentView(R.layout.dlg_connect);
        			setupButtonEvent();
        			break;
        		case Util.CONN_CLOSE:
        			if (!running)
        				break;
        			
        			/*
        			 * close program
        			 */        			
        			AlertDialog.Builder alert = new AlertDialog.Builder(c);

    				alert.setTitle("Server Disconnected");
    				alert.setMessage("Connection disconnected.");

    				alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
    					public void onClick(DialogInterface dialog, int whichButton) {
    						close();
    					}
    				});
    				
    				alert.show();
        			break;
        		case Util.CONN_FAIL:
        			dlg_Loading.hideLoading();
        			Toast.makeText(c, "Failed to connect server", Toast.LENGTH_SHORT).show();
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
        			Toast.makeText(c, "Completed join, and calibrating. PLEASE DON\'T MOVE PHONE!", Toast.LENGTH_SHORT)
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
        			 */
        			//setupSensor();
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
	}
}

