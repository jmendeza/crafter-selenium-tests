/**
 * 
 */
package org.craftercms.web.basic;

import static org.junit.Assert.assertTrue;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.craftercms.web.BaseTest;
import org.craftercms.web.CStudioSeleniumUtil;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * @author Praveen C Elineni
 *
 */
public class PageEditTests extends BaseTest {
    private String updateString = "About Us Page Updated";

    /**
     * Test Page Save and Close Functionality
     * 
     * @throws InterruptedException
     */
    @Test
    public void testPageEditSaveAndClose() throws InterruptedException {    	
    	driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

    	// Login
        CStudioSeleniumUtil.tryLogin(driver,
                seleniumProperties.getProperty("craftercms.admin.username"), 
                seleniumProperties.getProperty("craftercms.admin.password"),
                true);

        String dashboardUrl = String.format(seleniumProperties.getProperty("craftercms.site.dashboard.url"), seleniumProperties.getProperty("craftercms.sitename"));

        // Navigate to Dashboard page
        driver.navigate().to(dashboardUrl);

        // Execute JS before Edit Page
        CStudioSeleniumUtil.editPageJS(driver, seleniumProperties.getProperty("craftercms.page.to.edit"), 
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
        driver.findElement(By.cssSelector("#internal-name .datum")).sendKeys(updateString);

        // Click Save&Close button and wait for change to complete
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

        driver.navigate().to(dashboardUrl);
        assertTrue(driver.getTitle().equals("Crafter Studio"));

        // check my-recent-activity widget
        new WebDriverWait(driver, 60).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
              return d.findElement(By.id("MyRecentActivity-body")).getText().contains(updateString);
            }
          });

        assertTrue(driver.findElement(By.id("MyRecentActivity-body")).getText().contains(updateString));
    }

    /**
     * Test Page Save and Preview Functinality
     * 
     * @throws InterruptedException
     */
    @Test
    public void testPageEditSaveAndPreview() throws InterruptedException {
    	driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

    	// Login
        CStudioSeleniumUtil.tryLogin(driver,
                seleniumProperties.getProperty("craftercms.admin.username"), 
                seleniumProperties.getProperty("craftercms.admin.password"),
                true);

        String dashboardUrl = String.format(seleniumProperties.getProperty("craftercms.site.dashboard.url"), seleniumProperties.getProperty("craftercms.sitename"));

        // Navigate to Dashboard page
        driver.navigate().to(dashboardUrl);

        // Execute JS before Edit Page
        CStudioSeleniumUtil.editPageJS(driver, seleniumProperties.getProperty("craftercms.page.to.edit"), 
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
        driver.findElement(By.cssSelector("#internal-name .datum")).sendKeys(updateString);

        // Click Save&Close button and wait for change to complete
        driver.findElement(By.id("cstudioSaveAndPreview")).click();
        Thread.sleep(30000);

        // Navigate back to dashboard page and switch window
        handles = driver.getWindowHandles();
        for (String h : handles) {
          driver.switchTo().window(h);
          if (driver.getTitle().contains("Crafter Studio"))
        	  break;
        }

        assertTrue(driver.getTitle().equals("Crafter Studio"));
        assertTrue(CStudioSeleniumUtil.readFileContents(seleniumProperties.getProperty("craftercms.preview.deployer.path") + seleniumProperties.getProperty("craftercms.page.to.edit"), updateString));
    }
}