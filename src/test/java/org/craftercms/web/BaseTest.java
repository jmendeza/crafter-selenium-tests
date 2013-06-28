package org.craftercms.web;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.craftercms.web.basic.LoginTests;
import org.junit.After;
import org.junit.Before;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.thoughtworks.selenium.Selenium;

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
}