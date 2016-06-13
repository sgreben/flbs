package sgreben.flbs;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

public class IntPairMapTest {

	IntPairMap intPairMap;
	
	@Before
	public void SetUp() {
		 intPairMap = new IntPairMap();
	}
	
	@Test
	public void emptyMap_addMapping_containsAddedKey() {
		intPairMap.put(1, 2, 3);
		assertTrue(intPairMap.containsKey(1, 2));
	}
	
	@Test
	public void emptyMap_addMapping_returnsAddedValue() {
		intPairMap.put(1, 2, 3);
		assertEquals(3, intPairMap.get(1, 2));
	}
	
	@Test
	public void emptyMap_overrideMapping_returnsNewValue() {
		intPairMap.put(1, 2, 3);
		assertEquals(3, intPairMap.get(1, 2));
		intPairMap.put(1, 2, 4);
		assertEquals(4, intPairMap.get(1, 2));
	}
}