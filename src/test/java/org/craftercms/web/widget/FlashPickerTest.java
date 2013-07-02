package org.craftercms.web.widget;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.craftercms.web.CStudioSeleniumUtil;
import org.craftercms.web.WidgetBaseTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.thoughtworks.selenium.Selenium;

/**
 * 
 * @author roger.diaz
 * @TODO: Fix the test to work on phatomjs
 */
public class FlashPickerTest extends WidgetBaseTest {

	private static final Logger logger = Logger.getLogger("FlashPickerTest.class");
	
    protected String assetsPath;
    protected String uploadFlashDS;
    
    private String validationString;
    

    @Before
    public void setUp() throws Exception {
    	super.setUp("craftercms.flashpicker.widget.edit.page", "craftercms.flashpicker.widget.content.type");
    	assetsPath = seleniumProperties.getProperty("craftercms.assets.to.tests.path");
    	uploadFlashDS = seleniumProperties.getProperty("craftercms.flashpicker.widget.datasource.flashupload");
    	
    }
    
    public boolean containsClass(String[] classes, String className) {
    	boolean result = false;
    	if(classes != null) {
    		for(String c: classes){
    			if(c.equals(className)) {
    				result = true;
    				break;
    			}
    		}
    	}
    	
    	return result;
    }
    
    @Test
    public void testWidgetControlRequired() {
    	String flash = "/requiredFlash.swf";
    	//Clean the field
    	logger.info("Flash Required - Content no entered");
    	driver.findElement(By.cssSelector("#required-flash  input[value='Delete']")).click();
    	
    	validationString = driver.findElement(By.cssSelector("#required-flash .validation-hint")).getAttribute("class");
    	assertTrue(containsClass(validationString.split(" "), "cstudio-form-control-invalid"));
    	
    	logger.info("Flash Required - Content entered");
    	driver.findElement(By.cssSelector("#required-flash input[value='Add']")).click();
    	driver.findElement(By.cssSelector("#required-flash .cstudio-form-control-flash-picker-add-container-item")).click();
    	
    	WebElement element = (new WebDriverWait(driver, 30)).until(
        		ExpectedConditions.presenceOfElementLocated(By.cssSelector("#cstudio-wcm-popup-div")));
    	//Check the modal window
    	assertTrue(element!=null);
    	
    	driver.findElement(By.cssSelector("#uploadFileNameId")).sendKeys(assetsPath + flash);
    	driver.findElement(By.cssSelector("#uploadButton")).click();
    	
    	logger.info("Flash Required - Start Uploading...");
    	new WebDriverWait(driver, 60).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
              return !d.findElement(By.cssSelector("#required-flash .datum")).getAttribute("value").isEmpty();
            }
          });
    	
    	logger.info("Flash Required - Finish Uploading...");
    	validationString = driver.findElement(By.cssSelector("#required-flash .validation-hint")).getAttribute("class");
    	assertTrue(containsClass(validationString.split(" "), "cstudio-form-control-valid"));
    	validationString = driver.findElement(By.cssSelector("#required-flash .datum")).getAttribute("value");
    	assertTrue(validationString.equals(uploadFlashDS + flash));
    }
    
    @Test
    public void testWidgetControl() {
    	String flash = "/flash.swf";
    	//Clean the field
    	logger.info("Flash Required - Content no entered");
    	driver.findElement(By.cssSelector("#flash  input[value='Delete']")).click();
    	
    	validationString = driver.findElement(By.cssSelector("#flash .validation-hint")).getAttribute("class");
    	assertTrue(!containsClass(validationString.split(" "), "cstudio-form-control-invalid"));
    	
    	logger.info("Flash Required - Content entered");
    	driver.findElement(By.cssSelector("#flash input[value='Add']")).click();
    	driver.findElement(By.cssSelector("#flash .cstudio-form-control-flash-picker-add-container-item")).click();
    	
    	WebElement element = (new WebDriverWait(driver, 30)).until(
        		ExpectedConditions.presenceOfElementLocated(By.cssSelector("#cstudio-wcm-popup-div")));
    	//Check the modal window
    	assertTrue(element!=null);
    	
    	driver.findElement(By.cssSelector("#uploadFileNameId")).sendKeys(assetsPath + flash);
    	driver.findElement(By.cssSelector("#uploadButton")).click();
    	
    	logger.info("Flash Required - Start Uploading...");
    	new WebDriverWait(driver, 60).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
              return !d.findElement(By.cssSelector("#flash .datum")).getAttribute("value").isEmpty();
            }
          });
    	
    	logger.info("Flash Required - Finish Uploading...");
    	validationString = driver.findElement(By.cssSelector("#flash .validation-hint")).getAttribute("class");
    	assertTrue(!containsClass(validationString.split(" "), "cstudio-form-control-valid"));
    	validationString = driver.findElement(By.cssSelector("#flash .datum")).getAttribute("value");
    	assertTrue(validationString.equals(uploadFlashDS + flash));
    }
}
