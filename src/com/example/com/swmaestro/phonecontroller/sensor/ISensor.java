package com.example.com.swmaestro.phonecontroller.sensor;

import com.example.com.swmaestro.phonecontroller.common.Util;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.util.Log;

/* 1. 센서매니저를 호출
 * 2. 센서값 변환 시마다 센서값을 저장 및 핸들ㄹ로 호출하기
 * 3. 센서값을 가져갈수 있게 하는 메서드도 만들기
 */

public class ISensor {
	Handler mHandler;
	
	float mAccelX;
	float mAccelY;
	float mAccelZ;
	float mAngleX;
	float mAngleY;
	float mAngleZ;
	
	float[] mGravity = null;
	float[] mGeoMagnetic = null;
	
	SensorManager mSm;
	SensorEventListener mListener;
	
	public void Initalize(Handler h, Context c) {
		mHandler = h;
		mSm = (SensorManager)c.getSystemService(Activity.SENSOR_SERVICE);
		
		mListener = new listener();
		mSm.registerListener(mListener, mSm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_UI);	// GAME is too fast...
		mSm.registerListener(mListener, mSm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_GAME);	// GAME is too fast...
		mOriThread.start();
	}
	
	public void stopSensor() {
		mSm.unregisterListener(mListener);
		exec = false;
	}
	
	private class listener implements SensorEventListener {
		public void onAccuracyChanged(Sensor arg0, int arg1) {
			
		}

		public void onSensorChanged(SensorEvent arg0) {
			switch(arg0.sensor.getType()) {
			case Sensor.TYPE_ACCELEROMETER:
				mAccelX = arg0.values[0];
				mAccelY = arg0.values[1];
				mAccelZ = arg0.values[2];
				mGravity = arg0.values.clone();
				break;
			case Sensor.TYPE_MAGNETIC_FIELD:
				mGeoMagnetic = arg0.values.clone();				
			}
		}
	}

	boolean exec = false;
	private Thread mOriThread = new Thread() {		
		public void run() {
			exec = true;
			while (exec) {
				try {
					if (mGravity != null && mGeoMagnetic != null) {
						float[] R = new float[16];
		                SensorManager.getRotationMatrix(R, null, mGravity, mGeoMagnetic);
						float[] values = new float[3];
						SensorManager.getOrientation(R, values);
	
						mAngleX = values[0];
						mAngleY = values[1];
						mAngleZ = values[2];
	
						SendEvent(Util.EVENT_SENSOR_ACCL, 0, String.format("%f,%f,%f", mAccelX, mAccelY, mAccelZ));
						SendEvent(Util.EVENT_SENSOR_ANGLE, 0, String.format("%f,%f,%f", 
								Radian2Degree(mAngleX-cali_AngleX), Radian2Degree(mAngleY-cali_AngleY), Radian2Degree(mAngleZ-cali_AngleZ)));
					}
					sleep(500);		// TEMP VALUE
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	};
	
	private float Radian2Degree (float radian) {
		float r = radian * 180 / (float)Math.PI;
		if (r > 180) r -= 360;
		if (r < -180) r += 360;
		return r;
	}
	
	float cali_AngleX=0, cali_AngleY=0, cali_AngleZ=0;
	public void calibrateOri() {
		cali_AngleX = mAngleX;
		cali_AngleY = mAngleY;
		cali_AngleZ = mAngleZ;
	}
	
	private void SendEvent(int sensor, int value, String data)
	{
		mHandler.obtainMessage(sensor, value, 0, data).sendToTarget();
	}
}
