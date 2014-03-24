package org.craftercms.web.editing;

import org.craftercms.web.CStudioSeleniumUtil;
import org.craftercms.web.EditingTest;
import org.craftercms.web.TestConstants;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Jonathan Méndez
 */
public class ChangeTemplateTests extends EditingTest {

    @Test
    public void changeTemplateToArticleTest() throws InterruptedException {
        login();
        long time = System.currentTimeMillis();
        String articleUrl = "selenium" + time;
        String articleTitle = "Selenium Article" + time;
        String articleContent = "This is a testing article content. " + time;
        createArticle(articleTitle, articleContent, articleUrl);

        logger.info("Wait for item to be deployed");
        Thread.sleep(1000 * TestConstants.WAITING_SECONDS_DEPLOY);

        String articleFilePath = seleniumProperties.getProperty("craftercms.preview.deployer.path") + "/site/website/" + articleUrl + "/index.xml";

        String contentTypeTag = "<content-type>" + seleniumProperties.getProperty("craftercms.page.content.type") + "</content-type>";
        assertTrue(CStudioSeleniumUtil.readFileContents(articleFilePath, contentTypeTag));
        assertTrue(CStudioSeleniumUtil.readFileContents(articleFilePath, articleContent));

        String newContentType = seleniumProperties.getProperty("craftercms.change.template.content.type");

        logger.info("Refresh dashboard");
        driver.navigate().to(dashboardUrl);

        logger.info("Choose 'Change Template' in pages tree context menu");
        CStudioSeleniumUtil.ensurePagesTreeIsExpanded(driver);
        WebElement articleElement = CStudioSeleniumUtil.findItemWithName(driver, articleTitle);
        CStudioSeleniumUtil.contextMenuOption(driver, "Change&nbsp;Template", articleElement);

        logger.info("Confirm change template");
        CStudioSeleniumUtil.clickOn(driver, By.id("acceptCTChange"));

        logger.info("Choose '" + newContentType + "' as new content type");
        CStudioSeleniumUtil.clickOn(driver, By.cssSelector("option[value=\"" + newContentType + "\"]"));

        logger.info("Confirm operation");
        CStudioSeleniumUtil.clickOn(driver, By.id("submitWCMPopup"));

        CStudioSeleniumUtil.switchToEditWindow(driver);
        logger.info("Save and close");
        CStudioSeleniumUtil.clickOn(driver, By.id("cstudioSaveAndClose"));

        CStudioSeleniumUtil.switchToMainWindow(driver);

        logger.info("Wait for item to be deployed");
        Thread.sleep(1000 * TestConstants.WAITING_SECONDS_DEPLOY);

        String newContentTypeTag = "<content-type>" + newContentType + "</content-type>";

        logger.info("Check item has been updated");
        assertTrue(CStudioSeleniumUtil.readFileContents(articleFilePath, articleTitle));
        assertFalse(CStudioSeleniumUtil.readFileContents(articleFilePath, contentTypeTag));
        assertTrue(CStudioSeleniumUtil.readFileContents(articleFilePath, newContentTypeTag));
    }
}
