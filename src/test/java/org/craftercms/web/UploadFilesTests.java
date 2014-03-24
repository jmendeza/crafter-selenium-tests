package org.craftercms.web;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import java.io.File;

import static org.junit.Assert.assertTrue;

/**
 * @author Jonathan Méndez
 */
public class UploadFilesTests extends BaseTest {

    @Test
    public void UploadJSFileTest() throws Exception {
        login();

        logger.info("Navigate to dashboard");
        driver.navigate().to(dashboardUrl);

        logger.info("Get file local path");
        File file = new File(CStudioSeleniumUtil.class.getClassLoader().getResource("uploadFiles/javascript.js").toURI());

        CStudioSeleniumUtil.ensureStaticAssetsTreeIsExpanded(driver);
        WebElement jsElement = CStudioSeleniumUtil.findItemWithName(driver, "js");
        CStudioSeleniumUtil.uploadFile(driver, file, jsElement);

        String deployedFilePath = seleniumProperties.getProperty("craftercms.preview.deployer.path") + "/static-assets/js/" + file.getName();

        logger.info("Check file contents in preview deployer path");
        String contentToCheck = "/* This is an empty javascript file */";
        assertTrue(CStudioSeleniumUtil.readFileContents(deployedFilePath, contentToCheck));
    }
}
