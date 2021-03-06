package sgreben.flbs;
import java.util.HashMap;

public class IntPairMap {
	private static class IntPair {
		public final int _1;
		public final int _2;
		public IntPair(int _1, int _2) {
			this._1 = _1;
			this._2 = _2;
		}
		
		@Override
		public boolean equals(Object o) {
			if(o.getClass() != IntPair.class) {
				return false;
			}
			IntPair other = (IntPair)o;
			return ( 
				other._1 == _1 &&
				other._2 == _2 
			);
		}
		
		@Override
		public int hashCode() {
			return _1 ^ (_2 << 1);
		}
	}
	
	private HashMap<IntPair, Integer> map;
	
	public IntPairMap() {
		map = new HashMap<IntPair, Integer>();
	}
	
	public int get(int i, int j) {
		return map.get(new IntPair(i, j));
	}
	
	public boolean containsKey(int i, int j) {
		return map.containsKey(new IntPair(i, j));
	}
	
	public void put(int i, int j, int value) {
		map.put(new IntPair(i, j), value);
	}
}