package sgreben.flbs;

public interface StateTable {
	// Number of states in this table
	public int size();
	// The residuals of the given state
	public Residuals residuals(int state);
	// Returns, or creates and returns, the state with the given residuals 
	public int make(Residuals residuals);
	// True iff a state with the given residuals exists
	public boolean exists(Residuals residuals);
	// The state representing the empty language 
	public int ZERO();
	// The state representing the language containing only the empty word.
	public int EPSILON();
}