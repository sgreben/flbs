package sgreben.flbs;

import java.util.HashMap;
import java.util.Iterator;
import java.util.ArrayDeque;
import java.math.BigInteger;

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
		int state = L;
		for(int i = 0; i < symbols.length; ++i) {
			int symbol = 128+(int)symbols[symbols.length - 1 - i];
			state = stateTable.make(
				new PointResiduals(symbol, state, ZERO)
			);
		}
		return state;
	}

	public int prefixPadding(int L, int length) {
		int state = L;
		for(int i = 0; i < length; ++i) {
			state = stateTable.make(
				new ConstResiduals(state)
			);
		}
		return state;
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
	
	public int length(int L) {
		int length = 0;
		while(true) {
			if(L == EPSILON) {
				return length;
			}
			if(L == ZERO) {
				return 0;
			}
			Residuals R = stateTable.residuals(L);
			if(R.isConst(EPSILON)) {
				return length + 1;
			};
			for(int i = 0; i < 256; ++i) {
				int Ri = R.get(i);
				if(Ri != ZERO) {
					length += 1;
					L = Ri;
					break;
				}
			}
		}
	}
	
	public Iterator<byte[]> iterate(int L) {
		int length = length(L);
		return new WordIterator(L, length); 
	}

	public BigInteger size(int L) {
		HashMap<Integer, BigInteger> G = new HashMap<Integer, BigInteger>();
		return sizeLoop(G, L);
	}
	
		private class WordIterator implements Iterator<byte[]> {
		private byte[] word;
		private ArrayDeque<int[]> worklist;
		private boolean iterEpsilon;

		public WordIterator(int state, int length) {
			this.worklist = new ArrayDeque<int[]>(length);
			this.word = new byte[length];
			iterEpsilon = state == EPSILON;
			if(length > 0) {
				Residuals R = stateTable.residuals(state);
				for(int i = 0; i < 256; ++i) {
					if(R.get(i) != ZERO) {
						worklist.offerFirst(new int[]{ state, 0, i });
						break;
					}
				}
			}
		}
		
		public boolean hasNext() {
			return worklist.size() > 0 || iterEpsilon;
		}
		
		public byte[] next() {
			if(iterEpsilon) {
				iterEpsilon = false;
				return word;
			}
			while(true) {
				int[] item = worklist.removeFirst();
				int state = item[0];
				int offset = item[1];
				int symbol = item[2];
				word[offset] = (byte)(symbol-128);
				Residuals R = stateTable.residuals(state);
				for(int i = symbol+1; i < 256; ++i) {
					if(R.get(i) != ZERO) {
						item[2] = i;
						worklist.offerFirst(item);
						break;
					}
				}
				if(offset == word.length-1) {
					return word;
				}
				state = R.get(symbol);
				R = stateTable.residuals(state);
				for(int i = 0; i < 256; ++i) {
					if(R.get(i) != ZERO) {
						worklist.offerFirst(new int[]{ state, offset+1, i });
						break;
					}
				}
			}
		}
	}

		
	private BigInteger sizeLoop(HashMap<Integer, BigInteger> G, int L) {
		if(L == ZERO) {
			return BigInteger.ZERO;
		} 
		if (L == EPSILON) {
			return BigInteger.ONE;
		}
		Integer Lbox = Integer.valueOf(L);
		if(G.containsKey(Lbox)) {
			return G.get(Lbox);
		}
		Residuals R = stateTable.residuals(L);
		BigInteger size = BigInteger.ZERO;
		for(int i = 0; i < 256; ++i) {
			int Ri = R.get(i);
			if(Ri == EPSILON) {
				size = size.add(BigInteger.ONE);
			} else if(Ri != ZERO) {
				size = size.add(sizeLoop(G, Ri));
			}
		}
		G.put(Lbox, size);
		return size;
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