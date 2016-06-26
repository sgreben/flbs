package sgreben.flbs;
import java.util.ArrayList;
import java.util.Arrays;

public class FlatIndexedStateTable { 

	public static final int ZERO = Integer.MIN_VALUE;
	public static final int EPSILON = ZERO + 1;
	
	private static final Residuals ZERO_RESIDUALS = new ConstResiduals(ZERO); 

	private static final int INITIAL_SIZE = 32;
	private static final int INTS_PER_STATE = 256;

	private final FlatResidualsIndex index;
	private int[] states;
	
	private int numStates;
	
	public FlatIndexedStateTable() {
		numStates = 0;
		states = new int[INTS_PER_STATE * INITIAL_SIZE];
		index = new FlatResidualsIndex(INITIAL_SIZE, 0.5);
	}
	
	private static final int stateOffset(int state) {
		return state * INTS_PER_STATE;
	}
	
	private void checkResize() {
		if(numStates == states.length / INTS_PER_STATE) {
			resize();
		}
	}
	
	private void resize() {
		final int[] oldStates = states;
		final int N = oldStates.length;
		states = new int[2 * N];
		for(int i = 0; i < N; ++i) {
			states[i] = oldStates[i];
		}
	}
	
	public int ZERO() {
		return ZERO;
	}
	
	public int EPSILON() {
		return EPSILON;
	}
	
	public int size() {
		return numStates;
	}

	private boolean allZero(int[] residuals) {
		for(int i = 0; i < INTS_PER_STATE; ++i) {
			if(residuals[i] != ZERO) {
				return false;
			}
		}
		return true; 
	}
	
	public boolean exists(int[] residuals) {
		if(allZero(residuals)) {
			return true;
		}
		int state = state(residuals);
		return state >= 0;
	}
	
	void set(int state, int[] residuals) {
		int offset = stateOffset(state);
		for(int i = 0; i < INTS_PER_STATE; ++i) {
			states[offset + i] = residuals[i];
		}
	}
	
	int add(int[] residuals) {
		checkResize();
		final int newState = numStates;
		numStates += 1;
		set(newState, residuals);
		index.put(residuals, newState);
		return newState;
	}
		
	public int make(int[] residuals) {
		if(allZero(residuals)) {
			return ZERO;
		}
		int state = state(residuals);
		if(state >= 0) {
			return state;
		} else {
			return add(residuals);
		}
	}

	public int get(int state, int symbol) {
		if(state == ZERO || state == EPSILON) {
			return ZERO;
		} else {
			return states[stateOffset(state)+symbol];
		}
	}
	
	public Residuals get(int state) {
		if(state == ZERO || state == EPSILON) {
			return ZERO_RESIDUALS;
		} else {
			return new FlatResiduals(states, stateOffset(state));
		}
	}
	
	public void get(int state, int[] residuals) {
		if(state == ZERO || state == EPSILON) {
			for(int i = 0; i < 256; ++i) {
				residuals[i] = ZERO;
			}
		} else {
			int stateOffset = stateOffset(state);
			for(int i = 0; i < 256; ++i, ++stateOffset) {
				residuals[i] = states[stateOffset];
			}
		}
	}
	
	private int state(int[] residuals) {
		return index.get(residuals);
	}
}