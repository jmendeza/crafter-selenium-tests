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
public class SchedulingTests extends BaseTest {
	private static final Logger logger = Logger.getLogger("SchedulingTests.class");

    /**
     * Test scheduling an item to go live, then cancelling, editing
     * and scheduling again at same time as before.
     */
    @Test
    public void testScheduledCancelledScheduledItem() throws InterruptedException {
        String updateString = "Industry Solutions Updated Scheduled";
        editAndScheduleItem(seleniumProperties.getProperty("craftercms.page.to.edit1"), updateString);
        CStudioSeleniumUtil.tryLogout(driver);
        editAndScheduleItem(seleniumProperties.getProperty("craftercms.page.to.edit1"), updateString);
    }

    /**
     * Test Schedule Item to given date and time
     *
     * @throws InterruptedException
     */
    @Test
    public void testScheduledItem() throws InterruptedException {
        String updateString = "About Page Update Scheduled";
        editAndScheduleItem(seleniumProperties.getProperty("craftercms.page.to.edit"), updateString);
    }

    private void editAndScheduleItem(String item, final String updateString) throws InterruptedException {
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

        editAndSaveUtil(item, updateString);

        driver.navigate().to(dashboardUrl);
        // check my-recent-activity widget
        logger.info("Check my-recent activity widget");
        new WebDriverWait(driver, 60).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
              return d.findElement(By.id("MyRecentActivity-body")).getText().contains(updateString);
            }
        });

        assertTrue(driver.findElement(By.id("MyRecentActivity-body")).getText().contains(updateString));

        driver.navigate().to(dashboardUrl);
        // select and check the updated item
        logger.info("Check item and push it to schedule");
        CStudioSeleniumUtil.clickOn(driver, By.id("MyRecentActivity-" + item));
        Thread.sleep(10000);

        // click go-live now link
        logger.info("Select Schedule");
        driver.manage().window().maximize();
        CStudioSeleniumUtil.clickOn(driver, By.xpath("//a[text()='Schedule']"));
        Thread.sleep(10000);

        // set scheduled date and time
        logger.info("Setting date and time fields");
        WebElement element = driver.findElement(By.id("schedulingSelectionDatepickerOverlay"));
        element.click();

        CStudioSeleniumUtil.waitForItemToDisplay(driver,60, By.id("calendarWrapper"));

        WebElement today = driver.findElement(By.cssSelector("#calendarWrapper .today a"));
        today.click();

        Thread.sleep(10000);

        element = driver.findElement(By.id("timepicker"));
        element.clear();

        // Ensure time is after now.
        element.sendKeys("11:59:59 p.m.\n");

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
        driver.navigate().to(dashboardUrl);

        logger.info("Check approvedScheduledItems activity widget");
        new WebDriverWait(driver, 60).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                return d.findElement(By.id("approvedScheduledItems-body")).getText().contains(updateString);
            }
        });
        assertTrue(driver.findElement(By.id("approvedScheduledItems-tbody")).getText().contains(updateString));
    }
}