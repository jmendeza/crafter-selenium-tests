/**
 * 
 */
package org.craftercms.web.basic;

import org.craftercms.web.BaseTest;
import org.craftercms.web.CStudioSeleniumUtil;
import org.junit.Test;

import java.util.logging.Logger;

/**
 * @author Praveen C Elineni
 *
 */
public class LoginTests extends BaseTest {
	private static final Logger logger = Logger.getLogger("LoginTests.class");
    
    /**
     * Test Login Functionality
     * 
     * @throws InterruptedException
     */
	@Test
	public void testLogin() throws InterruptedException {
		logger.info("logging using wrong credentials");
        CStudioSeleniumUtil.tryLogin(driver, "wronguser", "worngpassword", false);
        CStudioSeleniumUtil.exit(driver);

        initializeDriver();
        logger.info("Logging using admin credentials");
        CStudioSeleniumUtil.tryLogin(driver,
                                        seleniumProperties.getProperty("craftercms.admin.username"), 
                                        seleniumProperties.getProperty("craftercms.admin.password"),
                                        true);
        CStudioSeleniumUtil.exit(driver);

        initializeDriver();
        logger.info("Logging using author credentials");
        CStudioSeleniumUtil.tryLogin(driver,
                seleniumProperties.getProperty("craftercms.author.username"), 
                seleniumProperties.getProperty("craftercms.author.password"),
                true);
	}
}