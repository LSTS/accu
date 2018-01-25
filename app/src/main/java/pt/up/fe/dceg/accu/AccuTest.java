package pt.up.fe.dceg.accu;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.lsts.accu.state.Accu;

public class AccuTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testRunTest() {
		
		assertEquals("size <> 0",0,Accu.getInstance().getSystemList().getList().size());
	}

	@Test
	public void testSetUp() {
		fail("Not yet implemented");
	}

	@Test
	public void testTearDown() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetActivity() {
		fail("Not yet implemented");
	}

	@Test
	public void testActivityInstrumentationTestCase2StringClassOfT() {
		fail("Not yet implemented");
	}

	@Test
	public void testActivityInstrumentationTestCase2ClassOfT() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetActivityIntent() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetActivityInitialTouchMode() {
		fail("Not yet implemented");
	}

}
