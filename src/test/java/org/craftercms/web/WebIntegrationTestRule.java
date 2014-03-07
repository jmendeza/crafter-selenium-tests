package org.craftercms.web;

import org.apache.commons.io.FileUtils;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public class WebIntegrationTestRule extends TestWatcher {

	private static final Logger logger = Logger.getLogger("WebIntegrationTestRule.class");

	private String screenshotOutputFolder;
    private WebDriver driver;


    public WebIntegrationTestRule(String screenshotOutputFolder, WebDriver driver) {
        this.screenshotOutputFolder = screenshotOutputFolder;
        this.driver = driver;
    }

    @Override
    public void failed(Throwable e, Description description) {

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
}