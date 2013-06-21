package kr.soma.filter;

import kr.soma.controller.GestureController;
import android.util.Log;


public class MotionDetectFilter implements Filter {
	
	private double sensitivity;
	private GestureController gc;
	private boolean isFiltered;
	private long timestamp;
	private long duration;
	private boolean isDelayTime;
	//private boolean oneMore;
	private int twoMore;
	/**
	 * Since an acceleration sensor usually provides information even
	 * if it doesn't move, this filter removes the data if it's in the
	 * idle state.
	 */
	public MotionDetectFilter(GestureController gc, double sensitivity, long duration) {
		super();
		this.timestamp = System.currentTimeMillis();
		this.isFiltered = false;
		this.gc = gc;
		this.sensitivity = sensitivity;
		this.duration = duration;
		//this.oneMore = false;
		this.twoMore = 0;
	}

   
    public void reset() {
        // not needed
    }

	public double[] filter(double[] vector) {
    	if (vector == null)
    		return null;
		
    	//double absvalue = Math.sqrt((vector[0]*vector[0])+(vector[1]*vector[1])+(vector[2]*vector[2]));
    	double absvalue = Math.sqrt((vector[0]*vector[0])+(vector[2]*vector[2]));
		
    	if (isDelayTime && (System.currentTimeMillis() - timestamp > duration)) {
    		isDelayTime = false;
    		if (isFiltered) {
    			if (twoMore < 2) {
    				twoMore++;
    				isDelayTime = true;
    				timestamp = System.currentTimeMillis();
    				Log.i("MotionDetectFilter", twoMore + "기회를 줬음");
    			}
    			else {
    				twoMore = 0;
					Log.i("MotionDetectFilter", "Idle 상태로 돌아옴 ");
					gc.cutAccelerationList();
    			}
			}
			else {
				Log.i("MotionDetectFilter", "모션 인식 계속 진행 중...");
			}
    	}
    	
		if (absvalue > sensitivity) {
			if (gc.isRecognitionMode() && !gc.isRecognizing()) {
				gc.setRecognizing(true);
				gc.startRecognition();
			}
			isFiltered = false;
		}
		else {		
			if ((gc.isLearning() || gc.isRecognizing()) && isDelayTime == false && isFiltered == false) {
				timestamp = System.currentTimeMillis();
				//oneMore = false;
				twoMore = 0;
				isDelayTime = true;
				Log.i("MotionDetectFilter", "delayTime start!!!!!!!!!!!!");
				Log.i("GestureController", "call quantizer's setCutInder()");
				//gc.getQuantizer().setCutIndex();
				gc.setCutIdx();
			}
			if (!isDelayTime && gc.isRecognitionMode() && gc.isRecognizing()) {
				gc.finishRecognition();
				gc.setRecognizing(false);
			}
			isFiltered = true;
		}
		return vector;
	}
	
	/**
	 * Defines the absolute value when the wiimote should react to acceleration.
	 * This is a parameter for the first of the two filters: idle state
	 * filter. For example: sensivity=0.2 makes the wiimote react to acceleration
	 * where the absolute value is equal or greater than 1.2g. The default value 0.1
	 * should work well. Only change if you are sure what you're doing.
	 * 
	 * @param sensivity
	 * 		acceleration data values smaller than this value wouldn't be detected.
	 */ 
	public void setSensivity(double sensivity) {
		this.sensitivity = sensivity;
	}
	
	public double getSensivity() {
		return this.sensitivity;
	}


	public boolean isFiltered() {
		return isFiltered;
	}

	public void setDelayTime(long mSec) {
		duration = mSec;
	}
	
	public boolean isDelayTime() {
		return isDelayTime;
	}
	
	public void enableDelayTime(boolean enable) {
		isDelayTime = enable;
	}
	
	
}
