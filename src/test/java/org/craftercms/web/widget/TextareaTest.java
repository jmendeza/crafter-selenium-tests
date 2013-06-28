/**
 * 
 */
package org.craftercms.web.widget;

import static org.junit.Assert.assertEquals;
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
public class TextareaTest extends WidgetBaseTest {
    private static final Logger logger = Logger.getLogger("TextareaTest.class");

    private String validationString;
    private final static String updateString = "Update Value";
    private final static String updateMaxString = "01234567890123456789012345678901234567890123fifty0123456789012345678901234567890123456789012hundred";

    @Before
    public void setUp() throws Exception {
    	super.setUp("craftercms.textarea.widget.edit.page", "craftercms.textarea.widget.content.type");
    }

    @Test
    public void testWidgetControlRequired() {
        // required - content not entered - invalid
        logger.info("Textarea Required Content Not Entered");
        driver.findElement(By.cssSelector("#textarea-required .datum")).clear();
        driver.findElement(By.cssSelector("#internal-name .datum")).clear();
        validationString = driver.findElement(By.cssSelector("#textarea-required .validation-hint")).getAttribute("class");
        assertTrue(validationString.contains("cstudio-form-control-invalid"));

        // required - content entered - valid
        logger.info("Textarea Required Content Entered");
        driver.findElement(By.cssSelector("#textarea-required .datum")).sendKeys(updateString);
        driver.findElement(By.cssSelector("#internal-name .datum")).clear();
        validationString = driver.findElement(By.cssSelector("#textarea-required .validation-hint")).getAttribute("class");
        assertTrue(validationString.contains("cstudio-form-control-valid"));
    }

    @Test
    public void testTextareaControlNotRequired() {
        // not required - content not entered - valid
        logger.info("Textarea Not Required Content Not Entered");
        driver.findElement(By.cssSelector("#textarea-not-required .datum")).clear();
        driver.findElement(By.cssSelector("#internal-name .datum")).clear();
        validationString = driver.findElement(By.cssSelector("#textarea-not-required .validation-hint")).getAttribute("class");
        assertEquals(validationString.contains("cstudio-form-control-invalid"), false);

        // not required - content entered - valid
        logger.info("Textarea Not Required Content Entered");
        driver.findElement(By.cssSelector("#textarea-not-required .datum")).sendKeys(updateString);
        driver.findElement(By.cssSelector("#internal-name .datum")).clear();
        validationString = driver.findElement(By.cssSelector("#textarea-not-required .validation-hint")).getAttribute("class");
        assertEquals(validationString.contains("cstudio-form-control-invalid"), false);    	
    }
    
    @Test
    public void testTextareaControlReadonly() {
        // read-only
        logger.info("Textarea Readonly");
        assertEquals(driver.findElement(By.cssSelector("#textarea-readonly .datum")).getAttribute("disabled"), "true");    	
    }

    @Test
    public void testTextareaControlCount() {
        // Textarea count check
        logger.info("Textarea Count");
        String widgetValue = driver.findElement(By.cssSelector("#textarea-count .datum")).getAttribute("value");
        driver.findElement(By.cssSelector("#internal-name .datum")).clear();
        String widgetCount = driver.findElement(By.cssSelector("#textarea-count .char-count")).getText();
        if (widgetValue != null && widgetCount != null && widgetCount.indexOf(" /") != -1) {
        	assertEquals(String.valueOf(widgetValue.length()), widgetCount.substring(0, widgetCount.indexOf(" /")));
        }
    }

    @Test
    public void testTextareaControlMaxLimit() {
        // count check trim after max limit
        logger.info("Textarea Count trim after max limit");
    	driver.findElement(By.cssSelector("#textarea-count .datum")).sendKeys(updateMaxString);
        driver.findElement(By.cssSelector("#internal-name .datum")).clear();

        String widgetValue = driver.findElement(By.cssSelector("#textarea-count .datum")).getAttribute("value");
        String widgetCount = driver.findElement(By.cssSelector("#textarea-count .char-count")).getText();
        if (widgetValue != null && widgetCount != null && widgetCount.indexOf(" / ") != -1) {
        	assertEquals(String.valueOf(widgetValue.length()), widgetCount.substring(widgetCount.indexOf(" / ") + 3));
        }
    }

    @Test
    public void testTextAreaRowsColumns() {
    	// check text area pre-set columns and rows
    	logger.info("Textarea pre-set columns and rows");
    	String rows = driver.findElement(By.cssSelector("#textarea-noresize .datum")).getAttribute("rows");
    	String cols = driver.findElement(By.cssSelector("#textarea-noresize .datum")).getAttribute("cols");
    	
    	assertEquals(rows, "10");
    	assertEquals(cols, "40");
    }
}