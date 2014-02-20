/**
 * 
 */
package org.craftercms.web.basic;

import static org.junit.Assert.assertTrue;

import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.craftercms.web.BaseTest;
import org.craftercms.web.CStudioSeleniumUtil;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * @author Praveen C Elineni
 *
 */
public class GoLiveTests extends BaseTest {
    private static final Logger logger = Logger.getLogger("GoLiveTest.class");

    private final String updateString = "About Page Updated";
    private final String updateString1 = "Industry Solutions Updated";

    /**
     * Test Go Live Now Functinality for one file 
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

        String dashboardUrl = String.format(seleniumProperties.getProperty("craftercms.site.dashboard.url"), seleniumProperties.getProperty("craftercms.sitename"));

        // Navigate to Dashboard page
        logger.info("navigate to dashboard");
        driver.navigate().to(dashboardUrl);

        editAndSaveUtil(seleniumProperties.getProperty("craftercms.page.to.edit"), updateString);

        driver.navigate().to(dashboardUrl);
        // check my-recent-activity widget
        logger.info("Check my-recent activity widget");
        new WebDriverWait(driver, 60).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
              return d.findElement(By.id("MyRecentActivity-body")).getText().contains(updateString);
            }
        });

        assertTrue(driver.findElement(By.id("MyRecentActivity-body")).getText().contains(updateString));

        // select and check the updated item
        logger.info("Check item and push it to go-live");
        CStudioSeleniumUtil.clickOn(driver, By.id("MyRecentActivity-" + seleniumProperties.getProperty("craftercms.page.to.edit")));
        Thread.sleep(10000);

        // click go-live now link
        logger.info("Select Go Live Now");
        CStudioSeleniumUtil.clickOn(driver, By.xpath("//a[text()='Go Live Now']"));
        Thread.sleep(10000);

        // confirm submission on dialog
        logger.info("Confirm Go Live Now");
        CStudioSeleniumUtil.clickOn(driver, By.id("golivesubmitButton"));
        Thread.sleep(10000);

        // close dialog and wait for 30 secs for deployment to finish
        CStudioSeleniumUtil.clickOn(driver, By.id("acnOKButton"));
        logger.info("Waiting for item to go-live...");
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

        // check in recently-made-live to see if item exists
        new WebDriverWait(driver, 60).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
              return d.findElement(By.id("recentlyMadeLive-body")).getText().contains(updateString);
            }
        });
        assertTrue(driver.findElement(By.id("recentlyMadeLive-body")).getText().contains(updateString));

        // goto live folder, readfile and check updated
        assertTrue(CStudioSeleniumUtil.readFileContents(seleniumProperties.getProperty("craftercms.live.deployer.path") + seleniumProperties.getProperty("craftercms.page.to.edit"), updateString));
    }
    
    /**
     * Test Go Live Now Functinality for multiple files 
     * 
     * @throws InterruptedException
     */
    @Test
    public void testMultiplePagesGoLiveNow() throws InterruptedException {    	
    	driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

    	// Login
    	logger.info("Login using admin credentials");
        CStudioSeleniumUtil.tryLogin(driver,
                seleniumProperties.getProperty("craftercms.admin.username"), 
                seleniumProperties.getProperty("craftercms.admin.password"),
                true);

        String dashboardUrl = String.format(seleniumProperties.getProperty("craftercms.site.dashboard.url"), seleniumProperties.getProperty("craftercms.sitename"));

        // Navigate to Dashboard page
        logger.info("navigate to dashboard");
        driver.navigate().to(dashboardUrl);

        // Execute JS before Edit Page
        editAndSaveUtil(seleniumProperties.getProperty("craftercms.page.to.edit"), updateString);
        editAndSaveUtil(seleniumProperties.getProperty("craftercms.page.to.edit1"), updateString1);
        Thread.sleep(5000);

        driver.navigate().to(dashboardUrl);
        // select and check the updated item
        logger.info("Check item and push it to go-live");
        CStudioSeleniumUtil.clickOn(driver, By.id("MyRecentActivity-" + seleniumProperties.getProperty("craftercms.page.to.edit1")));
        Thread.sleep(10000);
        CStudioSeleniumUtil.clickOn(driver, By.id("MyRecentActivity-" + seleniumProperties.getProperty("craftercms.page.to.edit")));
        Thread.sleep(10000);

        // click go-live now link
        logger.info("Select Go Live Now");
        CStudioSeleniumUtil.clickOn(driver, By.xpath("//a[text()='Go Live Now']"));
        Thread.sleep(10000);

        // Select "go live now" radio button
        CStudioSeleniumUtil.clickOn(driver, By.id("globalSetToNow"));

        // confirm submission on dialog
        logger.info("Confirm Go Live Now");
        CStudioSeleniumUtil.clickOn(driver, By.id("golivesubmitButton"));
        Thread.sleep(10000);

        // close dialog and wait for 30 secs for deployment to finish
        CStudioSeleniumUtil.clickOn(driver, By.id("acnOKButton"));
        logger.info("Waiting for item to go-live...");
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

        // check in recently-made-live to see if item exists
        new WebDriverWait(driver, 60).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
              return d.findElement(By.id("recentlyMadeLive-body")).getText().contains(updateString);
            }
        });
        assertTrue(driver.findElement(By.id("recentlyMadeLive-body")).getText().contains(updateString));

        // goto live folder get file and check updated
        logger.info("open file in live folder and check content exists");
        assertTrue(CStudioSeleniumUtil.readFileContents(seleniumProperties.getProperty("craftercms.live.deployer.path") + seleniumProperties.getProperty("craftercms.page.to.edit"), updateString));
        assertTrue(CStudioSeleniumUtil.readFileContents(seleniumProperties.getProperty("craftercms.live.deployer.path") + seleniumProperties.getProperty("craftercms.page.to.edit1"), updateString1));
    }
}