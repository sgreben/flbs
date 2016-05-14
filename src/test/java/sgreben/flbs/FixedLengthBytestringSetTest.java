import org.junit.Test;
import java.util.List;
import static org.junit.Assert.*;

public class FixedLengthBytestringSetTest {
	@Test
	public void emptySet_count_is0() {
		FixedLengthBytestringSet flbs = new FixedLengthBytestringSet();
		assertEquals(0,flbs.size());
	}
	
	@Test
	public void singleton_count_is1() {
		byte[] word = new byte[]{0x1,0x2,0x3,0xF,0xE,0xD};
		FixedLengthBytestringSet flbs = new FixedLengthBytestringSet(word);
		assertEquals(1,flbs.size());
	}
}