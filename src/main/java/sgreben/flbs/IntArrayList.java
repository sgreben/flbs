package sgreben.flbs;

public class IntArrayList {
	
	private int[][] data;
	private int length;
	
	private static final int DEFAULT_INITIAL_CAPACITY = 256;
	
	public IntArrayList(int initialCapacity) {
		data = new int[initialCapacity][];
		length = 0;
	}
	public IntArrayList() {
		this(DEFAULT_INITIAL_CAPACITY);
	}
	
	public int length() {
		return length;
	}
	
	public int[] get(int index) {
		return data[index];
	}
	
	public void set(int index, int[] value) {
		data[index] = value;
	}
	
	public int add(int[] element) {
		if(length == data.length) {
			grow();
		}
		data[length] = element;
		length += 1;
		return length-1;
	}
	
	private void grow() {
		int[][] newData = new int[data.length * 2][];
		for(int i = 0; i < length; ++i) {
			newData[i] = data[i];
		}
		data = newData;
	}
}