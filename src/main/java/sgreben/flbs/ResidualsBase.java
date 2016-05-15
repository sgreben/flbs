package sgreben.flbs;

public abstract class ResidualsBase {
	
	abstract int get(int symbol);
	
	@Override
	public int hashCode() {
		final int seed = 5381;
		final int m = 0x5bd1e995;
		final int r = 24;
		int h = seed;
		for(int i = 0; i < 256; i++) {
			int k = get(i);
			k *= m;
			k ^=  k >> r;
			k *= m;
			h *= m;
			h ^= k;
		}
		h ^= h >> 13;
		h *= m;
		h ^= h >> 13;
		return h;
	}
	
	public boolean equals(Object other) {
		if(other instanceof Residuals) {
			Residuals R = (Residuals) other;
			for(int i = 0; i < 256; ++i) {
				if(get(i) != R.get(i)) {
					return false;
				}
			}
			return true;
		}
		return false;
	}
}