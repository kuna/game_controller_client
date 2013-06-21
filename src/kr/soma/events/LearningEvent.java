package kr.soma.events;

import java.util.EventObject;
import java.util.List;

import kr.soma.filter.Direction;
/**
 * 학습 이벤트 
 * @author limjunsung
 *
 */
public class LearningEvent extends EventObject {
	private static final long serialVersionUID = -6651190637806848204L;
	/**
	 * 전달 메시지 
	 */
	private int msg;
	/**
	 * 전달 객체1
	 */
	private Object obj1;
	/**
	 * 전달 객체2
	 */
	private Object obj2;
	/**
	 * 생성자 
	 * @param source 이 이벤트를 발생시킨 클래스 
	 * @param msg 전달 메시지 
	 * @param obj1 전달 객체1
	 * @param obj2 전달 객체2
	 */
	public LearningEvent(Object source, int msg, Object obj1, Object obj2) {
		super(source);
		this.msg = msg;
		this.obj1 = obj1;
		this.obj2 = obj2;
	}
	/**
	 * 전달 메시지 반환 
	 * @return 전달 메시지 
	 */
	public int getMsg() {
		return msg;
	}
	/**
	 * 전달 객체1 반환 
	 * @return 전달 객체1
	 */
	public Object getObj1() {
		return obj1;
	}
	/**
	 * 전달 객체2 반환 
	 * @return 전달 객체2
	 */
	public Object getObj2() {
		return obj2;
	}
	
		
}
