package sgreben.flbs;

public class ConstResiduals implements Residuals {
	private int state;
	public ConstResiduals(int state) {
		this.state = state;
	}
	public int get(int i) {
		return state;
	}
}