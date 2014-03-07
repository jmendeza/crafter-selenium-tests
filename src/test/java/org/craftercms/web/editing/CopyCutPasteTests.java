package org.craftercms.web.editing;

import com.google.common.base.Predicate;
import org.craftercms.web.CStudioSeleniumUtil;
import org.craftercms.web.EditingTest;
import org.junit.Test;
import org.openqa.selenium.support.ui.FluentWait;

import java.io.File;
import java.util.concurrent.TimeUnit;

/**
 * @author Jonathan Méndez
 */
public class CopyCutPasteTests extends EditingTest {

    @Test
    public void copyPastePageTest() throws InterruptedException {
        login();
        logger.info("Navigate to dashboard");
        driver.navigate().to(dashboardUrl);

        logger.info("Create article");
        long time = System.currentTimeMillis();
        String articleTitle = "Article to copy/paste " + time;
        String articleContent = "This is the content of the article to be copied/pasted." + time;
        String articleUrl = "to_copy_paste_" + time;
        createArticle(articleTitle, articleContent, articleUrl);

        logger.info("Copy and paste page");
        pageOptionAndPaste(articleTitle, articleUrl, articleContent, "Copy");

        logger.info("Check original article still exists");
        checkPageFileExists("", articleUrl, true);
    }

    @Test
    public void cutPastePageTest() throws InterruptedException {
        login();
        logger.info("Navigate to dashboard");
        driver.navigate().to(dashboardUrl);

        logger.info("Create article");
        long time = System.currentTimeMillis();
        String articleTitle = "Article to cut/paste " + time;
        String articleContent = "This is the content of the article to be copied/pasted." + time;
        String articleUrl = "to_copy_paste_" + time;
        createArticle(articleTitle, articleContent, articleUrl);

        logger.info("Cut and paste page");
        pageOptionAndPaste(articleTitle, articleUrl, articleContent, "Cut");

        logger.info("Check original article no longer exists");
        checkPageFileExists("", articleUrl, false);
    }

    @Test
    public void copyPasteComponentTest() throws InterruptedException {
        login();
        logger.info("Navigate to dashboard");
        driver.navigate().to(dashboardUrl);

        logger.info("Create Tout");
        long time = System.currentTimeMillis();
        String toutName = "seleniumTout" + time;
        String headline = "Selenium tout headline " + time;
        String internalName = "Selenium Tout " + time;
        createTout(toutName, headline, internalName);

        logger.info("Copy and paste component");
        componentOptionAndPaste(internalName, toutName, headline, "Copy");

        logger.info("Check original article still exists");
        checkComponentFileExists("touts", toutName, true);
    }

    @Test
    public void cutPasteComponentTest() throws InterruptedException {
        login();
        logger.info("Navigate to dashboard");
        driver.navigate().to(dashboardUrl);

        logger.info("Create Tout");
        long time = System.currentTimeMillis();
        String toutName = "seleniumTout" + time;
        String headline = "Selenium tout headline " + time;
        String internalName = "Selenium Tout " + time;
        createTout(toutName, headline, internalName);

        logger.info("Cut and paste component");
        componentOptionAndPaste(internalName, toutName, headline, "Cut");

        logger.info("Check original article no longer exists");
        checkComponentFileExists("touts", toutName, false);
    }

    private void pageOptionAndPaste(String itemTitle, String itemUrl, String itemContent, String option) throws InterruptedException {
        logger.info("Create a folder to contain copy");
        String folderName = createPageFolder();

        logger.info("Refresh dashboard");
        driver.navigate().to(dashboardUrl);

        logger.info("Open context menu and click " + option + " for new article");
        CStudioSeleniumUtil.contextMenuOptionPage(driver, itemTitle, option);
        logger.info("Open context menu and click 'Paste' for new folder");
        CStudioSeleniumUtil.contextMenuOptionPage(driver, folderName, "Paste");
        checkPageFileExists(folderName, itemUrl, true);
    }

    private void componentOptionAndPaste(String itemTitle, String itemUrl, String itemContent, String option) throws InterruptedException {
        logger.info("Create a folder to contain copy");
        String folderName = createComponentFolder();

        logger.info("Refresh dashboard");
        driver.navigate().to(dashboardUrl);

        logger.info("Open context menu and click " + option + " for new article");
        CStudioSeleniumUtil.contextMenuOptionComponent(driver, itemTitle, option);
        logger.info("Open context menu and click 'Paste' for new folder");
        CStudioSeleniumUtil.contextMenuOptionComponent(driver, folderName, "Paste");
        checkComponentFileExists(folderName, itemUrl, true);
    }

    private void checkPageFileExists(String folderPath, String itemUrl, boolean expected) {
        String deployPath = seleniumProperties.getProperty("craftercms.preview.deployer.path");
        String copyFilePath = deployPath + "/site/website/" + folderPath + "/" + itemUrl + "/index.xml";
        checkItemFileExists(copyFilePath, expected);
    }

    private void checkComponentFileExists(String folderPath, String itemUrl, boolean expected) {
        String deployPath = seleniumProperties.getProperty("craftercms.preview.deployer.path");
        String copyFilePath = deployPath + "/site/components/" + folderPath + "/" + itemUrl + ".xml";
        checkItemFileExists(copyFilePath, expected);
    }

    private void checkItemFileExists(String copyFilePath, final boolean expected) {
        File copy = new File(copyFilePath);
        logger.info("Wait for item to be deployed");
        new FluentWait<File>(copy).withTimeout(30, TimeUnit.SECONDS).until(new Predicate<File>() {
            @Override
            public boolean apply(File file) {
                return file.exists() == expected;
            }
        });
    }
}
