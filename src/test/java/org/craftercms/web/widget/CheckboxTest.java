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

import com.thoughtworks.selenium.Selenium;

/**
 * @author Praveen C Elineni
 *
 */
public class CheckboxTest {
    private static final Logger logger = Logger.getLogger("CheckboxTest.class");

	private final static String SELENIUM_PROPERTIES = "selenium.properties";
    protected Selenium selenium;
    protected WebDriver driver;
    protected static DesiredCapabilities desiredCapabilities;
    protected Properties seleniumProperties = new Properties();
    private StringBuffer verificationErrors = new StringBuffer();
    private String validationString;

    @Before
    public void setUp() throws Exception {
    	seleniumProperties.load(CheckboxTest.class.getClassLoader().getResourceAsStream(SELENIUM_PROPERTIES));

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
    									seleniumProperties.getProperty("craftercms.checkbox.widget.edit.page"), 
   										seleniumProperties.getProperty("craftercms.checkbox.widget.content.type"), 
										seleniumProperties.getProperty("craftercms.sitename"));
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

    @After
    public void tearDown() throws Exception {
        CStudioSeleniumUtil.exit(driver);
        String verificationErrorString = verificationErrors.toString();
        if (!"".equals(verificationErrorString)) {
            fail(verificationErrorString);
        }
    }
}