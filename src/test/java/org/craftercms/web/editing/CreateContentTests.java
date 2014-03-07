package org.craftercms.web.editing;

import org.craftercms.web.CStudioSeleniumUtil;
import org.craftercms.web.EditingTest;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * @author Jonathan Méndez
 */
public class CreateContentTests extends EditingTest {

    @Test
    public void createArticleTest() {
        logger.info("Login as admin");
        login();

        logger.info("Navigate to dashboard");
        driver.navigate().to(dashboardUrl);

        long time = System.currentTimeMillis();
        String articleUrl = "selenium" + time;
        String articleContent = "Article main content " + time;
        String articlePath = "/site/website/" + articleUrl + "/index.xml";

        createArticle(articleUrl, articleContent, articleUrl);

        logger.info("Check content was created.");
        assertTrue(CStudioSeleniumUtil.readFileContents(seleniumProperties.getProperty("craftercms.preview.deployer.path") + articlePath, articleContent));
    }

    @Test
    public void createFolderTest() throws InterruptedException {
        logger.info("Login as admin");
        login();

        logger.info("Navigate to dashboard");
        driver.navigate().to(dashboardUrl);

        String folderName = createPageFolder();

        logger.info("Refresh dashboard");
        driver.navigate().to(dashboardUrl);

        logger.info("Check new folder exists");

        boolean folderFound = CStudioSeleniumUtil.checkItemIsInPageTree(driver, folderName);
        assertTrue(folderFound);
    }

    @Test
    public void createNestedContentTest() {
        logger.info("Login as admin");
        login();

        logger.info("Navigate to dashboard");
        driver.navigate().to(dashboardUrl);

        String outerFolderName = createPageFolder();
        String innerFolderName = "selenium" + System.currentTimeMillis();

        createFolder(innerFolderName, "/website/" + outerFolderName);


        String path = "/" + outerFolderName + "/" + innerFolderName;
        String articleUrl = "selenium" + System.currentTimeMillis();
        String articleContent = "Article main content " + System.currentTimeMillis();
        String articlePath = "/site/website/" + path + "/" + articleUrl + "/index.xml";

        createArticle(path, "Selenium test nested content", articleContent, articleUrl);

        logger.info("Check content was created.");
        assertTrue(CStudioSeleniumUtil.readFileContents(seleniumProperties.getProperty("craftercms.preview.deployer.path") + articlePath, articleContent));
    }
}
