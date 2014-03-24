/**
 *
 */
package org.craftercms.web;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.*;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;

import static org.junit.Assert.*;

/**
 * @author Praveen C Elineni
 *
 * Utility Class for Selenium
 *
 */
public class CStudioSeleniumUtil {
    private static final Logger logger = Logger.getLogger("CStudioSeleniumUtil.class");


    private static Properties seleniumProperties = new Properties();
    static {
        try {
    	    seleniumProperties.load(CStudioSeleniumUtil.class.getClassLoader().getResourceAsStream("selenium.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Login to Crafter Studio
     *
     * @param driver
     * @param userName
     * @param password
     * @param expected
     */
	public static void tryLogin(WebDriver driver, String userName, String password, boolean expected) {
        logger.info("Logging in");
        driver.get(seleniumProperties.getProperty("craftercms.login.page.url"));
        try {
            Thread.sleep(1000);
        } catch (Exception ex) {
        }
        CStudioSeleniumUtil.clickOn(driver, By.name("username"));
        WebElement userNameElement = driver.findElement(By.name("username"));
        userNameElement.clear();
        userNameElement.sendKeys(userName);
        CStudioSeleniumUtil.clickOn(driver, By.name("password"));
        WebElement passwordElement = driver.findElement(By.name("password"));
        passwordElement.clear();
        passwordElement.sendKeys(password);
        try {
            Thread.sleep(1000);
        } catch (Exception ex) {
        }
        logger.info("Username: '" + userNameElement.getAttribute("value") + "'");
        logger.info("Password: '" + passwordElement.getAttribute("value") + "'");
        WebElement submitButton = driver.findElement(By.id("page_x002e_components_x002e_slingshot-login_x0023_default-submit-button"));
        submitButton.click();

        String expectedUrl = String.format(seleniumProperties.getProperty("craftercms.user.dashboard.url"), userName);
        // Assert was rewritten this way so we can see both strings in tests log
        if (expected) {
            assertEquals(expectedUrl, driver.getCurrentUrl());
        } else {
            assertNotEquals(expectedUrl, driver.getCurrentUrl());
        }
    }

	/**
	 * Logout of crafter Studio
	 *
	 * @param driver
	 */
    public static void tryLogout(WebDriver driver) {
        driver.manage().window().maximize();
        By by = By.id("acn-logout-link");
        waitForItemToDisplay(driver, TestConstants.WAITING_SECONDS_WEB_ELEMENT, by);
        WebElement element = driver.findElement(by);
        element.click();
    }

	/**
	 * Try to access a page in Crafter Studio Site
	 *
	 * @param driver
	 * @param siteName
	 */
    public static void navigateToUrl(WebDriver driver, String siteName, String url) {
        driver.navigate().to(url);
        assertTrue(driver.getCurrentUrl().equals(String.format(seleniumProperties.getProperty("craftercms.site.dashboard.url"), siteName)));
    }

    /**
     * Execute JS before page edit
     *
     * @param driver
     * @param editPage
     */
    public static void editPageJS(WebDriver driver, String editPage, String contentType, String siteName) {
        try {
            // Wait for page to load
            Thread.sleep(5000);
        } catch (Exception ex) {
        }
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript(
        		"CStudioAuthoring.Operations.editContent(" +
        	            "'" + contentType + "', " +
        	            "'" + siteName + "', " +
        	            "'" + editPage + "', " +
        	            "'', " +
        	            "'" + editPage + "', " +
        	            "false, " +
        	            "{" +
        	            "  success: function() { " +
        	            "}," +
        	            "  failure: function() {" +
        	            "}" +
        	            "}" +
        	            ");");

        // Cancel schedule to edit, when needed
        List<WebElement> continueButton = driver.findElements(By.cssSelector(".cancel-workflow-view button.continue"));
        if(continueButton.size() > 0) {
            continueButton.get(0).click();
        }
    }

    /**
     * Execute JS to open create content window
     *
     * @param driver
     * @param path
     * @param siteName
     */
    public static void createContentJS(WebDriver driver, String path, String siteName) {
        try {// Wait for page to load
            Thread.sleep(5000);
        } catch (Exception ex) {
        }
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript(
                "CStudioAuthoring.Operations.createNewContent(" +
                        "'" + siteName + "', " +
                        "'" + path + "', " +
                        "false, " +
                        "{" +
                        "  success: function() { " +
                        "}," +
                        "  failure: function() {" +
                        "}" +
                        "}" +
                        ");");
    }

    /**
     * Execute JS to open create folder dialog
     *
     * @param driver
     * @param path
     * @param siteName
     */
    public static void createFolderJS(WebDriver driver, String path, String siteName) {
        try {// Wait for page to load
            Thread.sleep(5000);
        } catch (Exception ex) {
        }
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript(
                "CStudioAuthoring.Operations.createFolder(" +
                        "'" + siteName + "'," +
                        "'" + path + "'," +
                        "window," +
                        "{" +
                        "  success: function() { " +
                        "}," +
                        "  failure: function() {" +
                        "}" +
                        "}" +
                        ");");
    }

    /**
     * Click on element
     *
     * @param driver
     * @param by
     */
    public static void clickOn(WebDriver driver, By by) {
    	waitForItemToDisplay(driver, TestConstants.WAITING_SECONDS_WEB_ELEMENT, by);
    	waitForItemToBeEnabled(driver, TestConstants.WAITING_SECONDS_WEB_ELEMENT, by);
        driver.findElement(by).click();
    }

    /**
     * Wait for specified item to display
     *
     * @param driver
     * @param timeout
     * @param by
     */
    public static void waitForItemToDisplay(WebDriver driver, int timeout, final By by) {
        new WebDriverWait(driver, timeout).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                List<WebElement> elements = d.findElements(by);
                if (elements.size() == 0)
                    return false;
                return d.findElement(by).isDisplayed();
            }
        });
    }

    /**
     * Wait for specified item to be enabled
     *
     * @param driver
     * @param timeout
     * @param by
     */
    public static void waitForItemToBeEnabled(WebDriver driver, int timeout, final By by) {
        if (!driver.findElement(by).isEnabled())
          new WebDriverWait(driver, timeout).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
              return d.findElement(by).isEnabled();
            }
        });
    }

    /**
     * Read File Contents
     *
     * @param filePath
     * @param updatedString
     * @return
     */
    public static boolean readFileContents(String filePath, String updatedString) {
    	boolean result = false;
		try {
			File file = new File(filePath);
	        FileReader reader;
			reader = new FileReader(file);
	        BufferedReader in = new BufferedReader(reader);
	        String string;
	        while ((string = in.readLine()) != null) {
	            if (string.contains(updatedString)) {
	        	  result = true;
	        	  break;
	            }
	        }
	        in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
    }

    public static void loginAndEditPage(WebDriver driver, String userName, String password, String editPage, String contentType, String siteName) {
    	CStudioSeleniumUtil.tryLogin(driver, userName, password, true);

    	driver.navigate().to(String.format(seleniumProperties.getProperty("craftercms.site.dashboard.url"), siteName));

        // Execute JS before Edit Page
        CStudioSeleniumUtil.editPageJS(driver, editPage, contentType, siteName);

        switchToEditWindow(driver);
    }


    /**
     * Waits for a new window to show, then switches to it
     */
    public static void switchToEditWindow(WebDriver driver) {
        // Wait for the window to load
        new WebDriverWait(driver, TestConstants.WAITING_SECONDS_WEB_ELEMENT).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                return d.getWindowHandles().size() > 1;
            }
        });

        // Switch to edit window
        Set<String> handles = driver.getWindowHandles();
        for (String h : handles) {
            driver.switchTo().window(h);
            if (driver.getCurrentUrl().contains("cstudio-form"))
                break;
        }
        driver.manage().window().maximize();
    }

    /**
     * Waits for secondary window to close, then switch back to main window
     * @param driver
     */
    public static void switchToMainWindow(WebDriver driver) {
        new WebDriverWait(driver, TestConstants.WAITING_SECONDS_WEB_ELEMENT).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                return (d.getWindowHandles().size() == 1);
            }
        });

        // Navigate back to dashboard page and switch window
        Set<String> handles = driver.getWindowHandles();
        for (String h : handles) {
            driver.switchTo().window(h);
        }
    }

    public static void editAndSavePage(WebDriver driver, String editPage, String editString) {
        // Execute JS before Edit Page
        CStudioSeleniumUtil.editPageJS(driver, editPage,
                seleniumProperties.getProperty("craftercms.page.content.type"),
                seleniumProperties.getProperty("craftercms.sitename"));
        try {
            Thread.sleep(1000);
        } catch (Exception ex) {
        }
        // Cancel schedule to edit, when needed
        List<WebElement> continueButton = driver.findElements(By.cssSelector(".cancel-workflow-view button.continue"));
        if (continueButton.size() > 0) {
            continueButton.get(0).click();
        }
        switchToEditWindow(driver);

        // Find internal-name field and edit
        By internalNameBy = By.cssSelector("#internal-name .datum");
        CStudioSeleniumUtil.waitForItemToDisplay(driver, TestConstants.WAITING_SECONDS_WEB_ELEMENT, internalNameBy);
        WebElement internalNameElement = driver.findElement(internalNameBy);
        internalNameElement.clear();
        internalNameElement.sendKeys(editString);

        // Click Save&Close button and wait for change to complete
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        driver.findElement(By.id("cstudioSaveAndClose")).click();

        switchToMainWindow(driver);

        assertTrue(driver.getTitle().equals("Crafter Studio"));
    }

    public static void createArticle(WebDriver driver, String path, String articleUrl, String articleContent, String title, String siteName) {
        createContentJS(driver, path, siteName);

        clickOn(driver, By.cssSelector("option[value=\"/page/article\"]"));
        clickOn(driver, By.id("submitWCMPopup"));

        switchToEditWindow(driver);

        setField(driver, "div#file-name .datum", articleUrl);
        setField(driver, "div#internal-name .datum", title);
        setField(driver, "div#title .datum", title);
        setField(driver, "div#author .datum", "Crafter CMS");

        waitForItemToDisplay(driver, TestConstants.WAITING_SECONDS_WEB_ELEMENT, By.tagName("iframe"));

        // Put text in article main content
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript(
                "window.frames['mce_0_ifr'].document.getElementsByTagName('body')[0].innerHTML = '<p>" + articleContent + "</p>'");

        CStudioSeleniumUtil.clickOn(driver, By.id("cstudioSaveAndClose"));

        switchToMainWindow(driver);
    }

    private static void setField(WebDriver driver, String selector, String value) {
        waitForItemToDisplay(driver, TestConstants.WAITING_SECONDS_WEB_ELEMENT, By.cssSelector(selector));
        waitForItemToBeEnabled(driver, TestConstants.WAITING_SECONDS_WEB_ELEMENT, By.cssSelector(selector));
        WebElement element = driver.findElement(By.cssSelector(selector));
        element.sendKeys(value);
    }

    public static void createFolder(WebDriver driver, String folderName, String path, String siteName) {
        createFolderJS(driver, path, siteName);

        waitForItemToDisplay(driver, TestConstants.WAITING_SECONDS_WEB_ELEMENT, By.id("folderNameId"));

        WebElement folderNameField = driver.findElement(By.id("folderNameId"));
        folderNameField.sendKeys(folderName);

        driver.findElement(By.id("createButton")).click();

        new WebDriverWait(driver, TestConstants.WAITING_SECONDS_WEB_ELEMENT).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                return d.findElements(By.id("createButton")).size() == 0;
            }
        });
    }

    public static void createTout(WebDriver driver, String path, String toutName, String headline, String internalName, String siteName) {
        createContentJS(driver, path, siteName);

        clickOn(driver, By.cssSelector("option[value=\"/component/tout\"]"));
        clickOn(driver, By.id("submitWCMPopup"));

        switchToEditWindow(driver);

        setField(driver, "div#file-name .datum", toutName);
        setField(driver, "div#internal-name .datum", internalName);
        setField(driver, "div#headline .datum", headline);

        clickOn(driver, By.id("segments-All"));

        clickOn(driver, By.id("cstudioSaveAndClose"));

        switchToMainWindow(driver);
    }

    public static void contextMenuOption(WebDriver driver, String option, WebElement item) {
        final By menuItemsBy = By.cssSelector("#acn-context-menu li.yuimenuitem a");
        new Actions(driver).contextClick(item).perform();
        new WebDriverWait(driver, TestConstants.WAITING_SECONDS_WEB_ELEMENT).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                return d.findElements(menuItemsBy).size() > 0;
            }
        });
        new Actions(driver).moveToElement(driver.findElement(menuItemsBy)).build().perform();
        boolean optionFound = false;
        List<WebElement> menuItems = driver.findElements(menuItemsBy);
        assertTrue(menuItems.size() > 0);
        for (WebElement menuItem : menuItems) {
            if (menuItem.getAttribute("innerHTML").equals(option)) {
                menuItem.click();
                optionFound = true;
                break;
            }
        }
        assertTrue(optionFound);
    }

    public static void ensureDropDownIsVisible(WebDriver driver) throws InterruptedException {
        boolean dropDownVisible = false;
        final By dropDownMenuElementBy = By.id("acn-dropdown-menu-wrapper");
        List<WebElement> dropDownElement = driver.findElements(dropDownMenuElementBy);
        if (dropDownElement.size() > 0) {
            dropDownVisible = driver.findElement(dropDownMenuElementBy).isDisplayed();
        }

        if (!dropDownVisible) {
            By togglerBy = By.id("acn-dropdown-toggler");
            CStudioSeleniumUtil.clickOn(driver, togglerBy);

            new WebDriverWait(driver, TestConstants.WAITING_SECONDS_WEB_ELEMENT).until(new ExpectedCondition<Boolean>() {
                public Boolean apply(WebDriver d) {
                    return d.findElement(dropDownMenuElementBy).isDisplayed();
                }
            });
            Thread.sleep(5000);
        }
    }

    public static void ensurePagesTreeIsExpanded(WebDriver driver) throws InterruptedException {
        ensureDropDownIsVisible(driver);

        By homePageBy = By.xpath("//div[@id='acn-dropdown-menu-wrapper']//span[contains(.,'Home')]");
        List<WebElement> pagesTreeRoot = driver.findElements(homePageBy);
        boolean needToExpand = true;
        if (pagesTreeRoot.size() > 0) {
            if (pagesTreeRoot.get(0).isDisplayed()) {
                needToExpand = false;
            }
        }
        if (needToExpand) {
            clickOn(driver, By.id("pages-tree"));
            waitForItemToDisplay(driver, TestConstants.WAITING_SECONDS_WEB_ELEMENT, homePageBy);
        }

        final WebElement homeTableElement = driver.findElement(By.xpath("//span[contains(.,'Home')]/ancestor::table"));
        String homeTableClass = homeTableElement.getAttribute("class");
        needToExpand = homeTableClass.contains("collapsed");

        if (needToExpand) {
            homeTableElement.findElement(By.cssSelector("a.ygtvspacer")).click();
            new WebDriverWait(driver, TestConstants.WAITING_SECONDS_WEB_ELEMENT).until(new ExpectedCondition<Boolean>() {
                public Boolean apply(WebDriver d) {
                    return !homeTableElement.getAttribute("class").contains("collapsed");
                }
            });
        }
        Thread.sleep(2000);
    }

    public static void ensureComponentsTreeIsExpanded(WebDriver driver) throws InterruptedException {
        ensureDropDownIsVisible(driver);

        By componentsTreeRootBy = By.xpath("//div[@id='acn-dropdown-menu-wrapper']//span[contains(.,'components')]");
        List<WebElement> componentsTreeRoot = driver.findElements(componentsTreeRootBy);
        boolean needToExpand = true;
        if (componentsTreeRoot.size() > 0) {
            if (componentsTreeRoot.get(0).isDisplayed()) {
                needToExpand = false;
            }
        }
        if (needToExpand) {
            Thread.sleep(1000);
            clickOn(driver, By.id("components-tree"));
            waitForItemToDisplay(driver, TestConstants.WAITING_SECONDS_WEB_ELEMENT, componentsTreeRootBy);
        }

        By toutsFolderBy = By.xpath("//span[contains(.,'touts')]");
        List<WebElement> toutsFolder = driver.findElements(toutsFolderBy);
        needToExpand = true;
        if (toutsFolder.size() > 0) {
            if (toutsFolder.get(0).isDisplayed()) {
                needToExpand = false;
            }
        }
        if (needToExpand) {
            clickOn(driver, componentsTreeRootBy);
            waitForItemToDisplay(driver, TestConstants.WAITING_SECONDS_WEB_ELEMENT, toutsFolderBy);
        }
        final WebElement toutsTableElement = driver.findElement(By.xpath("//span[contains(.,'touts')]/ancestor::table"));
        String toutsClass = toutsTableElement.getAttribute("class");
        needToExpand = toutsClass.contains("collapsed");
        if (needToExpand) {
            clickOn(driver, toutsFolderBy);
            new WebDriverWait(driver, TestConstants.WAITING_SECONDS_WEB_ELEMENT).until(new ExpectedCondition<Boolean>() {
                public Boolean apply(WebDriver d) {
                    return !toutsTableElement.getAttribute("class").contains("collapsed");
                }
            });
        }
        Thread.sleep(2000);
    }

    public static void ensureTemplatesTreeIsExpanded(WebDriver driver) throws InterruptedException{
        ensureDropDownIsVisible(driver);

        By templatesTreeRootBy = By.xpath("//div[@id='acn-dropdown-menu-wrapper']//span[contains(.,'web')]");
        List<WebElement> componentsTreeRoot = driver.findElements(templatesTreeRootBy);
        boolean needToExpand = true;
        if (componentsTreeRoot.size() > 0) {
            if (componentsTreeRoot.get(0).isDisplayed()) {
                needToExpand = false;
            }
        }
        if (needToExpand) {
            Thread.sleep(1000);
            clickOn(driver, By.id("templates-tree"));
            waitForItemToDisplay(driver, TestConstants.WAITING_SECONDS_WEB_ELEMENT, templatesTreeRootBy);
        }



        By commonFolderBy = By.xpath("//span[contains(.,'common')]");
        List<WebElement> toutsFolder = driver.findElements(commonFolderBy);
        needToExpand = true;
        if (toutsFolder.size() > 0) {
            if (toutsFolder.get(0).isDisplayed()) {
                needToExpand = false;
            }
        }
        if (needToExpand) {
            clickOn(driver, templatesTreeRootBy);
            waitForItemToDisplay(driver, TestConstants.WAITING_SECONDS_WEB_ELEMENT, commonFolderBy);
        }
    }

    public static void ensureStaticAssetsTreeIsExpanded(WebDriver driver) throws InterruptedException {
        ensureDropDownIsVisible(driver);

        By assetsTreeFoldersBy = By.xpath("//a[@id='static assets-tree']/ancestor::div[contains(@class, 'assets-tree')]//table[contains(@class,'ygtvtable')]");
        List<WebElement> assetsTreeFolderTables = driver.findElements(assetsTreeFoldersBy);
        boolean needToExpand = true;
        if (assetsTreeFolderTables.size() > 0) {
            if (assetsTreeFolderTables.get(0).isDisplayed()) {
                needToExpand = false;
            }
        }
        if (needToExpand) {
            Thread.sleep(1000);
            clickOn(driver, By.id("static assets-tree"));
            waitForItemToDisplay(driver, TestConstants.WAITING_SECONDS_WEB_ELEMENT, assetsTreeFoldersBy);
        }
        assetsTreeFolderTables = driver.findElements(assetsTreeFoldersBy);
        for (WebElement folderTable : assetsTreeFolderTables) {
            if (folderTable.getAttribute("class").contains("collapsed")) {
                List<WebElement> folder = folderTable.findElements(By.cssSelector("span.acn-parent-folder"));
                if (folder.size() > 0) {
                    folder.get(0).click();
                    Thread.sleep(1000);
                }
            }
        }
    }

    /**
     * Find an element in content tree with name itemName
     * REQUIRES relevant part of content tree to be expanded.
     * @param driver web driver
     * @param itemName name of the item to find
     * @return WebElement with text containing itemName, or null if none was found
     */
    public static WebElement findItemWithName(WebDriver driver, String itemName) {
        List<WebElement> elements = driver.findElements(By.xpath("//div[@class='ygtvitem']//tr[@class='ygtvrow' and contains(.,'" + itemName + "')]"));
        if (elements.size() > 0) {
            return elements.get(0);
        }
        return null;
    }

    /**
     * Upload a file to the path represented by parentElement (element under Site Content drop down)
     * @param driver
     * @param file
     * @param parentElement
     */
    public static void uploadFile(WebDriver driver, File file, WebElement parentElement) {
        logger.info("Choose 'Upload' option in context menu");
        CStudioSeleniumUtil.contextMenuOption(driver, "Upload", parentElement);

        By fileInputBy = By.id("uploadFileNameId");
        CStudioSeleniumUtil.waitForItemToDisplay(driver, TestConstants.WAITING_SECONDS_WEB_ELEMENT, fileInputBy);
        CStudioSeleniumUtil.waitForItemToBeEnabled(driver, TestConstants.WAITING_SECONDS_DEPLOY, fileInputBy);

        logger.info("Indicate file path");
        WebElement fileInput = driver.findElement(fileInputBy);
        fileInput.sendKeys(file.getAbsolutePath());

        logger.info("Confirm upload");
        final By uploadButtonBy = By.id("uploadButton");
        driver.findElement(uploadButtonBy).click();

        logger.info("Wait for file to upload");
        new WebDriverWait(driver, TestConstants.WAITING_SECONDS_DEPLOY).until(new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver d) {
                return d.findElements(uploadButtonBy).size() == 0;
            }
        });
    }

    /**
     * Close and Quit driver connection
     *
     * @param driver
     */
    public static void exit(WebDriver driver) {
        driver.close();
        driver.quit();
    }
}