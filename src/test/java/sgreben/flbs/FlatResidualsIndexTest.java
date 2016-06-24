package sgreben.flbs;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

public class FlatResidualsIndexTest {

	FlatResidualsIndex index;
	int[][] residuals;
	
	@Before
	public void SetUp() {
		index = new FlatResidualsIndex(2, 0.75);
		residuals = new int[5][];
		for(int i = 0; i < residuals.length; ++i) {
			residuals[i] = new int[256];
			for(int j = 0; j < 256; ++j) {
				residuals[i][j] = (int)(i+((j*j) % (2*i+1)));
			}
		}
	}
	
	@Test
	public void emptyIndex_addMapping_containsAddedKey() {
		index.put(residuals[0], 123);
		assertTrue(index.containsKey(residuals[0]));
	}
	
	@Test
	public void emptyIndex_addMapping_containsAddedValue() {
		index.put(residuals[0], 123);
		assertEquals(123, index.get(residuals[0]));
	}

	@Test
	public void emptyIndex_addMappingTwice_sizeIs1() {
		index.put(residuals[0], 123);
		index.put(residuals[0], 456);
		assertEquals(1, index.size());
	}
	
	@Test
	public void emptyIndex_addMappingTwice_containsLastAddedValue() {
		index.put(residuals[0], 123);
		index.put(residuals[0], 456);
		assertEquals(456, index.get(residuals[0]));
	}

	@Test
	public void emptyIndex_addTwoMappings_containsBothAddedKeys() {
		index.put(residuals[0], 123);
		index.put(residuals[1], 456);
		assertTrue(index.containsKey(residuals[0]));
		assertTrue(index.containsKey(residuals[1]));
	}
	
	@Test
	public void emptyIndex_addTwoMappings_containsBothAddedValues() {
		index.put(residuals[0], 123);
		index.put(residuals[1], 456);
		assertEquals(123, index.get(residuals[0]));
		assertEquals(456, index.get(residuals[1]));
	}
	
	@Test
	public void emptyIndex_addThreeMappings_containsAddedValues() {
		index.put(residuals[0], 123);
		index.put(residuals[1], 456);
		index.put(residuals[2], 789);
		assertEquals(123, index.get(residuals[0]));
		assertEquals(456, index.get(residuals[1]));
		assertEquals(789, index.get(residuals[2]));
	}
	
	
	@Test
	public void emptyIndex_addTwoMappings_doesNotContainOtherKeys() {
		index.put(residuals[0], 123);
		index.put(residuals[1], 456);
		assertFalse(index.containsKey(residuals[2]));
		assertFalse(index.containsKey(residuals[3]));
		assertFalse(index.containsKey(residuals[4]));
	}
	
	@Test
	public void emptyIndex_addAllMappings_containsAllAddedKeys() {
		for(int i = 0; i < residuals.length; ++i) {
			index.put(residuals[i], 123 * i);
		}
		for(int i = 0; i < residuals.length; ++i) {
			assertTrue(index.containsKey(residuals[i]));
		}
	}
	
	@Test
	public void emptyIndex_addAllMappings_containsAllAddedValues() {
		for(int i = 0; i < residuals.length; ++i) {
			index.put(residuals[i], 123 * i);
		}
		for(int i = 0; i < residuals.length; ++i) {
			assertEquals(123*i, index.get(residuals[i]));
		}
	}
	
	@Test
	public void emptyIndex_addAllMappingsTwice_containsLatestAddedValues() {
		for(int i = 0; i < residuals.length; ++i) {
			index.put(residuals[i], 123 * i);
		}
		for(int i = 0; i < residuals.length; ++i) {
			index.put(residuals[i], 321 * i);
		}
		for(int i = 0; i < residuals.length; ++i) {
			assertEquals(321*i, index.get(residuals[i]));
		}
	}
	
	@Test
	public void emptyIndex_addAllMappingsTwice_sizeIsNumMappings() {
		for(int i = 0; i < residuals.length; ++i) {
			index.put(residuals[i], 123 * i);
		}
		for(int i = 0; i < residuals.length; ++i) {
			index.put(residuals[i], 321 * i);
		}
		assertEquals(residuals.length, index.size());
	}
	
	@Test
	public void emptyIndex_addMapping_sizeIs1() {
		index.put(residuals[0], 123);
		assertEquals(1, index.size());
	}
}