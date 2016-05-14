package sgreben.flbs;

interface Residuals {
	
	int get(int ofSymbol);
	
	// True iff all residuals are equal to the given state.
	boolean isConst(int state);
	
}