package org.craftercms.web.editing;

import com.google.common.base.Predicate;
import org.craftercms.web.CStudioSeleniumUtil;
import org.craftercms.web.EditingTest;
import org.craftercms.web.TestConstants;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.FluentWait;

import java.io.File;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;

/**
 * @author Jonathan Méndez
 */
public class DeleteContentTests extends EditingTest {

    @Test
    public void deletePageTest() throws InterruptedException {
        logger.info("Login as admin");
        CStudioSeleniumUtil.tryLogin(driver,
                seleniumProperties.getProperty("craftercms.admin.username"),
                seleniumProperties.getProperty("craftercms.admin.password"),
                true);

        logger.info("Navigate to dashboard");
        driver.navigate().to(dashboardUrl);

        long time = System.currentTimeMillis();
        String url = "selenium" + time;
        String content = "Article main content " + time;
        String articlePath = "/site/website/" + url + "/index.xml";

        logger.info("Create article");
        createArticle(url, content, url);

        logger.info("Check content exists");
        assertTrue(CStudioSeleniumUtil.readFileContents(seleniumProperties.getProperty("craftercms.preview.deployer.path") + articlePath, content));

        logger.info("Refresh dashboard");
        driver.navigate().to(dashboardUrl);

        logger.info("Select new article");
        CStudioSeleniumUtil.clickOn(driver, By.id("MyRecentActivity-" + articlePath));

        logger.info("Click Delete option");
        CStudioSeleniumUtil.clickOn(driver, By.xpath("//a[text()='Delete']"));

        logger.info("Confirm deletion");
        CStudioSeleniumUtil.clickOn(driver, By.cssSelector("input.do-delete"));
        CStudioSeleniumUtil.clickOn(driver, By.cssSelector("#acnVersionWrapper input[value=\"OK\"]"));

        logger.info("Navigate to dashboard");
        driver.navigate().to(dashboardUrl);

        logger.info("Check content does not exist anymore");
        File contentFile = new File(seleniumProperties.getProperty("craftercms.preview.deployer.path") + articlePath);

        // wait for file to be deleted
        new FluentWait<File>(contentFile).withTimeout(TestConstants.WAITING_SECONDS_WEB_ELEMENT, TimeUnit.SECONDS).until(new Predicate<File>() {
            @Override
            public boolean apply(File file) {
                return !file.exists();
            }
        });
    }
}
