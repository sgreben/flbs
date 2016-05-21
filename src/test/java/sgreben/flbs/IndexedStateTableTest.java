package sgreben.flbs;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

public class IndexedStateTableTest extends StateTableTest {

	@Before
	public void setUp() {
		 st = new IndexedStateTable();
		 ZERO = st.ZERO();
		 EPSILON = st.EPSILON();
	}
	
	@Test
	public void makeSigmaUsingPoint_existsUsingStepConstArray() {
		int[] residuals = new int[256];
		for(int i = 0; i < 256; ++i) {
			residuals[i] = EPSILON;
		}
		Residuals R_point = new PointResiduals(0, EPSILON, EPSILON);
		int L_point = st.make(R_point);
		Residuals R_step = new StepResiduals(0, EPSILON, EPSILON);
		Residuals R_const = new ConstResiduals(EPSILON);
		Residuals R_array = new ArrayResiduals(residuals);
		assertTrue(st.exists(R_const));
		assertTrue(st.exists(R_step));
		assertTrue(st.exists(R_array));
	}
	
	@Test
	public void makeSigmaMinus1UsingPoint_existsUsingArray() {
		int[] residuals = new int[256];
		for(int i = 0; i < 256; ++i) {
			residuals[i] = EPSILON;
		}
		residuals[1] = ZERO;
		Residuals R_point = new PointResiduals(1, ZERO, EPSILON);
		int L_point = st.make(R_point);
		Residuals R_array = new ArrayResiduals(residuals);
		assertTrue(st.exists(R_array));
	}
	
}