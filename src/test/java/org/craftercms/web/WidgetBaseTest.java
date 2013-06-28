package org.craftercms.web;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.thoughtworks.selenium.Selenium;

public class WidgetBaseTest extends TestWatcher {

	private static final Logger logger = Logger.getLogger("WidgetBaseTest.class");

	private final static String SELENIUM_PROPERTIES = "selenium.properties";
    protected WebDriver driver;
    protected Selenium selenium;
	private String screenshotOutputFolder;
    protected static DesiredCapabilities desiredCapabilities;
    protected Properties seleniumProperties = new Properties();

    public void setUp(String editPage, String contentType) throws Exception {
    	seleniumProperties.load(WidgetBaseTest.class.getClassLoader().getResourceAsStream(SELENIUM_PROPERTIES));

    	desiredCapabilities = new DesiredCapabilities();
        desiredCapabilities.setJavascriptEnabled(true);
        desiredCapabilities.setCapability("takesScreenshot", true);
        desiredCapabilities.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, 
        		                             seleniumProperties.getProperty("phantomjs.install.path"));
        driver = new PhantomJSDriver(desiredCapabilities);
    	driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
    	
    	this.setDriver(driver);
    	this.setScreenshotOutputFolder(seleniumProperties.getProperty("phantomjs.screenshot.folder.path"));

    	CStudioSeleniumUtil.loginAndEditPage(driver,
									seleniumProperties.getProperty("craftercms.admin.username"), 
									seleniumProperties.getProperty("craftercms.admin.password"),
									seleniumProperties.getProperty(editPage), 
									seleniumProperties.getProperty(contentType), 
									seleniumProperties.getProperty("craftercms.sitename"));
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