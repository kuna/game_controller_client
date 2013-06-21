package kr.soma.filter;

public class LowPassFilter implements Filter{
	// 중력 데이터를 구하기 위해서 저속 통과 필터를 적용할 때 사용하는 비율 데이터.
    // t : 저속 통과 필터의 시정수. 시정수란 센서가 가속도의 63% 를 인지하는데 걸리는 시간
    // dT : 이벤트 전송율 혹은 이벤트 전송속도.
    // alpha = t / (t + Dt)
	public final double alpha = 0.8f;
	public static double[] gravityData = new double[3];
	
	public double[] filter(double[] data) {
		for (int i = 0; i < 3; i++) {
			gravityData[i] = alpha * gravityData[i] + (1 - alpha) * data[i];
        }
		return data;
	}
	
}
