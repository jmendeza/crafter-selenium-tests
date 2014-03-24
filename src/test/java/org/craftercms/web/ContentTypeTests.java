package org.craftercms.web;

import org.junit.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Jonathan Méndez
 */
public class ContentTypeTests extends EditingTest {

    private static final String CONTENT_TYPE_NAME = "selenium";
    private static final String CONTENT_TYPE_DISPLAY_NAME = "SELENIUM";

    private String adminConsoleUrl;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        adminConsoleUrl = String.format(seleniumProperties.getProperty("craftercms.site.admin.console.url"), siteName);
    }

    @Test
    public void openContentTypeTest() {
        login();
        String contentType = "/page/article";

        openContentType(contentType);

        logger.info("Check content type fields");
        checkContentTypeField("file-name", "file-name");
        checkContentTypeField("input", "internal-name");
        checkContentTypeField("input", "title");
        checkContentTypeField("page-nav-order", "placeInNav");
        checkContentTypeField("rte", "body_html");
        checkContentTypeField("input", "author");
        checkContentTypeField("date-time", "publishDate_dt");
        checkContentTypeField("image-picker", "authorImage");
        checkContentTypeField("input", "pageTitle");
        checkContentTypeField("textarea", "keywords");
        checkContentTypeField("input", "description");

        logger.info("Check content type datasources");
        checkContentTypeDataSource("Desktop Images", "img-desktop-upload (image)", "desktopImages");
    }

    @Test
    public void editContentTypeFieldTitle() {
        login();
        String contentType = "/page/article";

        openContentType(contentType);

        logger.info("Update content type title field");
        String newTitle = "Title " + System.currentTimeMillis();
        By titleVariableBy = By.xpath("//div[@id='content-type-canvas']//div[contains(@class,'content-type-visual-field-container')]//span[contains(@class,'content-field-variable') and text()='title']");
        CStudioSeleniumUtil.clickOn(driver, titleVariableBy);

        By titleBy = By.xpath("//div[@id='properties-container']//div[contains(@class, 'property-wrapper')]/div[@class='property-label' and text()='Title']/following-sibling::input");
        CStudioSeleniumUtil.waitForItemToDisplay(driver, TestConstants.WAITING_SECONDS_WEB_ELEMENT, titleBy);
        WebElement titleInput = driver.findElement(titleBy);
        titleInput.clear();
        titleInput.sendKeys(newTitle);

        logger.info("Click 'Save'");
        CStudioSeleniumUtil.clickOn(driver, By.cssSelector("#cstudio-admin-console-command-bar input.cstudio-button[value='Save']"));

        logger.info("Wait for content type to be saved");
        new WebDriverWait(driver, TestConstants.WAITING_SECONDS_SAVE).until(new ExpectedCondition<Boolean>() {
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

        logger.info("Navigate to dashboard");
        driver.navigate().to(dashboardUrl);

        CStudioSeleniumUtil.createContentJS(driver, "/site/website", siteName);

        CStudioSeleniumUtil.clickOn(driver, By.cssSelector("option[value=\"/page/article\"]"));
        CStudioSeleniumUtil.clickOn(driver, By.id("submitWCMPopup"));

        CStudioSeleniumUtil.switchToEditWindow(driver);

        By titleLabelBy = By.cssSelector("#title span.label.cstudio-form-field-title");
        CStudioSeleniumUtil.waitForItemToDisplay(driver, TestConstants.WAITING_SECONDS_WEB_ELEMENT, titleLabelBy);
        WebElement titleLabelElement = driver.findElement(titleLabelBy);
        assertEquals(newTitle, titleLabelElement.getText());
    }

    @Test
    public void createContentTypeTest() throws URISyntaxException, InterruptedException {
        login();

        logger.info("Navigate to dashboard");
        driver.navigate().to(dashboardUrl);

        logger.info("Expand static assets tree");
        CStudioSeleniumUtil.ensureTemplatesTreeIsExpanded(driver);

        logger.info("Get file local path");
        File file = new File(CStudioSeleniumUtil.class.getClassLoader().getResource("uploadFiles/SELENIUM.ftl").toURI());

        WebElement jsElement = CStudioSeleniumUtil.findItemWithName(driver, "web");
        CStudioSeleniumUtil.uploadFile(driver, file, jsElement);

        logger.info("Navigate to admin console");
        driver.navigate().to(adminConsoleUrl);

        createContentType();

        addField("Input", "Title", "internal-name");
        addField("File name", "Page URL", "file-name");
        addField("Text Area", "Content", "seleniumContent");

        setTemplate();

        logger.info("Click 'Save'");
        By saveButtonBy = By.cssSelector("#cstudio-admin-console-command-bar input.cstudio-button[value='Save']");
        CStudioSeleniumUtil.clickOn(driver, saveButtonBy);

        new WebDriverWait(driver, TestConstants.WAITING_SECONDS_SAVE).until(new ExpectedCondition<Boolean>() {

            @Override
            public Boolean apply(WebDriver driver) {
                try {
                    Alert alert = driver.switchTo().alert();
                    assertTrue(alert.getText().contains("Saved"));
                    alert.accept();
                    return true;
                } catch (Exception ex) {
                    return false;
                }
            }
        });

        String url = "selenium" + System.currentTimeMillis();
        String content = createSeleniumContent(url);
        String contentTypeTag = "<content-type>/page/selenium</content-type>";

        logger.info("Wait for item to deploy");
        Thread.sleep(1000 * TestConstants.WAITING_SECONDS_DEPLOY);

        String deployedFilePath = seleniumProperties.getProperty("craftercms.preview.deployer.path") + "/site/website/" + url + "/index.xml";

        logger.info("Check file contents in preview deployer path");
        assertTrue(CStudioSeleniumUtil.readFileContents(deployedFilePath, content));
        assertTrue(CStudioSeleniumUtil.readFileContents(deployedFilePath, contentTypeTag));

        String baseUrl = seleniumProperties.getProperty("craftercms.base.url");

        logger.info("Navigate to new page");
        driver.navigate().to(baseUrl + "/" + url);

        logger.info("Check page");
        WebElement contentDiv = driver.findElement(By.tagName("div"));
        assertTrue(contentDiv.getText().equals(content));
    }

    private void setTemplate() {
        CStudioSeleniumUtil.clickOn(driver, By.cssSelector("#content-type-canvas div.content-form-name"));

        By templateInputBy = By.xpath("//div[@id='properties-container']//div[contains(@class, 'property-wrapper')]/div[@class='property-label' and text()='Display Template']/following-sibling::input");
        CStudioSeleniumUtil.clickOn(driver, templateInputBy);

        By templatePickBy = By.xpath("//div[@id='properties-container']//div[contains(@class, 'property-wrapper')]/div[@class='property-label' and text()='Display Template']/following-sibling::div[@class='options']/div[@class='pick']");
        CStudioSeleniumUtil.clickOn(driver, templatePickBy);

        // Wait for browse window to load
        new WebDriverWait(driver, TestConstants.WAITING_SECONDS_WEB_ELEMENT).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                return d.getWindowHandles().size() > 1;
            }
        });

        // Switch to browse window
        Set<String> handles = driver.getWindowHandles();
        for (String h : handles) {
            driver.switchTo().window(h);
            if (driver.getCurrentUrl().contains("cstudio-browse"))
                break;
        }
        driver.manage().window().maximize();

        By radioButtonBy = By.cssSelector("#result-select--templates-web-SELENIUM-ftl");
        CStudioSeleniumUtil.clickOn(driver, radioButtonBy);

        CStudioSeleniumUtil.clickOn(driver, By.id("formSaveButton"));

        CStudioSeleniumUtil.switchToMainWindow(driver);
    }

    private String createSeleniumContent(String url) {
        logger.info("Navigate to dashboard");
        driver.navigate().to(dashboardUrl);

        CStudioSeleniumUtil.createContentJS(driver, "/site/website", siteName);

        CStudioSeleniumUtil.clickOn(driver, By.cssSelector("option[value=\"/page/selenium\"]"));
        CStudioSeleniumUtil.clickOn(driver, By.id("submitWCMPopup"));

        CStudioSeleniumUtil.switchToEditWindow(driver);

        String contentText = "Selenium content type article main content " + url;

        logger.info("Set values to fields");
        setField("div#file-name .datum", url);
        setField("div#internal-name .datum", url);
        setField("div#seleniumContent .datum", contentText);

        logger.info("Save & Close");
        driver.findElement(By.id("cstudioSaveAndClose")).click();

        CStudioSeleniumUtil.switchToMainWindow(driver);

        CrafterContent content = new CrafterContent();
        content.path = "/site/website/" + url;
        content.uri = "/site/website/" + url + "/index.xml";
        content.browserUri = "/" + url;

        getCreatedContent().add(content);
        return contentText;
    }

    private void setField(String selector, String value) {
        By selectorBy = By.cssSelector(selector);
        CStudioSeleniumUtil.waitForItemToDisplay(driver, TestConstants.WAITING_SECONDS_WEB_ELEMENT, selectorBy);
        CStudioSeleniumUtil.waitForItemToBeEnabled(driver, TestConstants.WAITING_SECONDS_WEB_ELEMENT, selectorBy);
        WebElement element = driver.findElement(selectorBy);
        element.clear();
        element.sendKeys(value);
    }


    private void addField(String type, String title, String variable) throws InterruptedException {
        logger.info(String.format("Adding field: '%s' with type: '%s' and variable: '%s'", title, type, variable));

        By propertiesBy = By.cssSelector("#admin-console div.content-type-visual-section-container span.content-section-name");
        CStudioSeleniumUtil.waitForItemToDisplay(driver, TestConstants.WAITING_SECONDS_WEB_ELEMENT, propertiesBy);
        WebElement propertiesElement = driver.findElement(propertiesBy);
        propertiesElement.click();

        By inputControlBy = By.xpath("//div[@id='widgets-container']//div[contains(@class, 'new-control-type') and text()='" + type + "']");
        CStudioSeleniumUtil.waitForItemToDisplay(driver, TestConstants.WAITING_SECONDS_WEB_ELEMENT, inputControlBy);
        CStudioSeleniumUtil.waitForItemToBeEnabled(driver, TestConstants.WAITING_SECONDS_WEB_ELEMENT, inputControlBy);

        WebElement inputControl = driver.findElement(inputControlBy);

        new Actions(driver).clickAndHold(inputControl).release(propertiesElement).perform();

        By fieldBy = By.xpath("//div[@id='content-type-canvas']//div[contains(@class,'content-type-visual-field-container')]/span[contains(@class, 'content-field-name') and string-length(text()) = 0]/ancestor::*[1]");
        CStudioSeleniumUtil.waitForItemToBeEnabled(driver, TestConstants.WAITING_SECONDS_WEB_ELEMENT, fieldBy);
        CStudioSeleniumUtil.waitForItemToDisplay(driver, TestConstants.WAITING_SECONDS_WEB_ELEMENT, fieldBy);
        Thread.sleep(2000);
        driver.findElement(fieldBy).click();

        By titleBy = By.xpath("//div[@id='properties-container']//div[contains(@class, 'property-wrapper')]/div[@class='property-label' and text()='Title']/following-sibling::input");
        CStudioSeleniumUtil.waitForItemToDisplay(driver, TestConstants.WAITING_SECONDS_WEB_ELEMENT, titleBy);
        WebElement titleInput = driver.findElement(titleBy);
        titleInput.clear();
        titleInput.sendKeys(title);

        By variableBy = By.xpath("//div[@id='properties-container']//div[contains(@class, 'property-wrapper')]/div[@class='property-label' and text()='Variable / Name']/following-sibling::input");
        CStudioSeleniumUtil.waitForItemToDisplay(driver, TestConstants.WAITING_SECONDS_WEB_ELEMENT, variableBy);
        WebElement variableInput = driver.findElement(variableBy);
        variableInput.clear();
        variableInput.sendKeys(variable);
    }

    private void createContentType() throws InterruptedException {
        logger.info("Click 'Content Types'");
        By contentTypesBy = By.xpath("//div[contains(@class,'cstudio-admin-console-item') and text()='Content Types']");
        CStudioSeleniumUtil.clickOn(driver, contentTypesBy);

        logger.info("Click 'Create New Type'");
        By openTypeBy = By.xpath("//div[@id='acn-bar']//div[@class='acn-link']/a[text()='Create New Type']");
        CStudioSeleniumUtil.clickOn(driver, openTypeBy);

        logger.info("Setting creation parameters");
        logger.info("Setting display name");
        By displayNameInputBy = By.id("contentTypeDisplayName");
        CStudioSeleniumUtil.waitForItemToDisplay(driver, TestConstants.WAITING_SECONDS_WEB_ELEMENT, displayNameInputBy);
        WebElement displayNameInput = driver.findElement(displayNameInputBy);
        displayNameInput.clear();
        displayNameInput.sendKeys(CONTENT_TYPE_DISPLAY_NAME);

        logger.info("Setting system name");
        By systemNameInputBy = By.id("contentTypeName");
        CStudioSeleniumUtil.waitForItemToDisplay(driver, TestConstants.WAITING_SECONDS_WEB_ELEMENT, systemNameInputBy);
        WebElement systemNameInput = driver.findElement(systemNameInputBy);
        systemNameInput.clear();
        systemNameInput.sendKeys(CONTENT_TYPE_NAME);

        By typeSelectBy = By.id("contentTypeObjectType");
        CStudioSeleniumUtil.waitForItemToDisplay(driver, TestConstants.WAITING_SECONDS_WEB_ELEMENT, typeSelectBy);

        Select typeSelect = new Select(driver.findElement(typeSelectBy));
        typeSelect.selectByValue("page");

        logger.info("Setting 'Content as folder'");
        By modelAsIndexBy = By.id("contentTypeAsFolder");
        CStudioSeleniumUtil.waitForItemToBeEnabled(driver, TestConstants.WAITING_SECONDS_WEB_ELEMENT, modelAsIndexBy);
        WebElement modelAsIndex = driver.findElement(modelAsIndexBy);
        Boolean modelAsIndexChecked = Boolean.valueOf(modelAsIndex.getAttribute("checked"));
        if (!modelAsIndexChecked) {
            modelAsIndex.click();
        }

        logger.info("Create content type");
        By createButtonBy = By.id("createButton");
        CStudioSeleniumUtil.waitForItemToBeEnabled(driver, TestConstants.WAITING_SECONDS_WEB_ELEMENT, createButtonBy);
        driver.findElement(createButtonBy).click();
    }

    private void openContentType(String contentType) {
        logger.info("Navigate to admin console");
        driver.navigate().to(adminConsoleUrl);

        logger.info("Click 'Content Types'");
        By contentTypesBy = By.xpath("//div[contains(@class,'cstudio-admin-console-item') and text()='Content Types']");
        CStudioSeleniumUtil.clickOn(driver, contentTypesBy);

        logger.info("Click 'Open Existing Type'");
        By openTypeBy = By.xpath("//div[@id='acn-bar']//div[@class='acn-link']/a[text()='Open Existing Type']");
        CStudioSeleniumUtil.clickOn(driver, openTypeBy);

        logger.info("Choose article content type");
        By articleContentTypeBy = By.cssSelector("#wcm-content-types-dropdown option[value='" + contentType + "']");
        CStudioSeleniumUtil.clickOn(driver, articleContentTypeBy);

        logger.info("Confirm");
        CStudioSeleniumUtil.clickOn(driver, By.id("submitWCMPopup"));

        logger.info("Wait for content type to load");
        By contentTypeContainerBy = By.cssSelector("#content-type-canvas .content-type-visual-container");
        CStudioSeleniumUtil.waitForItemToDisplay(driver, TestConstants.WAITING_SECONDS_WEB_ELEMENT, contentTypeContainerBy);
    }

    private void checkContentTypeDataSource(String name, String type, String variable) {
        By fieldNameBy = By.xpath("//div[@id='content-type-canvas']//div[contains(@class,'content-type-datasources-container')]//span[contains(@class,'content-datasource-name') and text()='" + name + "']");
        By fieldTypeBy = By.xpath("//div[@id='content-type-canvas']//div[contains(@class,'content-type-datasources-container')]//span[contains(@class,'content-datasource-type') and text()='" + type + "']");
        By fieldVariableBy = By.xpath("//div[@id='content-type-canvas']//div[contains(@class,'content-type-datasources-container')]//span[contains(@class,'content-datasource-variable') and text()='" + variable + "']");

        CStudioSeleniumUtil.waitForItemToDisplay(driver, TestConstants.WAITING_SECONDS_WEB_ELEMENT, fieldNameBy);
        CStudioSeleniumUtil.waitForItemToDisplay(driver, TestConstants.WAITING_SECONDS_WEB_ELEMENT, fieldTypeBy);
        CStudioSeleniumUtil.waitForItemToDisplay(driver, TestConstants.WAITING_SECONDS_WEB_ELEMENT, fieldVariableBy);
    }

    private void checkContentTypeField(String type, String variable) {
        By fieldTypeBy = By.xpath("//div[@id='content-type-canvas']//div[contains(@class,'content-type-visual-field-container')]//span[contains(@class,'content-field-type') and text()='" + type + "']");
        By fieldVariableBy = By.xpath("//div[@id='content-type-canvas']//div[contains(@class,'content-type-visual-field-container')]//span[contains(@class,'content-field-variable') and text()='" + variable + "']");

        CStudioSeleniumUtil.waitForItemToDisplay(driver, TestConstants.WAITING_SECONDS_WEB_ELEMENT, fieldTypeBy);
        CStudioSeleniumUtil.waitForItemToDisplay(driver, TestConstants.WAITING_SECONDS_WEB_ELEMENT, fieldVariableBy);
    }
}
