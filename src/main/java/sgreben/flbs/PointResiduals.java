package sgreben.flbs;

public class PointResiduals implements Residuals {
	private int state;
	private int symbol;
	private int defaultSymbol;
	public PointResiduals(int symbol, int state, int defaultSymbol) {
		this.symbol = symbol;
		this.state = state;
		this.defaultSymbol = defaultSymbol;
	}
	public int get(int symbol) {
		if(symbol == this.symbol) {
			return state;
		} else {
			return defaultSymbol;
		}
	}
}