/**
 * 
 */
package org.craftercms.web.widget;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.craftercms.web.CStudioSeleniumUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.thoughtworks.selenium.Selenium;

/**
 * @author Praveen C Elineni
 *
 */
public class RepeatingGroupTest {
    private static final Logger logger = Logger.getLogger("RepeatingGroupTest.class");

	private final static String SELENIUM_PROPERTIES = "selenium.properties";
    protected Selenium selenium;
    protected WebDriver driver;
    protected static DesiredCapabilities desiredCapabilities;
    protected Properties seleniumProperties = new Properties();
    private StringBuffer verificationErrors = new StringBuffer();

    @Before
    public void setUp() throws Exception {
    	seleniumProperties.load(RepeatingGroupTest.class.getClassLoader().getResourceAsStream(SELENIUM_PROPERTIES));

    	desiredCapabilities = new DesiredCapabilities();
        desiredCapabilities.setJavascriptEnabled(true);
        desiredCapabilities.setCapability("takesScreenshot", true);
        desiredCapabilities.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, 
        		                             seleniumProperties.getProperty("craftercms.phantomjs.path"));
        driver = new PhantomJSDriver(desiredCapabilities);
    	driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

    	CStudioSeleniumUtil.loginAndEditPage(driver, 
    									seleniumProperties.getProperty("craftercms.admin.username"), 
    									seleniumProperties.getProperty("craftercms.admin.password"),
    									seleniumProperties.getProperty("craftercms.repeatgroup.widget.edit.page"), 
   										seleniumProperties.getProperty("craftercms.repeatgroup.widget.content.type"), 
										seleniumProperties.getProperty("craftercms.sitename"));
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

    @After
    public void tearDown() throws Exception {
        CStudioSeleniumUtil.exit(driver);
        String verificationErrorString = verificationErrors.toString();
        if (!"".equals(verificationErrorString)) {
            fail(verificationErrorString);
        }
    }
}