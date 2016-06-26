package sgreben.flbs;

import java.util.concurrent.locks.ReentrantReadWriteLock;

public class FlatResidualsIndex {
	
	private static final int INTS_PER_ENTRY = 256 + 1 + 1;
	private static final int OCCUPIED_OFFSET = 0;
	private static final int STATE_OFFSET = 1;
	private static final int RESIDUALS_OFFSET = 2;
	
	private int[] table;
	private volatile int numEntries;
	private int numEntrySlots;
	private ReentrantReadWriteLock lock;
	private final double loadFactor;
	
	public FlatResidualsIndex(int initialSize, double loadFactor) {
		this.numEntrySlots = initialSize;
		this.loadFactor = loadFactor;
		this.table = new int[numEntrySlots * INTS_PER_ENTRY];
		this.numEntries = 0;
		this.lock = new ReentrantReadWriteLock();
	}

	public int size() {
		return numEntries;
	}
	
	public boolean containsKey(int[] residuals) {
		return get(residuals) >= 0;
	}

	public int get(int[] residuals) {
		lock.readLock().lock();
		try {
			int h = hashIndex(residualsHash(residuals, 0));
			for(int i = 0; i < numEntrySlots && getOccupiedFlag(h); ++i, ++h) {
				if(h > numEntrySlots) {
					h = 0;
				}
				if(equalResiduals(h, residuals, 0)) {
					return getState(h);
				}
			}
			return -1;
		} finally {
			lock.readLock().unlock();
		}
	}

	public void put(int[] residuals, int state) {
		put(residuals, 0, state);
	}
	private void put(int[] residuals, int residualsOffset, int state) {
		lock.writeLock().lock();
		try { 
			if(numEntries >= loadFactor * numEntrySlots) {
				resize();
			}
		} finally {
			lock.writeLock().unlock();
		}
		lock.writeLock().lock();
		try {
			int h = hashIndex(residualsHash(residuals, residualsOffset));
			while(getOccupiedFlag(h)) {
				if(h > numEntrySlots) {
					h = 0;
				}
				if(equalResiduals(h, residuals, residualsOffset)) {
					table[entryOffset(h) + STATE_OFFSET] = state;
					return;
				}
				++h;
			}
			setEntry(h, residuals, residualsOffset, state);
			++numEntries;
		} finally {
			lock.writeLock().unlock();
		}
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
	
	private void resize() {
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