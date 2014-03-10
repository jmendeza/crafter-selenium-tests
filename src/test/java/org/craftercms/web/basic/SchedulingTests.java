/**
 *
 */
package org.craftercms.web.basic;

import org.craftercms.web.BaseTest;
import org.craftercms.web.CStudioSeleniumUtil;
import org.craftercms.web.TestConstants;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

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
    	driver.manage().timeouts().implicitlyWait(TestConstants.WAITING_SECONDS_WEB_ELEMENT, TimeUnit.SECONDS);

    	logger.info("Login using admin credentials");
        login();

        logger.info("navigate to dashboard");
        driver.navigate().to(dashboardUrl);

        logger.info("Edit page");
        CStudioSeleniumUtil.editAndSavePage(driver, item, updateString);

        logger.info("Refresh dashboard");
        driver.navigate().to(dashboardUrl);

        logger.info("Check my-recent activity widget");
        new WebDriverWait(driver, TestConstants.WAITING_SECONDS_WEB_ELEMENT).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
              return d.findElement(By.id("MyRecentActivity-body")).getText().contains(updateString);
            }
        });

        logger.info("Check item and push it to schedule");
        CStudioSeleniumUtil.clickOn(driver, By.id("MyRecentActivity-" + item));
        Thread.sleep(1000);

        logger.info("Select Schedule");
        driver.manage().window().maximize();
        CStudioSeleniumUtil.clickOn(driver, By.xpath("//a[text()='Schedule']"));
        Thread.sleep(1000);

        logger.info("Setting date and time fields");
        WebElement element = driver.findElement(By.id("schedulingSelectionDatepickerOverlay"));
        element.click();

        By todayBy = By.cssSelector("#calendarWrapper .today a");
        CStudioSeleniumUtil.waitForItemToDisplay(driver, TestConstants.WAITING_SECONDS_WEB_ELEMENT, todayBy);

        WebElement today = driver.findElement(todayBy);
        today.click();

        element = driver.findElement(By.id("timepicker"));
        element.clear();

        // Ensure time is after now.
        element.sendKeys("11:59:59 p.m.\n");

        Thread.sleep(1000);

        logger.info("Confirm Schedule");
        CStudioSeleniumUtil.clickOn(driver, By.id("golivesubmitButton"));
        Thread.sleep(1000);

        CStudioSeleniumUtil.clickOn(driver, By.id("acnOKButton"));
        logger.info("Waiting for item to get scheduled...");
        Thread.sleep(TestConstants.WAITING_SECONDS_DEPLOY);

        logger.info("refresh dashboard");
        logout();
        login();
        driver.navigate().to(dashboardUrl);

        logger.info("Check approvedScheduledItems activity widget");
        new WebDriverWait(driver, TestConstants.WAITING_SECONDS_WEB_ELEMENT).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                return d.findElement(By.id("approvedScheduledItems-body")).getText().contains(updateString);
            }
        });
    }
}