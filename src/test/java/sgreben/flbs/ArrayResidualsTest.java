package sgreben.flbs;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

public abstract class ArrayResidualsTest {

	static final int ZERO = 0;
	static int[] zeroResiduals;
	static int[] oneTwoThreeResiduals;
	static int[] constOneResiduals;
	ArrayResiduals residuals;
	@Before
	public void SetUp() {
		 zeroResiduals = new int[256];
		 oneTwoThreeResiduals = new int[256];
		 for(int i = 0; i < 256; ++i) {
		 	 oneTwoThreeResiduals[i] = 1 + (i % 3);
		 	 constOneResiduals[i] = 1;
		 	 zeroResiduals[i] = ZERO;
		 }
	}
	
	@Test
	public void zeroResiduals_isConstZero() {
		residuals = new ArrayResiduals(zeroResiduals);
		assertTrue(residuals.isConst(ZERO)); 
	}
	
	@Test
	public void zeroResiduals_changesToUnderlyingArrayPropagate() {
		residuals = new ArrayResiduals(zeroResiduals);
		assertEquals(ZERO, residuals.get(0));
		zeroResiduals[0] = 123;
		assertEquals(123, residuals.get(0));
	}
	
	@Test
	public void constOneResiduals_isConstOne() {
		residuals = new ArrayResiduals(constOneResiduals);
		assertTrue(residuals.isConst(1)); 
	}
	
	@Test
	public void oneTwoThreeResiduals_isNotConstOne() {
		residuals = new ArrayResiduals(oneTwoThreeResiduals);
		assertFalse(residuals.isConst(1)); 
	}

}
