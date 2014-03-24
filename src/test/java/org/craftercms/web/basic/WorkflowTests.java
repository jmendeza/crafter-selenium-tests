package org.craftercms.web.basic;

import org.craftercms.web.BaseTest;
import org.craftercms.web.CStudioSeleniumUtil;
import org.craftercms.web.TestConstants;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Jonathan Méndez
 */
public class WorkflowTests extends BaseTest {

    private String adminUsername;
    private String adminPassword;
    private String authorUsername;
    private String authorPassword;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        adminUsername = seleniumProperties.getProperty("craftercms.admin.username");
        adminPassword = seleniumProperties.getProperty("craftercms.admin.password");
        authorUsername = seleniumProperties.getProperty("craftercms.author.username");
        authorPassword = seleniumProperties.getProperty("craftercms.author.password");
    }

    private void useAdminUser() {
        setUsername(adminUsername);
        setPassword(adminPassword);
    }

    private void useAuthorUser() {
        setUsername(authorUsername);
        setPassword(authorPassword);
    }

    @Test
    public void acceptWorkflowTest() throws InterruptedException {
        final String updatedTitle = schedulePage();

        logout();
        useAdminUser();
        login();

        logger.info("Navigate to dashboard");
        driver.navigate().to(dashboardUrl);

        logger.info("Check Go Live Queue widget");
        new WebDriverWait(driver, TestConstants.WAITING_SECONDS_WEB_ELEMENT).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                return d.findElement(By.id("GoLiveQueue-body")).getText().contains(updatedTitle);
            }
        });

        logger.info("Check item");
        By checkInputBy = By.xpath("//div[@id='GoLiveQueue']//input[@id='" + seleniumProperties.getProperty("craftercms.page.to.edit") + "']");
        CStudioSeleniumUtil.clickOn(driver, checkInputBy);

        logger.info("Select Go Live Now");
        CStudioSeleniumUtil.clickOn(driver, By.xpath("//a[text()='Go Live Now']"));

        By setToNowBy = By.id("globalSetToNow");
        CStudioSeleniumUtil.waitForItemToDisplay(driver, 10, setToNowBy);
        // Wait for item to be fully initialized
        Thread.sleep(1000);
        CStudioSeleniumUtil.clickOn(driver, setToNowBy);

        logger.info("Confirm Go Live Now");
        By goLiveSubmitBy = By.id("golivesubmitButton");
        CStudioSeleniumUtil.waitForItemToDisplay(driver, 10, goLiveSubmitBy);
        // Wait for item to be fully initialized
        Thread.sleep(1000);
        CStudioSeleniumUtil.clickOn(driver, goLiveSubmitBy);

        By okBy = By.id("acnOKButton");
        logger.info("Waiting for item to go-live...");
        CStudioSeleniumUtil.waitForItemToDisplay(driver, 10, okBy);
        Thread.sleep(1000);
        CStudioSeleniumUtil.clickOn(driver, okBy);

        logger.info("Refresh dashboard");
        driver.navigate().to(dashboardUrl);

        logger.info("Waiting for item to deploy");
        Thread.sleep(1000 * TestConstants.WAITING_SECONDS_DEPLOY);

        logger.info("Open file in live folder and check content was updated");
        String filePath = seleniumProperties.getProperty("craftercms.live.deployer.path") + seleniumProperties.getProperty("craftercms.page.to.edit");
        assertTrue(CStudioSeleniumUtil.readFileContents(filePath, updatedTitle));
    }

    @Test
    public void rejectWorkflowTest() throws InterruptedException {
        final String updatedTitle = schedulePage();

        logout();
        useAdminUser();
        login();

        logger.info("Navigate to dashboard");
        driver.navigate().to(dashboardUrl);

        logger.info("Check Go Live Queue widget");
        new WebDriverWait(driver, TestConstants.WAITING_SECONDS_WEB_ELEMENT).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                return d.findElement(By.id("GoLiveQueue-body")).getText().contains(updatedTitle);
            }
        });

        logger.info("Check item");
        By checkInputBy = By.xpath("//div[@id='GoLiveQueue']//input[@id='" + seleniumProperties.getProperty("craftercms.page.to.edit") + "']");
        CStudioSeleniumUtil.clickOn(driver, checkInputBy);

        logger.info("Select Reject");
        CStudioSeleniumUtil.clickOn(driver, By.xpath("//a[text()='Reject']"));

        logger.info("Confirm Rejection");
        By goLiveSubmitBy = By.id("golivesubmitButton");
        CStudioSeleniumUtil.waitForItemToDisplay(driver, 10, goLiveSubmitBy);
        // Wait for item to be fully initialized
        Thread.sleep(1000);
        CStudioSeleniumUtil.clickOn(driver, goLiveSubmitBy);

        By okBy = By.id("acnOKButton");
        logger.info("Waiting for rejection to complete...");
        CStudioSeleniumUtil.waitForItemToDisplay(driver, 10, okBy);
        Thread.sleep(1000);
        CStudioSeleniumUtil.clickOn(driver, okBy);

        logger.info("Refresh dashboard");
        driver.navigate().to(dashboardUrl);


        Thread.sleep(1000 * TestConstants.WAITING_SECONDS_DEPLOY);

        logger.info("Open file in live folder and check content was not updated");
        String filePath = seleniumProperties.getProperty("craftercms.live.deployer.path") + seleniumProperties.getProperty("craftercms.page.to.edit");
        assertFalse(CStudioSeleniumUtil.readFileContents(filePath, updatedTitle));
    }

    private String schedulePage() throws InterruptedException {
        useAuthorUser();
        login();

        logger.info("Navigate to dashboard");
        driver.navigate().to(dashboardUrl);

        final String updatedTitle = "About Page workflow " + System.currentTimeMillis();
        String pageToSchedule = seleniumProperties.getProperty("craftercms.page.to.edit");

        logger.info("Edit page, updating title to '" + updatedTitle + "'");
        CStudioSeleniumUtil.editAndSavePage(driver, pageToSchedule, updatedTitle);

        logger.info("Refresh dashboard");
        driver.navigate().to(dashboardUrl);

        logger.info("Check my-recent activity widget");
        new WebDriverWait(driver, TestConstants.WAITING_SECONDS_WEB_ELEMENT).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                return d.findElement(By.id("MyRecentActivity-body")).getText().contains(updatedTitle);
            }
        });

        logger.info("Check item");
        CStudioSeleniumUtil.clickOn(driver, By.id("MyRecentActivity-" + seleniumProperties.getProperty("craftercms.page.to.edit")));

        logger.info("Select Go Live Now");
        CStudioSeleniumUtil.clickOn(driver, By.xpath("//a[text()='Submit to Go Live']"));
        Thread.sleep(1000);

        logger.info("Confirm Go Live Now");
        CStudioSeleniumUtil.clickOn(driver, By.id("golivesubmitButton"));
        Thread.sleep(1000);

        CStudioSeleniumUtil.clickOn(driver, By.id("acnOKButton"));
        logger.info("Waiting for item to go-live...");
        Thread.sleep(1000);

        return updatedTitle;
    }
}
