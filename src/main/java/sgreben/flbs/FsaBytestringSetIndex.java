package sgreben.flbs;

import java.util.HashMap;
import java.util.Iterator;
import java.util.ArrayDeque;
import java.math.BigInteger;

public class FsaBytestringSetIndex implements BytestringSetIndex {
	private StateTable stateTable;
	
	private final int ZERO;
	private final int EPSILON;
	
	public FsaBytestringSetIndex(StateTable stateTable) {
		this.stateTable = stateTable;
		this.ZERO = stateTable.ZERO();
		this.EPSILON = stateTable.EPSILON();
	}
	
	public int emptySet() {
		return ZERO;
	}
	
	public int singletonEmpty() {
		return EPSILON;
	}
	
	public int singleton(byte[] data) {
		return prefix(EPSILON, data);
	}
	
	public int prefix(int L, byte[] data) {
		int state = L;
		for(int i = data.length-1; i >= 0; --i) {
			int symbol = 128+(int)data[i];
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
	public int prefixPadding(int L, int length, byte symbol) {
		int state = L;
		int intSymbol = 128+(int)symbol;
		for(int i = 0; i < length; ++i) {
			state = stateTable.make(
				new PointResiduals(intSymbol, state, ZERO)
			);
		}
		return state;
	}

	public int suffix(int L, byte[] data) {
		int state = L;
		for(int i = 0; i < data.length && state != ZERO; ++i) {
			Residuals R = stateTable.residuals(state);
			state = R.get(128+(int)data[i]);
		}
		return state;
	}

	public boolean contains(int L, byte[] data) {
		if(data.length == 0) {
			return L == EPSILON;
		}
		for(int i = 0; i < data.length; ++i) {
			L = stateTable.residuals(L).get(128+(int)data[i]);
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
		return intersection(G, Math.min(L, R), Math.max(L,R));
	}

	public int union(int L, int R) {
		if(L == R) {
			return L;
		}
		IntPairMap G = new IntPairMap();
		return union(G, Math.min(L, R), Math.max(L,R));
	}
	
	public int complement(int L) {
		int length = length(L);
		IntPairMap G = new IntPairMap();
		return complement(length, G, L);
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
		return new DataIterator(L, length); 
	}

	public BigInteger size(int L) {
		HashMap<Integer, BigInteger> G = new HashMap<Integer, BigInteger>();
		return size(G, L);
	}
	
	private class DataIterator implements Iterator<byte[]> {
		
		private class Item {
			public final int state;
			public final int dataIndex;
			public int symbol;
			public Item(int state, int dataIndex, int symbol) {
				this.state = state;
				this.dataIndex = dataIndex;
				this.symbol = symbol;
			}
		}
		
		private byte[] data;
		private ArrayDeque<Item> stack;
		private boolean iterEpsilon;

		public DataIterator(int state, int length) {
			this.stack = new ArrayDeque<Item>(length);
			this.data = new byte[length];
			this.iterEpsilon = state == EPSILON;
			if(length > 0) {
				Residuals R = stateTable.residuals(state);
				for(int i = 0; i < 256; ++i) {
					if(R.get(i) != ZERO) {
						stack.push(new Item(state, 0, i));
						break;
					}
				}
			}
		}
		
		public boolean hasNext() {
			return stack.size() > 0 || iterEpsilon;
		}
		
		public byte[] next() {
			if(iterEpsilon) {
				iterEpsilon = false;
				return data;
			}
			while(true) {
				Item item = stack.pop();
				int state = item.state;
				int dataIndex = item.dataIndex;
				int symbol = item.symbol;
				data[dataIndex] = (byte)(symbol-128);
				Residuals R = stateTable.residuals(state);
				for(int i = symbol+1; i < 256; ++i) {
					if(R.get(i) != ZERO) {
						item.symbol = i;
						stack.push(item);
						break;
					}
				}
				if(dataIndex == data.length-1) {
					return data;
				}
				state = R.get(symbol);
				R = stateTable.residuals(state);
				for(int i = 0; i < 256; ++i) {
					if(R.get(i) != ZERO) {
						stack.push(new Item(state, dataIndex+1, i));
						break;
					}
				}
			}
		}
	}

	private BigInteger size(HashMap<Integer, BigInteger> G, int L) {
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
				size = size.add(size(G, Ri));
			}
		}
		G.put(Lbox, size);
		return size;
	}
		
	private int intersection(IntPairMap G, int L, int R) {
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
				residuals[i] = intersection(
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
	
	public int unionSingleton(int L, byte[] word) {
		return unionSingleton(L, word, 0);
	}
	
	private int unionSingleton(int L, byte[] word, int offset) {
		if(offset == word.length) {
			return union(L, EPSILON); 
		}
		int symbol = 128+(int)word[offset];
		int[] residuals = new int[256];
		for(int i = 0; i < 256; ++i) {
			residuals[i] = stateTable.residuals(L).get(i);
		}
		residuals[symbol] = unionSingleton(stateTable.residuals(L).get(symbol), word, offset + 1);
		return stateTable.make(new ArrayResiduals(residuals));
	}
	
	private int union(IntPairMap G, int L, int R) {
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
				residuals[i] = union(
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
	
	private int complement(int length, IntPairMap G, int L) {
		if(G.containsKey(length, L)) {
			return G.get(length, L);
		}
		if(length == 0 && L == ZERO) {
			return EPSILON;
		}
		if(length == 0 && L == EPSILON) {
			return ZERO;
		}
		// length >= 1
		Residuals R = stateTable.residuals(L);
		int[] Rcomp = new int[256];
		for(int i = 0; i < 256; ++i) {
			Rcomp[i] = complement(length - 1, G, R.get(i)); 
		}
		int Lcomp = stateTable.make(new ArrayResiduals(Rcomp));
		G.put(length, L, Lcomp);
		return Lcomp;
	}
}