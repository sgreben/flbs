package sgreben.flbs;

public class StepResiduals extends ResidualsBase implements Residuals {
	
	private final int rightStartSymbol;
	private final int leftState;
	private final int rightState;
	
	public StepResiduals(int rightStartSymbol, int leftState, int rightState) {
		this.rightStartSymbol = rightStartSymbol;
		this.leftState = leftState;
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
	
	public Residuals compact() {
		if(leftState == rightState || rightStartSymbol > 255) {
			return new ConstResiduals(leftState);
		}
		return this;
	}
	
	@Override
	public int hashCode() {
		return leftState ^ (rightState << 6) * (rightStartSymbol << 16);
	}
}