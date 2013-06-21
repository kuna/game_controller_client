package kr.soma.events;

import java.util.EventListener;
/**
 * 가속도 이벤트를 처리하는 리스너 클래스 
 * @author limjunsung
 *
 */
public interface AccelerationListener extends EventListener {
	/**
	 * 가속도 이벤트 발생시 처리하는 메소드 
	 * @param event
	 */
    public void doAccelerationProcess(AccelerationEvent event);
}