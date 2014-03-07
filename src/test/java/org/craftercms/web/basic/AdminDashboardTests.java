/**
 *
 */
package org.craftercms.web.basic;

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