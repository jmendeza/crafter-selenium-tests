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
 *
 */
public class ImagePickerTest extends WidgetBaseTest{

	private static final Logger logger = Logger.getLogger("ImagePickerTest.class");
	
    protected String assetsPath;
    protected String uploadImageDS;
    protected String uploadImageDS2;
    protected String browseImageDS;
    
    private String validationString;
    

    @Before
    public void setUp() throws Exception {
    	super.setUp("craftercms.imagepicker.widget.edit.page", "craftercms.imagepicker.widget.content.type");
    	assetsPath = seleniumProperties.getProperty("craftercms.assets.to.tests.path");
    	uploadImageDS = seleniumProperties.getProperty("craftercms.imagepicker.widget.datasource.imageupload");
    	browseImageDS = seleniumProperties.getProperty("craftercms.imagepicker.widget.datasource.imagebrowse");
    	
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
    	String image = "/requiredImage.png";
    	//Clean the field
    	logger.info("Image Required - Content no entered");
    	driver.findElement(By.cssSelector("#required-image  input[value='Delete']")).click();
    	
    	validationString = driver.findElement(By.cssSelector("#required-image .validation-hint")).getAttribute("class");
    	assertTrue(containsClass(validationString.split(" "), "cstudio-form-control-invalid"));
    	
    	logger.info("Image Required - Content entered");
    	driver.findElement(By.cssSelector("#required-image input[value='Add']")).click();
    	
    	WebElement element = (new WebDriverWait(driver, 30)).until(
        		ExpectedConditions.presenceOfElementLocated(By.cssSelector("#cstudio-wcm-popup-div")));
    	//Check the modal window
    	assertTrue(element!=null);
    	
    	driver.findElement(By.cssSelector("#uploadFileNameId")).sendKeys(assetsPath + image);
    	driver.findElement(By.cssSelector("#uploadButton")).click();
    	
    	logger.info("Image Required - Start Uploading...");
    	new WebDriverWait(driver, 60).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
              return !d.findElement(By.cssSelector("#required-image .datum")).getAttribute("value").isEmpty();
            }
          });
    	
    	logger.info("Image Required - Finish Uploading...");
    	validationString = driver.findElement(By.cssSelector("#required-image .validation-hint")).getAttribute("class");
    	assertTrue(containsClass(validationString.split(" "), "cstudio-form-control-valid"));
    	validationString = driver.findElement(By.cssSelector("#required-image .datum")).getAttribute("value");
    	assertTrue(validationString.equals(uploadImageDS + image));
    }
    
    @Test
    public void testWidgetControlFixedSize() {
    	String wrongImage = "/fixedImage2.png";
    	String image = "/fixedImage1.png";
    	//Clean the field
    	logger.info("Image Size - Content not entered");
    	driver.findElement(By.cssSelector("#fixed-size-image  input[value='Delete']")).click();
    	validationString = driver.findElement(By.cssSelector("#fixed-size-image .datum")).getAttribute("value");
    	assertTrue(validationString.isEmpty());
    	
    	logger.info("Image Size - Wrong content entered");
    	driver.findElement(By.cssSelector("#fixed-size-image input[value='Add']")).click();
    	
    	WebElement element = (new WebDriverWait(driver, 30)).until(
        		ExpectedConditions.presenceOfElementLocated(By.cssSelector("#cstudio-wcm-popup-div")));
    	//Check the modal window
    	assertTrue(element!=null);
    	
    	driver.findElement(By.cssSelector("#uploadFileNameId")).sendKeys(assetsPath + wrongImage);
    	driver.findElement(By.cssSelector("#uploadButton")).click();
    	
    	element = (new WebDriverWait(driver, 30)).until(
        		ExpectedConditions.presenceOfElementLocated(By.cssSelector("#alertDialog")));
    	
    	//Check warning message
        assertTrue(element.getText().contains("The uploaded file does not fulfill the width & height constraints"));
        driver.findElement(By.cssSelector("#alertDialog button")).click();

    	validationString = driver.findElement(By.cssSelector("#fixed-size-image .datum")).getAttribute("value");
    	assertTrue(validationString.isEmpty());
    	
    	logger.info("Image Size - Right content entered");
    	driver.findElement(By.cssSelector("#fixed-size-image input[value='Add']")).click();
    	
    	element = (new WebDriverWait(driver, 30)).until(
        		ExpectedConditions.presenceOfElementLocated(By.cssSelector("#cstudio-wcm-popup-div")));
    	//Check the modal window
    	assertTrue(element!=null);
    	
    	driver.findElement(By.cssSelector("#uploadFileNameId")).sendKeys(assetsPath + image);
    	driver.findElement(By.cssSelector("#uploadButton")).click();
    	
    	new WebDriverWait(driver, 60).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
              return !d.findElement(By.cssSelector("#fixed-size-image .datum")).getAttribute("value").isEmpty();
            }
          });
    	
    	validationString = driver.findElement(By.cssSelector("#fixed-size-image .datum")).getAttribute("value");
    	assertTrue(validationString.equals(uploadImageDS + image));
    }
    
    @Test
    public void testWidgetControlRangeSize() {
    	String wrongImage = "/rangeImage2.png";
    	String image = "/rangeImage1.png";
    	
    	//range-size-image
    	logger.info("Image Size - Content not entered");
    	driver.findElement(By.cssSelector("#range-size-image  input[value='Delete']")).click();
    	validationString = driver.findElement(By.cssSelector("#range-size-image .datum")).getAttribute("value");
    	assertTrue(validationString.isEmpty());
    	
    	logger.info("Image Size - Wrong content entered");
    	driver.findElement(By.cssSelector("#range-size-image input[value='Add']")).click();
    	
    	WebElement element = (new WebDriverWait(driver, 30)).until(
        		ExpectedConditions.presenceOfElementLocated(By.cssSelector("#cstudio-wcm-popup-div")));
    	//Check the modal window
    	assertTrue(element!=null);
    	
    	driver.findElement(By.cssSelector("#uploadFileNameId")).sendKeys(assetsPath + wrongImage);
    	driver.findElement(By.cssSelector("#uploadButton")).click();
    	
    	element = (new WebDriverWait(driver, 30)).until(
        		ExpectedConditions.presenceOfElementLocated(By.cssSelector("#alertDialog")));
    	
    	//Check warning message
        assertTrue(element.getText().contains("The uploaded file does not fulfill the width & height constraints"));
        driver.findElement(By.cssSelector("#alertDialog button")).click();

    	validationString = driver.findElement(By.cssSelector("#range-size-image .datum")).getAttribute("value");
    	assertTrue(validationString.isEmpty());
    	
    	logger.info("Image Size - Right content entered");
    	driver.findElement(By.cssSelector("#range-size-image input[value='Add']")).click();
    	
    	element = (new WebDriverWait(driver, 30)).until(
        		ExpectedConditions.presenceOfElementLocated(By.cssSelector("#cstudio-wcm-popup-div")));
    	//Check the modal window
    	assertTrue(element!=null);
    	
    	driver.findElement(By.cssSelector("#uploadFileNameId")).sendKeys(assetsPath + image);
    	driver.findElement(By.cssSelector("#uploadButton")).click();
    	
    	new WebDriverWait(driver, 60).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
              return !d.findElement(By.cssSelector("#range-size-image .datum")).getAttribute("value").isEmpty();
            }
          });
    	
    	validationString = driver.findElement(By.cssSelector("#range-size-image .datum")).getAttribute("value");
    	assertTrue(validationString.equals(uploadImageDS + image));
    }
    
    @Test
    public void testWidgetControlThumbnailSize() {
    	//range-size-image
    	logger.info("Image Thumbnail Size");
    	validationString = driver.findElement(By.id("thumbnail-image-cstudio-form-image-picker")).getAttribute("style");
    	assertEquals("height: 120px; width: 200px; ",validationString);
    }
    
    @Test
    public void testWidgetControlManyDatasources() {
    	String image = "/about-us-header-bg.png";
    	logger.info("Image Datasources - Content not entered");
    	driver.findElement(By.cssSelector("#many-ds-image  input[value='Delete']")).click();
    	validationString = driver.findElement(By.cssSelector("#many-ds-image .datum")).getAttribute("value");
    	assertTrue(validationString.isEmpty());
    	
    	logger.info("Image Browse");
    	driver.findElement(By.cssSelector("#many-ds-image input[value='Add']")).click();
    	List<WebElement> datasources =  driver.findElements(By.cssSelector("#many-ds-image .cstudio-form-control-image-picker-add-container-item"));
    	assertTrue(datasources.size() == 2);
    	
    	for(WebElement element: datasources) {
    		if(element.getText().equals("Image Browse")) {
    			element.click();
    			break;
    		}
    	}
    	
    	logger.info("Handler browser window");
    	String editWindow = driver.getWindowHandle();
    	String browseWindow = "";
    	Set<String> handles = driver.getWindowHandles();
        for (String windowHandle : handles) {
        	driver.switchTo().window(windowHandle);
        	if (driver.getCurrentUrl().contains("cstudio-browse")){
        		browseWindow = windowHandle;
        		break;
        	}
        }
        logger.info("Select Image");
        //The window can be switch
        assertTrue(!browseWindow.isEmpty());
        driver.findElement(By.cssSelector("#result-select--static-assets-images-about-us-header-bg-png input[type='radio']")).click();
        driver.findElement(By.cssSelector("#formSaveButton[value='Add Item']")).click();
        logger.info("Check Image");
        driver.switchTo().window(editWindow);
        validationString = driver.findElement(By.cssSelector("#many-ds-image .datum")).getAttribute("value");
    	assertTrue(validationString.equals(browseImageDS + image));
    }
}
