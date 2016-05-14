package sgreben.flbs;

public class ArrayResiduals implements Residuals {
	private final int[] residuals;
	public ArrayResiduals(int[] residuals) {
		this.residuals = residuals;
	}
	public int get(int ofSymbol) {
		return residuals[ofSymbol];
	}
}