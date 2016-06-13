package sgreben.flbs;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

public class ConstResidualsTest {

	ConstResiduals residuals;
	
	@Test
	public void const0Residuals_isConst0() {
		residuals = new ConstResiduals(0);
		assertTrue(residuals.isConst(0)); 
	}
	
	@Test
	public void constMinusOneResiduals_isConstMinusOne() {
		residuals = new ConstResiduals(-1);
		assertTrue(residuals.isConst(-1)); 
	}
	
	@Test
	public void constResiduals_hasSameValueAtAllIndices() {
		residuals = new ConstResiduals(123);
		for(int i = 0; i < 256; ++i) {
			assertEquals(123, residuals.get(i));
		} 
	}
	

}
