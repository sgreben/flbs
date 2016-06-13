package sgreben.flbs;

import java.util.HashMap;

public class IndexedStateTable implements StateTable {
	
	private final LinearScanStateTable stateTable;
	
	public final int ZERO;
	public final int EPSILON;
	private final HashMap<Residuals, Integer> residualsIndex;
	
	public IndexedStateTable() {
		stateTable = new LinearScanStateTable();
		residualsIndex = new HashMap<Residuals, Integer>();
		ZERO = stateTable.ZERO();
		EPSILON = stateTable.EPSILON();
	}
	
	public IndexedStateTable(int expectedsize) {
		stateTable = new LinearScanStateTable(expectedsize);
		residualsIndex = new HashMap<Residuals, Integer>(expectedsize);
		ZERO = stateTable.ZERO();
		EPSILON = stateTable.EPSILON();
	}
	
	public int ZERO() {
		return ZERO;
	}
	
	public int EPSILON() {
		return EPSILON;
	}
	
	public int size() {
		return stateTable.size();
	}
	
	public Residuals residuals(int state) {
		return stateTable.residuals(state);
	}

	public int make(Residuals residuals) {
		if(residualsIndex.containsKey(residuals)) {
			return residualsIndex.get(residuals);
		} else {
			int state = stateTable.make(residuals);
			residualsIndex.put(residuals, Integer.valueOf(state));
			return state;
		}
	}
	
	public boolean exists(Residuals residuals) {
		return residualsIndex.containsKey(residuals);
	}
}