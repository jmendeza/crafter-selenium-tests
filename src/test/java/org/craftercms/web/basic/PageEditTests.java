/**
 *
 */
package org.craftercms.web.basic;

import org.craftercms.web.BaseTest;
import org.craftercms.web.CStudioSeleniumUtil;
import org.junit.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static org.junit.Assert.assertTrue;

/**
 * @author Praveen C Elineni
 */
public class PageEditTests extends BaseTest {

    private static final Logger logger = Logger.getLogger("PageEditTests.class");

    private String updateString = "About Us Page Updated";

    /**
     * Test Page Save and Close Functionality
     *
     * @throws InterruptedException
     */
    @Test
    public void testPageEditSaveAndClose() throws InterruptedException {
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

        logger.info("Login as admin");
        login();

        logger.info("Navigate to Dashboard page");
        driver.navigate().to(dashboardUrl);

        logger.info("Edit page");
        CStudioSeleniumUtil.editPageJS(driver, seleniumProperties.getProperty("craftercms.page.to.edit"),
                seleniumProperties.getProperty("craftercms.page.content.type"),
                siteName);

        CStudioSeleniumUtil.switchToEditWindow(driver);

        logger.info("Find internal-name field and edit");
        driver.findElement(By.cssSelector("#internal-name .datum")).clear();
        driver.findElement(By.cssSelector("#internal-name .datum")).sendKeys(updateString);

        logger.info("Click Save&Close button and wait for change to complete");
        driver.findElement(By.id("cstudioSaveAndClose")).click();

        CStudioSeleniumUtil.switchToMainWindow(driver);

        logger.info("Navigate back to dashboard");
        driver.navigate().to(dashboardUrl);
        assertTrue(driver.getTitle().equals("Crafter Studio"));

        logger.info("Check my-recent-activity widget");
        new WebDriverWait(driver, 60).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                return d.findElement(By.id("MyRecentActivity-body")).getText().contains(updateString);
            }
        });
    }

    /**
     * Test Page Save and Preview Functionality
     *
     * @throws InterruptedException
     */
    @Test
    public void testPageEditSaveAndPreview() throws InterruptedException {
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

        logger.info("Login as admin");
        login();

        String mainWindowHandle = driver.getWindowHandle();

        logger.info("Navigate to Dashboard page");
        driver.navigate().to(dashboardUrl);

        logger.info("Edit page");
        CStudioSeleniumUtil.editPageJS(driver, seleniumProperties.getProperty("craftercms.page.to.edit"),
                seleniumProperties.getProperty("craftercms.page.content.type"),
                siteName);

        CStudioSeleniumUtil.switchToEditWindow(driver);

        String editWindowHandle = driver.getWindowHandle();

        logger.info("Edit internal-name field");
        WebElement internalNameElement = driver.findElement(By.cssSelector("#internal-name .datum"));
        internalNameElement.clear();
        internalNameElement.sendKeys(updateString);


        logger.info("Click Save&Preview button and wait for change to complete");
        driver.findElement(By.id("cstudioSaveAndPreview")).click();

        logger.info("Switch back to first window");
        driver.switchTo().window(mainWindowHandle);

        logger.info("Wait for preview to load");
        new WebDriverWait(driver, 30, 100).until(new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver webDriver) {
                try {
                    Alert alert = driver.switchTo().alert();
                    alert.accept();
                    return true;
                } catch (Exception ex) {
                    return false;
                }
            }
        });

        String pageUrl = seleniumProperties.getProperty("craftercms.base.url") + seleniumProperties.getProperty("craftercms.page.to.edit.url");

        logger.info("Check url match edited page url");
        assertTrue(driver.getCurrentUrl().equals(pageUrl));
        logger.info("Check item content has changed");
        assertTrue(CStudioSeleniumUtil.readFileContents(seleniumProperties.getProperty("craftercms.preview.deployer.path") + seleniumProperties.getProperty("craftercms.page.to.edit"), updateString));

        logger.info("Go back and close edit window so item is not locked");
        driver.switchTo().window(editWindowHandle);
        driver.findElement(By.cssSelector("input[value=\"Cancel\"]")).click();
        List<WebElement> buttonConfirm = driver.findElements(By.xpath("//button[text()='Yes']"));
        if (buttonConfirm.size() > 0)
            buttonConfirm.get(0).click();
    }
}