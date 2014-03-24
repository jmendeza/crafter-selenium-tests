package org.craftercms.web.basic;

import org.craftercms.web.BaseTest;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * @author Jonathan Méndez
 */
public class SearchTests extends BaseTest {

    @Test
    public void searchTest() {
        login();
        driver.navigate().to(dashboardUrl);

        String checkText = "Can big data deliver on the promise of real-time risk management?";
        String searchQuery = "big data";
        String searchBaseUrl = seleniumProperties.getProperty("craftercms.base.url") + seleniumProperties.getProperty("craftercms.site.search.url");

        String searchUrl = String.format(searchBaseUrl, searchQuery);

        logger.info("Checking search with url: '" + searchUrl + "'");
        driver.navigate().to(searchUrl);

        logger.info("Checking element with text '" + checkText + "' exists");
        boolean textFound = false;
        List<WebElement> elements = driver.findElements(By.cssSelector("ul.search-results li span.muted"));
        for (WebElement element : elements) {
            if (element.getText().contains(checkText)) {
                textFound = true;
                break;
            }
        }
        assertTrue(textFound);
    }
}
