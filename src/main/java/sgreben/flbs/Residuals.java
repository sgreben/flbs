package sgreben.flbs;

interface Residuals {
	int get(int ofSymbol);
	boolean isConst(int state);
}