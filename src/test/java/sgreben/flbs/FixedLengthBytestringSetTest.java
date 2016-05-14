package sgreben.flbs;

import org.junit.Test;
import org.junit.Before;
import java.math.BigInteger;
import static org.junit.Assert.*;

public class FixedLengthBytestringSetTest {
	static byte[] word1 = new byte[]{0x1,0x2,0x3,0xF,0xE,0xD};
	static byte[] word2 = new byte[]{0x1,0x2,0x3,0xF,0xF,0xF};
	static byte[] word3 = new byte[]{0xF,0xF,0xF,0xF,0xF,0xF};
	StateTable stateTable;
	FixedLengthBytestringSet flbs;
	
	@Before
	public void setup() {
		stateTable = new LinearScanStateTable();
		flbs = new FixedLengthBytestringSet(stateTable);
	}
	
	@Test
	public void singleton_builtTwice_isSameState() {
		int Lw = flbs.singleton(word1);
		int Lw2 = flbs.singleton(word1);
		assertEquals(Lw, Lw2);
	}
	
	@Test
	public void buildSingleton_resultContainsWord() {
		int Lw = flbs.singleton(word1);
		assertTrue(flbs.contains(Lw, word1));
	}

	@Test
	public void buildSingleton_sizeIs1() {
		int Lw = flbs.singleton(word1);
		assertEquals(BigInteger.ONE, flbs.size(Lw));
	}

	@Test
	public void buildSingleton_resultDoesNotContainDiffWord() {
		int Lw = flbs.singleton(word1);
		assertFalse(flbs.contains(Lw, word2));
	}

	@Test
	public void unionOfTwoWords_resultContainsBoth() {
		int Lw1 = flbs.singleton(word1);
		int Lw2 = flbs.singleton(word2);
		int Lw12 = flbs.union(Lw1, Lw2);
		assertTrue(flbs.contains(Lw12, word1));
		assertTrue(flbs.contains(Lw12, word2));
	}

	@Test
	public void unionOfTwoWords_sizeIs2() {
		int Lw1 = flbs.singleton(word1);
		int Lw2 = flbs.singleton(word2);
		int Lw12 = flbs.union(Lw1, Lw2);
		assertEquals(BigInteger.valueOf(2), flbs.size(Lw12));
	}

	@Test
	public void unionOfTwoWords_resultDoesNotContainDiffWord() {
		int Lw1 = flbs.singleton(word1);
		int Lw2 = flbs.singleton(word2);
		int Lw12 = flbs.union(Lw1, Lw2);
		assertFalse(flbs.contains(Lw12, word3));
	}

	@Test
	public void unionOf3Words_intersectionWithFirst2_sizeIs2() {
		int Lw1 = flbs.singleton(word1);
		int Lw2 = flbs.singleton(word2);
		int Lw3 = flbs.singleton(word3);
		int Lw12 = flbs.union(Lw1, Lw2);
		int Lw123 = flbs.union(Lw12, Lw3);
		int Lw12_ = flbs.intersection(Lw12, Lw123);
		assertEquals(BigInteger.valueOf(2), flbs.size(Lw12_));
	}
	
	@Test
	public void unionOf3Words_prefixPadding10_sizeMultipliedby256exp10() {
		int Lw1 = flbs.singleton(word1);
		int Lw2 = flbs.singleton(word2);
		int Lw3 = flbs.singleton(word3);
		int Lw12 = flbs.union(Lw1, Lw2);
		int Lw123 = flbs.union(Lw12, Lw3);
		int Lw123_pad = flbs.prefixPadding(Lw123, 10);
		assertEquals(
			BigInteger.valueOf(256).pow(10).multiply(BigInteger.valueOf(3)), 
			flbs.size(Lw123_pad)
		);
	}

	@Test
	public void unionOf3Words_intersectionWithUnionOfFirst2_isUnionOfFirst2() {
		int Lw1 = flbs.singleton(word1);
		int Lw2 = flbs.singleton(word2);
		int Lw3 = flbs.singleton(word3);
		int Lw12 = flbs.union(Lw1, Lw2);
		int Lw123 = flbs.union(Lw12, Lw3);
		int Lw12_ = flbs.intersection(Lw12, Lw123);
		assertEquals(Lw12, Lw12_);
	}


	@Test
	public void singleton_intersectSelf_isSameState() {
		int Lw = flbs.singleton(word1);
		int Lw2 = flbs.singleton(word1);
		int Lw3 = flbs.intersection(Lw, Lw2);
		assertEquals(Lw, Lw3);
		assertEquals(Lw2, Lw3);
	}
	
	@Test
	public void singleton_intersectZero_isZero() {
		int Lw = flbs.singleton(word1);
		int Lw2 = flbs.intersection(Lw, stateTable.ZERO());
		assertEquals(stateTable.ZERO(), Lw2);
	}
	
	@Test
	public void singleton_intersectEpsilon_isZero() {
		int Lw = flbs.singleton(word1);
		int Lw2 = flbs.intersection(Lw, stateTable.EPSILON());
		assertEquals(stateTable.ZERO(), Lw2);
	}
}