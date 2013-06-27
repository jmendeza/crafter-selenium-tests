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

import org.apache.commons.lang.StringUtils;
import org.craftercms.web.CStudioSeleniumUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.thoughtworks.selenium.Selenium;

/**
 * @author Praveen C Elineni
 *
 */
public class RTETest {
    private static final Logger logger = Logger.getLogger("RTETest.class");

	private final static String SELENIUM_PROPERTIES = "selenium.properties";
    protected Selenium selenium;
    protected WebDriver driver;
    protected static DesiredCapabilities desiredCapabilities;
    protected Properties seleniumProperties = new Properties();
    private StringBuffer verificationErrors = new StringBuffer();
    private String validationString;

    @Before
    public void setUp() throws Exception {
    	seleniumProperties.load(RTETest.class.getClassLoader().getResourceAsStream(SELENIUM_PROPERTIES));

    	desiredCapabilities = new DesiredCapabilities();
        desiredCapabilities.setJavascriptEnabled(true);
        desiredCapabilities.setCapability("takesScreenshot", true);
        desiredCapabilities.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, 
        		                             seleniumProperties.getProperty("craftercms.phantomjs.path"));
        driver = new FirefoxDriver(desiredCapabilities);
    	driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

    	CStudioSeleniumUtil.loginAndEditPage(driver,
									seleniumProperties.getProperty("craftercms.admin.username"), 
									seleniumProperties.getProperty("craftercms.admin.password"),
									seleniumProperties.getProperty("craftercms.rte.widget.edit.page"), 
									seleniumProperties.getProperty("craftercms.rte.widget.content.type"), 
									seleniumProperties.getProperty("craftercms.sitename"));
    }

    @Test
    public void testWidgetControlRequired() throws InterruptedException {
    	logger.info("Widget Required");
    	String inputText = driver.findElement(By.cssSelector("#rte-required .datum")).getAttribute("value");
    	validationString = driver.findElement(By.cssSelector("#rte-required .validation-hint")).getAttribute("class");
    	if (inputText != "") {
    		assertTrue(validationString.contains("cstudio-form-control-valid"));
    	} else {
    		assertTrue(validationString.contains("cstudio-form-control-invalid"));
    	}    	
    }

    @Test
    public void testWidgetControlNotRequired() {
    	logger.info("Widget Not Required");
    	validationString = driver.findElement(By.cssSelector("#rte-not-required .validation-hint")).getAttribute("class");
    	assertEquals(validationString.contains("cstudio-form-control-invalid"), false);
    }

    @Test
    public void testWidgetControlForcePTag() {
    	logger.info("Widget Force p Tag");
    	String inputText = driver.findElement(By.cssSelector("#rte-force-p-tag .datum")).getAttribute("value");
    	assertEquals(StringUtils.countMatches(inputText, "<p>"), 4);
    	
    	logger.info("Widget Force Start with p Tag");
    	assertTrue(inputText.startsWith("<p>"));
    }

    @Test
    public void testWidgetControlForceBRTag() {
    	logger.info("Widget Force br Tag");
    	String inputText = driver.findElement(By.cssSelector("#rte-force-br-tag .datum")).getAttribute("value");
    	assertEquals(StringUtils.countMatches(inputText, "<br>"), 3);
    }

    @Test
    public void testWidgetControlMaxLength() {
    	logger.info("Widget Max Length");
    	String inputText = driver.findElement(By.cssSelector("#rte-max-length .datum")).getAttribute("value");
    	assertTrue(inputText.length() == 10);
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