package sgreben.flbs;

public class FlatResidualsIndex {
	
	private static final int INTS_PER_ENTRY = 256 + 1 + 1;
	private static final int OCCUPIED_OFFSET = 0;
	private static final int STATE_OFFSET = 1;
	private static final int RESIDUALS_OFFSET = 2;
	
	private int[] table;
	private int numEntries;
	private int numEntrySlots;
	private final double loadFactor;
	
	public FlatResidualsIndex(int initialSize, double loadFactor) {
		this.numEntrySlots = initialSize;
		this.loadFactor = loadFactor;
		this.table = new int[numEntrySlots * INTS_PER_ENTRY];
		this.numEntries = 0;
	}

	public int size() {
		return numEntries;
	}
	
	public boolean containsKey(int[] residuals) {
		return get(residuals) >= 0;
	}

	public int get(int[] residuals) {
		int h = hashIndex(residualsHash(residuals, 0));
		for(int i = 0; i < numEntrySlots && getOccupiedFlag(h); ++i) {
			if(equalResiduals(h, residuals, 0)) {
				return getState(h);
			}
			h = hashIndex(h + 1);
		}
		return -1;
	}

	public void put(int[] residuals, int state) {
		put(residuals, 0, state);
	}
	
	private void put(int[] residuals, int residualsOffset, int state) {
		checkResize();
		int h = hashIndex(residualsHash(residuals, residualsOffset));
		while(getOccupiedFlag(h)) {
			if(equalResiduals(h, residuals, residualsOffset)) {
				table[entryOffset(h) + STATE_OFFSET] = state;
				return;
			}
			h = hashIndex(h + 1);
		}
		setEntry(h, residuals, residualsOffset, state);
		++numEntries;
	}
	
	private boolean getOccupiedFlag(int hash) {
		return table[entryOffset(hash) + OCCUPIED_OFFSET] > 0;
	}
	
	private int getState(int hash) {
		return table[entryOffset(hash) + STATE_OFFSET];
	}
	
	private void setEntry(int hash, int[] residuals, int residualsOffset, int state) {
		final int entryOffset = entryOffset(hash);
		table[entryOffset + OCCUPIED_OFFSET] = 1;
		table[entryOffset + STATE_OFFSET] = state;
		int tableOffset = entryOffset + RESIDUALS_OFFSET; 
		for(int i = 0; i < 256; ++i, ++tableOffset, ++residualsOffset) {
			table[tableOffset] = residuals[residualsOffset];
		}
	}
	
	private boolean equalResiduals(int hash, int[] residuals, int residualsOffset) {
		int tableOffset = entryOffset(hash) + RESIDUALS_OFFSET;
		for(int i = 0; i < 256; ++i, ++tableOffset, ++residualsOffset) {
			if(table[tableOffset] != residuals[residualsOffset]) {
				return false;
			}
		}
		return true;
	}

	private int hashIndex(int hash) {
		return hash % numEntrySlots;
	}

	private int entryOffset(int hash) {
		hash %= numEntrySlots;
		if(hash < 0) {
			hash += numEntrySlots;
		}
		return hash * INTS_PER_ENTRY;
	}

	private void checkResize() {
		if(numEntries >= loadFactor * numEntrySlots) {
			resize();
		}
	}
	
	private synchronized void resize() {
		final int[] oldTable = table;
		numEntrySlots = numEntrySlots * 2;
		table = new int[oldTable.length * 2];
		numEntries = 0;
		for(int i = 0; i < oldTable.length; i += INTS_PER_ENTRY) {
			if(oldTable[i + OCCUPIED_OFFSET] > 0) {
				put(oldTable, i + RESIDUALS_OFFSET, oldTable[i + STATE_OFFSET]);
			}
		}
	}
	
	private int residualsHash(int[] array, int offset) {
		int hash = 1;
		for(int i = 0; i < 256; ++i, ++offset) {
			hash = 31 * hash + array[offset];
		}
		return hash;
	}
}