package kr.soma.events;

import java.util.EventObject;
/**
 * 가속도 센서 이벤트 
 * @author limjunsung
 *
 */
public class AccelerationEvent extends EventObject {
    
	private static final long serialVersionUID = -5708148319868848498L;
	/**
	 * 3축에 대한 가속도 센서 값 
	 */
	private double x, y, z;
	/**
	 * 아이디 
	 */
    private long id;
    /**
     * 생성자 
     * @param source 이 이벤트를 발생시킨 클래스 
     * @param x x축 가속도 
     * @param y y축 가속도 
     * @param z z축 가속도 
     */
    public AccelerationEvent(Object source, double x, double y, double z) {
        super(source);
        this.x = x;
        this.y = y;
        this.z = z;
    }
    /**
     * 생성자 
     * @param source 이 이벤트를 발생시킨 클래스 
     * @param x x축 가속도 
     * @param y y축 가속도 
     * @param z z축 가속도 
     * @param id 가속도 센서 id
     */
    public AccelerationEvent(Object source, double x, double y, double z, long id) {
        super(source);
        this.x = x;
        this.y = y;
        this.z = z;
        this.id = id;
    }
    /**
     * x축 가속도 값 반환 
     * @return x축 가속도 값 
     */
	public double getX() {
		return x;
	}
	/**
	 * y축 가속도 값 반환 
	 * @return y축 가속도 값 
	 */
	public double getY() {
		return y;
	}
	/**
	 * z축 가속도 값 반환 
	 * @return z축 가속도 값 
	 */
	public double getZ() {
		return z;
	}
    /**
     * 가속도 id 반환 
     * @return 가속도 id
     */
	public long getId() {
		return id;
	}
    
}