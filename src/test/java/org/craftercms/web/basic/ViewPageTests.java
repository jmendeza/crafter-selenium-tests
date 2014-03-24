package org.craftercms.web.basic;

import org.craftercms.web.BaseTest;
import org.craftercms.web.CStudioSeleniumUtil;
import org.craftercms.web.TestConstants;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Jonathan Méndez
 */
public class ViewPageTests extends BaseTest {

    @Test
    public void viewPageTest() throws InterruptedException {
        login();

        logger.info("Navigate to dashboard");
        driver.navigate().to(dashboardUrl);

        final String updatedTitle = "About Page workflow " + System.currentTimeMillis();
        String pageToSchedule = seleniumProperties.getProperty("craftercms.page.to.edit");

        logger.info("Edit page, updating title to '" + updatedTitle + "'");
        CStudioSeleniumUtil.editAndSavePage(driver, pageToSchedule, updatedTitle);

        logger.info("Refresh dashboard");
        driver.navigate().to(dashboardUrl);

        logger.info("Selecting 'View' option in page tree under 'Site Content'");
        CStudioSeleniumUtil.ensurePagesTreeIsExpanded(driver);
        WebElement pageElement = CStudioSeleniumUtil.findItemWithName(driver, updatedTitle);
        CStudioSeleniumUtil.contextMenuOption(driver, "View", pageElement);

        logger.info("Switch to 'View' window");
        CStudioSeleniumUtil.switchToEditWindow(driver);

        logger.info("Checking item name has been updated");
        By internalNameBy = By.cssSelector("#internal-name input.datum");
        CStudioSeleniumUtil.waitForItemToDisplay(driver, TestConstants.WAITING_SECONDS_WEB_ELEMENT, internalNameBy);
        WebElement internalName = driver.findElement(internalNameBy);
        assertTrue(internalName.getAttribute("value").equals(updatedTitle));

        logger.info("Check input elements are disabled");
        List<WebElement> inputs = driver.findElements(By.cssSelector("input"));
        for (WebElement input : inputs) {
            if (input.isDisplayed() && !input.getAttribute("type").equals("button"))
                assertFalse(input.isEnabled());
        }
    }
}
