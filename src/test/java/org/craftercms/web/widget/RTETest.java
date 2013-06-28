/**
 * 
 */
package org.craftercms.web.widget;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;
import org.craftercms.web.WidgetBaseTest;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;

/**
 * @author Praveen C Elineni
 *
 */
public class RTETest extends WidgetBaseTest {
	// TODO: Cleanup test cases after phantomJS research with tinyMCE / iframes

	private static final Logger logger = Logger.getLogger("RTETest.class");

    private String validationString;

    @Before
    public void setUp() throws Exception {
    	super.setUp("craftercms.rte.widget.edit.page", "craftercms.rte.widget.content.type");
    }

    @Test
    public void testWidgetControlRequired() throws InterruptedException {
    	logger.info("Widget Required");
    	String inputText = driver.findElement(By.cssSelector("#rte-required .datum")).getAttribute("value");
    	validationString = driver.findElement(By.cssSelector("#rte-required .validation-hint")).getAttribute("class");
    	if (inputText != "") {
    		assertTrue(validationString.contains("cstudio-form-control-valid"));
    	} else {
    		assertTrue(validationString.contains("cstudio-form-control-invalid"));
    	}    	
    }

    @Test
    public void testWidgetControlNotRequired() {
    	logger.info("Widget Not Required");
    	validationString = driver.findElement(By.cssSelector("#rte-not-required .validation-hint")).getAttribute("class");
    	assertEquals(validationString.contains("cstudio-form-control-invalid"), false);
    }

    @Test
    public void testWidgetControlForcePTag() {
    	logger.info("Widget Force p Tag");
    	String inputText = driver.findElement(By.cssSelector("#rte-force-p-tag .datum")).getAttribute("value");
    	assertEquals(StringUtils.countMatches(inputText, "<p>"), 4);
    	
    	logger.info("Widget Force Start with p Tag");
    	assertTrue(inputText.startsWith("<p>"));
    }

    @Test
    public void testWidgetControlForceBRTag() {
    	logger.info("Widget Force br Tag");
    	String inputText = driver.findElement(By.cssSelector("#rte-force-br-tag .datum")).getAttribute("value");
    	assertEquals(StringUtils.countMatches(inputText, "<br>"), 3);
    }

    @Test
    public void testWidgetControlMaxLength() {
    	logger.info("Widget Max Length");
    	String inputText = driver.findElement(By.cssSelector("#rte-max-length .datum")).getAttribute("value");
    	assertTrue(inputText.length() == 10);
    }
}