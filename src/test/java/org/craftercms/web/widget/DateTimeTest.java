/**
 * 
 */
package org.craftercms.web.widget;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import org.craftercms.web.WidgetBaseTest;
import org.craftercms.web.CStudioSeleniumUtil;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * @author Praveen C Elineni
 *
 */
public class DateTimeTest extends WidgetBaseTest {
    private static final Logger logger = Logger.getLogger("DateTimeTest.class");

    private String validationString;
    private static String updateString = "0700";
    private static int dateString;

	@SuppressWarnings("deprecation")
	@Before
    public void setUp() throws Exception {
		super.setUp("craftercms.datetime.widget.edit.page", "craftercms.datetime.widget.content.type");
    	Date todayDate = new Date();
    	dateString = todayDate.getDate();
    }

    @Test
    public void testWidgetControlRequired() {
        logger.info("Date Required Entered");
        CStudioSeleniumUtil.clickOn(driver, By.cssSelector("#datetime-required .date"));
        CStudioSeleniumUtil.clickOn(driver, By.linkText(String.valueOf(dateString)));
        driver.findElement(By.cssSelector("#internal-name .datum")).click();
        validationString = driver.findElement(By.cssSelector("#datetime-required .validation-hint")).getAttribute("class");
        assertTrue(validationString.contains("cstudio-form-control-valid"));

        logger.info("Time Required Not Entered");
        driver.findElement(By.cssSelector("#datetime-required .time")).clear();
        driver.findElement(By.cssSelector("#internal-name .datum")).clear();
        validationString = driver.findElement(By.cssSelector("#datetime-required .validation-hint")).getAttribute("class");
        assertTrue(validationString.contains("cstudio-form-control-invalid"));

        logger.info("Time Required Entered");
        driver.findElement(By.cssSelector("#datetime-required .time")).sendKeys(updateString);
        driver.findElement(By.cssSelector("#internal-name .datum")).click();
        validationString = driver.findElement(By.cssSelector("#datetime-required .validation-hint")).getAttribute("class");
        assertTrue(validationString.contains("cstudio-form-control-valid"));
    }

    @Test
    public void testWidgetControlNotRequired() {
        // not required - content not entered - valid
        logger.info("Datetime Not Required Content Not Entered");
        driver.findElement(By.cssSelector("#datetime-not-required .time")).clear();
        driver.findElement(By.cssSelector("#internal-name .datum")).click();
        validationString = driver.findElement(By.cssSelector("#datetime-not-required .validation-hint")).getAttribute("class");
        assertEquals(validationString.contains("cstudio-form-control-invalid"), false);

        // not required - content entered - valid
        logger.info("Input Not Required Content Entered");
        driver.findElement(By.cssSelector("#datetime-not-required .datum")).sendKeys(updateString);
        driver.findElement(By.cssSelector("#internal-name .datum")).click();
        validationString = driver.findElement(By.cssSelector("#datetime-not-required .validation-hint")).getAttribute("class");
        assertEquals(validationString.contains("cstudio-form-control-invalid"), false);    	
    }

    @Test
    public void testWidgetControlReadonly() {
        // read-only
        logger.info("Widget Readonly");
        assertEquals(driver.findElement(By.cssSelector("#date-time-readonly .date")).getAttribute("disabled"), "true");
        assertEquals(driver.findElement(By.cssSelector("#date-time-readonly .time")).getAttribute("disabled"), "true");
    }

    @Test
    public void testWidgetControlDateOnly() {
        // Date Only Widget
        logger.info("Widget Date Only");
        List<WebElement> elements = driver.findElements(By.cssSelector("#date-only .date"));
        assertEquals(elements.size(), 1);
        
        elements = driver.findElements(By.cssSelector("#date-only .time"));
        assertEquals(elements.size(), 0);
    }

    @Test
    public void testWidgetControlTimeOnly() {
        // Time Only Widget
        logger.info("Widget Time Only");
        List<WebElement> elements = driver.findElements(By.cssSelector("#time-only .date"));
        assertEquals(elements.size(), 0);
        
        elements = driver.findElements(By.cssSelector("#time-only .time"));
        assertEquals(elements.size(), 1);
    }

    @Test
    public void testWidgetControlDateAndTime() {
        // Date and Time Widget
        logger.info("Widget Date and Time");
        List<WebElement> elements = driver.findElements(By.cssSelector("#date-time-allow-past .date"));
        assertEquals(elements.size(), 1);
        
        elements = driver.findElements(By.cssSelector("#date-time-allow-past .time"));
        assertEquals(elements.size(), 1);
    }

    @Test
    public void testAllowPastDate() throws InterruptedException {
        logger.info("Past Date Entered");
        int prevDate = dateString == 1 ? dateString : dateString - 1; 
        CStudioSeleniumUtil.clickOn(driver, By.cssSelector("#date-time-allow-past .date"));
        CStudioSeleniumUtil.clickOn(driver, By.linkText(String.valueOf(prevDate)));
        driver.findElement(By.cssSelector("#internal-name .datum")).click();

        validationString = driver.findElement(By.cssSelector("#date-time-allow-past .validation-hint")).getAttribute("class");
        assertEquals(validationString.contains("cstudio-form-control-invalid"), false);
   }
}