package org.craftercms.web;

import java.util.logging.Logger;

/**
 * @author Jonathan Méndez
 */
public class EditingTest extends BaseTest {

    protected final Logger logger = Logger.getLogger(getClass().getName());

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
     *
     * @return generated name
     */
    protected void createPageFolder(String folderName) {
        createFolder(folderName, "/website");
    }

    /**
     * Creates a folder at components root with the name name.
     *
     * @return generated name
     */
    protected void createComponentFolder(String folderName) {
        createFolder(folderName, "/components");
    }

    /**
     * Creates a folder with the name given, at the path given.
     *
     * @param folderName name the new folder will have
     * @param path       path to create the folder at. This path should be relative to /site/
     */
    protected void createFolder(String folderName, String path) {
        CStudioSeleniumUtil.createFolder(driver, folderName, "/site" + path, siteName);
    }
}
