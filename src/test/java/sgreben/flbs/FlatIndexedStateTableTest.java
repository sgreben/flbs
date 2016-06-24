package sgreben.flbs;

import org.junit.Test;
import static org.junit.Assert.*;

public class FlatIndexedStateTableTest {

	FlatIndexedStateTable st;
	static int ZERO;
	static int EPSILON;
	
	public FlatIndexedStateTableTest() {
		st = new FlatIndexedStateTable();
		ZERO = st.ZERO();
		EPSILON = st.EPSILON();
	}
	
	public static int[] R_Sigma() {
		int[] R = new int[256];
		for(int i = 0; i < 256; ++i) {
			R[i] = EPSILON;
		}
		return R;
	}
	
	public static int[] R_Zero() {
		int[] R = new int[256];
		for(int i = 0; i < 256; ++i) {
			R[i] = ZERO;
		}
		return R;
	}
	
	@Test
	public void emptyLanguage_R_allSelfLoops() {
		for(int i = 0; i < 256; ++i) {
			assertEquals(ZERO, st.get(ZERO, i)); 
		}
	}
	
	@Test
	public void epsilonLanguage_R_allEmptyLanguage() {
		for(int i = 0; i < 256; ++i) {
			assertEquals(ZERO, st.get(EPSILON, i)); 
		}
	}
	
	@Test
	public void emptyResiduals_make_returnsZero() {
		// L = 0
		int[] R = R_Zero();
		int L = st.make(R);
		assertEquals(ZERO, L);
	}
	
	@Test
	public void emptyTable_makeNew_resultExists() {
		// L = Sigma
		int[] R = new int[256];
		for(int i = 0; i < 256; ++i) {
			R[i] = EPSILON;
		}
		int L = st.make(R);
		assertTrue(st.exists(R));
	}
	
	@Test
	public void emptyTable_makeNew_repeatedMakeReturnsSameState() {
		// L = Sigma
		int[] R = R_Sigma();
		int L = st.make(R);
		int L_later = st.make(R);
		assertEquals(L, L_later);
	}
	
	@Test
	public void emptyTableNewState_exists_isFalse() {
		// L = Sigma
		int[] R = new int[256];
		for(int i = 0; i < 256; ++i) {
			R[i] = EPSILON;
		}
		assertFalse(st.exists(R));
	}
}