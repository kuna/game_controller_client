package kr.soma.events;

import java.util.EventListener;
/**
 * 방향 이벤트 처리에 대한 리스너 클래스 
 * @author limjunsung
 *
 */
public interface OrientationListener extends EventListener {
	/**
	 * 방향 이벤트 처리를 담당하는 메소드 
	 * @param event 방향 이벤트 
	 */
    public void doOrientationProcess(OrientationEvent event);
}