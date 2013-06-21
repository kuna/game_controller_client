package kr.soma.filter;

import android.util.Log;
import kr.soma.controller.GestureController;

public class DirectionalEquvalenceFilter implements Filter{
	private double sensitivity;
	private double[] reference;
	private boolean isFiltered;
	private GestureController gc;
	
	public DirectionalEquvalenceFilter(GestureController gc, double sensitivity) {
		super();
		this.gc = gc;
		this.sensitivity = sensitivity;
		this.reset();
	}
	
	public void reset() {
		this.isFiltered = false;
		this.reference = new double[] {0.0f, 0.0f, 0.0f};
	}
	
	public double[] filter(double[] vector) {
		if (!gc.isRecognizing())
			return vector;
		
		if(vector[0]<reference[0]-this.sensitivity ||
		   vector[0]>reference[0]+this.sensitivity ||
		   vector[1]<reference[1]-this.sensitivity ||
		   vector[1]>reference[1]+this.sensitivity ||
		   vector[2]<reference[2]-this.sensitivity ||
		   vector[2]>reference[2]+this.sensitivity) {
			this.reference=vector;
			this.isFiltered = true;
			return vector;
		} else {
			this.isFiltered = false;
			return vector;
			//return null;
		}
	}
	
	public void setSensivity(float sensivity) {
		this.sensitivity=sensivity;
	}
	
	public double getSensivity() {
		return this.sensitivity;
	}
	
	public boolean isFiltered() {
		return isFiltered;
	}

}
