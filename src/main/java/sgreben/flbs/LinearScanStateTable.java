package sgreben.flbs;
import java.util.ArrayList;

public class LinearScanStateTable implements StateTable {

	public static final int ZERO = Integer.MIN_VALUE;
	public static final int EPSILON = ZERO + 1;
	
	private static final Residuals ZERO_RESIDUALS = new ConstResiduals(ZERO);
	
	private ArrayList<Residuals> states;
	                                        
	public LinearScanStateTable() {
		states = new ArrayList<Residuals>();
	}
	
	public LinearScanStateTable(int expectedsize) {
		states = new ArrayList<Residuals>(expectedsize);
	}
	
	public int ZERO() {
		return ZERO;
	}
	
	public int EPSILON() {
		return EPSILON;
	}
	
	public int size() {
		return states.size();
	}

	public boolean exists(Residuals residuals){
		if(residuals.isConst(ZERO)) {
			return true;
		}
		int state = state(residuals);
		return state >= 0;
	}
	
	private int add(Residuals residuals) {
		final int N = size();
		states.add(residuals);
		return N;
	}
		
	public int make(Residuals residuals) {
		if(residuals.isConst(ZERO)) {
			return ZERO;
		}
		int state = state(residuals);
		if(state >= 0) {
			return state;
		} else {
			return add(residuals);
		}
	}

	public Residuals residuals(int state) {
		if(state == ZERO || state == EPSILON) {
			return ZERO_RESIDUALS;
		} else {
			return states.get(state);
		}
	}
	
	private int state(Residuals residuals) {
		final int N = size();
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
		return (-1);
	}
}