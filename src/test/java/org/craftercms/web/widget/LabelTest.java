/**
 * 
 */
package org.craftercms.web.widget;

import static org.junit.Assert.assertTrue;

import java.util.logging.Logger;

import org.craftercms.web.WidgetBaseTest;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;

/**
 * @author Praveen C Elineni
 *
 */
public class LabelTest extends WidgetBaseTest {
	private static final Logger logger = Logger.getLogger("LabelTest.class");

    private String validationString;
    private final static String updateString = "label field";

    @Before
    public void setUp() throws Exception {
    	super.setUp("craftercms.label.widget.edit.page", "craftercms.label.widget.content.type");
    }

    @Test
    public void testWidgetControlRequired() {
        // required - content not entered - invalid
        logger.info("Label Test");
        validationString = driver.findElement(By.cssSelector("#label .datum")).getText();
        assertTrue(validationString.contains(updateString));
    }
}