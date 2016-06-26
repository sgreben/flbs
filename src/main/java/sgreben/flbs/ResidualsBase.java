package sgreben.flbs;

public abstract class ResidualsBase {
	
	abstract int get(int symbol);
	
	@Override
	public int hashCode() {
		int hash = 1;
		for(int i = 0; i < 256; ++i) {
			hash = 31 * hash + get(i);
		}
		return hash;
	}
	
	@Override
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