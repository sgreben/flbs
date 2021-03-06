package sgreben.flbs;

public class ArrayResiduals extends ResidualsBase implements Residuals {
	
	private final int[] residuals;
	
	public ArrayResiduals(int[] residuals) {
		assert(residuals.length == 256);
		this.residuals = residuals;
	}
	
	public int get(int symbol) {
		return residuals[symbol];
	}
	
	public boolean isConst(int state) {
		for(int i = 0; i < 256; ++i) {
			if(residuals[i] != state) {
				return false;
			}
		}
		return true;
	}
	
	public Residuals compact() {
		int r0 = residuals[0];
		if(isConst(r0)) {
			return new ConstResiduals(r0);
		}
		return this;
	}
}