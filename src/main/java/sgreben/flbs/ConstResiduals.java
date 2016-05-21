package sgreben.flbs;

public class ConstResiduals extends ResidualsBase implements Residuals {
	
	private final int state;
	
	public ConstResiduals(int state) {
		this.state = state;
	}
	
	public int get(int symbol) {
		return state;
	}
	
	public boolean isConst(int state) {
		return this.state == state;
	}
	
	public Residuals compact() {
		return this;
	}
}