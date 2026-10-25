package tests;

import base.BaseTest;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;
import pages.LoginPage;
import pages.SearchPage;
import utils.ConfigReader;

import java.time.Duration;

public class OpenResultTest extends BaseTest {

    @Test(description = "TC04 - clicking the first person result opens that profile")
    public void openFirstResult() {
        String user = ConfigReader.username();
        String pass = ConfigReader.password();
        if (user.isEmpty() || pass.isEmpty() || user.equals("your-email@example.com")) {
            throw new SkipException("Credentials are not configured in config.properties");
        }

        LoginPage loginPage = new LoginPage(driver).open();
        loginPage.login(user, pass);
        loginPage.waitForOutcome();
        if (loginPage.isSecurityCheckpoint()) {
            loginPage.waitForManualVerification();
        }
        Assert.assertTrue(loginPage.isLoggedIn(), "Login failed: " + driver.getCurrentUrl());

        SearchPage searchPage = new SearchPage(driver).search("software testing");
        Assert.assertTrue(searchPage.isResultsPage(),
                "Expected a search results URL but was: " + driver.getCurrentUrl());
        searchPage.filterByPeople();

        WebElement first = searchPage.firstResultLink();
        String expectedPath = java.net.URI.create(first.getAttribute("href")).getPath().replaceAll("/$", "");
        first.click();

        new WebDriverWait(driver, Duration.ofSeconds(45))
                .until(d -> d.getCurrentUrl().contains(expectedPath));

        Assert.assertTrue(driver.getCurrentUrl().contains("/in/"),
                "Expected a profile URL but was: " + driver.getCurrentUrl());
        Assert.assertTrue(driver.getCurrentUrl().contains(expectedPath),
                "Expected URL containing " + expectedPath + " but was: " + driver.getCurrentUrl());
        Assert.assertFalse(driver.getCurrentUrl().contains("/search/results"),
                "Should have left the results page");
    }
}