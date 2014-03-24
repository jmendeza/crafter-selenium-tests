package org.craftercms.web.editing;

import org.craftercms.web.CStudioSeleniumUtil;
import org.craftercms.web.EditingTest;
import org.craftercms.web.TestConstants;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

/**
 * @author Jonathan Méndez
 */
public class ContextEditingTests extends EditingTest {

    @Test
    public void inContextEditTemplateTest() throws InterruptedException {
        login();

        logger.info("Navigate to dashboard");
        driver.navigate().to(dashboardUrl);

        final String baseUrl = seleniumProperties.getProperty("craftercms.base.url");
        final String pageToEditUrl = seleniumProperties.getProperty("craftercms.page.to.edit.url");

        logger.info("Navigate to '" + pageToEditUrl + "'");
        driver.navigate().to(baseUrl + pageToEditUrl);

        openTemplateEditor();

        String pageTitle = driver.getTitle();

        By titleBy = By.xpath("//div[@id='editor-container']//pre[text()='" + pageTitle + "'][1]");
        CStudioSeleniumUtil.waitForItemToDisplay(driver, TestConstants.WAITING_SECONDS_WEB_ELEMENT, titleBy);
        WebElement title = driver.findElement(titleBy);

        String sequence = "";
        // Get inside <title>...</title>
        for (int i = 0; i < 8; i++) {
            sequence += Keys.LEFT;
        }
        for (int i = 0; i < pageTitle.length(); i++) {
            sequence += Keys.BACK_SPACE;
        }

        final String newTitle = "Global Integrity " + System.currentTimeMillis();
        sequence += newTitle;

        logger.info("Update page title");
        new Actions(driver).click(title).sendKeys(sequence).perform();

        logger.info("Click 'Update' button");
        CStudioSeleniumUtil.clickOn(driver, By.id("template-editor-update-button"));

        logger.info("Wait for title to update");
        new WebDriverWait(driver, TestConstants.WAITING_SECONDS_DEPLOY).until(new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver driver) {
                driver.navigate().to(baseUrl + pageToEditUrl);
                return newTitle.equals(driver.getTitle());
            }
        });
    }

    @Test
    public void editPageIcePencilTest() throws InterruptedException {
        login();

        logger.info("Navigate to dashboard");
        driver.navigate().to(dashboardUrl);

        final String baseUrl = seleniumProperties.getProperty("craftercms.base.url");
        final String pageToEditUrl = seleniumProperties.getProperty("craftercms.page.to.edit.url");

        logger.info("Navigate to '" + pageToEditUrl + "'");
        driver.navigate().to(baseUrl + pageToEditUrl);

        openPreviewTools();

        logger.info("Turn on In-Context Edit");

        By titlePencilBy = By.cssSelector("#title img");
        List<WebElement> titlePencilList = driver.findElements(titlePencilBy);
        boolean pencilVisible = false;
        if (titlePencilList.size() > 0) {
            pencilVisible = titlePencilList.get(0).isDisplayed();
        }
        if (!pencilVisible) {
            By toggleIceBy = By.xpath("//div[@id='preview-tools-panel-container']//span[contains(.,'In-Context Edit')]/preceding-sibling::*[1]");
            CStudioSeleniumUtil.clickOn(driver, toggleIceBy);
        }

        logger.info("Click on title ice pencil");
        CStudioSeleniumUtil.clickOn(driver, titlePencilBy);

        CStudioSeleniumUtil.waitForItemToDisplay(driver, TestConstants.WAITING_SECONDS_WEB_ELEMENT, By.tagName("iframe"));
        Thread.sleep(1000);
        driver.switchTo().frame("in-context-edit-editor");

        logger.info("Update title");
        String newTitle = "Big Data for Risk Reduction " + System.currentTimeMillis();

        By titleInputBy = By.cssSelector("#title input.datum");
        CStudioSeleniumUtil.waitForItemToDisplay(driver, TestConstants.WAITING_SECONDS_WEB_ELEMENT, titleInputBy);
        CStudioSeleniumUtil.waitForItemToBeEnabled(driver, TestConstants.WAITING_SECONDS_WEB_ELEMENT, titleInputBy);

        WebElement titleInput = driver.findElement(titleInputBy);
        titleInput.clear();
        titleInput.sendKeys(newTitle);
        Thread.sleep(1000);

        logger.info("Save And Close");
        CStudioSeleniumUtil.clickOn(driver, By.id("cstudioSaveAndClose"));

        logger.info("Check title has been updated");
        CStudioSeleniumUtil.waitForItemToDisplay(driver, TestConstants.WAITING_SECONDS_WEB_ELEMENT, By.xpath("//*[contains(., '" + newTitle + "')]"));
    }

}
