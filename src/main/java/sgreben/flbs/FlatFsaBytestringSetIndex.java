package sgreben.flbs;

import java.util.HashMap;
import java.util.Iterator;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.math.BigInteger;

public class FlatFsaBytestringSetIndex implements BytestringSetIndex {
	private FlatIndexedStateTable stateTable;

	private final int ZERO;
	private final int EPSILON;
	
	private final int[] scratch = new int[256];
	
	public FlatFsaBytestringSetIndex(FlatIndexedStateTable stateTable) {
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
	
	private int[] constResiduals(int state) {
		for(int i = 0; i < 256; ++i) {
			scratch[i] = state;
		}
		return scratch;
	}
	
	private int[] pointResiduals(int symbol, int state, int defaultState) {
		for(int i = 0; i < 256; ++i) {
			scratch[i] = defaultState;
		}
		scratch[symbol] = state;
		return scratch;
	}
	
	public int prefix(int L, byte[] data) {
		int state = L;
		for(int i = data.length - 1; i >= 0; --i) {
			int symbol = 128+(int)data[i];
			state = stateTable.make(
				pointResiduals(symbol, state, ZERO)
			);
		}
		return state;
	}

	public int prefixPadding(int L, int length) {
		int state = L;
		for(int i = 0; i < length; ++i) {
			state = stateTable.make(
				constResiduals(state)
			);
		}
		return state;
	}
	public int prefixPadding(int L, int length, byte symbol) {
		int state = L;
		int intSymbol = 128+(int)symbol;
		for(int i = 0; i < length; ++i) {
			state = stateTable.make(
				pointResiduals(intSymbol, state, ZERO)
			);
		}
		return state;
	}

	public int suffix(int L, byte[] data) {
		int state = L;
		for(int i = 0; i < data.length && state != ZERO; ++i) {
			state = stateTable.get(state, 128+(int)data[i]);
		}
		return state;
	}

	public boolean contains(int L, byte[] data) {
		if(data.length == 0) {
			return L == EPSILON;
		}
		for(int i = 0; i < data.length; ++i) {
			L = stateTable.get(L, 128+(int)data[i]);
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

	public int unionSingleton(int L, byte[] word) {
		final int N = word.length;
		final int[] Lstates = new int[N];
		for(int i = 0; i < N; ++i) {
			Lstates[i] = L;
			int symbol = 128+(int)word[i];
			L = stateTable.get(L, symbol);
		}
		int state = EPSILON;
		for(int i = N - 1; i >= 0; --i) {
			stateTable.get(Lstates[i], scratch);
			int symbol = 128+(int)word[i];
			scratch[symbol] = state;
			state = stateTable.make(scratch);
		}
		return state;
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
			for(int i = 0; i < 256; ++i) {
				int Li = stateTable.get(L, i);
				if(Li != ZERO) {
					length += 1;
					L = Li;
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
				for(int i = 0; i < 256; ++i) {
					if(stateTable.get(state, i) != ZERO) {
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
				data[dataIndex] = (byte)(symbol - 128);
				for(int i = symbol + 1; i < 256; ++i) {
					if(stateTable.get(state, i) != ZERO) {
						item.symbol = i;
						stack.push(item);
						break;
					}
				}
				if(dataIndex == data.length - 1) {
					return data;
				}
				state = stateTable.get(state, symbol);
				for(int i = 0; i < 256; ++i) {
					if(stateTable.get(state, i) != ZERO) {
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
		BigInteger size = BigInteger.ZERO;
		for(int i = 0; i < 256; ++i) {
			int Li = stateTable.get(L, i);
			if(Li == EPSILON) {
				size = size.add(BigInteger.ONE);
			} else if(Li != ZERO) {
				size = size.add(size(G, Li));
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
		for(int i = 0; i < 256; ++i) {
			int Li = stateTable.get(L, i);
			int Ri = stateTable.get(R, i);
			if(Li == Ri) {
				residuals[i] = Li;
			} else if(Li == ZERO || Ri == ZERO) {
				residuals[i] = ZERO;
			} else {
				residuals[i] = intersection(
					G,
					Math.min(Li, Ri),
					Math.max(Li, Ri)
				);
			}
		}
		int L_n_R = stateTable.make(residuals);
		G.put(L, R, L_n_R);
		return L_n_R;
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
		for(int i = 0; i < 256; ++i) {
			int Li = stateTable.get(L, i);
			int Ri = stateTable.get(R, i);
			if(Li == Ri) {
				residuals[i] = Li;
			} else if(Li == ZERO) {
				residuals[i] = Ri;
			} else if(Ri == ZERO) {
				residuals[i] = Li;
			} else {
				residuals[i] = union(
					G,
					Math.min(Li, Ri),
					Math.max(Li, Ri)
				);
			}
		}
		int L_u_R = stateTable.make(residuals);
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
		int[] Rcomp = new int[256];
		for(int i = 0; i < 256; ++i) {
			Rcomp[i] = complement(length - 1, G, stateTable.get(L, i)); 
		}
		int Lcomp = stateTable.make(Rcomp);
		G.put(length, L, Lcomp);
		return Lcomp;
	}
}