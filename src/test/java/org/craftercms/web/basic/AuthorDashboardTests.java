/**
 *
 */
package org.craftercms.web.basic;

/**
 * @author Praveen C Elineni
 */
public class AuthorDashboardTests extends DashboardTestsBase {

    @Override
    protected String getUpdateString() {
        return "About Page Updated Author";
    }

    @Override
    protected String getUsername() {
        return seleniumProperties.getProperty("craftercms.author.username");
    }

    @Override
    protected String getPassword() {
        return seleniumProperties.getProperty("craftercms.author.password");
    }
}