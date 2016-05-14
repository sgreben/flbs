package sgreben.flbs;

public class ArrayResiduals implements Residuals {
	
	private final int[] residuals;
	
	public ArrayResiduals(int[] residuals) {
		this.residuals = residuals;
	}
	
	public int get(int ofSymbol) {
		return residuals[ofSymbol];
	}
	
	public boolean isConst(int state) {
		for(int i = 0; i < residuals.length; ++i) {
			if(residuals[i] != state) {
				return false;
			}
		}
		return true;
	}
}