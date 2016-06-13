package sgreben.flbs;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

public class PointResidualsTest {

	PointResiduals residuals;
	
	@Test
	public void Point0Default0_isConst0() {
		residuals = new PointResiduals(0, 0, 0);
		assertTrue(residuals.isConst(0)); 
	}
	
	@Test
	public void Point0Default1_isNotConst() {
		residuals = new PointResiduals(0, 0, 1);
		assertFalse(residuals.isConst(0));
		assertFalse(residuals.isConst(1));
	}
	
	@Test
	public void Point0Default1_CorrectValuesUpTo256() {
		residuals = new PointResiduals(127, 0, 1);
		for(int i = 0; i < 256; ++i) {
			if(i != 127) {
				assertEquals(1, residuals.get(i));
			} else {
				assertEquals(0, residuals.get(i));
			}
		} 
	}
}
