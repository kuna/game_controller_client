package kr.soma.classifier;

import java.util.ArrayList;
import java.util.List;

import kr.soma.filter.Direction;
import android.util.Log;
import be.ac.ulg.montefiore.run.jahmm.Hmm;
import be.ac.ulg.montefiore.run.jahmm.ObservationDiscrete;
import be.ac.ulg.montefiore.run.jahmm.learn.BaumWelchLearner;

/**
 * 모델과 모델의 정보를 담고 있는 모델 Wrapper 클래스 
 * @author limjunsung
 *
 */
public class HMM {
	/**
	 * 스테이트 갯수 
	 */
	public static final int STATE_SIZE 		= 8;
	
	/**
	 * 심볼 갯수 
	 */
	public static final int SYMBOL_SIZE		= 8;
	
	/**
	 * 모델명 
	 */
	private final String							modelName;
	
	/**
	 * Hidden Markov Model
	 */
	private Hmm<ObservationDiscrete<Direction>>		hmmModel;
	
	/**
	 * 모델의 기본 확률 
	 */
	private double									defaultProbability;
	
	/**
	 * 모델 생성자
	 * @param name 모델명 
	 */
	public HMM(String name) {
		modelName	= name;
		hmmModel	= HMMGenerator.newLeftToRightHMM(STATE_SIZE, SYMBOL_SIZE);
	}
	
	/**
	 * 모델명 반환 
	 * @return 모델명 
	 */
	public String getName() {
		return modelName;
	}
	
	/**
	 * 학습 데이터를 통해 모델을 생성
	 * @param trainingMotions 같은 동작이 여러 번 반복된 사용자의 움직임 정보  
	 */
	public void train(List<List<Direction>> trainingMotions) {
		List<List<ObservationDiscrete<Direction>>> sequences = new ArrayList<List<ObservationDiscrete<Direction>>>();
		for (List<Direction> motion : trainingMotions) {
			sequences.add(makeObservationSequence(motion));
		}
		BaumWelchLearner learner = new BaumWelchLearner();
		hmmModel = learner.learn(hmmModel, sequences);
		
		double probability = 0.0;
		int cnt = 1;
		for (List<Direction> motion : trainingMotions) {
			probability += getProbability(motion);
		}
		defaultProbability = probability / trainingMotions.size();
	}
	
	/**
	 * 학습 과정을 통해 구한 모델의 기본 확률 반환 
	 * @return 기본 확률
	 */
	public double getDefaultProbability() {
		return defaultProbability;
	}
	
	/**
	 * 사용자의 움직임이 이 모델과 비슷한 정도가 얼마나 되는지 확률로 반환 
	 * @param motion 사용자의 움직임 정보를 담고 있는 리스트 
	 * @return 사용자의 움직임이 이 모델일 확률 
	 */
	public double getProbability(List<Direction> motion) {
		return hmmModel.probability(makeObservationSequence(motion));
	}
	
	/**
	 * 사용자의 움직임 정보를 분석하기 편한 자료 구조로 변환 
	 * @param motion 사용자의 움직임 정보 
	 * @return 변환된 사용자의 움직임 정보 
	 */
	private List<ObservationDiscrete<Direction>> makeObservationSequence(List<Direction> motion) {
		List<ObservationDiscrete<Direction>> sequence = new ArrayList<ObservationDiscrete<Direction>>();
		for (Direction direction : motion) {
			sequence.add(new ObservationDiscrete<Direction>(direction));
		}
		return sequence;
	}

	/**
	 * Hidden Markov 모델 클래스를 반환 
	 * @return Hidden Markov 모델 클래스 
	 */
	public Hmm<ObservationDiscrete<Direction>> getHmm() {
		return hmmModel;
	}
	
	/**
	 * 모델 클래스를 저장 
	 * @param hmm Hidden Markov 모델 클래스 
	 */
	public void setHmm(Hmm<ObservationDiscrete<Direction>> hmm) {
		hmmModel = hmm;
	}
	
}
