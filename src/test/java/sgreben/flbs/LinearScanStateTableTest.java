package sgreben.flbs;

import org.junit.Test;
import static org.junit.Assert.*;

public class LinearScanStateTableTest {

	private static Residuals R_Sigma() {
		int[] R = new int[256];
		for(int i = 0; i < 256; ++i) {
			R[i] = LinearScanStateTable.EPSILON;
		}
		return new ArrayResiduals(R);
	}
	
	private static Residuals R_Zero() {
		int[] R = new int[256];
		for(int i = 0; i < 256; ++i) {
			R[i] = LinearScanStateTable.ZERO;
		}
		return new ArrayResiduals(R);
	}
	
	@Test
	public void emptyLanguage_R_allSelfLoops() {
		LinearScanStateTable st = new LinearScanStateTable();
		Residuals R = st.residuals(LinearScanStateTable.ZERO);
		for(int i = 0; i < 256; ++i) {
			assertEquals(LinearScanStateTable.ZERO, R.get(i)); 
		}
	}
	
	@Test
	public void epsilonLanguage_R_allEmptyLanguage() {
		LinearScanStateTable st = new LinearScanStateTable();
		Residuals R = st.residuals(LinearScanStateTable.EPSILON);
		for(int i = 0; i < 256; ++i) {
			assertEquals(LinearScanStateTable.ZERO, R.get(i)); 
		}
	}
	
	@Test
	public void emptyResiduals_make_returnsZero() {
		LinearScanStateTable st = new LinearScanStateTable();
		// L = 0
		Residuals R = R_Zero();
		int L = st.make(R);
		assertEquals(LinearScanStateTable.ZERO, L);
	}
	
	@Test
	public void emptyTable_makeNew_resultExists() {
		LinearScanStateTable st = new LinearScanStateTable();
		// L = Sigma
		int[] R = new int[256];
		for(int i = 0; i < 256; ++i) {
			R[i] = LinearScanStateTable.EPSILON;
		}
		Residuals Rr = new ArrayResiduals(R);
		int L = st.make(Rr);
		assertTrue(st.exists(Rr));
	}
	
	@Test
	public void emptyTable_makeNew_repeatedMakeReturnsSameState() {
		LinearScanStateTable st = new LinearScanStateTable();
		// L = Sigma
		Residuals R = R_Sigma();
		int L = st.make(R);
		int L_later = st.make(R);
		assertEquals(L, L_later);
	}
	
	@Test
	public void emptyTableNewState_exists_isFalse() {
		LinearScanStateTable st = new LinearScanStateTable();
		// L = Sigma
		int[] R = new int[256];
		for(int i = 0; i < 256; ++i) {
			R[i] = LinearScanStateTable.EPSILON;
		}
		assertFalse(st.exists(new ArrayResiduals(R)));
	}
}