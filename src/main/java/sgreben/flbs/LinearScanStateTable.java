package sgreben.flbs;
import java.util.ArrayList;

public class LinearScanStateTable implements StateTable {

	public static final int ZERO = Integer.MIN_VALUE;
	public static final int EPSILON = ZERO + 1;
	
	private static final Residuals ZERO_RESIDUALS = 
		new ConstResiduals(ZERO);
	
	private ArrayList<Residuals> states;
	                                        
	public LinearScanStateTable() {
		states = new ArrayList<Residuals>(256);
	}
	
	public int ZERO() {
		return ZERO;
	}
	
	public int EPSILON() {
		return EPSILON;
	}
	
	public int numberOfStates() {
		return states.size();
	}

	public boolean exists(Residuals residuals){
		try {
			state(residuals);
			return true;
		} catch(StateNotFoundException e) {
			return false;
		}
	}
		
	public int make(Residuals residuals) {
		try {
			return state(residuals);
		} catch(StateNotFoundException e) {
			int state = numberOfStates();
			states.add(residuals);
			return state;
		}
	}

	public Residuals residuals(int state) {
		if(state == ZERO || state == EPSILON) {
			return ZERO_RESIDUALS;
		} else {
			return states.get(state);
		}
	}

	private int state(Residuals residuals) throws StateNotFoundException {
		final int N = numberOfStates();
		boolean allZero = true;
		for(int j = 0; j < 256; ++j) {
			if(residuals.get(j) != ZERO) {
				allZero = false;
				break;
			}
		}
		if(allZero) {
			return ZERO;
		}
		for(int i = 0; i < N; ++i) {
			Residuals currentState = residuals(i);
			boolean different = false;
			for(int j = 0; j < 256; ++j) {
				if(residuals.get(j) != currentState.get(j)) {
					different = true;
					break;
				}
			}
			if(!different) {
				return i;
			}
		}
		throw new StateNotFoundException();
	}
}