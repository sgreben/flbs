package sgreben.flbs;

public class StepResiduals implements Residuals {
	
	private final int leftState;
	private final int rightStartSymbol;
	private final int rightState;
	
	public StepResiduals(int leftState, int rightStartSymbol, int rightState) {
		this.leftState = leftState;
		this.rightStartSymbol = rightStartSymbol;
		this.rightState = rightState;
	}
	
	public int get(int symbol) {
		if(symbol < rightStartSymbol) {
			return leftState;
		} else {
			return rightState;
		}
	}
	
	public boolean isConst(int state) {
		return (
			this.leftState == state &&
			(rightState == state || rightStartSymbol > 255)
		);
	}
}