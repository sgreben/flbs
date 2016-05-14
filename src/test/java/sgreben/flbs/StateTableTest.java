import org.junit.Test;
import java.util.List;
import static org.junit.Assert.*;

public class StateTableTest {
	@Test
	public void emptyLanguage_successors_allSelfLoops() {
		StateTable st = new StateTable();
		for(int i = 0; i < 255; ++i) {
			assertEquals(StateTable.ZERO, st.successor(StateTable.ZERO, (byte) i)); 
		}
	}
}