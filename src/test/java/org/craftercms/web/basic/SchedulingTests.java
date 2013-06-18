/**
 * 
 */
package org.craftercms.web.basic;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Properties;
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
public class SchedulingTests {
	// TODO: Re-Verify test case
	private static final Logger logger = Logger.getLogger("SchedulingTests.class");

    private final static String SELENIUM_PROPERTIES = "selenium.properties";
    protected Selenium selenium;
    protected WebDriver driver;
    protected static DesiredCapabilities desiredCapabilities;
    protected Properties seleniumProperties = new Properties();
    private StringBuffer verificationErrors = new StringBuffer();
    private String updateString = "About Page Update Scheduled";

    @Before
    public void setUp() throws Exception {
    	seleniumProperties.load(SchedulingTests.class.getClassLoader().getResourceAsStream(SELENIUM_PROPERTIES));

    	desiredCapabilities = new DesiredCapabilities();
        desiredCapabilities.setJavascriptEnabled(true);
        desiredCapabilities.setCapability("takesScreenshot", true);
        desiredCapabilities.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, 
        		                             seleniumProperties.getProperty("craftercms.phantomjs.path"));
        driver = new PhantomJSDriver(desiredCapabilities);
    }

    /**
     * Test Schedule Item to given date and time
     * 
     * @throws InterruptedException
     */
    @Test
    public void testScheduledItem() throws InterruptedException {
    	driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

    	// Login
    	logger.info("Login using admin credentials");
        CStudioSeleniumUtil.tryLogin(driver,
                seleniumProperties.getProperty("craftercms.admin.username"), 
                seleniumProperties.getProperty("craftercms.admin.password"),
                true);

        // Navigate to Dashboard page
        logger.info("navigate to dashboard");
        driver.navigate().to(String.format(seleniumProperties.getProperty("craftercms.site.dashboard.url"), seleniumProperties.getProperty("craftercms.sitename")));

        editAndSaveUtil(seleniumProperties.getProperty("craftercms.page.to.edit"), updateString);

        // check my-recent-activity widget
        logger.info("Check my-recent activity widget");
        new WebDriverWait(driver, 60).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
              return d.findElement(By.id("MyRecentActivity-body")).getText().contains(updateString);
            }
        });

        assertTrue(driver.findElement(By.id("MyRecentActivity-body")).getText().contains(updateString));

        // select and check the updated item
        logger.info("Check item and push it to schedule");
        CStudioSeleniumUtil.clickOn(driver, By.id("MyRecentActivity-" + seleniumProperties.getProperty("craftercms.page.to.edit")));
        Thread.sleep(10000);

        // click go-live now link
        logger.info("Select Schedule");
        CStudioSeleniumUtil.clickOn(driver, By.xpath("//a[text()='Schedule']"));
        Thread.sleep(10000);

        // set scheduled date and time
        logger.info("Setting date and time fields");
        WebElement element = driver.findElement(By.id("datepicker"));
        element.clear();
        element.sendKeys("6/30/2020");
        Thread.sleep(10000);

        element = driver.findElement(By.id("timepicker"));
        element.clear();
        element.sendKeys("10:00:00 a.m.");
        Thread.sleep(10000);

        // confirm submission on dialog
        logger.info("Confirm Schedule");
        CStudioSeleniumUtil.clickOn(driver, By.id("golivesubmitButton"));
        Thread.sleep(10000);

        // close dialog and wait for 30 secs for deployment to finish
        CStudioSeleniumUtil.clickOn(driver, By.id("acnOKButton"));
        logger.info("Waiting for item to get scheduled...");
        Thread.sleep(30000);

        // Refresh dashboard by logout and login
        logger.info("refresh dashboard");
        CStudioSeleniumUtil.tryLogout(driver);
        CStudioSeleniumUtil.tryLogin(driver,
                seleniumProperties.getProperty("craftercms.admin.username"), 
                seleniumProperties.getProperty("craftercms.admin.password"),
                true);

        // navigate to dashboard
        driver.navigate().to(String.format(seleniumProperties.getProperty("craftercms.site.dashboard.url"), seleniumProperties.getProperty("craftercms.sitename")));
    }

    /*
     * Helper utility for code re-use
     */
    private void editAndSaveUtil(String editPage, String editString) throws InterruptedException {
        // Execute JS before Edit Page
        logger.info("edit page" + editPage);
        CStudioSeleniumUtil.editPageJS(driver, editPage, 
				seleniumProperties.getProperty("craftercms.page.content.type"), 
				seleniumProperties.getProperty("craftercms.sitename"));

        // Wait for the window to load
        new WebDriverWait(driver, 60).until(new ExpectedCondition<Boolean>() {
          public Boolean apply(WebDriver d) {
            return d.getWindowHandles().size() > 1;
          }
        });

        // Switch to edit window
        Set<String> handles = driver.getWindowHandles();
        for (String h : handles) {
          driver.switchTo().window(h);
          if (driver.getCurrentUrl().contains("cstudio-form"))
        	  break;
        }

        // Find internal-name field and edit
        driver.findElement(By.cssSelector("#internal-name .datum")).clear();
        driver.findElement(By.cssSelector("#internal-name .datum")).sendKeys(editString);

        // Click Save&Close button and wait for change to complete
        logger.info("save and close form");
		Thread.sleep(5000);
        driver.findElement(By.id("cstudioSaveAndClose")).click();
        Thread.sleep(15000);

        new WebDriverWait(driver, 60).until(new ExpectedCondition<Boolean>() {
          public Boolean apply(WebDriver d) {
            return (d.getWindowHandles().size() == 1);
          }
        });

        // Navigate back to dashboard page and switch window
        logger.info("Navigate back to dashboard");
        handles = driver.getWindowHandles();
        for (String h : handles) {
          driver.switchTo().window(h);
        }

        assertTrue(driver.getTitle().equals("Crafter Studio"));
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