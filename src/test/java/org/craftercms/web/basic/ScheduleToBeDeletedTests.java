package org.craftercms.web.basic;

import org.craftercms.web.BaseTest;
import org.craftercms.web.CStudioSeleniumUtil;
import org.craftercms.web.TestConstants;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * @author Jonathan Méndez
 */
public class ScheduleToBeDeletedTests extends BaseTest {

    @Test
    public void scheduleToBeDeletedTest() throws InterruptedException {
        login();

        logger.info("Navigate to dashboard");
        driver.navigate().to(dashboardUrl);

        String pageToUpdate = seleniumProperties.getProperty("craftercms.page.to.edit");
        String updateString = "About Page " + System.currentTimeMillis();

        logger.info("Edit page");
        CStudioSeleniumUtil.editAndSavePage(driver, pageToUpdate, updateString);

        logger.info("Refresh dashboard");
        driver.navigate().to(dashboardUrl);

        CStudioSeleniumUtil.ensurePagesTreeIsExpanded(driver);
        WebElement articleItem = CStudioSeleniumUtil.findItemWithName(driver, updateString);

        logger.info("Choose delete in pages tree context menu");
        CStudioSeleniumUtil.contextMenuOption(driver, "Delete", articleItem);

        logger.info("Schedule item and dependencies");
        By datepickerBy = By.cssSelector("#datepicker.date-picker");
        By timepickerBy = By.cssSelector("input.time-picker");
        By scheduleTimeLinksBy = By.cssSelector("#acnVersionWrapper a[checkid]");
        CStudioSeleniumUtil.waitForItemToDisplay(driver, TestConstants.WAITING_SECONDS_WEB_ELEMENT, scheduleTimeLinksBy);
        List<WebElement> scheduleTimeLinks = driver.findElements(scheduleTimeLinksBy);
        for (WebElement scheduleTimeLink : scheduleTimeLinks) {
            scheduleTimeLink.click();

            CStudioSeleniumUtil.waitForItemToDisplay(driver, TestConstants.WAITING_SECONDS_WEB_ELEMENT, datepickerBy);

            WebElement datepicker = driver.findElement(datepickerBy);
            datepicker.clear();
            datepicker.sendKeys("1/01/3001");

            CStudioSeleniumUtil.waitForItemToDisplay(driver, TestConstants.WAITING_SECONDS_WEB_ELEMENT, timepickerBy);

            WebElement timepicker = driver.findElement(timepickerBy);
            timepicker.sendKeys("12:00:01 a.m.");

            CStudioSeleniumUtil.clickOn(driver, By.xpath("//div[@id='acnVersionWrapper']//a[text()='Done']"));
        }

        logger.info("Click 'Delete' button");
        CStudioSeleniumUtil.clickOn(driver, By.cssSelector("#acnVersionWrapper input.do-delete[type='submit']"));

        logger.info("Wait for item to be scheduled");
        Thread.sleep(2000);

        String baseUrl = seleniumProperties.getProperty("craftercms.base.url");
        String pageToEditUrl = seleniumProperties.getProperty("craftercms.page.to.edit.url");

        logger.info("Navigate to '" + pageToEditUrl + "'");
        driver.navigate().to(baseUrl + pageToEditUrl);

        logger.info("Check item was scheduled to be deleted");
        By statusIconBy = By.cssSelector("#acn-active-content div.status-icon.deleted");
        CStudioSeleniumUtil.waitForItemToDisplay(driver, TestConstants.WAITING_SECONDS_WEB_ELEMENT, statusIconBy);
        WebElement statusIcon = driver.findElement(statusIconBy);
        assertTrue(statusIcon.getText().contains("Deleted"));
    }
}
