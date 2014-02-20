/**
 * 
 */
package org.craftercms.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.Set;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * @author Praveen C Elineni
 * 
 * Utility Class for Selenium
 *
 */
public class CStudioSeleniumUtil {
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
        driver.get(seleniumProperties.getProperty("craftercms.login.page.url"));

        WebElement element = driver.findElement(By.name("username"));
        waitForItemToBeEnabled(driver, 30, By.name("username"));
        waitForItemToDisplay(driver, 30, By.name("username"));
        element.sendKeys(userName);
        element = driver.findElement(By.name("password"));
        element.sendKeys(password);
        element = driver.findElement(By.id("page_x002e_components_x002e_slingshot-login_x0023_default-submit-button"));
        element.click();

        assertEquals(driver.getCurrentUrl().equals(String.format(seleniumProperties.getProperty("craftercms.user.dashboard.url"), userName)), expected);
	}

	/**
	 * Logout of crafter Studio
	 * 
	 * @param driver
	 */
    public static void tryLogout(WebDriver driver) {
        WebElement element = driver.findElement(By.id("acn-logout-link"));
        driver.manage().window().maximize();
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
        try {
            WebElement continueButton = driver.findElement(By.cssSelector(".cancel-workflow-view button.continue"));
            continueButton.click();
        } catch (NoSuchElementException ex) {
        }
    }

    /**
     * Click on element
     * 
     * @param driver
     * @param by
     */
    public static void clickOn(WebDriver driver, By by) {
    	waitForItemToDisplay(driver, 30, by);
    	waitForItemToBeEnabled(driver, 30, by);
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
        if (!driver.findElement(by).isDisplayed())
          new WebDriverWait(driver, timeout).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
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

        // Wait for the window to load
        new WebDriverWait(driver, 60).until(new ExpectedCondition<Boolean>() {
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
    }

    public static void editAndSavePage(WebDriver driver, String editPage, String editString) {
        // Execute JS before Edit Page
        CStudioSeleniumUtil.editPageJS(driver, editPage,
                seleniumProperties.getProperty("craftercms.page.content.type"),
                seleniumProperties.getProperty("craftercms.sitename"));

        // Wait for the window to load
        new WebDriverWait(driver, 60).until(new ExpectedCondition<Boolean>() {
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

        // Find internal-name field and edit
        driver.findElement(By.cssSelector("#internal-name .datum")).clear();
        driver.findElement(By.cssSelector("#internal-name .datum")).sendKeys(editString);

        // Click Save&Close button and wait for change to complete
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        driver.findElement(By.id("cstudioSaveAndClose")).click();

        new WebDriverWait(driver, 60).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                return (d.getWindowHandles().size() == 1);
            }
        });

        // Navigate back to dashboard page and switch window
        handles = driver.getWindowHandles();
        for (String h : handles) {
            driver.switchTo().window(h);
        }

        assertTrue(driver.getTitle().equals("Crafter Studio"));
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