package kr.soma.filter;


public class HighPassFilter implements Filter{

	public double[] filter(double[] data) {
		double[] accWithoutGravity = new double[data.length];
		for (int i = 0; i < data.length; i++) {
			accWithoutGravity[i] = data[i] - LowPassFilter.gravityData[i];
        }
		return accWithoutGravity;
	}
}
