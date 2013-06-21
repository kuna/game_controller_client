package kr.soma.filter;


public class IdleStateFilter implements Filter {
	
	private double sensivity;
	
	/**
	 * Since an acceleration sensor usually provides information even
	 * if it doesn't move, this filter removes the data if it's in the
	 * idle state.
	 */
	public IdleStateFilter() {
		super();
		this.sensivity = 0.1;
	}

   
    public void reset() {
        // not needed
    }

	public double[] filter(double[] vector) {
		// calculate values needed for filtering:
		// absolute value
		double absvalue = Math.sqrt((vector[0]*vector[0])+
				(vector[1]*vector[1])+(vector[2]*vector[2]));
		
		// filter formulaes and return values
		if(absvalue > 1+this.sensivity ||
		   absvalue < 1-this.sensivity) {
			return vector;
		} else {
			return null;	
		}
	}
	
	/**
	 * Defines the absolute value when the wiimote should react to acceleration.
	 * This is a parameter for the first of the two filters: idle state
	 * filter. For example: sensivity=0.2 makes the wiimote react to acceleration
	 * where the absolute value is equal or greater than 1.2g. The default value 0.1
	 * should work well. Only change if you are sure what you're doing.
	 * 
	 * @param sensivity
	 * 		acceleration data values smaller than this value wouldn't be detected.
	 */ 
	public void setSensivity(double sensivity) {
		this.sensivity = sensivity;
	}
	
	public double getSensivity() {
		return this.sensivity;
	}

}
