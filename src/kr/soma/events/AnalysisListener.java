package kr.soma.events;

import java.util.EventListener;
import java.util.EventObject;
/**
 * 모델링 이벤트를 처리하는 리스너 클래스 
 * @author limjunsung
 *
 */
public interface AnalysisListener extends EventListener {
	/**
	 * 모델링 이벤트 발생시 처리하는 메소드 
	 * @param event 모델링 이벤트 
	 */
	public void doAnalysisProcess(AnalysisEvent event);
}
