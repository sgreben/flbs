package sgreben.flbs;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

public class IntervalResidualsTest {

	IntervalResiduals residuals;
	
	@Test
	public void Left1From5To10Rest1_isConst1() {
		residuals = new IntervalResiduals(1, 5, 10, 1);
		assertTrue(residuals.isConst(1)); 
	}
	
	@Test
	public void Left0From5To10Rest1_isNotConst() {
		residuals = new IntervalResiduals(0, 5, 10, 1);
		assertFalse(residuals.isConst(0));
		assertFalse(residuals.isConst(1));
	}
	
	@Test
	public void Left123From33To66Rest456_CorrectValuesUpTo256() {
		residuals = new IntervalResiduals(123, 33, 66, 456);
		for(int i = 0; i < 256; ++i) {
			if(i >= 33 && i <= 66) {
				assertEquals(123, residuals.get(i));
			} else {
				assertEquals(456, residuals.get(i));
			}
		} 
	}
}
