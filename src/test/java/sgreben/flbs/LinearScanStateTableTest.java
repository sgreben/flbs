package sgreben.flbs;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

public class LinearScanStateTableTest extends StateTableTest {

	@Before
	public void setUp() {
		 st = new LinearScanStateTable();
		 ZERO = LinearScanStateTable. ZERO;
		 EPSILON = LinearScanStateTable.EPSILON;
	}
	
}