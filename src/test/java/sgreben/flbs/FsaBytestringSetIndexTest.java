package sgreben.flbs;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.Arrays;

public class FsaBytestringSetIndexTest extends BytestringSetIndexTest {
	@Before
	public void setup() {
		StateTable stateTable = new LinearScanStateTable();
		ZERO = stateTable.ZERO();
		EPSILON = stateTable.EPSILON();
		flbs = new FsaBytestringSetIndex(stateTable);
	}
}