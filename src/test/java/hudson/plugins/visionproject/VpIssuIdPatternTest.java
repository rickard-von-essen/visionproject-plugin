package hudson.plugins.visionproject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;


public class VpIssuIdPatternTest {

	private static final Pattern VP_ID = VisionProjectChangelogAnnotator.VP_ID;
	
	@Test
	public void matching_VP_Issue_Ids() throws Exception {
	
		assertTrue(VP_ID.matcher("DEV-197").find());
		
		assertFalse(VP_ID.matcher("DEV197").find());
		assertFalse(VP_ID.matcher("DEV-").find());
		assertFalse(VP_ID.matcher("-197").find());
		assertFalse(VP_ID.matcher("197").find());
		
		assertTrue(VP_ID.matcher("Dev-197333").find());
		assertTrue(VP_ID.matcher("[DEV-197]").find());
		assertTrue(VP_ID.matcher("[DEV-197] Some random text.").find());
		assertTrue(VP_ID.matcher("{DEV-197}").find());
		assertTrue(VP_ID.matcher("#DEV-197").find());
		
		assertGroupFound("DEV-197", "[DEV-197]");
		assertGroupFound("DEV-197", "{DEV-197}");
		assertGroupFound("DEV-197", "#DEV-197");
		assertGroupFound("DEV-197", "[DEV-197] Some random text.");
	}

	private void assertGroupFound(String expected, String toMatch) {
		Matcher res;
		res = VP_ID.matcher(toMatch);
		assertTrue(res.find());
		assertEquals(expected, res.group());
	}
}
