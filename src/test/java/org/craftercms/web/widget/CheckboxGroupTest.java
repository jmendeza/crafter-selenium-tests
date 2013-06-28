/**
 * 
 */
package org.craftercms.web.widget;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.logging.Logger;

import org.craftercms.web.CStudioSeleniumUtil;
import org.craftercms.web.WidgetBaseTest;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;

/**
 * @author Praveen C Elineni
 *
 */
public class CheckboxGroupTest extends WidgetBaseTest {
    private static final Logger logger = Logger.getLogger("CheckboxGroupTest.class");

    private String validationString;

    @Before
    public void setUp() throws Exception {
    	super.setUp("craftercms.checkboxgroup.widget.edit.page", "craftercms.checkboxgroup.widget.content.type");
    }

    @Test
    public void testWidgetControlRequired() {
        // required - content not entered - invalid (default checkbox is checked)
        logger.info("Checkbox Group Required Content Not Entered");
        CStudioSeleniumUtil.clickOn(driver, By.id("checkbox-required-all"));
        if (!driver.findElement(By.cssSelector("#checkbox-required .datum")).getAttribute("value").equals("[]")) {
        	CStudioSeleniumUtil.clickOn(driver, By.id("checkbox-required-all"));
        }

        driver.findElement(By.cssSelector("#internal-name .datum")).click();
        validationString = driver.findElement(By.cssSelector("#checkbox-required .validation-hint")).getAttribute("class");
        assertTrue(validationString.contains("cstudio-form-control-invalid"));

        // required - content entered - valid
        logger.info("Checkbox Group Required Content Entered");
        CStudioSeleniumUtil.clickOn(driver, By.id("checkbox-required-all"));
        driver.findElement(By.cssSelector("#internal-name .datum")).click();
        validationString = driver.findElement(By.cssSelector("#checkbox-required .validation-hint")).getAttribute("class");
        assertEquals(validationString.contains("cstudio-form-control-invalid"), false);
    }

    @Test
    public void testWidgetControlNotRequired() {
        // not required - content not entered - valid
        logger.info("Checkbox Group Not Required Content Not Entered");
        CStudioSeleniumUtil.clickOn(driver, By.id("checkbox-not-required-all"));
        if (driver.findElement(By.cssSelector("#checkbox-not-required .datum")).getAttribute("value") != "[]") {
        	CStudioSeleniumUtil.clickOn(driver, By.id("checkbox-not-required-all"));
        }
        driver.findElement(By.cssSelector("#internal-name .datum")).click();

        validationString = driver.findElement(By.cssSelector("#checkbox-not-required .validation-hint")).getAttribute("class");
        assertEquals(validationString.contains("cstudio-form-control-invalid"), false);

        // not required - content entered - valid
        logger.info("Checkbox Group Not Required Content Entered");
    	if (driver.findElement(By.cssSelector("#checkbox-not-required .datum")).getAttribute("value").equals("[]")) {
    		CStudioSeleniumUtil.clickOn(driver, By.id("checkbox-not-required-all"));
    	}
        driver.findElement(By.cssSelector("#internal-name .datum")).click();

        validationString = driver.findElement(By.cssSelector("#checkbox-not-required .validation-hint")).getAttribute("class");
        assertEquals(validationString.contains("cstudio-form-control-invalid"), false);
    }

    @Test
    public void testWidgetSelectDeSelectAll() {
    	logger.info("de-select all");
        CStudioSeleniumUtil.clickOn(driver, By.id("checkbox-required-all"));
        if (driver.findElement(By.cssSelector("#checkbox-required .datum")).getAttribute("value") != "[]") {
        	CStudioSeleniumUtil.clickOn(driver, By.id("checkbox-required-all"));
        }
        driver.findElement(By.cssSelector("#internal-name .datum")).click();
        assertEquals(driver.findElement(By.cssSelector("#checkbox-required .datum")).getAttribute("value").equals("[]"), true);

        logger.info("select all");
    	if (driver.findElement(By.cssSelector("#checkbox-required .datum")).getAttribute("value").equals("[]")) {
    		CStudioSeleniumUtil.clickOn(driver, By.id("checkbox-required-all"));
    	}
        driver.findElement(By.cssSelector("#internal-name .datum")).click();
        assertEquals(driver.findElement(By.cssSelector("#checkbox-required .datum")).getAttribute("value").equals("[]"), false);
    }

    @Test
    public void testCheckboxControlReadonly() {
        // read-only
        logger.info("Checkbox Group Readonly");
        assertEquals(driver.findElement(By.id("checkbox-readonly-1")).getAttribute("disabled"), "true");    	
    }

    @Test
    public void testCheckboxDefaultValue() {
        // checkbox default value selected
        logger.info("Checkbox default value");
    	validationString = driver.findElement(By.cssSelector("#checkbox-default-value .datum")).getAttribute("value");
        driver.findElement(By.cssSelector("#internal-name .datum")).click();

       	assertEquals(validationString, "[{ \"key\": \"2\", \"value_s\":\"2\"}]");
    }

    @Test
    public void testCheckboxMinCountCheck() {
        logger.info("Checkbox Group Min Count");
        CStudioSeleniumUtil.clickOn(driver, By.id("checkbox-min-check-all"));
        if (driver.findElement(By.cssSelector("#checkbox-min-check .datum")).getAttribute("value") != "[]") {
        	CStudioSeleniumUtil.clickOn(driver, By.id("checkbox-min-check-all"));
        }
        driver.findElement(By.cssSelector("#internal-name .datum")).click();
        logger.info("select one and check validation - fail");
        CStudioSeleniumUtil.clickOn(driver, By.id("checkbox-min-check-1"));
        driver.findElement(By.cssSelector("#internal-name .datum")).click();
        validationString = driver.findElement(By.cssSelector("#checkbox-min-check .validation-hint")).getAttribute("class");
        assertTrue(validationString.contains("cstudio-form-control-invalid"));

        logger.info("select one and check validation - pass");
        CStudioSeleniumUtil.clickOn(driver, By.id("checkbox-min-check-2"));
        driver.findElement(By.cssSelector("#internal-name .datum")).click();
        validationString = driver.findElement(By.cssSelector("#checkbox-min-check .validation-hint")).getAttribute("class");
        assertEquals(validationString.contains("cstudio-form-control-invalid"), false);
    }
}