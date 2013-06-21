/**
 * 이벤트 및 이벤트 리스너 패키지 
 */
package kr.soma.events;

import java.util.EventListener;
import java.util.EventObject;
/**
 * 모션 분석 이벤트를 처리하는 리스너 클래스 
 * @author limjunsung
 *
 */
public interface RecognitionListener extends EventListener {
	/**
	 * 모션 분석 이벤트를 처리하는 메소드  
	 * @param event 모션 분석 이벤트 
	 */
    public void doRecognitionProcess(RecognitionEvent event);
}