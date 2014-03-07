/**
 * 
 */
package org.craftercms.web.basic;

import org.craftercms.web.BaseTest;
import org.craftercms.web.CStudioSeleniumUtil;
import org.junit.Test;

/**
 * @author Praveen C Elineni
 *
 */
public class LogoutTests extends BaseTest {
    /**
     * Test Logout Functionality
     */
    @Test
    public void testLogout() {
    	CStudioSeleniumUtil.tryLogin(driver,
                                          seleniumProperties.getProperty("craftercms.admin.username"), 
                                          seleniumProperties.getProperty("craftercms.admin.password"),
                                           true);

        CStudioSeleniumUtil.navigateToUrl(driver, siteName, dashboardUrl);

        CStudioSeleniumUtil.tryLogout(driver);
    }
}