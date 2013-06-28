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
     * Test Logout Functinality
     */
    @Test
    public void testLogout() {
    	String url = String.format(seleniumProperties.getProperty("craftercms.site.dashboard.url"), 
    									seleniumProperties.getProperty("craftercms.sitename"));

    	CStudioSeleniumUtil.tryLogin(driver,
                                          seleniumProperties.getProperty("craftercms.admin.username"), 
                                          seleniumProperties.getProperty("craftercms.admin.password"),
                                           true);

        CStudioSeleniumUtil.navigateToUrl(driver, seleniumProperties.getProperty("craftercms.sitename"), url);

        CStudioSeleniumUtil.tryLogout(driver);
    }
}