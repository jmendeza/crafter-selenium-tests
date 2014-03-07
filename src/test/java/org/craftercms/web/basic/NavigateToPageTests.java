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
public class NavigateToPageTests extends BaseTest {

    /**
     * Navigate to Dashboard Page after Login
     */
    @Test
    public void testNavigateToDashboardPage() {
    	CStudioSeleniumUtil.tryLogin(driver,
                                          seleniumProperties.getProperty("craftercms.admin.username"), 
                                          seleniumProperties.getProperty("craftercms.admin.password"),
                                           true);

    	CStudioSeleniumUtil.navigateToUrl(driver, siteName, dashboardUrl);
    }

    /**
     * Navigate To Un-Authorized Access Page after failed Login 
     */
    @Test
    public void testUnauthorizedAccess() {
    	CStudioSeleniumUtil.tryLogin(driver, "wronguser", "worngpassword", false);

    	CStudioSeleniumUtil.navigateToUrl(driver, siteName, dashboardUrl);
    }
}