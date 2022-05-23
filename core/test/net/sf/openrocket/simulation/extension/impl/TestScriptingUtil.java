package net.sf.openrocket.simulation.extension.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import net.sf.openrocket.startup.MockPreferences;

import org.junit.Before;
import org.junit.Test;

public class TestScriptingUtil {
	
	private static final String HASH_JavaScript_foobar = "SHA-256:f6b9d913ad1b1ea3a8bf39951ecfb9ec78fd84c3858ff7d226bf5e9edba0f21f";

	private ScriptingUtil util;

	@Before
	public void setup() {
		util = new ScriptingUtil();
		util.prefs = new MockPreferences();
	}

	/*
	 * Note:  This class assumes that the JRE supports JavaScript scripting.
	 */

	@Test
	public void testGetLanguage() {
		assertEquals(null, util.getLanguage(null));
		assertEquals(null, util.getLanguage(""));
		assertEquals(null, util.getLanguage("foobar"));
		assertEquals("JavaScript", util.getLanguage("graal.js"));
		assertEquals("JavaScript", util.getLanguage("JavaScript"));
		assertEquals("JavaScript", util.getLanguage("javascript"));
		assertEquals("JavaScript", util.getLanguage("ECMAScript"));
		assertEquals("JavaScript", util.getLanguage("js"));
	}


	@Test
	public void testGetLanguages() {
		assertTrue(util.getLanguages().size() >= 1);
		assertTrue(util.getLanguages().contains("JavaScript"));
	}

	@Test
	public void testIsTrustedScript() {
		util.setTrustedScript("graal.js", "foobar", true);
		assertTrue(util.isTrustedScript("graal.js", "foobar"));
		assertTrue(util.isTrustedScript("graal.js", "  \n foobar  \n\t\r"));
		assertFalse(util.isTrustedScript("graal.js", "foo\nbar"));
		assertFalse(util.isTrustedScript("Graal.js", "foobar"));

		// Empty script is always considered trusted
		assertFalse(util.isTrustedScript("foo", null));
		assertTrue(util.isTrustedScript("foo", ""));
		assertTrue(util.isTrustedScript("foo", " \n\r\t "));
	}

	@Test
	public void testSetTrustedScript() {
		util.setTrustedScript("graal.js", " \n foobar \n\r ", true);
		assertTrue(util.prefs.getNode(ScriptingUtil.NODE_ID).getBoolean(HASH_JavaScript_foobar, false));
		util.setTrustedScript("graal.js", " foobar ", false);
		assertTrue(util.prefs.getNode(ScriptingUtil.NODE_ID).getBoolean(HASH_JavaScript_foobar, true));
		assertFalse(util.prefs.getNode(ScriptingUtil.NODE_ID).getBoolean(HASH_JavaScript_foobar, false));
	}

	@Test
	public void testClearTrustedScripts() {
		util.setTrustedScript("graal.js", "foobar", true);
		assertTrue(util.isTrustedScript("graal.js", "foobar"));
		util.clearTrustedScripts();
		assertFalse(util.isTrustedScript("graal.js", "foobar"));
	}

	@Test
	public void testNormalize() {
		assertEquals("foo", ScriptingUtil.normalize("foo"));
		assertEquals("foo  bar", ScriptingUtil.normalize("  \n\r\t foo \r bar  \n\t\r "));
	}

	@Test
	public void testHash() {
		assertEquals("SHA-256:12e6a78889b96a16d305b8e4af81119545f89eccba5fb37cc3a1ec2c53eab514", ScriptingUtil.hash("JS", ""));
		assertEquals("SHA-256:000753e5deb2d8fa80e602ca03bcdb8e12a6b14b2b4a4d0abecdc976ad26e3ef", ScriptingUtil.hash("foo", "1165"));
		assertEquals(HASH_JavaScript_foobar, ScriptingUtil.hash("graal.js", "foobar"));
	}
}
