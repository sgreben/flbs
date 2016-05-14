package sgreben.flbs;

public class PointResiduals implements Residuals {
	
	private final int state;
	private final int symbol;
	private final int defaultState;
	
	public PointResiduals(int symbol, int state, int defaultState) {
		this.symbol = symbol;
		this.state = state;
		this.defaultState = defaultState;
	}
	
	public int get(int symbol) {
		if(symbol == this.symbol) {
			return state;
		} else {
			return defaultState;
		}
	}
	
	public boolean isConst(int state) {
		return (
			this.state == state &&
			defaultState == state
		);
	}
}