/**
 * 모션 모델링 및 모델 분석 패키지 
 */
package kr.soma.classifier;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import kr.soma.filter.Direction;
/**
 * 사용자의 모션에 대한 가장 확률이 높은 모델을 찾아주는 클래스 
 * @author limjunsung
 *
 */
public class Analyzer {
	/**
	 * 분석 기준이 되는 모델들을 가지고 있는 리스트  
	 */
	private List<HMM>		hmmModels;
	
	/**
	 * Analyzer 생성자 
	 */
	public Analyzer() {
		hmmModels = new ArrayList<HMM>();
	}

	/**
	 * 분석 기준이 되는 모델을 추가 
	 * @param model HMM 클래스로 구현된 모델 
	 */
	public void addModel(HMM model) {
		hmmModels.add(model);
	}
	
	/**
	 * 사용자의 모션을 분석
	 * @param motion 시간에 따른 사용자의 모션 정보를 담고 있는 리스트 
	 * @return HMM 분석 기준이 되는 모델들 중에 사용자의 모션일 확률이 가장 높은 모델 
	 */
	public HMM analyze(List<Direction> motion) {
		double sum = 0.0;
		for (HMM model : hmmModels) {
//			sum += (model.getDefaultProbability() * model.getProbability(motion));
			sum += model.getProbability(motion); //추가 
		}
		if (sum <= 0) {
			return null;
		}
		
		HMM recogModel = null;
		double probRecog	= Double.MIN_VALUE;
		double probMotion	= 0;
		double probModel	= 0;
		
		for (HMM model : hmmModels) {
			double tmpProbMotion 	= model.getProbability(motion);
			double tmpProbModel 	= model.getDefaultProbability();
//			double tmpProbRecog		= (tmpProbMotion * tmpProbModel / sum);
			double tmpProbRecog		= (tmpProbMotion / sum); // 추가 
			Log.i("Analyzer", model.getName() + "모델의 계산 결과 : " + tmpProbRecog);
			if (tmpProbRecog > probRecog) {
				probRecog	= tmpProbRecog;
				probMotion	= tmpProbMotion;
				probModel	= tmpProbModel;
				recogModel	= model;
			}
			
		}
//		return (probRecog > 0 && probModel > 0 && probMotion > 0) ? recogModel : null;
		return (probRecog > 0 && probMotion > 0) ? recogModel : null;
	}

	/**
	 * 기준 모델을 반환 
	 * @return HMM 기준 모델들을 저장하고 있는 리스트 
	 */
	public List<HMM> getModels() {
		return hmmModels;
	}
	
	
}
