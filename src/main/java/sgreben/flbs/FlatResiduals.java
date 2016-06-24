package sgreben.flbs;

public class FlatResiduals extends ResidualsBase implements Residuals {
	private final int[] table;
	private final int offset;
	
	public FlatResiduals(int[] table, int offset) {
		this.table = table;
		this.offset = offset;
	}
	
	public int get(int symbol) {
		return table[offset + symbol];
	}
	
	public boolean isConst(int state) {
		for(int i = 0; i < 256; ++i) {
			if(table[offset+i] != state) {
				return false;
			}
		}
		return true;
	}
}