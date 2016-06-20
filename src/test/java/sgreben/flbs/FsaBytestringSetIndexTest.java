package sgreben.flbs;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.Arrays;

public class FsaBytestringSetIndexTest {
	
	static byte[] word1 = new byte[]{0x1,0x2,0x3,0xF,0xE,0xD};
	static byte[] word2 = new byte[]{0x1,0x2,0x3,0xF,0xF,0xF};
	static byte[] word3 = new byte[]{0xF,0xF,0xF,0xF,0xF,0xF};
	
	static byte[] word1U = new byte[]{0x1,0x2,0x3};
	static byte[] word1L = new byte[]{0xF,0xE,0xD};
	
	static byte[] word2U = new byte[]{0x1,0x2,0x3};
	static byte[] word2L = new byte[]{0xF,0xF,0xF};
	
	static byte[] word3U = new byte[]{0xF,0xF,0xF};
	static byte[] word3L = word3U;
	
	static byte[] word4 = new byte[]{0xF,0xF};
	
	StateTable stateTable;
	FsaBytestringSetIndex flbs;
	
	@Before
	public void setup() {
		stateTable = new LinearScanStateTable();
		flbs = new FsaBytestringSetIndex(stateTable);
	}
	
	@Test
	public void singleton_builtTwice_isSameState() {
		int Lw = flbs.singleton(word1);
		int Lw2 = flbs.singleton(word1);
		assertEquals(Lw, Lw2);
	}
	
	@Test
	public void singleton_containsWord() {
		int Lw = flbs.singleton(word1);
		assertTrue(flbs.contains(Lw, word1));
	}

	@Test
	public void singleton_iteratorVisitsWordThenEmpty() {
		int Lw = flbs.singleton(word1);
		Iterator<byte[]> iterator = flbs.iterate(Lw);
		assertTrue(iterator.hasNext());
		byte[] iteratorNext = iterator.next();
		assertArrayEquals(iteratorNext, word1);
		assertFalse(iterator.hasNext());
	}

	@Test
	public void singleton_sizeIs1() {
		int Lw = flbs.singleton(word1);
		assertEquals(BigInteger.ONE, flbs.size(Lw));
	}

	@Test
	public void singleton_doesNotContainDiffWord() {
		int Lw = flbs.singleton(word1);
		assertFalse(flbs.contains(Lw, word2));
	}
	
	@Test
	public void singleton_length_returnsLengthOfWord() {
		int Lw = flbs.singleton(word1);
		assertEquals(word1.length, flbs.length(Lw));
	}
	
	@Test
	public void unionOfTwoWords_length_returnsLengthOfWords() {
		int Lw1 = flbs.singleton(word1);
		int Lw2 = flbs.singleton(word2);
		int Lw12 = flbs.union(Lw1, Lw2);
		assertEquals(word1.length, flbs.length(Lw12));
		assertEquals(word2.length, flbs.length(Lw12));
	}
	
	@Test
	public void unionOfTwoWords_iteratorVisitsBothWordsThenEmpty() {
		int Lw1 = flbs.singleton(word1);
		int Lw2 = flbs.singleton(word2);
		int Lw12 = flbs.union(Lw1, Lw2);
		Iterator<byte[]> iterator = flbs.iterate(Lw12);
		assertTrue(iterator.hasNext());
		boolean word1Visited = false;
		boolean word2Visited = false;
		byte[] iterator1 = iterator.next();
		word1Visited = Arrays.equals(iterator1, word1);
		word2Visited = Arrays.equals(iterator1, word2);
		byte[] iterator2 = iterator.next();
		word1Visited |= Arrays.equals(iterator2, word1);
		word2Visited |= Arrays.equals(iterator2, word2);
		assertTrue(word1Visited && word2Visited);
		assertFalse(iterator.hasNext());
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
	public void unionOf3Words_iteratorVisitsAllWordsThenEmpty() {
		int Lw1 = flbs.singleton(word1);
		int Lw2 = flbs.singleton(word2);
		int Lw3 = flbs.singleton(word3);
		int Lw12 = flbs.union(Lw1, Lw2);
		int Lw123 = flbs.union(Lw12, Lw3);
		Iterator<byte[]> iterator = flbs.iterate(Lw123);
		assertTrue(iterator.hasNext());
		boolean word1Visited = false;
		boolean word2Visited = false;
		boolean word3Visited = false;
		byte[] iterator1 = iterator.next();
		word1Visited = Arrays.equals(iterator1, word1);
		word2Visited = Arrays.equals(iterator1, word2);
		word3Visited = Arrays.equals(iterator1, word3);
		byte[] iterator2 = iterator.next();
		word1Visited |= Arrays.equals(iterator2, word1);
		word2Visited |= Arrays.equals(iterator2, word2);
		word3Visited |= Arrays.equals(iterator2, word3);
		byte[] iterator3 = iterator.next();
		word1Visited |= Arrays.equals(iterator3, word1);
		word2Visited |= Arrays.equals(iterator3, word2);
		word3Visited |= Arrays.equals(iterator3, word3);
		assertTrue(word1Visited && word2Visited && word3Visited);
		assertFalse(iterator.hasNext());
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
	
	@Test
	public void zero_iteratorMakesNoSteps() {
		Iterator<byte[]> iterator = flbs.iterate(stateTable.ZERO());
		int count = 0;
		while(iterator.hasNext()) {
			iterator.next();
			++count;
		}
		assertEquals(0, count);
	}
	
	@Test
	public void epsilon_iteratorMakesOneStep() {
		Iterator<byte[]> iterator = flbs.iterate(stateTable.EPSILON());
		int count = 0;
		while(iterator.hasNext()) {
			iterator.next();
			++count;
		}
		assertEquals(1, count);
	}
	
	@Test
	public void epsilon_paddedTwice_iteratorMakes256squaredSteps() {
		int L = flbs.prefixPadding(stateTable.EPSILON(), 2);
		Iterator<byte[]> iterator = flbs.iterate(L);
		int count = 0;
		while(iterator.hasNext()) {
			iterator.next();
			++count;
		}
		assertEquals(256*256, count);
	}
	
	@Test
	public void singletonThenPrefix_sameStateAsFullWord() {
		int Lw1 = flbs.singleton(word1);
		int Lw1L = flbs.singleton(word1L);
		int Lw1_ = flbs.prefix(Lw1L, word1U);
		System.out.println(stateTable.size());
		assertEquals(Lw1, Lw1_);
	}
	
	@Test
	public void singleton_complement_doesNotContainWord() {
		byte[] word = word4;
		int length = word.length;
		int Lw = flbs.singleton(word);
		int Lw_comp = flbs.complement(Lw);
		assertFalse(flbs.contains(Lw_comp, word));
	}
	
	@Test
	public void singleton_complement_intersectionWithSingleton_isZero() {
		byte[] word = word1;
		int length = word.length;
		int Lw = flbs.singleton(word);
		int Lw_comp = flbs.complement(Lw);
		int Lw_n_Lw_comp = flbs.intersection(Lw, Lw_comp);
		assertEquals(stateTable.ZERO(), Lw_n_Lw_comp);
	}
	
	@Test
	public void singleton_complement_complement_isSingleton() {
		byte[] word = word4;
		int length = word.length;
		int Lw = flbs.singleton(word);
		int Lw_comp = flbs.complement(Lw);
		int Lw_comp_comp = flbs.complement(Lw_comp);
		assertEquals(Lw, Lw_comp_comp);
	}
	
	@Test
	public void singleton_complement_sizeIs256powNMinusOne() {
		int length = word4.length;
		int Lw = flbs.singleton(word4);
		// (2^length) - 1
		BigInteger expectedComplementSize = 
			BigInteger.valueOf(256).pow(length).subtract(BigInteger.ONE);
		int Lw_comp = flbs.complement(Lw);
		assertEquals(expectedComplementSize, flbs.size(Lw_comp));
	}
}