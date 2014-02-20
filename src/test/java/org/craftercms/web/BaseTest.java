package org.craftercms.web;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.craftercms.web.basic.LoginTests;
import org.junit.After;
import org.junit.Before;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.openqa.selenium.*;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.thoughtworks.selenium.Selenium;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.junit.Assert.assertTrue;

public class BaseTest extends TestWatcher {

	private static final Logger logger = Logger.getLogger("BaseTest.class");

    private final static String SELENIUM_PROPERTIES = "selenium.properties";

    protected WebDriver driver;
	private String screenshotOutputFolder;
    protected Selenium selenium;
    protected static DesiredCapabilities desiredCapabilities;
    protected Properties seleniumProperties = new Properties();

    @Before
    public void setUp() throws Exception {
    	seleniumProperties.load(LoginTests.class.getClassLoader().getResourceAsStream(SELENIUM_PROPERTIES));

    	desiredCapabilities = new DesiredCapabilities();
        desiredCapabilities.setJavascriptEnabled(true);
        desiredCapabilities.setCapability("takesScreenshot", true);
        desiredCapabilities.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, 
        		                             seleniumProperties.getProperty("phantomjs.install.path"));
        driver = new PhantomJSDriver(desiredCapabilities);
        screenshotOutputFolder = seleniumProperties.getProperty("phantomjs.screenshot.folder.path");
        driver.manage().window().maximize();
    }

	@Override
    public void failed(Throwable e, Description description) {
	   System.out.println("TEST CASE FAILED");
       try {
            File shoot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(shoot, new File(screenshotOutputFolder +
                    File.separator + description.getMethodName() + ".png"));
        } catch (IOException ex) {
        	logger.info("Unable to save screenshot");
        }
    }

    @Override
    protected void finished(Description description) {
        driver.quit();
    }

    @After
    public void tearDown() throws Exception {
        CStudioSeleniumUtil.exit(driver);
    }

    public String getScreenshotOutputFolder() {
		return screenshotOutputFolder;
	}

	public void setScreenshotOutputFolder(String screenshotOutputFolder) {
		this.screenshotOutputFolder = screenshotOutputFolder;
	}

	public WebDriver getDriver() {
		return driver;
	}

	public void setDriver(WebDriver driver) {
		this.driver = driver;
	}

    protected void editAndSaveUtil(String editPage, String editString) {
        // Execute JS before Edit Page
        logger.info("edit page");
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
        logger.info("save and close form");
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
        logger.info("Navigate back to dashboard");
        handles = driver.getWindowHandles();
        for (String h : handles) {
            driver.switchTo().window(h);
        }

        assertTrue(driver.getTitle().equals("Crafter Studio"));
    }

}