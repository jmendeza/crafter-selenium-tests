package org.craftercms.web.basic;

import org.craftercms.web.BaseTest;
import org.craftercms.web.CStudioSeleniumUtil;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;

/**
 * @author Jonathan Méndez
 */
public abstract class DashboardTestsBase extends BaseTest{

    protected abstract String getUpdateString();
    protected abstract String getUsername();
    protected abstract String getPassword();

    /**
     * Test Dashboard Page Context Nav Functionality
     */
    @Test
    public void testContextNav() {
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

        CStudioSeleniumUtil.tryLogin(driver,
                getUsername(),
                getPassword(),
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
                getUsername(),
                getPassword(),
                true);
        driver.navigate().to(String.format(seleniumProperties.getProperty("craftercms.site.dashboard.url"), seleniumProperties.getProperty("craftercms.sitename")));

        // check if title exists and match title with correct site value
        WebElement element = driver.findElement(By.id("pageTitle"));
        assertTrue(element.getText().contains(seleniumProperties.getProperty("craftercms.sitetitle")));
        assertTrue(element.isDisplayed());
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
                getUsername(),
                getPassword(),
                true);
        String dashboardUrl = String.format(seleniumProperties.getProperty("craftercms.site.dashboard.url"), seleniumProperties.getProperty("craftercms.sitename"));
        driver.navigate().to(dashboardUrl);

        editAndSaveUtil(seleniumProperties.getProperty("craftercms.page.to.edit"), getUpdateString());

        driver.navigate().to(dashboardUrl);
        // check my-recent-activity widget
        new WebDriverWait(driver, 60).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                return d.findElement(By.id("MyRecentActivity-body")).getText().contains(getUpdateString());
            }
        });
        Thread.sleep(10000);
        assertTrue(driver.findElement(By.id("MyRecentActivity-body")).getText().contains(getUpdateString()));
    }

    /**
     * Test Dashboard Page Icon Guide
     */
    @Test
    public void testIconGuide() {
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

        CStudioSeleniumUtil.tryLogin(driver,
                getUsername(),
                getPassword(),
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
                getUsername(),
                getPassword(),
                true);
        driver.navigate().to(String.format(seleniumProperties.getProperty("craftercms.site.dashboard.url"), seleniumProperties.getProperty("craftercms.sitename")));

        WebElement element = driver.findElement(By.id("footer"));
        assertTrue(element.isDisplayed());
        assertTrue(element.getText().contains("Crafter Software"));
    }
}
