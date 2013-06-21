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
public class InputTest {
    private static final Logger logger = Logger.getLogger("InputTest.class");

	private final static String SELENIUM_PROPERTIES = "selenium.properties";
    protected Selenium selenium;
    protected WebDriver driver;
    protected static DesiredCapabilities desiredCapabilities;
    protected Properties seleniumProperties = new Properties();
    private StringBuffer verificationErrors = new StringBuffer();
    private String validationString;
    private final static String updateString = "Update Value";
    private final static String updateMaxString = "01234567890123456789012345678901234567890123fifty0123456789012345678901234567890123456789012hundred";

    @Before
    public void setUp() throws Exception {
    	seleniumProperties.load(InputTest.class.getClassLoader().getResourceAsStream(SELENIUM_PROPERTIES));

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
									seleniumProperties.getProperty("craftercms.input.widget.edit.page"), 
									seleniumProperties.getProperty("craftercms.input.widget.content.type"), 
									seleniumProperties.getProperty("craftercms.sitename"));
    }
    
    @Test
    public void testInputControlRequired() {
        // required - content not entered - invalid
        logger.info("Input Required Content Not Entered");
        driver.findElement(By.cssSelector("#input-required .datum")).clear();
        driver.findElement(By.cssSelector("#internal-name .datum")).clear();
        validationString = driver.findElement(By.cssSelector("#input-required .validation-hint")).getAttribute("class");
        assertTrue(validationString.contains("cstudio-form-control-invalid"));

        // required - content entered - valid
        logger.info("Input Required Content Entered");
        driver.findElement(By.cssSelector("#input-required .datum")).sendKeys(updateString);
        driver.findElement(By.cssSelector("#internal-name .datum")).clear();
        validationString = driver.findElement(By.cssSelector("#input-required .validation-hint")).getAttribute("class");
        assertTrue(validationString.contains("cstudio-form-control-valid"));
    }
    
    @Test
    public void testInputControlNotRequired() {
        // not required - content not entered - valid
        logger.info("Input Not Required Content Not Entered");
        driver.findElement(By.cssSelector("#input-not-required .datum")).clear();
        driver.findElement(By.cssSelector("#internal-name .datum")).clear();
        validationString = driver.findElement(By.cssSelector("#input-not-required .validation-hint")).getAttribute("class");
        assertEquals(validationString.contains("cstudio-form-control-invalid"), false);

        // not required - content entered - valid
        logger.info("Input Not Required Content Entered");
        driver.findElement(By.cssSelector("#input-not-required .datum")).sendKeys(updateString);
        driver.findElement(By.cssSelector("#internal-name .datum")).clear();
        validationString = driver.findElement(By.cssSelector("#input-not-required .validation-hint")).getAttribute("class");
        assertEquals(validationString.contains("cstudio-form-control-invalid"), false);    	
    }
    
    @Test
    public void testInputControlReadonly() {
        // read-only
        logger.info("Input Readonly");
        assertEquals(driver.findElement(By.cssSelector("#input-readonly .datum")).getAttribute("disabled"), "true");    	
    }

    @Test
    public void testInputControlCount() {
        // count check
        logger.info("Input Count");
        String inputValue = driver.findElement(By.cssSelector("#input-count .datum")).getAttribute("value");
        driver.findElement(By.cssSelector("#internal-name .datum")).clear();
        String inputCount = driver.findElement(By.cssSelector("#input-count .char-count")).getText();
        if (inputValue != null && inputCount != null && inputCount.indexOf(" /") != -1) {
        	assertEquals(String.valueOf(inputValue.length()), inputCount.substring(0, inputCount.indexOf(" /")));
        }
    }

    @Test
    public void testInputControlMaxLimit() {
        // count check trim after max limit
        logger.info("Input Count trim after max limit");
    	driver.findElement(By.cssSelector("#input-count .datum")).sendKeys(updateMaxString);
        driver.findElement(By.cssSelector("#internal-name .datum")).clear();

        String inputValue = driver.findElement(By.cssSelector("#input-count .datum")).getAttribute("value");
        String inputCount = driver.findElement(By.cssSelector("#input-count .char-count")).getText();
        if (inputValue != null && inputCount != null && inputCount.indexOf(" / ") != -1) {
        	assertEquals(String.valueOf(inputValue.length()), inputCount.substring(inputCount.indexOf(" / ") + 3));
        }
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