package sgreben.flbs;

public class FixedLengthBytestringSet {
	private StateTable stateTable;
	
	public FixedLengthBytestringSet(StateTable stateTable) {
		this.stateTable = stateTable; 
	}
	
	public int singleton(byte[] symbols) {
		int previousState = stateTable.EPSILON();
		for(int i = 0; i < symbols.length; ++i) {
			int symbol = 128+(int)symbols[symbols.length - 1 - i];
			previousState = stateTable.make(
				new PointResiduals(symbol, previousState, stateTable.ZERO())
			);
		}
		return previousState;
	}
	
	public boolean contains(int L, byte[] word) {
		if(word.length == 0) {
			return L == stateTable.EPSILON();
		}
		for(int i = 0; i < word.length; ++i) {
			L = stateTable.residuals(L).get(128+(int)word[i]);
			if(L == stateTable.ZERO()) {
				return false;
			}
		}
		return L == stateTable.EPSILON();
	}
	
	public int intersection(int L, int R) {
		if(L == R) {
			return L;
		}
		IntPairMap G = new IntPairMap();
		return intersectionLoop(G, Math.min(L, R), Math.max(L,R));
	}
	
	private int intersectionLoop(IntPairMap G, int L, int R) {
		assert(L <= R);
		if(L == R) {
			return L;
		}
		if(G.containsKey(L,R)) {
			return G.get(L,R);
		}
		if(L == stateTable.ZERO() || R == stateTable.ZERO()) {
			return stateTable.ZERO();
		}
		int[] residuals = new int[256];
		Residuals resL = stateTable.residuals(L);
		Residuals resR = stateTable.residuals(R);
		for(int i = 0; i < 256; ++i) {
			int succL = resL.get(i);
			int succR = resR.get(i);
			if(succL == succR) {
				residuals[i] = succL;
			} else if(succL == stateTable.ZERO() || succR == stateTable.ZERO()) {
				residuals[i] = stateTable.ZERO();
			} else {
				residuals[i] = intersectionLoop(
					G,
					Math.min(succL, succR),
					Math.max(succL, succR)
				);
			}
		}
		int L_n_R = stateTable.make(new ArrayResiduals(residuals));
		G.put(L, R, L_n_R);
		return L_n_R;
	}
	
	public int union(int L, int R) {
		if(L == R) {
			return L;
		}
		IntPairMap G = new IntPairMap();
		return unionLoop(G, Math.min(L, R), Math.max(L,R));
	}
	
	private int unionLoop(IntPairMap G, int L, int R) {
		assert(L <= R);
		if(L == R) {
			return L;
		}
		if(G.containsKey(L,R)) {
			return G.get(L,R);
		}
		if(L == stateTable.ZERO()) {
			return R;
		}
		if(R == stateTable.ZERO()) {
			return L;
		}
		int[] residuals = new int[256];
		Residuals resL = stateTable.residuals(L);
		Residuals resR = stateTable.residuals(R);
		for(int i = 0; i < 256; ++i) {
			int succL = resL.get(i);
			int succR = resR.get(i);
			if(succL == succR) {
				residuals[i] = succL;
			} else if(succL == stateTable.ZERO()) {
				residuals[i] = succR;
			} else if(succR == stateTable.ZERO()) {
				residuals[i] = succL;
			} else {
				residuals[i] = unionLoop(
					G,
					Math.min(succL, succR),
					Math.max(succL, succR)
				);
			}
		}
		int L_u_R = stateTable.make(new ArrayResiduals(residuals));
		G.put(L, R, L_u_R);
		return L_u_R;
	}
}