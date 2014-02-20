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
 */
public class AdminDashboardTests extends DashboardTestsBase {

    @Override
    protected String getUpdateString() {
        return "About Page Updated1";
    }

    @Override
    protected String getUsername() {
        return seleniumProperties.getProperty("craftercms.admin.username");
    }

    @Override
    protected String getPassword() {
        return seleniumProperties.getProperty("craftercms.admin.password");
    }
}