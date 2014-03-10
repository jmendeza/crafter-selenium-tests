package org.craftercms.web.editing;

import org.craftercms.web.CStudioSeleniumUtil;
import org.craftercms.web.EditingTest;
import org.craftercms.web.TestConstants;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Jonathan Méndez
 */
public class HistoryTests extends EditingTest {

    @Test
    public void checkHistoryTest() throws InterruptedException {
        login();
        createVersionedArticle(4, "selenium_" + System.currentTimeMillis());
    }

    @Test
    public void restorePreviousVersionTest() throws InterruptedException {
        login();
        int versionCount = 4;
        int restoredVersion = 1;
        long time = System.currentTimeMillis();
        String titlePrefix = "selenium" + "_" + time;
        String articleFullUrl = createVersionedArticle(versionCount, titlePrefix);
        // Version number, ignoring original one.
        String restoredVersionTitle = createTitle(titlePrefix, restoredVersion - 1);

        historyOption(articleFullUrl);

        logger.info("Revert to second version");
        // Since versions are sorted in descendant order, nth version would be (versionCount - n)
        By revertBy = By.cssSelector("#acnVersionWrapper .history-listing table :nth-child(" + (versionCount - restoredVersion) + ") .c5");
        CStudioSeleniumUtil.waitForItemToDisplay(driver, TestConstants.WAITING_SECONDS_WEB_ELEMENT, revertBy);
        WebElement revertElement = driver.findElement(revertBy);
        revertElement.click();

        String deployPath = seleniumProperties.getProperty("craftercms.preview.deployer.path");
        String filePath = deployPath + articleFullUrl;

        logger.info("Wait for deployment");
        Thread.sleep(5000);

        logger.info("Check content corresponds to restoredVersion");
        assertTrue(CStudioSeleniumUtil.readFileContents(filePath, restoredVersionTitle));
    }

    private String createTitle(String prefix, int version) {
        return prefix + "_version_" + version;
    }

    private String createVersionedArticle(int versions, String titlePrefix) throws InterruptedException {
        String articleUrl = titlePrefix;
        String articleContent = titlePrefix + " Article main content ";
        String articleTitle = titlePrefix + "_original";

        createArticle(articleTitle, articleContent, articleUrl);
        String articleFullUrl = "/site/website/" + articleUrl + "/index.xml";
        for (int currentVersion = 0; currentVersion < versions - 1; currentVersion++) {
            logger.info("Wait to create a new version");
            Thread.sleep(3000);
            String newValue = createTitle(titlePrefix, currentVersion);
            CStudioSeleniumUtil.editAndSavePage(driver, articleFullUrl, newValue);
        }
        historyOption(articleFullUrl);

        logger.info("Check number of versions.");
        By historyTableItemsBy = By.cssSelector("#acnVersionWrapper .history-listing tr .c5");
        CStudioSeleniumUtil.waitForItemToDisplay(driver, TestConstants.WAITING_SECONDS_WEB_ELEMENT, historyTableItemsBy);

        List<WebElement> historyTableItems = driver.findElements(historyTableItemsBy);
        assertEquals(historyTableItems.size(), versions);

        return articleFullUrl;
    }

    private void historyOption(String articleFullUrl) {
        logger.info("Check item history");
        driver.navigate().to(dashboardUrl);

        logger.info("Select new article");
        CStudioSeleniumUtil.clickOn(driver, By.id("MyRecentActivity-" + articleFullUrl));

        logger.info("Click History option");
        CStudioSeleniumUtil.clickOn(driver, By.xpath("//a[text()='History']"));
    }
}
