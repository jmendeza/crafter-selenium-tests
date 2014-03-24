package org.craftercms.web;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jonathan Méndez
 */
public class WebsiteTests extends BaseTest {

    private List<String> createdWebsites;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        createdWebsites = new ArrayList<String>();
    }

    @Override
    public void tearDown() throws Exception {
        for (String website : createdWebsites) {
            try {
                deleteWebsite(website);
                logger.info("Website '" + website + "' has been deleted");
            } catch (Exception ex) {
                logger.info("Error trying to delete website '" + website + "'");
            }
        }
        super.tearDown();
    }

    @Test
    public void createWebsiteTest() {
        login();

        long time = System.currentTimeMillis();
        String siteName = "Selenium Testing Site " + time;
        final String url = "selenium" + time;
        String description = "Selenium Testing Site";

        createWebsite(siteName, url, description);
    }

    @Test
    public void deleteWebsiteTest() throws InterruptedException {
        login();

        long time = System.currentTimeMillis();
        String siteName = "Selenium Testing Site " + time;
        final String url = "selenium" + time;
        String description = "Selenium Testing Site";

        createWebsite(siteName, url, description);

        deleteWebsite(url);
    }

    private void deleteWebsite(String url) throws InterruptedException {
        String baseUrl = seleniumProperties.getProperty("craftercms.base.url");
        String siteFinderUrl = seleniumProperties.getProperty("craftercms.site.site.finder");
        String newSiteDashboardUrl = String.format(seleniumProperties.getProperty("craftercms.site.dashboard.url"), url);
        final String siteRelativeUrl = newSiteDashboardUrl.substring(baseUrl.length());
        final By siteLinkBy = By.xpath("//a[@href='" + siteRelativeUrl + "']/ancestor::tr[1]");
        final String userDashBoardUrl = String.format(seleniumProperties.getProperty("craftercms.user.dashboard.url"), getUsername());

        logger.info("Navigate to site finder");
        driver.navigate().to(baseUrl + siteFinderUrl);

        logger.info("Search for site '" + url + "'");
        WebElement searchTermInput = driver.findElement(By.cssSelector("div.search-text input.search-term"));
        searchTermInput.sendKeys(url + "\n");

        logger.info("Click 'Delete'");
        CStudioSeleniumUtil.clickOn(driver, By.xpath("//div[contains(@class, 'results')]//button[text()='Delete']"));

        logger.info("Confirm deletion");
        CStudioSeleniumUtil.clickOn(driver, By.xpath("//div[@id='prompt']//button[text()='Delete']"));

        logger.info("Confirm deletion");
        CStudioSeleniumUtil.clickOn(driver, By.xpath("//div[@id='prompt']//button[text()='Yes']"));

        logger.info("Wait for site to be deleted");
        new WebDriverWait(driver, TestConstants.WAITING_SECONDS_SITE_DELETION).until(new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver driver) {
                driver.navigate().to(userDashBoardUrl);
                List<WebElement> siteLink = driver.findElements(siteLinkBy);
                return siteLink.size() == 0;
            }
        });
    }

    private void createWebsite(String siteName, final String url, String description) {
        String userDashBoardUrl = String.format(seleniumProperties.getProperty("craftercms.user.dashboard.url"), getUsername());

        logger.info("Creating site '" + url + "'");

        logger.info("Navigate to user dashboard");
        driver.navigate().to(userDashBoardUrl);

        logger.info("Click on 'Create Website'");
        By createSiteBy = By.xpath("//a[contains(@id,'createSite')]");
        CStudioSeleniumUtil.clickOn(driver, createSiteBy);

        logger.info("Enter site name");
        By siteNameInputBy = By.id("alfresco-createSite-instance-title");
        CStudioSeleniumUtil.clickOn(driver, siteNameInputBy);
        WebElement siteNameInput = driver.findElement(siteNameInputBy);
        siteNameInput.clear();
        siteNameInput.sendKeys(siteName);

        logger.info("Enter site url");
        By siteUrlInputBy = By.id("alfresco-createSite-instance-shortName");
        CStudioSeleniumUtil.clickOn(driver, siteUrlInputBy);
        WebElement siteUrlInput = driver.findElement(siteUrlInputBy);
        siteUrlInput.clear();
        siteUrlInput.sendKeys(url);

        logger.info("Enter site description");
        By siteDescriptionInputBy = By.id("alfresco-createSite-instance-description");
        CStudioSeleniumUtil.clickOn(driver, siteDescriptionInputBy);
        WebElement siteDescriptionInput = driver.findElement(siteDescriptionInputBy);
        siteDescriptionInput.clear();
        siteDescriptionInput.sendKeys(description);

        logger.info("Choose corporate blueprint");
        By siteBlueprintSelectBy = By.id("alfresco-createSite-instance-siteBlueprint");
        CStudioSeleniumUtil.waitForItemToBeEnabled(driver, TestConstants.WAITING_SECONDS_WEB_ELEMENT, siteBlueprintSelectBy);
        CStudioSeleniumUtil.waitForItemToDisplay(driver, TestConstants.WAITING_SECONDS_WEB_ELEMENT, siteBlueprintSelectBy);
        Select blueprintSelect = new Select(driver.findElement(siteBlueprintSelectBy));
        blueprintSelect.selectByValue("corporate");

        logger.info("Confirm site creation");
        CStudioSeleniumUtil.clickOn(driver, By.id("alfresco-createSite-instance-ok-button-button"));

        createdWebsites.add(url);

        logger.info("Wait for site to be created");
        new WebDriverWait(driver, TestConstants.WAITING_SECONDS_SITE_CREATION).until(new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver driver) {
                return driver.getTitle().contains("Crafter Studio") && driver.getCurrentUrl().contains(url);
            }
        });
    }
}
