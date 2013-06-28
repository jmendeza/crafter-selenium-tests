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
public class CheckboxTest extends WidgetBaseTest {
    private static final Logger logger = Logger.getLogger("CheckboxTest.class");
    private String validationString;

    @Before
    public void setUp() throws Exception {
    	super.setUp("craftercms.checkbox.widget.edit.page", "craftercms.checkbox.widget.content.type");
    }

    @Test
    public void testWidgetControlRequired() {
        // required - content not entered - invalid (default checkbox is checked)
        logger.info("Checkbox Required Content Not Entered");
        driver.findElement(By.cssSelector("#checkbox-required .datum")).click();
        driver.findElement(By.cssSelector("#internal-name .datum")).clear();
        validationString = driver.findElement(By.cssSelector("#checkbox-required .validation-hint")).getAttribute("class");
        assertTrue(validationString.contains("cstudio-form-control-invalid"));

        // required - content entered - valid
        logger.info("Checkbox Required Content Entered");
        driver.findElement(By.cssSelector("#checkbox-required .datum")).click();
        driver.findElement(By.cssSelector("#internal-name .datum")).clear();
        validationString = driver.findElement(By.cssSelector("#checkbox-required .validation-hint")).getAttribute("class");
        assertTrue(validationString.contains("cstudio-form-control-valid"));
    }

    @Test
    public void testWidgetControlNotRequired() {
        // not required - content not entered - valid
        logger.info("Checkbox Not Required Content Not Entered");
        driver.findElement(By.cssSelector("#checkbox-not-required .datum")).click();
        driver.findElement(By.cssSelector("#internal-name .datum")).clear();
        validationString = driver.findElement(By.cssSelector("#checkbox-not-required .validation-hint")).getAttribute("class");
        assertEquals(validationString.contains("cstudio-form-control-invalid"), false);

        // not required - content entered - valid
        logger.info("Checkbox Not Required Content Entered");
        driver.findElement(By.cssSelector("#checkbox-not-required .datum")).click();
        driver.findElement(By.cssSelector("#internal-name .datum")).clear();
        validationString = driver.findElement(By.cssSelector("#checkbox-not-required .validation-hint")).getAttribute("class");
        assertEquals(validationString.contains("cstudio-form-control-invalid"), false);
    }

    @Test
    public void testCheckboxControlReadonly() {
        // read-only
        logger.info("Checkbox Readonly");
        assertEquals(driver.findElement(By.cssSelector("#checkbox-readonly .datum")).getAttribute("disabled"), "true");    	
    }

    @Test
    public void testCheckboxDefaultValue() {
        // checkbox default value selected
        logger.info("Checkbox default value");
    	validationString = driver.findElement(By.cssSelector("#checkbox-default-value .datum")).getAttribute("value");
        driver.findElement(By.cssSelector("#internal-name .datum")).clear();

       	assertEquals(validationString, "on");
    }
    
    @Test
    public void testCheckboxHelpField() {
    	// checkbox help span exists
    	validationString = driver.findElement(By.cssSelector("#checkbox-required .cstudio-form-field-help")).getText();
    	assertEquals(validationString, " ");
    }
}