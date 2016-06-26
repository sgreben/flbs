package sgreben.flbs;

public interface StateTable {
	
	/** Number of states in this table
	 */
	public int size();
	
	/** The residuals of the given state
	 */
	public Residuals get(int state);
	
	/** The residual of the given state w.r.t. the given symbol.
	 */
	public int make(Residuals residuals);
	
	/** True iff a state with the given residuals exists
	 */
	public boolean exists(Residuals residuals);
	
	/** The state representing the empty set 
	 */
	public int ZERO();
	
	/** The state representing the language containing only the empty word.
	 */
	public int EPSILON();
}