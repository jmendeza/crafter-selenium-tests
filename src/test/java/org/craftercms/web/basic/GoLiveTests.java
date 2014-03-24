/**
 * 
 */
package org.craftercms.web.basic;

import com.google.common.base.Predicate;
import org.craftercms.web.BaseTest;
import org.craftercms.web.CStudioSeleniumUtil;
import org.craftercms.web.TestConstants;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;

/**
 * @author Praveen C Elineni
 *
 */
public class GoLiveTests extends BaseTest {

    private final String updateString = "About Page Updated";
    private final String updateString1 = "Industry Solutions Updated";

    /**
     * Test Go Live Now Functionality for one file
     * 
     * @throws InterruptedException
     */
    @Test
    public void testGoLiveNow() throws InterruptedException {    	
    	driver.manage().timeouts().implicitlyWait(TestConstants.WAITING_SECONDS_WEB_ELEMENT, TimeUnit.SECONDS);

    	logger.info("Login using admin credentials");
        login();

        logger.info("Navigate to dashboard");
        driver.navigate().to(dashboardUrl);

        logger.info("Edit page");
        CStudioSeleniumUtil.editAndSavePage(driver, seleniumProperties.getProperty("craftercms.page.to.edit"), updateString);

        logger.info("Refresh dashboard");
        driver.navigate().to(dashboardUrl);

        logger.info("Check my-recent activity widget");
        new WebDriverWait(driver, TestConstants.WAITING_SECONDS_WEB_ELEMENT).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
              return d.findElement(By.id("MyRecentActivity-body")).getText().contains(updateString);
            }
        });

        logger.info("Check item and push it to go-live");
        CStudioSeleniumUtil.clickOn(driver, By.id("MyRecentActivity-" + seleniumProperties.getProperty("craftercms.page.to.edit")));

        logger.info("Select Go Live Now");
        CStudioSeleniumUtil.clickOn(driver, By.xpath("//a[text()='Go Live Now']"));
        Thread.sleep(1000);

        logger.info("Confirm Go Live Now");
        CStudioSeleniumUtil.clickOn(driver, By.id("golivesubmitButton"));
        Thread.sleep(1000);

        CStudioSeleniumUtil.clickOn(driver, By.id("acnOKButton"));
        logger.info("Waiting for item to go-live...");
        Thread.sleep(1000);

        logger.info("refresh dashboard");
        logout();
        login();

        driver.navigate().to(dashboardUrl);

        logger.info("Check in recently-made-live to see if item exists");
        new WebDriverWait(driver, TestConstants.WAITING_SECONDS_WEB_ELEMENT).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
              return d.findElement(By.id("recentlyMadeLive-body")).getText().contains(updateString);
            }
        });

        File fileItem = new File(seleniumProperties.getProperty("craftercms.live.deployer.path") + seleniumProperties.getProperty("craftercms.page.to.edit"));
        new FluentWait<File>(fileItem).withTimeout(TestConstants.WAITING_SECONDS_DEPLOY, TimeUnit.SECONDS).until(new Predicate<File>() {
            @Override
            public boolean apply(File file) {
                return file.exists();
            }
        });
        logger.info("Open file in live folder and check content exists");
        assertTrue(CStudioSeleniumUtil.readFileContents(fileItem.getAbsolutePath(), updateString));
    }
    
    /**
     * Test Go Live Now Functionality for multiple files
     * 
     * @throws InterruptedException
     */
    @Test
    public void testMultiplePagesGoLiveNow() throws InterruptedException {    	
    	driver.manage().timeouts().implicitlyWait(TestConstants.WAITING_SECONDS_WEB_ELEMENT, TimeUnit.SECONDS);

    	logger.info("Login using admin credentials");
        login();

        logger.info("Navigate to dashboard");
        driver.navigate().to(dashboardUrl);

        logger.info("Edit pages");
        CStudioSeleniumUtil.editAndSavePage(driver, seleniumProperties.getProperty("craftercms.page.to.edit"), updateString);
        CStudioSeleniumUtil.editAndSavePage(driver, seleniumProperties.getProperty("craftercms.page.to.edit1"), updateString1);

        logger.info("Refresh dashboard");
        driver.navigate().to(dashboardUrl);

        logger.info("Check item and push it to go-live");
        CStudioSeleniumUtil.clickOn(driver, By.id("MyRecentActivity-" + seleniumProperties.getProperty("craftercms.page.to.edit1")));
        CStudioSeleniumUtil.clickOn(driver, By.id("MyRecentActivity-" + seleniumProperties.getProperty("craftercms.page.to.edit")));

        Thread.sleep(1000);
        logger.info("Select Go Live Now");
        CStudioSeleniumUtil.clickOn(driver, By.xpath("//a[text()='Go Live Now']"));

        By setToNowBy = By.id("globalSetToNow");
        CStudioSeleniumUtil.waitForItemToDisplay(driver, 10, setToNowBy);
        // Wait for item to be fully initialized
        Thread.sleep(1000);
        CStudioSeleniumUtil.clickOn(driver, setToNowBy);

        logger.info("Confirm Go Live Now");
        By goLiveSubmitBy = By.id("golivesubmitButton");
        CStudioSeleniumUtil.waitForItemToDisplay(driver, 10, goLiveSubmitBy);
        // Wait for item to be fully initialized
        Thread.sleep(1000);
        CStudioSeleniumUtil.clickOn(driver, goLiveSubmitBy);


        By okBy = By.id("acnOKButton");
        logger.info("Waiting for item to go-live...");
        CStudioSeleniumUtil.waitForItemToDisplay(driver, 10, okBy);
        // Wait for item to be fully initialized
        Thread.sleep(1000);
        CStudioSeleniumUtil.clickOn(driver, okBy);

        logger.info("Refresh dashboard");
        logout();
        login();

        driver.navigate().to(dashboardUrl);

        logger.info("Check in recently-made-live to see if item exists");
        new WebDriverWait(driver, TestConstants.WAITING_SECONDS_WEB_ELEMENT).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
              return d.findElement(By.id("recentlyMadeLive-body")).getText().contains(updateString);
            }
        });

        String page1Path = seleniumProperties.getProperty("craftercms.live.deployer.path") + seleniumProperties.getProperty("craftercms.page.to.edit");
        String page2Path = seleniumProperties.getProperty("craftercms.live.deployer.path") + seleniumProperties.getProperty("craftercms.page.to.edit1");

        Thread.sleep(1000 * TestConstants.WAITING_SECONDS_DEPLOY);

        logger.info("Open file in live folder and check content exists");
        assertTrue(CStudioSeleniumUtil.readFileContents(page1Path, updateString));
        assertTrue(CStudioSeleniumUtil.readFileContents(page2Path, updateString1));
    }
}