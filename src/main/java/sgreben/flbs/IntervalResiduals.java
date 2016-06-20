package sgreben.flbs;

public class IntervalResiduals implements Residuals {
	
	private final int insideState;
	private final int leftSymbol;
	private final int rightSymbol;
	private final int outsideState;
	
	public IntervalResiduals(int insideState, int leftSymbol, int rightSymbol, int outsideState) {
		this.insideState = insideState;
		this.leftSymbol = leftSymbol;
		this.rightSymbol = rightSymbol;
		this.outsideState = outsideState;
	}
	
	public int get(int symbol) {
		if(leftSymbol <= symbol && symbol <= rightSymbol) {
			return insideState;
		} else {
			return outsideState;
		}
	}
	
	public boolean isConst(int state) {
		return (
			this.insideState == state &&
			(outsideState == state || (rightSymbol > 255 && leftSymbol < 0))
		);
	}
	
	public Residuals compact() {
		if(insideState == outsideState || (rightSymbol > 255 && leftSymbol < 0)) {
			return new ConstResiduals(insideState);
		}
		if(leftSymbol < 0) {
			return new StepResiduals(rightSymbol+1,insideState,outsideState); 
		}
		if(rightSymbol > 255) {
			return new StepResiduals(leftSymbol,outsideState, insideState); 
		}
		return this;
	}
}