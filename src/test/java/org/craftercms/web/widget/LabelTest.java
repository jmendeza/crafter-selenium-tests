/**
 * 
 */
package org.craftercms.web.widget;

import static org.junit.Assert.*;

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
public class LabelTest {
    private static final Logger logger = Logger.getLogger("LabelTest.class");

	private final static String SELENIUM_PROPERTIES = "selenium.properties";
    protected Selenium selenium;
    protected WebDriver driver;
    protected static DesiredCapabilities desiredCapabilities;
    protected Properties seleniumProperties = new Properties();
    private StringBuffer verificationErrors = new StringBuffer();
    private String validationString;
    private final static String updateString = "label field";

    @Before
    public void setUp() throws Exception {
    	seleniumProperties.load(LabelTest.class.getClassLoader().getResourceAsStream(SELENIUM_PROPERTIES));

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
									seleniumProperties.getProperty("craftercms.label.widget.edit.page"), 
									seleniumProperties.getProperty("craftercms.label.widget.content.type"), 
									seleniumProperties.getProperty("craftercms.sitename"));
    }

    @Test
    public void testWidgetControlRequired() {
        // required - content not entered - invalid
        logger.info("Label Test");
        validationString = driver.findElement(By.cssSelector("#label .datum")).getText();
        assertTrue(validationString.contains(updateString));
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