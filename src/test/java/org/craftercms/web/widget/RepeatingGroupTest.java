/**
 * 
 */
package org.craftercms.web.widget;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
public class RepeatingGroupTest extends WidgetBaseTest {	
	// TODO: Cleanup test cases after id's attached to widget

	private static final Logger logger = Logger.getLogger("RepeatingGroupTest.class");

    @Before
    public void setUp() throws Exception {
    	super.setUp("craftercms.repeatgroup.widget.edit.page", "craftercms.repeatgroup.widget.content.type");
    }

    @Test
    public void testWidgetControlMinOccurrence() {
        logger.info("Repeat Group Basic No Elements");
        List<WebElement> elements = driver.findElements(By.xpath("//a[text()='Add First Item']"));
        assertEquals(elements.size(), 1);

        elements = driver.findElements(By.cssSelector("#cstudio-form-repeat-container .cstudio-form-repeat-control"));
        assertEquals(elements.size(), 0);
        
        elements = driver.findElements(By.cssSelector("#cstudio-form-repeat-container .cstudio-form-field-container"));
        assertEquals(elements.size(), 0);
    }

    @Test
    public void testWidgetControlAddElements() {
        logger.info("Repeat Group Add Elements");
        int initialCount = driver.findElements(By.className("cstudio-form-field-container")).size();

        List<WebElement> elements = driver.findElements(By.xpath("//a[text()='Add First Item']"));
        if (elements.size() == 1) {
        	// click on add first item
        	CStudioSeleniumUtil.clickOn(driver, By.xpath("//a[text()='Add First Item']"));
        	assertEquals(driver.findElements(By.className("cstudio-form-field-container")).size(), initialCount + 1);
        	initialCount ++;
        }

        // click add another
    	CStudioSeleniumUtil.clickOn(driver, By.xpath("//a[text()='Add Another']"));
    	assertEquals(driver.findElements(By.className("cstudio-form-field-container")).size(), initialCount + 1);
    	initialCount ++;

        // click add another
    	CStudioSeleniumUtil.clickOn(driver, By.xpath("//a[text()='Add Another']"));
    	assertEquals(driver.findElements(By.className("cstudio-form-field-container")).size(), initialCount + 1);
    	initialCount ++;
    }
    
    @Test
    public void testWidgetControlDeleteElements() {
        logger.info("Repeat Group Delete Elements");
        int initialCount = driver.findElements(By.className("cstudio-form-field-container")).size();

        List<WebElement> elements = driver.findElements(By.xpath("//a[text()='Add First Item']"));
        if (elements.size() == 1) {
        	// add first element
        	CStudioSeleniumUtil.clickOn(driver, By.xpath("//a[text()='Add First Item']"));
        	assertEquals(driver.findElements(By.className("cstudio-form-field-container")).size(), initialCount + 1);
        	initialCount ++;
        }

        // add 2 more elements
    	CStudioSeleniumUtil.clickOn(driver, By.xpath("//a[text()='Add Another']"));
    	assertEquals(driver.findElements(By.className("cstudio-form-field-container")).size(), initialCount + 1);
    	initialCount ++;
    	CStudioSeleniumUtil.clickOn(driver, By.xpath("//a[text()='Add Another']"));
    	assertEquals(driver.findElements(By.className("cstudio-form-field-container")).size(), initialCount + 1);
    	initialCount ++;
    	
    	// delete 3 elements
    	CStudioSeleniumUtil.clickOn(driver, By.xpath("//a[text()='Delete']"));
    	assertEquals(driver.findElements(By.className("cstudio-form-field-container")).size(), initialCount - 1);
    	initialCount --;
    	
    	CStudioSeleniumUtil.clickOn(driver, By.xpath("//a[text()='Delete']"));
    	assertEquals(driver.findElements(By.className("cstudio-form-field-container")).size(), initialCount - 1);
    	initialCount --;

    	CStudioSeleniumUtil.clickOn(driver, By.xpath("//a[text()='Delete']"));
    	assertEquals(driver.findElements(By.className("cstudio-form-field-container")).size(), initialCount - 1);
    	initialCount --;
    }

    @Test
    public void testWidgetControlMaxOccurrence() {
        logger.info("Repeat Group Max Elements");
    	int initialCount = driver.findElements(By.className("cstudio-form-field-container")).size();

        List<WebElement> elements = driver.findElements(By.xpath("//a[text()='Add First Item']"));
        if (elements.size() == 1) {
        	// add first element
        	CStudioSeleniumUtil.clickOn(driver, By.xpath("//a[text()='Add First Item']"));
        	assertEquals(driver.findElements(By.className("cstudio-form-field-container")).size(), initialCount + 1);
        	initialCount ++;
        }

        // add 2 more elements
    	CStudioSeleniumUtil.clickOn(driver, By.xpath("//a[text()='Add Another']"));
    	assertEquals(driver.findElements(By.className("cstudio-form-field-container")).size(), initialCount + 1);
    	initialCount ++;

    	CStudioSeleniumUtil.clickOn(driver, By.xpath("//a[text()='Add Another']"));
    	assertEquals(driver.findElements(By.className("cstudio-form-field-container")).size(), initialCount + 1);
    	initialCount ++;

    	assertTrue(driver.findElement(By.xpath("//a[text()='Add Another']")).getAttribute("class").contains("cstudio-form-repeat-control-disabled"));
    }
}