package org.craftercms.web.basic;

import org.apache.commons.io.FileUtils;
import org.craftercms.web.BaseTest;
import org.craftercms.web.CStudioSeleniumUtil;
import org.craftercms.web.TestConstants;
import org.junit.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;

/**
 * @author Jonathan Méndez
 */
public class PreviewSyncTests extends BaseTest {

    @Test
    public void previewSyncTest() throws IOException, InterruptedException {
        logger.info("Delete preview deployer folder");
        String deployerContentPath = seleniumProperties.getProperty("craftercms.preview.deployer.content.root");
        String deployerMetadataPath = seleniumProperties.getProperty("craftercms.preview.deployer.metadata.root");
        File deployerContentRoot = new File(deployerContentPath);
        File deployerMetadataRoot = new File(deployerMetadataPath);

        logger.info("Delete Content folder");
        FileUtils.deleteDirectory(deployerContentRoot);
        logger.info("Delete Metadata folder");
        FileUtils.deleteDirectory(deployerMetadataRoot);

        login();

        logger.info("Navigate to dashboard");
        driver.navigate().to(dashboardUrl);

        logger.info("Click on Preview Sync");
        CStudioSeleniumUtil.ensureDropDownIsVisible(driver);
        CStudioSeleniumUtil.clickOn(driver, By.id("previewsync"));

        logger.info("Wait for preview sync to start");
        new WebDriverWait(driver, TestConstants.WAITING_SECONDS_DEPLOY).until(new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver driver) {
                try {
                    Alert alert = driver.switchTo().alert();
                    alert.accept();
                    return true;
                } catch (Exception ex) {
                    return false;
                }
            }
        });

        String baseUrl = seleniumProperties.getProperty("craftercms.base.url");
        final String pageToTestUrl = baseUrl + seleniumProperties.getProperty("craftercms.page.to.edit.url");

        String previewDeployerPath = seleniumProperties.getProperty("craftercms.preview.deployer.path");
        final File pageFile = new File(previewDeployerPath + seleniumProperties.getProperty("craftercms.page.to.edit"));

        logger.info("Wait for page file to be created again");
        new WebDriverWait(driver, TestConstants.WAITING_SECONDS_PREVIEW_SYNC).until(new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver driver) {
                return pageFile.exists();
            }
        });

        logger.info("Wait for preview sync for '" + pageToTestUrl + "' to be ready");
        new WebDriverWait(driver, TestConstants.WAITING_SECONDS_PREVIEW_SYNC).until(new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver driver) {
                try {
                    driver.navigate().to(pageToTestUrl);
                    CStudioSeleniumUtil.waitForItemToDisplay(driver, TestConstants.WAITING_SECONDS_WEB_ELEMENT, By.id("authoringContextNavHeader"));
                    return true;
                } catch (Exception ex) {
                    return false;
                }
            }
        });
    }
}
