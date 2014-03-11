package org.craftercms.web.editing;

import org.craftercms.web.CStudioSeleniumUtil;
import org.craftercms.web.EditingTest;
import org.craftercms.web.TestConstants;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;

import java.io.File;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Jonathan Méndez
 */
public class DuplicateItemTests extends EditingTest {

    @Test
    public void duplicatePageTest() {
        logger.info("Login as admin");
        login();

        String duplicatedContent = "Duplicated main content " + System.currentTimeMillis();

        String articleUrl = createArticle();
        String articlePath = "/site/website/" + articleUrl + "/index.xml";

        String duplicatedUrl = openDuplicateWindow(articlePath);

        logger.info("Update duplicate main content");
        CStudioSeleniumUtil.waitForItemToDisplay(driver, TestConstants.WAITING_SECONDS_WEB_ELEMENT, By.tagName("iframe"));
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript(
                "window.frames['mce_0_ifr'].document.getElementsByTagName('body')[0].innerHTML = '<p>" + duplicatedContent + "</p>'");

        logger.info("Save and close duplicated");
        CStudioSeleniumUtil.clickOn(driver, By.id("cstudioSaveAndClose"));

        logger.info("Switch back to main window");
        CStudioSeleniumUtil.switchToMainWindow(driver);

        String duplicatedPath = "/site/website/" + duplicatedUrl + "/index.xml";

        logger.info("Check content was created.");
        assertTrue(CStudioSeleniumUtil.readFileContents(seleniumProperties.getProperty("craftercms.preview.deployer.path") + duplicatedPath, duplicatedContent));
    }

    @Test
    public void duplicatePageAndCancelTest() {
        logger.info("Login as admin");
        login();

        String articleUrl = createArticle();
        String articlePath = "/site/website/" + articleUrl + "/index.xml";

        String duplicatedUrl = openDuplicateWindow(articlePath);

        CStudioSeleniumUtil.waitForItemToDisplay(driver, 20, By.cssSelector("#internal-name input.datum"));

        logger.info("Cancel edition");
        CStudioSeleniumUtil.clickOn(driver, By.cssSelector(".cstudio-form-controls-button-container [value=\"Cancel\"]"));
        // Confirm to cancel
        CStudioSeleniumUtil.clickOn(driver, By.xpath("//button[text()='Yes']"));

        logger.info("Switch back to main window");
        CStudioSeleniumUtil.switchToMainWindow(driver);

        String duplicatedPath = "/site/website/" + duplicatedUrl + "/index.xml";

        logger.info("Check content was not created.");
        File contentFile = new File(seleniumProperties.getProperty("craftercms.preview.deployer.path") + duplicatedPath);
        assertFalse(contentFile.exists());
    }
}
