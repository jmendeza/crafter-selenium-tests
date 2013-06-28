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
import org.openqa.selenium.support.ui.Select;

/**
 * @author Praveen C Elineni
 *
 */
public class DropdownTest extends WidgetBaseTest {
    private static final Logger logger = Logger.getLogger("DropdownTest.class");

    private String validationString;
    private final static String updateString = "one";
    private final static String updateString1 = "";

    @Before
    public void setUp() throws Exception {
    	super.setUp("craftercms.dropdown.widget.edit.page", "craftercms.dropdown.widget.content.type");
    }

    @Test
    public void testWidgetControlRequired() {
        // required - content not entered - invalid
        logger.info("Dropdown Required Content Not Entered");
        Select select = new Select(driver.findElement(By.cssSelector("#dropdown-required .datum")));
        select.selectByValue(updateString1);
        validationString = driver.findElement(By.cssSelector("#dropdown-required .validation-hint")).getAttribute("class");
        assertTrue(validationString.contains("cstudio-form-control-invalid"));

        // required - content entered - valid
        logger.info("Dropdown Required Content Entered");
        select.selectByValue(updateString);
        driver.findElement(By.cssSelector("#internal-name .datum")).clear();
        validationString = driver.findElement(By.cssSelector("#dropdown-required .validation-hint")).getAttribute("class");
        assertTrue(validationString.contains("cstudio-form-control-valid"));
    }

    @Test
    public void testWidgetControlNotRequired() {
    	// not required - content not entered
        Select select = new Select(driver.findElement(By.cssSelector("#dropdown-not-required .datum")));
        select.selectByValue(updateString1);
        validationString = driver.findElement(By.cssSelector("#dropdown-not-required .validation-hint")).getAttribute("class");
        assertEquals(validationString.contains("cstudio-form-control-invalid"), false);

    	// not required - content entered - valid
        logger.info("Dropdown Not Required Content Entered");
        select.selectByValue(updateString);
        driver.findElement(By.cssSelector("#internal-name .datum")).clear();
        validationString = driver.findElement(By.cssSelector("#dropdown-not-required .validation-hint")).getAttribute("class");
        assertEquals(validationString.contains("cstudio-form-control-invalid"), false);
    }

    @Test
    public void testWidgetControlReadonly() {
        // read-only
        logger.info("Dropdown Readonly");
        assertEquals(driver.findElement(By.cssSelector("#dropdown-readonly .datum")).getAttribute("disabled"), "true");    	
    }

    @Test
    public void testWidgetControlAllowEmpty() {
    	// allow empty value in dropdown
        Select select = new Select(driver.findElement(By.cssSelector("#dropdown-allow-empty-value .datum")));
        select.selectByValue(updateString1);

        validationString = driver.findElement(By.cssSelector("#dropdown-allow-empty-value .validation-hint")).getAttribute("class");
        assertTrue(validationString.contains("cstudio-form-control-valid"));
    }
}