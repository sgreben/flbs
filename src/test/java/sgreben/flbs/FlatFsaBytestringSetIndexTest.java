package sgreben.flbs;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.Arrays;
import java.util.ArrayList;

public class FlatFsaBytestringSetIndexTest extends BytestringSetIndexTest {
	private FlatFsaBytestringSetIndex ffbsi;
	@Before
	public void setup() {
		FlatIndexedStateTable stateTable = new FlatIndexedStateTable();
		ZERO = stateTable.ZERO();
		EPSILON = stateTable.EPSILON();
		ffbsi = new FlatFsaBytestringSetIndex(stateTable);
		flbs = ffbsi;
	}
}