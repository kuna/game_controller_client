package kr.soma.events;

import java.util.EventObject;

public class OrientationEvent extends EventObject {
    
	private static final long serialVersionUID = 7489661728727129079L;
	
	private double x, y, z;
    
    public OrientationEvent(Object source, double x, double y, double z) {
        super(source);
        this.x = x;
        this.y = y;
        this.z = z;
    }

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getZ() {
		return z;
	}
    
    
}