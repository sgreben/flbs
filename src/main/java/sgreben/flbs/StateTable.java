package sgreben.flbs;

public interface StateTable {
	public int ZERO();
	public int EPSILON();
	public Residuals residuals(int state);
	public boolean exists(Residuals residuals);
	public int numberOfStates();
	public int make(Residuals residuals);
}