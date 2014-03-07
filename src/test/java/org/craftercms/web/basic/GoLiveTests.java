/**
 * 
 */
package org.craftercms.web.basic;

import org.craftercms.web.BaseTest;
import org.craftercms.web.CStudioSeleniumUtil;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static org.junit.Assert.assertTrue;

/**
 * @author Praveen C Elineni
 *
 */
public class GoLiveTests extends BaseTest {
    private static final Logger logger = Logger.getLogger("GoLiveTest.class");

    private final String updateString = "About Page Updated";
    private final String updateString1 = "Industry Solutions Updated";

    /**
     * Test Go Live Now Functionality for one file
     * 
     * @throws InterruptedException
     */
    @Test
    public void testGoLiveNow() throws InterruptedException {    	
    	driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

    	// Login
    	logger.info("Login using admin credentials");
        CStudioSeleniumUtil.tryLogin(driver,
                seleniumProperties.getProperty("craftercms.admin.username"), 
                seleniumProperties.getProperty("craftercms.admin.password"),
                true);

        // Navigate to Dashboard page
        logger.info("navigate to dashboard");
        driver.navigate().to(dashboardUrl);

        CStudioSeleniumUtil.editAndSavePage(driver, seleniumProperties.getProperty("craftercms.page.to.edit"), updateString);

        driver.navigate().to(dashboardUrl);
        // check my-recent-activity widget
        logger.info("Check my-recent activity widget");
        new WebDriverWait(driver, 60).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
              return d.findElement(By.id("MyRecentActivity-body")).getText().contains(updateString);
            }
        });

        // select and check the updated item
        logger.info("Check item and push it to go-live");
        CStudioSeleniumUtil.clickOn(driver, By.id("MyRecentActivity-" + seleniumProperties.getProperty("craftercms.page.to.edit")));

        // click go-live now link
        logger.info("Select Go Live Now");
        CStudioSeleniumUtil.clickOn(driver, By.xpath("//a[text()='Go Live Now']"));
        Thread.sleep(1000);

        // confirm submission on dialog
        logger.info("Confirm Go Live Now");
        CStudioSeleniumUtil.clickOn(driver, By.id("golivesubmitButton"));

        // close dialog and wait for 10 secs for deployment to finish
        CStudioSeleniumUtil.clickOn(driver, By.id("acnOKButton"));
        logger.info("Waiting for item to go-live...");
        Thread.sleep(10000);

        // Refresh dashboard by logout and login
        logger.info("refresh dashboard");
        logout();
        login();

        // navigate to dashboard
        driver.navigate().to(dashboardUrl);

        // check in recently-made-live to see if item exists
        new WebDriverWait(driver, 60).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
              return d.findElement(By.id("recentlyMadeLive-body")).getText().contains(updateString);
            }
        });

        logger.info("Open file in live folder and check content exists");
        assertTrue(CStudioSeleniumUtil.readFileContents(seleniumProperties.getProperty("craftercms.live.deployer.path") + seleniumProperties.getProperty("craftercms.page.to.edit"), updateString));
    }
    
    /**
     * Test Go Live Now Functionality for multiple files
     * 
     * @throws InterruptedException
     */
    @Test
    public void testMultiplePagesGoLiveNow() throws InterruptedException {    	
    	driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

    	logger.info("Login using admin credentials");
        login();

        // Navigate to Dashboard page
        logger.info("navigate to dashboard");
        driver.navigate().to(dashboardUrl);

        logger.info("Edit pages");
        CStudioSeleniumUtil.editAndSavePage(driver, seleniumProperties.getProperty("craftercms.page.to.edit"), updateString);
        CStudioSeleniumUtil.editAndSavePage(driver, seleniumProperties.getProperty("craftercms.page.to.edit1"), updateString1);

        driver.navigate().to(dashboardUrl);
        // select and check the updated item
        logger.info("Check item and push it to go-live");
        CStudioSeleniumUtil.clickOn(driver, By.id("MyRecentActivity-" + seleniumProperties.getProperty("craftercms.page.to.edit1")));
        CStudioSeleniumUtil.clickOn(driver, By.id("MyRecentActivity-" + seleniumProperties.getProperty("craftercms.page.to.edit")));

        // click go-live now link
        logger.info("Select Go Live Now");
        CStudioSeleniumUtil.clickOn(driver, By.xpath("//a[text()='Go Live Now']"));

        // Select "go live now" radio button
        By setToNowBy = By.id("globalSetToNow");
        CStudioSeleniumUtil.waitForItemToDisplay(driver, 10, setToNowBy);
        // Wait for item to be fully initialized
        Thread.sleep(1000);
        CStudioSeleniumUtil.clickOn(driver, setToNowBy);

        // confirm submission on dialog
        logger.info("Confirm Go Live Now");
        By goLiveSubmitBy = By.id("golivesubmitButton");
        CStudioSeleniumUtil.waitForItemToDisplay(driver, 10, goLiveSubmitBy);
        // Wait for item to be fully initialized
        Thread.sleep(1000);
        CStudioSeleniumUtil.clickOn(driver, goLiveSubmitBy);


        By okBy = By.id("acnOKButton");
        CStudioSeleniumUtil.waitForItemToDisplay(driver, 10, okBy);
        // Wait for item to be fully initialized
        Thread.sleep(1000);
        CStudioSeleniumUtil.clickOn(driver, okBy);
        logger.info("Waiting for item to go-live...");

        logger.info("Refresh dashboard");
        logout();
        login();

        driver.navigate().to(dashboardUrl);

        // check in recently-made-live to see if item exists
        new WebDriverWait(driver, 60).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
              return d.findElement(By.id("recentlyMadeLive-body")).getText().contains(updateString);
            }
        });

        logger.info("Open file in live folder and check content exists");
        assertTrue(CStudioSeleniumUtil.readFileContents(seleniumProperties.getProperty("craftercms.live.deployer.path") + seleniumProperties.getProperty("craftercms.page.to.edit"), updateString));
        assertTrue(CStudioSeleniumUtil.readFileContents(seleniumProperties.getProperty("craftercms.live.deployer.path") + seleniumProperties.getProperty("craftercms.page.to.edit1"), updateString1));
    }
}