package sgreben.flbs;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.Arrays;

public class FlatFsaBytestringSetIndexTest extends BytestringSetIndexTest {
	@Before
	public void setup() {
		FlatIndexedStateTable stateTable = new FlatIndexedStateTable();
		ZERO = stateTable.ZERO();
		EPSILON = stateTable.EPSILON();
		flbs = new FlatFsaBytestringSetIndex(stateTable);
	}
}