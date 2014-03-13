package org.craftercms.web;

import com.thoughtworks.selenium.Selenium;
import org.apache.commons.io.FileUtils;
import org.craftercms.web.basic.LoginTests;
import org.junit.After;
import org.junit.Before;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

public class BaseTest extends TestWatcher {

    protected final Logger logger = Logger.getLogger(getClass().getName());

    private final static String SELENIUM_PROPERTIES = "selenium.properties";

    protected WebDriver driver;
    private String screenshotOutputFolder;
    protected Selenium selenium;
    protected static DesiredCapabilities desiredCapabilities;
    protected Properties seleniumProperties = new Properties();
    protected String dashboardUrl;
    protected String siteName;

    @Before
    public void setUp() throws Exception {
        seleniumProperties.load(LoginTests.class.getClassLoader().getResourceAsStream(SELENIUM_PROPERTIES));

        initializeDriver();
        screenshotOutputFolder = seleniumProperties.getProperty("phantomjs.screenshot.folder.path");
        driver.manage().window().maximize();
        siteName = seleniumProperties.getProperty("craftercms.sitename");
        dashboardUrl = String.format(seleniumProperties.getProperty("craftercms.site.dashboard.url"), siteName);
    }

    protected void initializeDriver() {
        desiredCapabilities = new DesiredCapabilities();
        desiredCapabilities.setJavascriptEnabled(true);
        desiredCapabilities.setCapability("takesScreenshot", true);

        driver = new ChromeDriver(desiredCapabilities);
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

    protected void login() {
        logger.info("Login as " + getUsername());
        CStudioSeleniumUtil.tryLogin(driver,
                getUsername(),
                getPassword(),
                true);
    }

    protected void logout() {
        CStudioSeleniumUtil.tryLogout(driver);
    }

    protected String getUsername() {
        return seleniumProperties.getProperty("craftercms.admin.username");
    }

    protected String getPassword() {
        return seleniumProperties.getProperty("craftercms.admin.password");
    }

}