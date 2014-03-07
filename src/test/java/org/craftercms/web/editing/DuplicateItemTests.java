package org.craftercms.web.editing;

import org.craftercms.web.CStudioSeleniumUtil;
import org.craftercms.web.EditingTest;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

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

        openDuplicateWindow(articlePath);

        logger.info("Read duplicate article url");
        CStudioSeleniumUtil.waitForItemToDisplay(driver, 20, By.cssSelector("#file-name input.datum"));
        WebElement urlElement = driver.findElement(By.cssSelector("#file-name input.datum"));
        String duplicatedUrl = urlElement.getAttribute("value");

        logger.info("Update duplicate main content");
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

    private void openDuplicateWindow(String articlePath) {
        logger.info("Refresh dashboard");
        driver.navigate().to(dashboardUrl);

        CStudioSeleniumUtil.clickOn(driver, By.id("MyRecentActivity-" + articlePath));
        CStudioSeleniumUtil.clickOn(driver, By.xpath("//a[text()='Duplicate']"));

        CStudioSeleniumUtil.switchToEditWindow(driver);
    }

    @Test
    public void duplicatePageAndCancelTest() {
        logger.info("Login as admin");
        login();

        String articleUrl = createArticle();
        String articlePath = "/site/website/" + articleUrl + "/index.xml";

        openDuplicateWindow(articlePath);

        logger.info("Read duplicate article fields");
        CStudioSeleniumUtil.waitForItemToDisplay(driver, 20, By.cssSelector("#file-name input.datum"));
        WebElement urlElement = driver.findElement(By.cssSelector("#file-name input.datum"));
        String duplicatedUrl = urlElement.getAttribute("value");

        CStudioSeleniumUtil.waitForItemToDisplay(driver, 20, By.cssSelector("#internal-name input.datum"));
        WebElement internalNameElement = driver.findElement(By.cssSelector("#internal-name input.datum"));
        String duplicatedInternalName = internalNameElement.getAttribute("value");

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
