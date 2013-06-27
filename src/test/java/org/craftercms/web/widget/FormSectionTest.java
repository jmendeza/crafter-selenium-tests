/**
 * 
 */
package org.craftercms.web.widget;

import static org.junit.Assert.assertEquals;
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
public class FormSectionTest {
    private static final Logger logger = Logger.getLogger("FormSectionTest.class");

	private final static String SELENIUM_PROPERTIES = "selenium.properties";
    protected Selenium selenium;
    protected WebDriver driver;
    protected static DesiredCapabilities desiredCapabilities;
    protected Properties seleniumProperties = new Properties();
    private StringBuffer verificationErrors = new StringBuffer();
    private String validationString;
    private static final String displayNoneString = "display: none";
    private String updateString = "update value";

    @Before
    public void setUp() throws Exception {
    	seleniumProperties.load(FormSectionTest.class.getClassLoader().getResourceAsStream(SELENIUM_PROPERTIES));

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
    									seleniumProperties.getProperty("craftercms.formsection.widget.edit.page"), 
   										seleniumProperties.getProperty("craftercms.formsection.widget.content.type"), 
										seleniumProperties.getProperty("craftercms.sitename"));
    }

    @Test
    public void testWidgetControlDefaultOpenClose() throws InterruptedException {
        logger.info("Form Section default Open");
        validationString = driver.findElement(By.id("form-default-open-body")).getAttribute("style");
        assertEquals(validationString.contains("display:none"), false);

        logger.info("Form Section default Closed");
        validationString = driver.findElement(By.id("form-default-close-body")).getAttribute("style");
        assertEquals(validationString.contains(displayNoneString), true);

    }

    @Test
    public void testWidgetExpandAll() {
        logger.info("Form Section Click Expand All");
        CStudioSeleniumUtil.clickOn(driver, By.id("cstudio-form-expand-all"));
        assertEquals(driver.findElement(By.id("form-section-properties-body")).getAttribute("style").contains(displayNoneString), false);
        assertEquals(driver.findElement(By.id("form-default-open-body")).getAttribute("style").contains(displayNoneString), false);
        assertEquals(driver.findElement(By.id("form-default-close-body")).getAttribute("style").contains(displayNoneString), false);
    }

    @Test
    public void testWidgetCollapseAll() {
        logger.info("Form Section Click Collapse All");
        CStudioSeleniumUtil.clickOn(driver, By.id("cstudio-form-collapse-all"));
        assertEquals(driver.findElement(By.id("form-section-properties-body")).getAttribute("style").contains(displayNoneString), true);
        assertEquals(driver.findElement(By.id("form-default-open-body")).getAttribute("style").contains(displayNoneString), true);
        assertEquals(driver.findElement(By.id("form-default-close-body")).getAttribute("style").contains(displayNoneString), true);
    }

    @Test
    public void testWidgetExpandOne() {
        logger.info("Form Section Click Collapse All");
        CStudioSeleniumUtil.clickOn(driver, By.id("cstudio-form-collapse-all"));
        assertEquals(driver.findElement(By.id("form-default-close-body")).getAttribute("style").contains(displayNoneString), true);
        
        logger.info("Form Section Click Open");
        CStudioSeleniumUtil.clickOn(driver, By.cssSelector("#form-default-close-container .cstudio-form-section-widget"));
        assertEquals(driver.findElement(By.id("form-default-close-body")).getAttribute("style").contains(displayNoneString), false);
    }

    @Test
    public void testWidgetCollapseOne() {
        logger.info("Form Section Click Expand All");
        CStudioSeleniumUtil.clickOn(driver, By.id("cstudio-form-expand-all"));
        assertEquals(driver.findElement(By.id("form-default-close-body")).getAttribute("style").contains(displayNoneString), false);
        
        logger.info("Form Section Click Collapse");
        CStudioSeleniumUtil.clickOn(driver, By.cssSelector("#form-default-close-container .cstudio-form-section-widget"));
        assertEquals(driver.findElement(By.id("form-default-close-body")).getAttribute("style").contains(displayNoneString), true);
    }

    @Test
    public void testWidgetInvalidInfo() {
    	logger.info("Form Section Invalid Check");
    	driver.findElement(By.cssSelector("#requiredField .datum")).clear();
    	driver.findElement(By.cssSelector("#internal-name .datum")).click();
    	validationString = driver.findElement(By.cssSelector("#form-default-open-container .cstudio-form-section-validation")).getText();
    	assertEquals(validationString.startsWith("1"), true);

    	List<WebElement> elements = driver.findElements(By.cssSelector("#form-default-open-container .cstudio-form-section-invalid"));
    	assertEquals(elements.size(), 1);

    	elements = driver.findElements(By.cssSelector("#form-default-open-container .cstudio-form-section-valid"));
    	assertEquals(elements.size(), 0);

    	logger.info("Form Section Valid Check");
    	driver.findElement(By.cssSelector("#requiredField .datum")).sendKeys(updateString);
    	driver.findElement(By.cssSelector("#internal-name .datum")).click();
    	validationString = driver.findElement(By.cssSelector("#form-default-open-container .cstudio-form-section-validation")).getText();
    	assertEquals(validationString.length(), 0);

    	elements = driver.findElements(By.cssSelector("#form-default-open-container .cstudio-form-section-valid"));
    	assertEquals(elements.size(), 1);

    	elements = driver.findElements(By.cssSelector("#form-default-open-container .cstudio-form-section-invalid"));
    	assertEquals(elements.size(), 0);
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