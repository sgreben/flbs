package sgreben.flbs;

interface Residuals {
	// Return the state corresponding to the residual w.r.t. the given symbol.
	int get(int symbol);
	// True iff all residuals are equal to the given state.
	boolean isConst(int state);
	
}