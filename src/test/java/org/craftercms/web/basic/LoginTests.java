/**
 * 
 */
package org.craftercms.web.basic;

import static org.junit.Assert.fail;

import java.util.Properties;
import java.util.logging.Logger;

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
public class LoginTests {
	private static final Logger logger = Logger.getLogger("LoginTests.class");

    private final static String SELENIUM_PROPERTIES = "selenium.properties";
    protected Selenium selenium;
    protected WebDriver driver;
    protected static DesiredCapabilities desiredCapabilities;
    protected Properties seleniumProperties = new Properties();
    private StringBuffer verificationErrors = new StringBuffer();

    @Before
    public void setUp() throws Exception {
    	seleniumProperties.load(LoginTests.class.getClassLoader().getResourceAsStream(SELENIUM_PROPERTIES));

    	desiredCapabilities = new DesiredCapabilities();
        desiredCapabilities.setJavascriptEnabled(true);
        desiredCapabilities.setCapability("takesScreenshot", true);
        desiredCapabilities.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, 
        		                             seleniumProperties.getProperty("craftercms.phantomjs.path"));
        driver = new PhantomJSDriver(desiredCapabilities);
    }
    
    /**
     * Test Login Functinality
     * 
     * @throws InterruptedException
     */
	@Test
	public void testLogin() throws InterruptedException {
		logger.info("logging using wrong credentials");
        CStudioSeleniumUtil.tryLogin(driver, "wronguser", "worngpassword", false);
        CStudioSeleniumUtil.exit(driver);

        driver = new PhantomJSDriver(desiredCapabilities);
        logger.info("Logging using admin credentials");
        CStudioSeleniumUtil.tryLogin(driver,
                                        seleniumProperties.getProperty("craftercms.admin.username"), 
                                        seleniumProperties.getProperty("craftercms.admin.password"),
                                        true);
        CStudioSeleniumUtil.exit(driver);

        driver = new PhantomJSDriver(desiredCapabilities);
        logger.info("Logging using author credentials");
        CStudioSeleniumUtil.tryLogin(driver,
                seleniumProperties.getProperty("craftercms.author.username"), 
                seleniumProperties.getProperty("craftercms.author.password"),
                true);
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