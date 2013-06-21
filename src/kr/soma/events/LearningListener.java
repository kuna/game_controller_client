package kr.soma.events;

import java.util.EventListener;
/**
 * 학습 이벤트 처리를 위한 리스너 클래스 
 * @author limjunsung
 *
 */
public interface LearningListener extends EventListener {
	/**
	 * 학습 이벤트 발생시 처리하는 메소드 
	 * @param event 학습 이벤트 
	 */
    public void doLearningProcess(LearningEvent event);
}