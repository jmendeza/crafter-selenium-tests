/**
 * 
 */
package org.craftercms.web.basic;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.craftercms.web.CStudioSeleniumUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.thoughtworks.selenium.Selenium;

/**
 * @author Praveen C Elineni
 *
 */
public class AuthorDashboardTests {
    private final static String SELENIUM_PROPERTIES = "selenium.properties";
    protected Selenium selenium;
    protected WebDriver driver;
    protected static DesiredCapabilities desiredCapabilities;
    protected Properties seleniumProperties = new Properties();
    private StringBuffer verificationErrors = new StringBuffer();
    private String updateString = "About Page Updated Author";

    @Before
    public void setUp() throws Exception {
    	seleniumProperties.load(AuthorDashboardTests.class.getClassLoader().getResourceAsStream(SELENIUM_PROPERTIES));

    	desiredCapabilities = new DesiredCapabilities();
        desiredCapabilities.setJavascriptEnabled(true);
        desiredCapabilities.setCapability("takesScreenshot", true);
        desiredCapabilities.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, 
        		                             seleniumProperties.getProperty("craftercms.phantomjs.path"));
        driver = new PhantomJSDriver(desiredCapabilities);
    }

    /**
     * Test Dashboard Page Context Nav Functionality
     */
    @Test
    public void testContextNav() {
    	driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

        CStudioSeleniumUtil.tryLogin(driver,
                seleniumProperties.getProperty("craftercms.author.username"), 
                seleniumProperties.getProperty("craftercms.author.password"),
                true);
        driver.navigate().to(String.format(seleniumProperties.getProperty("craftercms.site.dashboard.url"), seleniumProperties.getProperty("craftercms.sitename")));

        CStudioSeleniumUtil.waitForItemToDisplay(driver, 30, By.id("authoringContextNavHeader"));
        CStudioSeleniumUtil.waitForItemToDisplay(driver, 30, By.id("acn-wcm-logo-link"));
        CStudioSeleniumUtil.waitForItemToDisplay(driver, 30, By.id("acn-dropdown-toggler"));

        WebElement element = driver.findElement(By.id("acn-dropdown-toggler"));
        assertTrue(element.getText().equals("Site Content"));
        element.click();

        new WebDriverWait(driver, 30).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
              return d.findElement(By.id("acn-dropdown-menu-wrapper")).isDisplayed();
            }
        });

        element = driver.findElement(By.id("acn-dropdown-menu-wrapper"));
        assertTrue(element.isDisplayed());
    }

    /**
     * Test Dashboard Page Title Functionality
     */
    @Test
    public void testSiteDashboardTitle() {
    	driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

        CStudioSeleniumUtil.tryLogin(driver,
                seleniumProperties.getProperty("craftercms.author.username"), 
                seleniumProperties.getProperty("craftercms.author.password"),
                true);
        driver.navigate().to(String.format(seleniumProperties.getProperty("craftercms.site.dashboard.url"), seleniumProperties.getProperty("craftercms.sitename")));

        // check if title exists and match title with correct site value
        WebElement element = driver.findElement(By.id("pageTitle"));
        assertTrue(element.getText().contains(seleniumProperties.getProperty("craftercms.sitetitle")));
        assertTrue(element.isDisplayed());
    }

    /**
     * Test Dashboard Page Icon Guide
     */    
    @Test
    public void testIconGuide() {
    	driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

        CStudioSeleniumUtil.tryLogin(driver,
                seleniumProperties.getProperty("craftercms.author.username"), 
                seleniumProperties.getProperty("craftercms.author.password"),
                true);
        driver.navigate().to(String.format(seleniumProperties.getProperty("craftercms.site.dashboard.url"), seleniumProperties.getProperty("craftercms.sitename")));

        WebElement element = driver.findElement(By.id("icon-guide"));
        assertTrue(element.isDisplayed());
        assertTrue(element.getText().contains("Icon Guide"));
    }

    /**
     * Test Dashboard Page Footer
     */    
    @Test
    public void testFooter() {
    	driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

        CStudioSeleniumUtil.tryLogin(driver,
                seleniumProperties.getProperty("craftercms.author.username"), 
                seleniumProperties.getProperty("craftercms.author.password"),
                true);
        driver.navigate().to(String.format(seleniumProperties.getProperty("craftercms.site.dashboard.url"), seleniumProperties.getProperty("craftercms.sitename")));

        WebElement element = driver.findElement(By.id("footer"));
        assertTrue(element.isDisplayed());
        assertTrue(element.getText().contains("Crafter Software"));
    }

    /**
     * Test Dashboard Page My Recent Activity Functinality
     * 
     * @throws InterruptedException 
     */
    @Test
    public void testMyRecentActivity() throws InterruptedException {
    	driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

        CStudioSeleniumUtil.tryLogin(driver,
                seleniumProperties.getProperty("craftercms.author.username"), 
                seleniumProperties.getProperty("craftercms.author.password"),
                true);
        driver.navigate().to(String.format(seleniumProperties.getProperty("craftercms.site.dashboard.url"), seleniumProperties.getProperty("craftercms.sitename")));
        
        editAndSaveUtil(seleniumProperties.getProperty("craftercms.page.to.edit"), updateString);

        // check my-recent-activity widget
        new WebDriverWait(driver, 60).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
              return d.findElement(By.id("MyRecentActivity-body")).getText().contains(updateString);
            }
        });
        Thread.sleep(10000);
        assertTrue(driver.findElement(By.id("MyRecentActivity-body")).getText().contains(updateString));
    }

    /*
     * Helper utility for code re-use
     */
    private void editAndSaveUtil(String editPage, String editString) {
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

    @After
    public void tearDown() throws Exception {
        CStudioSeleniumUtil.exit(driver);
        String verificationErrorString = verificationErrors.toString();
        if (!"".equals(verificationErrorString)) {
            fail(verificationErrorString);
        }
    }
}