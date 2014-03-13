package org.craftercms.web;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jonathan Méndez
 */
public class EditingTest extends BaseTest {

    private List<CrafterContent> createdContents;

    /**
     * Creates a new article with generated fields.
     *
     * @return article url
     */
    protected String createArticle() {
        long time = System.currentTimeMillis();
        String articleUrl = "selenium" + time;
        String articleContent = "Article main content " + time;
        String articleTitle = "SELENIUM" + time;
        createArticle(articleTitle, articleContent, articleUrl);
        return articleUrl;
    }

    /**
     * Creates a new article in root.
     *
     * @param title   title of new article
     * @param content content of new article
     * @param url     url of new article
     */
    protected void createArticle(String title, String content, String url) {
        createArticle("", title, content, url);
    }

    /**
     * Creates a new article
     *
     * @param path    path to create the article in. Path should be relative to "/site/website/"
     * @param title   title of new article
     * @param content content of new article
     * @param url     url of new article
     */
    protected void createArticle(String path, String title, String content, String url) {
        logger.info("Navigate to dashboard");
        driver.navigate().to(dashboardUrl);

        logger.info("Create article");
        CStudioSeleniumUtil.createArticle(driver, "/site/website" + path, url, content, title, siteName);

        CrafterContent article = new CrafterContent();
        article.path = "/site/website/" + url;
        article.uri = "/site/website/" + url + "/index.xml";
        article.browserUri = "/" + url;
        createdContents.add(article);
    }

    /**
     * Creates a tout
     *
     * @param path         path to create the tout in. Should be relative to /site/components/touts
     * @param toutName     name of the tout
     * @param headline     tout's headline
     * @param internalName internal name of the tout
     */
    protected void createTout(String path, String toutName, String headline, String internalName) {
        CStudioSeleniumUtil.createTout(driver, "/site/components/touts/" + path, toutName, headline, internalName, siteName);

        CrafterContent tout = new CrafterContent();
        tout.path = "/site/components/touts/" + path;
        tout.browserUri = "/touts/" + toutName + ".xml";
        tout.uri = "/site/components" + tout.browserUri;
        createdContents.add(tout);
    }

    /**
     * Creates a tout at touts root
     *
     * @param toutName     name of the tout
     * @param headline     tout's headline
     * @param internalName internal name of the tout
     */
    protected void createTout(String toutName, String headline, String internalName) {
        createTout("", toutName, headline, internalName);
    }

    /**
     * Creates a folder at pages root with a generated name.
     *
     * @return generated name
     */
    protected String createPageFolder() {
        String folderName = "selenium" + System.currentTimeMillis();
        createPageFolder(folderName);
        return folderName;
    }

    /**
     * Creates a folder at components root with a generated name.
     *
     * @return generated name
     */
    protected String createComponentFolder() {
        String folderName = "selenium" + System.currentTimeMillis();
        createComponentFolder(folderName);
        return folderName;
    }

    /**
     * Creates a folder at pages root with the name name.
     */
    protected void createPageFolder(String folderName) {
        createPageFolder(folderName, "");
    }

    /**
     * Creates a folder at path with the name name.
     */
    protected void createPageFolder(String folderName, String path) {
        createFolder(folderName, "/website" + path);

        CrafterContent folder = new CrafterContent();
        folder.uri = folder.path = "/site/website/" + folderName;
        folder.browserUri = path + "/" + folderName;
        createdContents.add(folder);
    }

    /**
     * Creates a folder at components root with the name name.
     *
     * @return generated name
     */
    protected void createComponentFolder(String folderName) {
        createFolder(folderName, "/components");

        CrafterContent folder = new CrafterContent();
        folder.browserUri = folder.uri = folder.path = "/site/components/" + folderName;
        createdContents.add(folder);
    }

    /**
     * Creates a folder with the name given, at the path given.
     *
     * @param folderName name the new folder will have
     * @param path       path to create the folder at. This path should be relative to /site/
     */
    private void createFolder(String folderName, String path) {
        CStudioSeleniumUtil.createFolder(driver, folderName, "/site" + path, siteName);
    }

    protected String openDuplicateWindow(String articlePath) {
        logger.info("Refresh dashboard");
        driver.navigate().to(dashboardUrl);

        CStudioSeleniumUtil.clickOn(driver, By.id("MyRecentActivity-" + articlePath));
        CStudioSeleniumUtil.clickOn(driver, By.xpath("//a[text()='Duplicate']"));

        CStudioSeleniumUtil.switchToEditWindow(driver);

        logger.info("Read duplicate article url");
        CStudioSeleniumUtil.waitForItemToDisplay(driver, 20, By.cssSelector("#file-name input.datum"));
        WebElement urlElement = driver.findElement(By.cssSelector("#file-name input.datum"));
        String duplicatedUrl = urlElement.getAttribute("value");

        CrafterContent article = new CrafterContent();
        article.path = "/site/website/" + duplicatedUrl;
        article.uri = "/site/website/" + duplicatedUrl + "/index.xml";
        article.browserUri = "/" + duplicatedUrl;
        createdContents.add(article);

        return duplicatedUrl;
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        createdContents = new ArrayList<CrafterContent>();
    }

    @Override
    public void tearDown() throws Exception {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        for (CrafterContent content : createdContents) {
            try {
                driver.navigate().to(dashboardUrl);
                js.executeScript(
                        "CStudioAuthoring.Operations.deleteContent([{" +
                                "'path': '" + content.path + "'," +
                                "'uri': '" + content.uri + "'," +
                                "'browserUri': '" + content.browserUri + "'" +
                                "}]);");
                CStudioSeleniumUtil.clickOn(driver, By.cssSelector("input.do-delete"));
            } catch (Exception ex) {
                logger.info("Error while trying to delete '" + content.path + "', " + ex.getMessage());
            }
        }
        super.tearDown();
    }

    private class CrafterContent {
        public String path;
        public String uri;
        public String browserUri;
    }
}
