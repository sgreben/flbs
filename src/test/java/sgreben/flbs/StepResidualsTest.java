package sgreben.flbs;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

public class StepResidualsTest {

	StepResiduals residuals;
	
	@Test
	public void Left1Step5Right1_isConst1() {
		residuals = new StepResiduals(5, 1, 1);
		assertTrue(residuals.isConst(1)); 
	}
	
	@Test
	public void Left0Step2Default1_isNotConst() {
		residuals = new StepResiduals(2, 0, 1);
		assertFalse(residuals.isConst(0));
		assertFalse(residuals.isConst(1));
	}
	
	@Test
	public void Left123Step33Right456_CorrectValuesUpTo256() {
		residuals = new StepResiduals(33, 123, 456);
		for(int i = 0; i < 256; ++i) {
			if(i < 33) {
				assertEquals(123, residuals.get(i));
			} else {
				assertEquals(456, residuals.get(i));
			}
		} 
	}
}
