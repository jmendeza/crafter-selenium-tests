/**
 * 
 */
package org.craftercms.web.widget;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
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
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.thoughtworks.selenium.Selenium;

/**
 * @author Praveen C Elineni
 *
 */
public class NodeSelectorTest {
    private static final Logger logger = Logger.getLogger("NodeSelectorTest.class");

	private final static String SELENIUM_PROPERTIES = "selenium.properties";
    protected Selenium selenium;
    protected WebDriver driver;
    protected static DesiredCapabilities desiredCapabilities;
    protected Properties seleniumProperties = new Properties();
    private StringBuffer verificationErrors = new StringBuffer();
    private String validationString;
    private String updateString = "Child Update";

    @Before
    public void setUp() throws Exception {
    	seleniumProperties.load(NodeSelectorTest.class.getClassLoader().getResourceAsStream(SELENIUM_PROPERTIES));

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
									seleniumProperties.getProperty("craftercms.nodeselector.widget.edit.page"), 
									seleniumProperties.getProperty("craftercms.nodeselector.widget.content.type"), 
									seleniumProperties.getProperty("craftercms.sitename"));
    }

  	@Test
    public void testWidgetControlRequired() {
        // required - content entered - valid
        logger.info("Node Selector Required");
        String inputValue = driver.findElement(By.cssSelector("#node-selector-required .datum")).getAttribute("value");
        validationString = driver.findElement(By.cssSelector("#node-selector-required .validation-hint")).getAttribute("class");
        
        if (inputValue == "") {
        	assertTrue(validationString.contains("cstudio-form-control-invalid"));
        } else {
        	assertTrue(validationString.contains("cstudio-form-control-valid"));
        }        
    }

    @Test
    public void testWidgetAddChildElement() {
    	logger.info("Add Element to Node Selector");
        List<WebElement> elements = driver.findElements(By.cssSelector("#node-selector-required .cstudio-form-control-node-selector-item"));
        int initialCount = elements.size();

        // click on cstudio-drop-arrow-button (opens dropdown)
        CStudioSeleniumUtil.clickOn(driver, By.xpath("//input[@value='Add']"));
        CStudioSeleniumUtil.clickOn(driver, By.xpath("//div[text()='Create New']"));

        // Wait for the window to load
        new WebDriverWait(driver, 60).until(new ExpectedCondition<Boolean>() {
          public Boolean apply(WebDriver d) {
            return d.getWindowHandles().size() > 2;
          }
        });

        // Switch to child window
        Set<String> handles = driver.getWindowHandles();
        for (String h : handles) {
          driver.switchTo().window(h);
          if (driver.getCurrentUrl().contains("node-selector-widget-test-child"))
        	  break;
        }

        Random randomGenerator = new Random();

        // Enter all fields
        driver.findElement(By.cssSelector("#internal-name .datum")).sendKeys(String.valueOf(randomGenerator.nextInt()));
        driver.findElement(By.cssSelector("#file-name .datum")).sendKeys(String.valueOf(randomGenerator.nextInt()));

        // save and close child form
        CStudioSeleniumUtil.clickOn(driver, By.id("cstudioSaveAndClose"));

        // Wait for the window to close
        new WebDriverWait(driver, 60).until(new ExpectedCondition<Boolean>() {
          public Boolean apply(WebDriver d) {
            return d.getWindowHandles().size() < 3;
          }
        });

        // Switch back to parent window
        handles = driver.getWindowHandles();
        for (String h : handles) {
          driver.switchTo().window(h);
          if (driver.getCurrentUrl().contains("cstudio-form"))
        	  break;
        }

        elements = driver.findElements(By.cssSelector("#node-selector-required .cstudio-form-control-node-selector-item"));
        assertEquals(initialCount + 1, elements.size());
    }

    @Test
    public void testWidgetDeleteChildElement() {
        logger.info("Delete child element in node-selector");
        CStudioSeleniumUtil.clickOn(driver, By.cssSelector("#node-selector-required .cstudio-form-control-node-selector-item"));
        CStudioSeleniumUtil.clickOn(driver, By.xpath("//input[@value='X']"));

    	List<WebElement> elements = driver.findElements(By.cssSelector("#node-selector-required .cstudio-form-control-node-selector-item"));
    	assertEquals(elements.size(), 0);

    	logger.info("Validate Node Selector Count");
    	validationString = driver.findElement(By.cssSelector("#node-selector-required .item-count")).getText();
    	validationString = validationString.substring(0, validationString.indexOf(" "));
    	assertEquals(validationString, String.valueOf(elements.size()));
    }

    @Test
    public void testWidgetEditChildElement() {
        logger.info("Edit child element in node-selector");
        CStudioSeleniumUtil.clickOn(driver, By.cssSelector("#node-selector-required .cstudio-form-control-node-selector-item"));
        CStudioSeleniumUtil.clickOn(driver, By.xpath("(//input[@value='Edit'])[2]"));

        // Wait for the window to load
        new WebDriverWait(driver, 60).until(new ExpectedCondition<Boolean>() {
          public Boolean apply(WebDriver d) {
            return d.getWindowHandles().size() > 2;
          }
        });

        // Switch to child window
        Set<String> handles = driver.getWindowHandles();
        for (String h : handles) {
          driver.switchTo().window(h);
          if (driver.getCurrentUrl().contains("node-selector-widget-test-child"))
        	  break;
        }

        // Enter all fields
        driver.findElement(By.cssSelector("#internal-name .datum")).sendKeys(updateString);

        // save and close child form
        CStudioSeleniumUtil.clickOn(driver, By.id("cstudioSaveAndClose"));

        // Wait for the window to close
        new WebDriverWait(driver, 60).until(new ExpectedCondition<Boolean>() {
          public Boolean apply(WebDriver d) {
            return d.getWindowHandles().size() < 3;
          }
        });

        // Switch back to parent window
        handles = driver.getWindowHandles();
        for (String h : handles) {
          driver.switchTo().window(h);
          if (driver.getCurrentUrl().contains("cstudio-form"))
        	  break;
        }

        String inputValue = driver.findElement(By.cssSelector("#node-selector-required .datum")).getAttribute("value");
        assertTrue(inputValue.contains(updateString));
    }

    @Test
    public void testWidgetCount() {
    	logger.info("Node Selector Count");
    	List<WebElement> elements = driver.findElements(By.cssSelector("#node-selector-required .cstudio-form-control-node-selector-item"));
    	validationString = driver.findElement(By.cssSelector("#node-selector-required .item-count")).getText();
    	validationString = validationString.substring(0, validationString.indexOf(" "));

    	assertEquals(validationString, String.valueOf(elements.size()));
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