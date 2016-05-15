package sgreben.flbs;

import org.junit.Test;
import static org.junit.Assert.*;

public abstract class StateTableTest {

	LinearScanStateTable st;
	int ZERO;
	int EPSILON;
	
	public static Residuals R_Sigma() {
		int[] R = new int[256];
		for(int i = 0; i < 256; ++i) {
			R[i] = LinearScanStateTable.EPSILON;
		}
		return new ArrayResiduals(R);
	}
	
	public static Residuals R_Zero() {
		int[] R = new int[256];
		for(int i = 0; i < 256; ++i) {
			R[i] = LinearScanStateTable.ZERO;
		}
		return new ArrayResiduals(R);
	}
	
	@Test
	public void emptyLanguage_R_allSelfLoops() {
		Residuals R = st.residuals(ZERO);
		for(int i = 0; i < 256; ++i) {
			assertEquals(ZERO, R.get(i)); 
		}
	}
	
	@Test
	public void epsilonLanguage_R_allEmptyLanguage() {
		Residuals R = st.residuals(EPSILON);
		for(int i = 0; i < 256; ++i) {
			assertEquals(ZERO, R.get(i)); 
		}
	}
	
	@Test
	public void emptyResiduals_make_returnsZero() {
		// L = 0
		Residuals R = R_Zero();
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
		Residuals Rr = new ArrayResiduals(R);
		int L = st.make(Rr);
		assertTrue(st.exists(Rr));
	}
	
	@Test
	public void emptyTable_makeNew_repeatedMakeReturnsSameState() {
		// L = Sigma
		Residuals R = R_Sigma();
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
		assertFalse(st.exists(new ArrayResiduals(R)));
	}
}
