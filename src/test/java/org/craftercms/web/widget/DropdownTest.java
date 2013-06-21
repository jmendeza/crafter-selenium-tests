/**
 * 
 */
package org.craftercms.web.widget;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.craftercms.web.CStudioSeleniumUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.Select;

import com.thoughtworks.selenium.Selenium;

/**
 * @author Praveen C Elineni
 *
 */
public class DropdownTest {
    private static final Logger logger = Logger.getLogger("DropdownTest.class");

	private final static String SELENIUM_PROPERTIES = "selenium.properties";
    protected Selenium selenium;
    protected WebDriver driver;
    protected static DesiredCapabilities desiredCapabilities;
    protected Properties seleniumProperties = new Properties();
    private StringBuffer verificationErrors = new StringBuffer();
    private String validationString;
    private final static String updateString = "one";
    private final static String updateString1 = "";

    @Before
    public void setUp() throws Exception {
    	seleniumProperties.load(DropdownTest.class.getClassLoader().getResourceAsStream(SELENIUM_PROPERTIES));

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
									seleniumProperties.getProperty("craftercms.dropdown.widget.edit.page"), 
									seleniumProperties.getProperty("craftercms.dropdown.widget.content.type"), 
									seleniumProperties.getProperty("craftercms.sitename"));
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

    @After
    public void tearDown() throws Exception {
        CStudioSeleniumUtil.exit(driver);
        String verificationErrorString = verificationErrors.toString();
        if (!"".equals(verificationErrorString)) {
            fail(verificationErrorString);
        }
    }
}