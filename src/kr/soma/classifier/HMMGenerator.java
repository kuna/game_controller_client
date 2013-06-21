package kr.soma.classifier;

import kr.soma.filter.Direction;
import be.ac.ulg.montefiore.run.jahmm.Hmm;
import be.ac.ulg.montefiore.run.jahmm.ObservationDiscrete;
import be.ac.ulg.montefiore.run.jahmm.OpdfDiscrete;
import be.ac.ulg.montefiore.run.jahmm.OpdfDiscreteFactory;
/**
 * Hidden Markov 모델을 생성하는 클래스  
 * @author limjunsung
 *
 */
public class HMMGenerator {
	private HMMGenerator() {
	}
	
	/**
	 * Hidden Markov 모델을 구현하는 방식 중 left-to-right 방식을 적용하여 모델을 생성 
	 * @param stateSize 스테이트 갯수 
	 * @param sigmaSize 심볼 갯수 
	 * @return left-to-right 방식의 Hidden Markov 모델 
	 */
	public static Hmm<ObservationDiscrete<Direction>> newLeftToRightHMM(int stateSize, int sigmaSize) {
		OpdfDiscreteFactory<Direction> factory = new OpdfDiscreteFactory<Direction>(Direction.class);
		Hmm<ObservationDiscrete<Direction>> hmm = new Hmm<ObservationDiscrete<Direction>>(stateSize, factory);
		
		for (int i=0; i<stateSize; i++) {
			double[] prob = new double[sigmaSize];
			for (int j=0; j<sigmaSize; j++) {
				prob[j] = 1.0 / sigmaSize;
			}
			hmm.setOpdf(i, new OpdfDiscrete<Direction>(Direction.class, prob));
			hmm.setPi(i, 0);
		}
		hmm.setPi(0, 1.0);
		
		int jumpLimit = 2;
		for (int i=0; i<stateSize; i++) {
			for (int j=0; j<stateSize; j++) {
				if (i==stateSize-1 && j==stateSize-1) {
					hmm.setAij(i, j, 1.0);
				} else if (i==stateSize-2 && j==stateSize-2) {
					hmm.setAij(i, j, 0.5);
				} else if (i==stateSize-2 && j==stateSize-1) {
					hmm.setAij(i, j, 0.5);
				} else if (i<=j && i>j-jumpLimit-1) {
					hmm.setAij(i, j, 1.0 / (jumpLimit+1));
				} else {
					hmm.setAij(i, j, 0.0);
				}
			}
		}
		return hmm;
	}
}
