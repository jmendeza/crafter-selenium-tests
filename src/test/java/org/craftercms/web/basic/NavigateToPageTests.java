/**
 * 
 */
package org.craftercms.web.basic;

import static org.junit.Assert.fail;

import java.util.Properties;

import org.craftercms.web.CStudioSeleniumUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.thoughtworks.selenium.Selenium;

/**
 * @author Praveen C Elineni
 *
 */
public class NavigateToPageTests {
    private final static String SELENIUM_PROPERTIES = "selenium.properties";
    protected Selenium selenium;
    protected WebDriver driver;
    protected static DesiredCapabilities desiredCapabilities;
    protected Properties seleniumProperties = new Properties();
    private StringBuffer verificationErrors = new StringBuffer();

    @Before
    public void setUp() throws Exception {
    	seleniumProperties.load(NavigateToPageTests.class.getClassLoader().getResourceAsStream(SELENIUM_PROPERTIES));

    	desiredCapabilities = new DesiredCapabilities();
        desiredCapabilities.setJavascriptEnabled(true);
        desiredCapabilities.setCapability("takesScreenshot", true);
        desiredCapabilities.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, 
        		                             seleniumProperties.getProperty("craftercms.phantomjs.path"));
        driver = new PhantomJSDriver(desiredCapabilities);
    }

    /**
     * Navigate to Dashboard Page after Login
     */
    @Test
    public void testNavigateToDashboardPage() {
    	String url = String.format(seleniumProperties.getProperty("craftercms.site.dashboard.url"), seleniumProperties.getProperty("craftercms.sitename"));

    	CStudioSeleniumUtil.tryLogin(driver,
                                          seleniumProperties.getProperty("craftercms.admin.username"), 
                                          seleniumProperties.getProperty("craftercms.admin.password"),
                                           true);

    	CStudioSeleniumUtil.navigateToUrl(driver, seleniumProperties.getProperty("craftercms.sitename"), url);
    }

    /**
     * Navigate To Un-Authorized Access Page after failed Login 
     */
    @Test
    public void testUnauthorizedAccess() {
    	String url = String.format(seleniumProperties.getProperty("craftercms.site.dashboard.url"), seleniumProperties.getProperty("craftercms.sitename"));

    	CStudioSeleniumUtil.tryLogin(driver, "wronguser", "worngpassword", false);

    	CStudioSeleniumUtil.navigateToUrl(driver, seleniumProperties.getProperty("craftercms.sitename"), url);
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