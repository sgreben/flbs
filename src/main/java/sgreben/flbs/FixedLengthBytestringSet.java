package sgreben.flbs;

import java.util.HashMap;

public class FixedLengthBytestringSet {
	private StateTable stateTable;
	
	private final int ZERO;
	private final int EPSILON;
	
	public FixedLengthBytestringSet(StateTable stateTable) {
		this.stateTable = stateTable;
		this.ZERO = stateTable.ZERO();
		this.EPSILON = stateTable.EPSILON();
	}
	
	public int singleton(byte[] symbols) {
		return prefix(EPSILON, symbols);
	}
	
	public int prefix(int L, byte[] symbols) {
		int previousState = L;
		for(int i = 0; i < symbols.length; ++i) {
			int symbol = 128+(int)symbols[symbols.length - 1 - i];
			previousState = stateTable.make(
				new PointResiduals(symbol, previousState, ZERO)
			);
		}
		return previousState;
	}
	
	public boolean contains(int L, byte[] word) {
		if(word.length == 0) {
			return L == EPSILON;
		}
		for(int i = 0; i < word.length; ++i) {
			L = stateTable.residuals(L).get(128+(int)word[i]);
			if(L == ZERO) {
				return false;
			}
		}
		return L == EPSILON;
	}

	public long size(int L) {
		HashMap<Integer, Long> G = new HashMap<Integer, Long>();
		return sizeLoop(G, L);
	}
	
	private long sizeLoop(HashMap<Integer, Long> G, int L) {
		if(L == ZERO) {
			return 0;
		} 
		if (L == EPSILON) {
			return 1;
		}
		Integer Lbox = Integer.valueOf(L);
		if(G.containsKey(Lbox)) {
			return G.get(Lbox);
		}
		Residuals R = stateTable.residuals(L);
		long size = 0L;
		for(int i = 0; i < 256; ++i) {
			int Ri = R.get(i);
			if(Ri == EPSILON) {
				size += 1;
			} else if(Ri != ZERO) {
				size += sizeLoop(G, Ri);
			}
		}
		G.put(Lbox, size);
		return size;
	}

	public int intersection(int L, int R) {
		if(L == R) {
			return L;
		}
		IntPairMap G = new IntPairMap();
		return intersectionLoop(G, Math.min(L, R), Math.max(L,R));
	}

	public int union(int L, int R) {
		if(L == R) {
			return L;
		}
		IntPairMap G = new IntPairMap();
		return unionLoop(G, Math.min(L, R), Math.max(L,R));
	}
	
		
	private int intersectionLoop(IntPairMap G, int L, int R) {
		assert(L <= R);
		if(L == R) {
			return L;
		}
		if(G.containsKey(L,R)) {
			return G.get(L,R);
		}
		if(L == ZERO || R == ZERO) {
			return ZERO;
		}
		int[] residuals = new int[256];
		Residuals resL = stateTable.residuals(L);
		Residuals resR = stateTable.residuals(R);
		for(int i = 0; i < 256; ++i) {
			int succL = resL.get(i);
			int succR = resR.get(i);
			if(succL == succR) {
				residuals[i] = succL;
			} else if(succL == ZERO || succR == ZERO) {
				residuals[i] = ZERO;
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
	
	private int unionLoop(IntPairMap G, int L, int R) {
		assert(L <= R);
		if(L == R) {
			return L;
		}
		if(G.containsKey(L,R)) {
			return G.get(L,R);
		}
		if(L == ZERO) {
			return R;
		}
		if(R == ZERO) {
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
			} else if(succL == ZERO) {
				residuals[i] = succR;
			} else if(succR == ZERO) {
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