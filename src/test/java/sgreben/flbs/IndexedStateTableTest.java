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
	public void indexing_is_representation_independent() {
		Residuals R_point = new PointResiduals(0, EPSILON, EPSILON);
		int L_point = st.make(R_point);
		Residuals R_step = new StepResiduals(0, EPSILON, EPSILON);
		Residuals R_const = new ConstResiduals(EPSILON);
		assertTrue(st.exists(R_const));
	}
	
}